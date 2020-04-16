package com.autoreason.setmincheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;

import com.autoreason.setmincheck.setobjects.BitVectorSet;
import com.autoreason.setmincheck.setobjects.BitVectorSet2;
import com.autoreason.setmincheck.setobjects.BoolVectorSet;
import com.autoreason.setmincheck.setobjects.SetRepresent;

public class RunExperiment {

	public static <C extends SetRepresent<C, ?, ?>> void main(String[] args) {

		// path to test data file			 jsSets-galen.txt
		String file = "src\\main\\resources\\rndmCols-10x10000.txt";
		// list of objects realizing different set representations
		ArrayList<C> setRepList = new ArrayList<C>();
		setRepList.add((C) new BitVectorSet());
		setRepList.add((C) new BitVectorSet2());
		setRepList.add((C) new BoolVectorSet());

		// create DataProvider for data given in test file
		DataProvider dataProvider = new DataProvider(file);

		// perform experiment
		ArrayList<Long> measuredTimes = getTimeForMinCheck(setRepList, dataProvider, 100);

		// TEST TODO
		for (Long t : measuredTimes) {
			System.out.println(t);
		}

//		// TEST TODO
//		for (ArrayList<Collection<C>> arrayList : setRepConvertList) {
//			for (Collection<C> col : arrayList) {
//				for (C c : col) {
//					BitVectorSet bvs = (BitVectorSet) c;
//					System.out.print(bvs.setRepresentation[0] + " ");
//				}
//				System.out.println();
//			}
//		}

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
	 * @return An {@link ArrayList} containing a {@code long} value that represents
	 *         the measured time in nanoseconds, whereby the first element contains
	 *         the time for the standard approach of considering every set in a
	 *         collection until a subset is found, while the remaining list entries
	 *         relate to each element of {@code setRepList}
	 * @see {@link System#nanoTime()}
	 */
	public static <C extends SetRepresent<C, ?, ?>> ArrayList<Long> getTimeForMinCheck(ArrayList<C> setRepList,
			DataProvider dataProvider, int repeat) {
		// get number of tested set representations
		int setRepNr = setRepList.size();
		// create list to store measured time for each set representation
		ArrayList<Long> measuredTimes = new ArrayList<Long>(setRepNr + 2);
		// variables for time measuring
		long start;
		long end;

		// start time measuring
		start = System.nanoTime();
		// perform minimality checking by simply going through collection and looking
		// for subsets
		for (Collection<Set<Integer>> col : dataProvider.fileCollections) {
			// repeat process for more accurate time measurement
			for (int j = 0; j < repeat; j++) {
				// perform minimality check on collection
				simpleMinimalityCheck(col, dataProvider.testSet);
			}
		}
		// end time measuring
		end = System.nanoTime();
		// save measurement for current set representation
		measuredTimes.add((end - start) / repeat);

//		
//		// convert the collections into UBTree objects
//		ArrayList<UBTree<Integer>> ubTreeList = new ArrayList<UBTree<Integer>>();
//		for (Collection<Set<Integer>> col : dataProvider.fileCollections) {
//			ubTreeList.add(new UBTree<Integer>(col));
//		}
//		// start time measuring
//		start = System.nanoTime();
//		// check minimality for each UBTree
//		for (UBTree<Integer> tree : ubTreeList) {
//			// repeat process for more accurate time measurement
//			for (int j = 0; j < repeat; j++) {
//				// perform minimality check
//				tree.checkMinimal(dataProvider.testSet);
//			}
//		}
//		// end time measuring
//		end = System.nanoTime();
//		// save measurement for current set representation
//		measuredTimes.add((end - start) / repeat);

		
		// convert the collections into the different provided set representations
		ArrayList<ArrayList<NavigableSet<C>>> setRepConvertList = dataProvider.getConvertedCollections(setRepList);
		// conduct minimality check for each set representation
		for (int i = 0; i < setRepNr; i++) {
			// get MinimalityChecker instance for current SetRepresent implementation
			MinimalityChecker<?> minChecker = setRepList.get(i).getMinChecker();
			// start time measuring
			start = System.nanoTime();
			// check minimality for each collection
			for (NavigableSet col : setRepConvertList.get(i)) {
				// repeat process for more accurate time measurement
				for (int j = 0; j < repeat; j++) {
					// perform minimality check
					minChecker.isMinimal(col, dataProvider.testSet);
				}
			}
			// end time measuring
			end = System.nanoTime();
			// save measurement for current set representation
			measuredTimes.add((end - start) / repeat);
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
