package com.autoreason.setmincheck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A tree data structure based on the Unlimited Branching Tree (UBTree) by
 * Hoffmann and Koehler ('A New Method to Index and Query Sets', 1999)
 *
 */
public class UBTree<C extends Comparable<C>> {
	// set of all root nodes of the included set trees
	SortedSet<UBTreeNode<C>> T;

	// Comparator based on distance to next end-of-path marker
	Comparator<UBTreeNode<C>> distComparator = Comparator.comparing(n -> n, (a, b) -> {
		int c = Integer.compare(a.distanceToNextEOP, b.distanceToNextEOP);
		// use element for equal distances
		if (c == 0) {
			c = a.element.compareTo(b.element);
		}
		return c;
	});

	
	public UBTree() {
		T = new TreeSet<UBTreeNode<C>>();
	}

	/**
	 * Construct a UBTree that contains the sets of the given collection
	 * 
	 * @param col A {@link Collection} of {@link SortedSet} elements
	 */
	public UBTree(Collection<Set<C>> col) {
		T = new TreeSet<UBTreeNode<C>>();
		// add sets of collection to tree
		for (Set<C> set : col) {
			// insert sorted set to tree
			insert(new TreeSet<C>(set));
		}
	}

	/**
	 * Insert a {@link SortedSet} into the UBTree
	 * 
	 * @param set A {@link SortedSet}
	 */
	public void insert(SortedSet<C> set) {
		// initialize currently regarded tree
		SortedSet<UBTreeNode<C>> tree = this.T;
		// currently regarded node
		UBTreeNode<C> curNode = null;
		// current element of set
		C elem;
		// states if element is already present in tree
		boolean found;

		// number of remaining set elements
		int remain = set.size();
		Iterator<C> iter = set.iterator();
		// insert each element
		while (iter.hasNext()) {
			// get current element
			elem = iter.next();
			remain--;
			// look if element already present
			found = false;
			for (UBTreeNode<C> node : tree) {
				if (node.element.equals(elem)) {
					curNode = node;
					// adapt distance to next EOP if necessary
					if (curNode.distanceToNextEOP > remain) {
						curNode.distanceToNextEOP = remain;
					}
					found = true;
					break;
				}
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
	 * Get all subsets of a given {@link SortedSet} that occur in a given set of
	 * {@link UBTreeNode} objects
	 * 
	 * @param treeNodes A {@link SortedSet} of {@link UBTreeNode} objects
	 * @param set       A {@link SortedSet}
	 * @return A {@link Collection} containing all the subsets of {@code set}
	 *         contained in {@code treeNodes}
	 */
	public Collection<SortedSet<C>> lookup_subs(SortedSet<UBTreeNode<C>> treeNodes, SortedSet<C> set) {
		// collection for subsets
		Collection<SortedSet<C>> subsets = new ArrayList<SortedSet<C>>();

		// find all nodes with a related set element and sort them by distance to next
		// end-of-path marker
		Collection<UBTreeNode<C>> matchNodes = new TreeSet<UBTreeNode<C>>(distComparator);
		int c;
		for (C e : set) {
			for (UBTreeNode<C> node : treeNodes) {
				// compare element with node element
				c = e.compareTo(node.element);
				if (c <= 0) {
					if (c == 0) {
						// matching node found
						matchNodes.add(node);
					}
					// stop search if element is not greater than node due to sorted order of tree
					// nodes
					break;
				}
			}
		}
		
		SortedSet<C> remainSet;
		int remainSetSize;
		// look for end-of-path marker, thus indicating a subset
		for (UBTreeNode<C> node : matchNodes) {
			// determine remaining set
			remainSet = set.tailSet(node.element);
			remainSetSize = remainSet.size();
			// only consider node if distance to next end-of-path marker is not greater than
			// number of remaining elements
			if (node.distanceToNextEOP <= remainSetSize) {
				if (node.endOfPath) {
					// subset found
					subsets.add(getSet(node));
				}
				if (remainSetSize > 0) {
					// consider children of node with remaining elements
					subsets.addAll(lookup_subs(node.children, remainSet));
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
	private SortedSet<C> getSet(UBTreeNode<C> node) {
		SortedSet<C> set = new TreeSet<C>();

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
		// look for subsets subsets
		Collection<SortedSet<C>> subsets = lookup_subs(this.T, new TreeSet<C>(testSet));

		// set is only minimal w.r.t. collection if no subsets could be found
		return subsets.isEmpty();

	}

	/**
	 * Find all the {@link UBTreeNode} objects in a collection that relate to the
	 * given element {@code e}
	 * 
	 * @param treeNodes A {@link Collection} of {@link UBTreeNode} objects
	 * @param e         An element of {@link Comparable} type {@code C}
	 * @return A {@link Collection} of {@link UBTreeNode} elements with
	 *         {@link UBTreeNode#element} {@code = e}
	 */
	protected Collection<UBTreeNode<C>> findNodes(Collection<UBTreeNode<C>> treeNodes, C e) {
		Collection<UBTreeNode<C>> matchNodes = new ArrayList<UBTreeNode<C>>();
		Collection<UBTreeNode<C>> nextNodes = new ArrayList<UBTreeNode<C>>();

		for (UBTreeNode<C> node : treeNodes) {
			// compare element with node element
			int c = e.compareTo(node.element);
			// matching node found
			if (c == 0) {
				matchNodes.add(node);
			} else {
				// element is bigger -> save children of node
				if (c == 1) {
					nextNodes.addAll(node.children);
				}
			}
		}
		// check children nodes for matches (if available)
		if (!nextNodes.isEmpty()) {
			matchNodes.addAll(findNodes(nextNodes, e));
		}

		return matchNodes;
	}

}
