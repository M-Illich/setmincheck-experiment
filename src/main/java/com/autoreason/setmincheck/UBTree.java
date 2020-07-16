package com.autoreason.setmincheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

/**
 * A tree data structure based on the Unlimited Branching Tree (UBTree) by
 * Hoffmann and Koehler ('A New Method to Index and Query Sets', 1999)
 *
 */
public class UBTree<C extends Comparable<C>> {
	
	// set of all root nodes of the included set trees
	ArrayList<UBTreeNode<C>> T;


	public UBTree() {
		T = new ArrayList<UBTreeNode<C>>();
	}

	/**
	 * Construct a UBTree that contains the sets of the given collection
	 * 
	 * @param col A {@link Collection} of {@link Set} elements
	 */
	public UBTree(Collection<Set<C>> col) {
		T = new ArrayList<UBTreeNode<C>>();
		// add sets of collection to tree
		for (Set<C> set : col) {
			// insert sorted set to tree
			insert(set);
		}
		// sort nodes
		Collections.sort(T);
	}

	/**
	 * Insert a {@link Set} into the UBTree
	 * 
	 * @param set A {@link Set}
	 */
	public void insert(Set<C> set) {
		ArrayList<C> setList = new ArrayList<>(set);
		// sort set
		Collections.sort(setList);
		// initialize currently regarded tree
		ArrayList<UBTreeNode<C>> tree = this.T;
		// currently regarded node
		UBTreeNode<C> curNode = null;
		// current element of set
		C elem;
		// states if element is already present in tree
		boolean found;

		// number of remaining set elements
		int remain = setList.size();
		Iterator<C> iter = setList.iterator();
		// insert each element
		while (iter.hasNext()) {
			// get current element
			elem = iter.next();
			remain--;
			// look if element already present
			found = false;

			// look for node with set element
			int i = Collections.binarySearch(tree, new UBTreeNode<C>(elem));
			if (i > -1) {
				curNode = tree.get(i);
				// adapt distance to next EOP if necessary
				if (curNode.distanceToNextEOP > remain) {
					curNode.distanceToNextEOP = remain;
				}
				found = true;
			}


			// introduce new node if element not found
			if (!found) {
				curNode = new UBTreeNode<C>(elem, remain);
				tree.add(curNode);
			}
			// consider children of current node
			tree = curNode.children;
		}
		// mark last node as end of path
		curNode.endOfPath = true;
	}

	/**
	 * Get all subsets of a given set that occur in a list of {@link UBTreeNode}
	 * objects
	 * 
	 * @param treeNodes  A sorted {@link ArrayList} of {@link UBTreeNode} objects
	 * @param set        A sorted {@link ArrayList}
	 * @param startIndex An {@code int} stating the index starting from which the
	 *                   set elements are considered
	 * @return A {@link Collection} containing all the subsets of {@code set}
	 *         contained in {@code treeNodes}
	 */
	public Collection<Set<C>> lookup_subs(ArrayList<UBTreeNode<C>> treeNodes, ArrayList<C> set, int startIndex) {
		// collection for subsets
		Collection<Set<C>> subsets = new ArrayList<Set<C>>();

		int setSize = set.size();
		int remainSetSize = setSize - startIndex;
		// find all nodes with a related set element
		for (int index = startIndex; index < setSize; index++) {
			C setElem = set.get(index);
			remainSetSize--;

			// look for node with set element
			int i = Collections.binarySearch(treeNodes, new UBTreeNode<C>(setElem));
			if (i > -1) {
				UBTreeNode<C> node = treeNodes.get(i);
				// only consider node if distance to next end-of-path marker is not greater than
				// number of remaining elements
				if (node.distanceToNextEOP <= remainSetSize) {
					if (node.endOfPath) {
						// subset found
						subsets.add(getSet(node));
					}
					if (remainSetSize > 0) {
						// consider children of node with remaining set elements
						subsets.addAll(lookup_subs(node.children, set, index + 1));
					}
				}
			}

		}
		return subsets;
	}

	/**
	 * Get the original set that is defined by the tree path ending with the given
	 * node
	 * 
	 * <p>
	 * Note: Here, only the last element of the set is returned, since its existence
	 * is sufficient to determine minimality of a set
	 * </p>
	 * 
	 * @param node A {@link UBTreeNode} with {@code endOfPath = true}
	 * @return The original {@link SortedSet} whose last element relates to the
	 *         given node
	 */
	private HashSet<C> getSet(UBTreeNode<C> node) {
		HashSet<C> set = new HashSet<C>();
		// here, for minimality check sufficient to return last element, indicating
		// that a subset exists
		set.add(node.element);

		return set;
	}

	/**
	 * Check if a given given {@link Set} is minimal w.r.t. the sets represented by
	 * this UBTree
	 * 
	 * @param testSet A {@link Set} with elements of {@link Comparable} type
	 *                {@code C}
	 * @return {@code true} if the UBTree does not contain any subset of
	 *         {@code testSet}, otherwise {@code false}
	 */
	public boolean checkMinimal(Set<C> testSet) {
		// sort set
		ArrayList<C> set = new ArrayList<C>(testSet);
		Collections.sort(set);
		// look for subsets subsets
		Collection<Set<C>> subsets = lookup_subs(this.T, set, 0);

		// set is only minimal w.r.t. collection if no subsets could be found
		return subsets.isEmpty();

	}

}
