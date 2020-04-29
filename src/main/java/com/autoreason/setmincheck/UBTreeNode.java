package com.autoreason.setmincheck;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A node for an {@link UBTree}
 *
 */
public class UBTreeNode<C extends Comparable<C>> implements Comparable<UBTreeNode<C>> {
	/**
	 * the element represented by the node
	 */
	C element;

	/**
	 * the children of the node
	 */
	SortedSet<UBTreeNode<C>> children;

	/**
	 * the End-of-Path marker, where a path refers to a sorted arrangement of a set
	 */
	boolean endOfPath;

	/**
	 * distance to next closest end-Of-Path marked node (serves as heuristic for
	 * sorting)
	 */
	int distanceToNextEOP;

	public UBTreeNode(C e) {
		this.element = e;
		this.children = new TreeSet<UBTreeNode<C>>();
		this.endOfPath = false;
		this.distanceToNextEOP = Integer.MAX_VALUE;
	}

	public UBTreeNode(C e, TreeSet<UBTreeNode<C>> ch) {
		this.element = e;
		this.children = ch;
		this.endOfPath = false;
		this.distanceToNextEOP = Integer.MAX_VALUE;
	}

	public UBTreeNode(C e, boolean eop) {
		this.element = e;
		this.children = new TreeSet<UBTreeNode<C>>();
		this.endOfPath = eop;
		this.distanceToNextEOP = Integer.MAX_VALUE;
	}

	/**
	 * Determine the distance to the next closest node that is marked as End-Of-Path
	 * 
	 * @return A positive {@code int} defining the length of the shortest path to
	 *         the next end-of-path node
	 */
	public int determineDistanceToNextEOP() {
		if (endOfPath) {
			distanceToNextEOP = 0;
		} else {
			// get minimum distance from children
			int minDistance = Integer.MAX_VALUE;
			int childDistance;
			for (UBTreeNode<C> child : children) {
				childDistance = child.determineDistanceToNextEOP();
				if (minDistance > childDistance) {
					// update minimum
					minDistance = childDistance;
					if (childDistance == 0) {
						// zero is minimum -> no further search necessary
						break;
					}
				}
			}
			// define distance for node (one greater than child minimum)
			distanceToNextEOP = minDistance + 1;
		}
		return distanceToNextEOP;
	}

	public int getDistanceToNextEOP() {
		return this.distanceToNextEOP;
	}

	@Override
	public int compareTo(UBTreeNode<C> other) {
		// use element for comparison (since method is used to sort members of an UBTree
		// where different ones always possess different elements)
		int c = this.element.compareTo(other.element);
		// use distance to next end-of-path marker for equal elements 
		// TODO (?) only needed for subset search where it is explicitly applied by the defined Comparator
//		if (c == 0) {				
//			c = Integer.compare(this.distanceToNextEOP, other.distanceToNextEOP);
//		}
		return c;
	}

// 			NOT NEEDED	
//	public UBTreeNode(SortedSet<C> set) {
//	if(!set.isEmpty()) {
//		C first = set.first();
//		this.element = first;
//		// define children with remaining set
//		set.remove(first);
//		TreeSet<UBTreeNode<C>> ch = new TreeSet<UBTreeNode<C>>();
//		if(!set.isEmpty()) {
//			ch.add(new UBTreeNode<C>(set));
//			this.endOfPath = false;
//		}
//		else {
//			// no remaining elements in set
//			this.endOfPath = true;
//		}			
//		this.children = ch;
//	}		
//}

}
