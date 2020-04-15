package com.autoreason.setmincheck;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class UBTreeNodeTest {

	@Test
	public void testDetermineDistanceToNextEOP() {
		UBTreeNode<Integer> n0 = new UBTreeNode<Integer>(0);
		UBTreeNode<Integer> n1 = new UBTreeNode<Integer>(1);
		UBTreeNode<Integer> n2 = new UBTreeNode<Integer>(2);
		UBTreeNode<Integer> n3 = new UBTreeNode<Integer>(3);
		UBTreeNode<Integer> n4 = new UBTreeNode<Integer>(4);
		UBTreeNode<Integer> n5 = new UBTreeNode<Integer>(5, true);
		UBTreeNode<Integer> n6 = new UBTreeNode<Integer>(6, true);

		n0.children.add(n1);
		n1.children.add(n2);
		n2.children.add(n3);
		n2.children.add(n6);
		n3.children.add(n4);
		n4.children.add(n5);

		n0.determineDistanceToNextEOP();

		assertTrue(n6.distanceToNextEOP == 0);
		assertTrue(n5.distanceToNextEOP == 0);
		assertTrue(n4.distanceToNextEOP == 1);
		assertTrue(n3.distanceToNextEOP == 2);
		assertTrue(n2.distanceToNextEOP == 1);
		assertTrue(n1.distanceToNextEOP == 2);
		assertTrue(n0.distanceToNextEOP == 3);
	}

}
