package com.autoreason.setmincheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.autoreason.setmincheck.setobjects.ExpBitVectorSet;
import com.autoreason.setmincheck.setobjects.ExpSumVectorSet;
import com.autoreason.setmincheck.setobjects.ExpBoolVectorSet;
import com.autoreason.setmincheck.setobjects.ExpBoolVectorSet2;
import com.autoreason.setmincheck.setobjects.ExpSetRepresent;
import com.autoreason.setmincheck.setobjects.SetRepresent;

public class RunExperiment {

	final static String RESULT_FILE = "results.csv";
	final static int EXPERIMENT_REPETITIONS = 20;
	final static int MEASUREMENT_REPETITIONS = 10;

	@SuppressWarnings("unchecked")
	public static <E extends ExpSetRepresent<S>, S extends SetRepresent<?> & Comparable<S>> void main(String[] args) {

		// list of objects realizing different set representations
		ArrayList<E> setRepList = new ArrayList<E>();
		setRepList.add((E) new ExpBitVectorSet());		
		setRepList.add((E) new ExpBoolVectorSet());
		setRepList.add((E) new ExpBoolVectorSet2());
		setRepList.add((E) new ExpSumVectorSet());

		// array with names of executed classes
		String[] testedClasses = new String[setRepList.size() + 2];
		testedClasses[0] = "Simple";
		testedClasses[testedClasses.length - 1] = "UBTree";
		for (int i = 1; i <= setRepList.size(); i++) {
			testedClasses[i] = setRepList.get(i - 1).getClass().getSimpleName().substring(3);
		}

		DataProvider dataProvider;
		long[] measuredTimes;
		BufferedWriter buffWriter;

		try {
			// prepare file for results
			FileWriter writer = new FileWriter(RESULT_FILE);
			// create writer
			writer = new FileWriter(RESULT_FILE, true);
			buffWriter = new BufferedWriter(writer);

			// write used classes to file as names for columns
			String line = "test-file";
			for (int i = 0; i < testedClasses.length; i++) {
				line = line + "," + testedClasses[i];
			}
			buffWriter.write(line);
			buffWriter.newLine();

			// list all test files
			BufferedReader nameReader = new BufferedReader(
					new InputStreamReader(RunExperiment.class.getResourceAsStream("/fileNames.txt")));
			String[] fileNames = nameReader.lines().toArray(String[]::new);

			// show current experiment progress
			System.out.println("running: " + "1/" + fileNames.length);
			// create DataProvider for data given in first test file
			dataProvider = new DataProvider("/files/" + fileNames[0]);

			// conduct experiment with gradually growing collection
			measuredTimes = getTimeForMinCheckGrow(setRepList, dataProvider, MEASUREMENT_REPETITIONS);
			// create String containing file name and results
			line = "grow";
			for (int k = 0; k < measuredTimes.length; k++) {
				line = line + "," + measuredTimes[k];
			}
			// write results to file
			buffWriter.write(line);
			buffWriter.newLine();

			// conduct performance measurement for each test file
			for (int i = 1; i < fileNames.length; i++) {
				String fileName = fileNames[i];
				// show current experiment progress
				System.out.println("running: " + (i + 1) + "/" + fileNames.length);

				// create DataProvider for data given in test file
				dataProvider = new DataProvider("/files/" + fileName);

				// initialize arrays for measured times
				measuredTimes = new long[testedClasses.length];
				long[] currentTimes = new long[measuredTimes.length];
				// repeat computation with different test sets
				for (int k = 0; k < EXPERIMENT_REPETITIONS; k++) {
					// perform experiment
					currentTimes = getTimeForMinCheck(setRepList, dataProvider, MEASUREMENT_REPETITIONS);
					// update times
					for (int j = 0; j < currentTimes.length; j++) {
						measuredTimes[j] += currentTimes[j];
					}
					// get new test set
					dataProvider.getNewTestSet();
				}
				// compute average of measured times
				for (int j = 0; j < measuredTimes.length; j++) {
					measuredTimes[j] /= EXPERIMENT_REPETITIONS;
				}
				// create String containing file name and results
				line = fileName.substring(0, fileName.indexOf("."));
				for (int k = 0; k < measuredTimes.length; k++) {
					line = line + "," + measuredTimes[k];
				}
				// write results to file
				buffWriter.write(line);
				buffWriter.newLine();
			}

			nameReader.close();
			buffWriter.close();

			// experiment finished
			System.out.println("done");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Measure the performance time for conducting set minimality checking
	 * 
	 * @param <E>          An implementation of {@link ExpSetRepresent} for type
	 *                     {@code S}
	 * @param <S>          An implementation of both {@link SetRepresent} and
	 *                     {@link Comparable}
	 * @param setRepList   An {@link ArrayList} containing different
	 *                     {@link ExpSetRepresent} implementations used to perform
	 *                     set minimality checking
	 * @param dataProvider A {@link DataProvider} providing the sets for the
	 *                     minimality checking
	 * @param repeat       An {@code int} defining how often the process is repeated
	 *                     to allow more accurate time measurements
	 * @return An {@code long[]} containing values that represent the measured time
	 *         in nanoseconds, whereby the first element contains the time for the
	 *         standard approach of considering every set in a collection until a
	 *         subset is found, while the remaining list entries relate to each
	 *         element of {@code setRepList} and at last the representation based on
	 *         {@link UBTree}
	 * @see {@link System#nanoTime()}
	 */
	public static <E extends ExpSetRepresent<S>, S extends SetRepresent<?> & Comparable<S>> long[] getTimeForMinCheck(
			ArrayList<E> setRepList, DataProvider dataProvider, int repeat) {
		// get number of tested set representations
		int setRepNr = setRepList.size();
		// convert the collections into the different provided set representations
		ArrayList<ArrayList<NavigableSet<S>>> setRepConvertList = dataProvider.getConvertedCollections(setRepList);
		// convert the collections into UBTree objects
		ArrayList<UBTree<Integer>> ubTreeList = new ArrayList<UBTree<Integer>>();
		for (Collection<Set<Integer>> col : dataProvider.fileCollections) {
			ubTreeList.add(new UBTree<Integer>(col));
		}
		// create list to store measured time for each set representation
		long[] measuredTimes = new long[setRepNr + 2];
		// variables for time measuring
		long start;
		long end;

		// repeat process for more accurate time measurement
		for (int j = 0; j < repeat; j++) {

			// start time measuring
			start = System.nanoTime();
			// perform minimality checking by simply going through collection and looking
			// for subsets
			for (Collection<Set<Integer>> col : dataProvider.fileCollections) {
				// perform minimality check on collection
				simpleMinimalityCheck(col, dataProvider.testSet);
			}
			// end time measuring
			end = System.nanoTime();
			// save measurement for current set representation
			measuredTimes[0] += end - start;

			// conduct minimality check for each set representation
			for (int i = 0; i < setRepNr; i++) {
				// prepare currently tested SetRepresent implementation
				ExpSetRepresent<S> expSetRep = setRepList.get(i);
				MatchProvider<S, Set<?>> matchProvider = expSetRep.getMatchProvider();
				SubsetChecker<S> subsetChecker = expSetRep.getSubsetChecker();

				// start time measuring
				start = System.nanoTime();
				// check minimality for each collection
				for (NavigableSet<S> col : setRepConvertList.get(i)) {
					// perform minimality check
					SetMinimalityChecker.<S>isMinimal(col, dataProvider.testSet, matchProvider, subsetChecker);
				}
				// end time measuring
				end = System.nanoTime();
				// save measurement for current set representation
				measuredTimes[i + 1] += end - start;
			}

			// use UBTree representation
			// start time measuring
			start = System.nanoTime();
			// check minimality for each UBTree
			for (UBTree<Integer> tree : ubTreeList) {
				// perform minimality check
				tree.checkMinimal((Set<Integer>) dataProvider.testSet);
			}
			// end time measuring
			end = System.nanoTime();
			// save measurement for current set representation
			measuredTimes[measuredTimes.length - 1] += end - start;

		}
		// compute average
		for (int i = 0; i < measuredTimes.length; i++) {
			measuredTimes[i] /= repeat;
		}

		return measuredTimes;

	}

	/**
	 * Measure the performance time for conducting set minimality checking, where
	 * the collection of sets is gradually increased in order to additionally take
	 * into account the building time of the deployed set representations
	 * 
	 * @param <E>          An implementation of {@link ExpSetRepresent} for type
	 *                     {@code S}
	 * @param <S>          An implementation of both {@link SetRepresent} and
	 *                     {@link Comparable}
	 * @param setRepList   An {@link ArrayList} containing different
	 *                     {@link ExpSetRepresent} implementations used to perform
	 *                     set minimality checking
	 * @param dataProvider A {@link DataProvider} providing the sets for the
	 *                     minimality checking
	 * @param repeat       An {@code int} defining how often the process is repeated
	 *                     to allow more accurate time measurements
	 * @return An {@code long[]} containing values that represent the measured time
	 *         in nanoseconds, whereby the first element contains the time for the
	 *         standard approach of considering every set in a collection until a
	 *         subset is found, while the remaining list entries relate to each
	 *         element of {@code setRepList} and at last the representation based on
	 *         {@link UBTree}
	 * @see {@link System#nanoTime()}
	 */
	public static <E extends ExpSetRepresent<S>, S extends SetRepresent<?> & Comparable<S>> long[] getTimeForMinCheckGrow(
			ArrayList<E> setRepList, DataProvider dataProvider, int repeat) {
		// collection of sets for minimality check
		Collection<Set<Integer>> setCol = dataProvider.fileCollections.get(0);
		// test set used for minimality check w.r.t. collection of sets
		Set<Integer> testSet = dataProvider.testSet;

		// get number of tested set representations
		int setRepNr = setRepList.size();
		// create list to store measured time for each set representation
		long[] measuredTimes = new long[setRepNr + 2];
		// variables for time measuring
		long start;
		long end;

		// repeat process for more accurate time measurement
		for (int j = 0; j < repeat; j++) {
			Collection<Set<Integer>> col = new HashSet<Set<Integer>>();
			// start time measuring
			start = System.nanoTime();
			// gradually introduce each set of the collection
			for (Set<Integer> set : setCol) {
				// increase collection
				col.add(set);
				// perform simple minimality check
				simpleMinimalityCheck(col, testSet);
			}
			// end time measuring
			end = System.nanoTime();
			// save measurement for current set representation
			measuredTimes[0] += end - start;

			// conduct minimality check for each set representation
			for (int i = 0; i < setRepNr; i++) {
				// prepare currently tested SetRepresent implementation
				ExpSetRepresent<S> expSetRep = setRepList.get(i);
				MatchProvider<S, Set<?>> matchProvider = expSetRep.getMatchProvider();
				SubsetChecker<S> subsetChecker = expSetRep.getSubsetChecker();

				// collection for set representations
				NavigableSet<S> convertSets = new TreeSet<S>();
				// start time measuring
				start = System.nanoTime();
				// gradually introduce each set of the collection
				for (Set<Integer> set : setCol) {
					// add converted set to collection
					convertSets.add(expSetRep.getSetRepresent(set));
					// perform minimality check
					SetMinimalityChecker.<S>isMinimal(convertSets, dataProvider.testSet, matchProvider, subsetChecker);

				}
				// end time measuring
				end = System.nanoTime();
				// save measurement for current set representation
				measuredTimes[i + 1] += end - start;
			}

			// collection for set representations
			UBTree<Integer> ubTree = new UBTree<Integer>();
			// start time measuring
			start = System.nanoTime();
			// gradually introduce each set of the collection
			for (Set<Integer> set : setCol) {
				// add converted set to collection
				ubTree.insert(new TreeSet<Integer>(set));
				// perform minimality check
				ubTree.checkMinimal(testSet);
			}
			// end time measuring
			end = System.nanoTime();
			// save measurement for current set representation
			measuredTimes[measuredTimes.length - 1] += end - start;
		}

		// compute average
		for (int i = 0; i < measuredTimes.length; i++) {
			measuredTimes[i] /= repeat;
		}

		return measuredTimes;

	}

	/**
	 * Simple set minimality check by considering every set of a collection to find
	 * all the subsets
	 * 
	 * @param col     A {@link Collection} of {@link Set} elements
	 * @param testSet A {@link Set}
	 * @return {@code true} if the collection {@code col} does not contain any
	 *         subset of {@code testSet}, otherwise {@code false}
	 */
	private static boolean simpleMinimalityCheck(Collection<Set<Integer>> col, Set<Integer> testSet) {
		Collection<Set<Integer>> subsets = new HashSet<Set<Integer>>();
		// get all subsets from collection
		for (Set<Integer> set : col) {
			if (testSet.containsAll(set)) {
				subsets.add(set);
			}
		}
		// testSet is minimal w.r.t. col if no subset could be found
		return subsets.isEmpty();
	}

}
