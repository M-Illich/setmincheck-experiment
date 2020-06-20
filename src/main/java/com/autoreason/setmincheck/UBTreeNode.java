package com.autoreason.setmincheck;

import java.util.ArrayList;
import java.util.Collections;

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
	ArrayList<UBTreeNode<C>> children;

	/**
	 * the End-of-Path marker, where a path refers to a sorted arrangement of a set
	 */
	boolean endOfPath;

	/**
	 * distance to next closest end-Of-Path marked node
	 */
	int distanceToNextEOP;

	public UBTreeNode(C e) {
		this.element = e;
		this.children = new ArrayList<UBTreeNode<C>>();
		this.endOfPath = false;
		this.distanceToNextEOP = Integer.MAX_VALUE;
	}

	public UBTreeNode(C e, ArrayList<UBTreeNode<C>> ch) {
		this.element = e;
		Collections.sort(ch);
		this.children = ch;
		this.endOfPath = false;
		this.distanceToNextEOP = Integer.MAX_VALUE;
	}

	public UBTreeNode(C e, boolean eop) {
		this.element = e;
		this.children = new ArrayList<UBTreeNode<C>>();
		this.endOfPath = eop;
		if (eop) {
			this.distanceToNextEOP = 0;
		} else {
			this.distanceToNextEOP = Integer.MAX_VALUE;
		}
	}

	public UBTreeNode(C e, int dist) {
		this.element = e;
		this.children = new ArrayList<UBTreeNode<C>>();
		if (dist == 0) {
			this.endOfPath = true;
		} else {
			this.endOfPath = false;
		}
		this.distanceToNextEOP = dist;
	}

	@Override
	public int compareTo(UBTreeNode<C> other) {
		// use element for comparison (since method is used to sort members of an UBTree
		// where different ones always possess different elements)		
		return this.element.compareTo(other.element);
	}

	/**
	 * Determine the distance to the next closest node that is marked as End-Of-Path
	 * 
	 * <p>
	 * Note: This method is mainly used for testing purposes, since the distances
	 * are already computed during the {@link UBTree#insert} phase
	 * </p>
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

}
