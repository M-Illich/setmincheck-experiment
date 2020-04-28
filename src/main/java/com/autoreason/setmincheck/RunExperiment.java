package com.autoreason.setmincheck;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;

import com.autoreason.setmincheck.setobjects.BitVectorSet;
import com.autoreason.setmincheck.setobjects.BitVectorSet2;
import com.autoreason.setmincheck.setobjects.BoolVectorSet;
import com.autoreason.setmincheck.setobjects.BoolVectorSet2;
import com.autoreason.setmincheck.setobjects.SetRepresent;

public class RunExperiment {

	public static <C extends SetRepresent<C, ?, ?>> void main(String[] args) {
		
		final String RESULT_FILE = "results.csv";
		final int REPETITIONS = 20;
			
		// list of objects realizing different set representations
		ArrayList<C> setRepList = new ArrayList<C>();
		setRepList.add((C) new BitVectorSet());
		setRepList.add((C) new BitVectorSet2());
		setRepList.add((C) new BoolVectorSet());
		setRepList.add((C) new BoolVectorSet2());

		// array with names of executed classes
		String[] testedClasses = new String[setRepList.size() + 2];
		testedClasses[0] = "Simple";
		testedClasses[testedClasses.length - 1] = "UBTree";
		for (int i = 1; i <= setRepList.size(); i++) {
			testedClasses[i] = setRepList.get(i - 1).getClass().getSimpleName();
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
			BufferedReader nameReader = new BufferedReader(new InputStreamReader(RunExperiment.class.getResourceAsStream("/fileNames.txt")));			
			String[] fileNames = nameReader.lines().toArray(String[]::new);
			// conduct performance measurement for each test file
			for (int i = 0; i < fileNames.length; i++) {			
				String fileName = fileNames[i];
				// show current experiment progress
				System.out.println("running: " + (i+1) + "/" + fileNames.length);
				
				// create DataProvider for data given in test file
				dataProvider = new DataProvider("/files/" + fileName);
												
				// initialize arrays for measured times
				measuredTimes = new long[testedClasses.length];
				long[] currentTimes = new long[measuredTimes.length];
				// repeat computation with different test sets
				for (int k = 0; k < REPETITIONS; k++) {
					// perform experiment
					currentTimes = getTimeForMinCheck(setRepList, dataProvider, 20);	
					// update times
					for (int j = 0; j < currentTimes.length; j++) {
						measuredTimes[j] += currentTimes[j];
					}
					// get new test set
					dataProvider.getNewTestSet();
				}
				// compute average of measured times
				for (int j = 0; j < measuredTimes.length; j++) {
					measuredTimes[j] /= REPETITIONS;
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
	 * @param <C>
	 * @param setRepList   An {@link ArrayList} containing different
	 *                     {@link SetRepresent} implementations used to perform set
	 *                     minimality checking
	 * @param dataProvider A {@link DataProvider} providing the sets for the
	 *                     minimality checking
	 * @param repeat       An {@code int} defining how often the process is repeated
	 *                     to allow more accurate time measurements
	 * @return An {@code long[]} containing values that represent the measured time
	 *         in nanoseconds, whereby the first element contains the time for the
	 *         standard approach of considering every set in a collection until a
	 *         subset is found, while the remaining list entries relate to each
	 *         element of {@code setRepList}
	 * @see {@link System#nanoTime()}
	 */
	public static <C extends SetRepresent<C, ?, ?>> long[] getTimeForMinCheck(ArrayList<C> setRepList,
			DataProvider dataProvider, int repeat) {
		// get number of tested set representations
		int setRepNr = setRepList.size();
		// convert the collections into the different provided set representations
		ArrayList<ArrayList<NavigableSet<C>>> setRepConvertList = dataProvider.getConvertedCollections(setRepList);
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
				// get MinimalityChecker instance for current SetRepresent implementation
				MinimalityChecker<?> minChecker = setRepList.get(i).getMinChecker();
				// start time measuring
				start = System.nanoTime();
				// check minimality for each collection
				for (NavigableSet col : setRepConvertList.get(i)) {
					// perform minimality check
					minChecker.isMinimal(col, dataProvider.testSet);
				}
				// end time measuring
				end = System.nanoTime();
				// save measurement for current set representation
				measuredTimes[i + 1] += end - start;
			}

//	TODO
//			// convert the collections into UBTree objects
//			ArrayList<UBTree<Integer>> ubTreeList = new ArrayList<UBTree<Integer>>();
//			for (Collection<Set<Integer>> col : dataProvider.fileCollections) {
//				ubTreeList.add(new UBTree<Integer>(col));
//			}
//			// start time measuring
//			start = System.nanoTime();
//			// check minimality for each UBTree
//			for (UBTree<Integer> tree : ubTreeList) {
//				// perform minimality check
//				tree.checkMinimal(dataProvider.testSet);
//			}
//			// end time measuring
//			end = System.nanoTime();
//			// save measurement for current set representation
//			measuredTimes[measuredTimes.length - 1] += end - start;

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
	private static boolean simpleMinimalityCheck(Collection<Set<Integer>> col, Set testSet) {
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
