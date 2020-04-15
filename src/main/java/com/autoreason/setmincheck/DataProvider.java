package com.autoreason.setmincheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.autoreason.setfileconverter.FileSetConverter;
import com.autoreason.setfileconverter.SetFileConverter;
import com.autoreason.setmincheck.datagenerator.SetGenerator;
import com.autoreason.setmincheck.setobjects.SetRepresent;

public class DataProvider {
	/**
	 * A list of set collections read from a file
	 * 
	 * @see FileSetConverter
	 */
	ArrayList<Collection<Set<Integer>>> fileCollections;

	/**
	 * A list of random set collections
	 * 
	 * @see SetGenerator
	 */
	ArrayList<Collection<Set<Integer>>> randomCollections;

	/**
	 * A {@link Set} used for performing of set minimality check
	 */
	Set testSet;
	
	// TODO choose appropriate test objects (?)
		// look for range of numbers or take subset of element (?) ...
	

	/**
	 * Construct a {@link DataProvider} object based on the data retrieved from the
	 * given file
	 * 
	 * @param file A {@link String} that defines the path to a text file containing
	 *             sets generated with {@link SetFileConverter}
	 */
	public DataProvider(String file) {
		// read collections from file
		this.fileCollections = FileSetConverter.readCollectionsFromFile(file);
		// initialize test set with first set of read collections
		this.testSet = generateTestSet(fileCollections.get(0));
	}
	
	/**
	 * Construct a {@link DataProvider} object based on the data retrieved from the
	 * given file and a {@link Set}
	 * 
	 * @param file A {@link String} that defines the path to a text file containing
	 *             sets generated with {@link SetFileConverter}
	 * @param test A {@link Set}
	 */
	public DataProvider(String file, Set test) {
		// read collections from file
		this.fileCollections = FileSetConverter.readCollectionsFromFile(file);
		// define test set
		this.testSet = test;
	}

	/**
	 * Create a file that contains randomly generated collections of integer value
	 * sets
	 * 
	 * @param file    A {@link String} that defines the path to the file where the
	 *                created collections are stored
	 * @param numCols An {@code int} value defining the number of created
	 *                collections
	 * @param numSets An {@code int} value defining the number of sets contained in
	 *                a collection
	 * @param maxSize An {@code int} value defining the maximum number of elements
	 *                for the generated sets
	 * @param range   An {@code int} value defining the upper limit of the randomly
	 *                drawn {@link Integer} values for the sets
	 * @param seed    A {@code long} value used for the generation of random values
	 * @see SetFileConverter
	 * @see SetGenerator
	 */
	public static void createRandomSetsFile(String file, int numCols, int numSets, int maxSize, int range, long seed) {
		Random seedGenerator = new Random(seed);
		for (int i = 0; i < numCols; i++) {
			SetFileConverter.writeCollectionToFile(
					SetGenerator.randomMinSetCollection(numSets, maxSize, range, seedGenerator.nextLong()), file);
		}
		// reset hash table for future converting processes
		SetFileConverter.reset();
	}

	/**
	 * Convert all the sets from {@link DataProvider#fileCollections} to
	 * representative objects of type {@link SetRepresent}
	 * 
	 * @param setRepList An {@link ArrayList} with objects of different types
	 *                   {@code C} used to convert the sets
	 * @return An {@link ArrayList} containing {@link Collection} elements with
	 *         objects of type {@code C} representing the sets from
	 *         {@link DataProvider#fileCollections}
	 */
	public <C extends SetRepresent<C, ?, ?>> ArrayList<ArrayList<NavigableSet<C>>> getConvertedCollections(
			ArrayList<C> setRepList) {
		// list to store all the lists with differently converted collections of sets
		ArrayList<ArrayList<NavigableSet<C>>> setRepConvertList = new ArrayList<ArrayList<NavigableSet<C>>>();

		// convert collections to each provided type
		for (C c : setRepList) {
			// list to store converted collections
			ArrayList<NavigableSet<C>> convertList = new ArrayList<NavigableSet<C>>();
			// convert each collection
			for (Collection<Set<Integer>> collection : fileCollections) {
				// add sorted converted collection to list
				convertList.add(new TreeSet<C>(c.convertCollection(collection)));
			}
			setRepConvertList.add(convertList);
		}

		return setRepConvertList;

	}
	

	private Set generateTestSet(Collection<Set<Integer>> col) {
		// take random set from collection
		 int index = new Random().nextInt(col.size());		 
		 Iterator<Set<Integer>> iter = col.iterator();		 
		 for (int i = 0; i < index; i++) {
			iter.next();
		}		 
		 Set<Integer> set = iter.next();
		 // remove set from collection
		 iter.remove();
		 
		 // alter set TODO
//		 set.remove(set.iterator().next());
		 
		 return set;
	}

	
	public static void main(String[] args) {
		int numCols = 10;
		int numSets = 10000;
		int maxSize = 100;
		int range = 30000;
		long seed = 11042020;
		String file = "src\\main\\resources\\rndmCols-" + numCols + "x" + numSets + "xmaxSize" + ".txt";

		// generate files with test data
		createRandomSetsFile(file, numCols, numSets, maxSize, range, seed);
	}

}
