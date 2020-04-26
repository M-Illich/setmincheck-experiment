package com.autoreason.setmincheck;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UBTreeTest {

	@Test
	public void testInsert() {
		TreeSet<Integer> set1 = new TreeSet<Integer>();
		set1.add(1);
		set1.add(2);
		set1.add(3);
		set1.add(4);
		TreeSet<Integer> set2 = new TreeSet<Integer>();
		set2.add(2);
		set2.add(5);

		UBTree<Integer> ubTree = new UBTree<Integer>();
		ubTree.insert(set1);
		int second = ubTree.T.first().children.first().element;
		assertEquals(2, second);

		set1.remove(4);
		ubTree.insert(set1);
		boolean notEnd = ubTree.T.first().children.first().endOfPath;
		boolean end1 = ubTree.T.first().children.first().children.first().endOfPath;
		boolean end2 = ubTree.T.first().children.first().children.first().children.first().endOfPath;
		assertFalse(notEnd);
		assertTrue(end1);
		assertTrue(end2);

		ubTree.insert(set2);
		assertTrue(ubTree.T.size() == 2);

		set2.add(1);
		ubTree.insert(set2);
		int len2 = ubTree.T.first().children.first().children.size();
		assertTrue(len2 == 2);
	}

	@Test
	public void testFindNodes() {
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
		
		TreeSet<UBTreeNode<Integer>> nodes = new TreeSet<UBTreeNode<Integer>>();
		nodes.add(n0);
		nodes.add(n3);
		
		Collection<UBTreeNode<Integer>> foundNodes = new UBTree<Integer>().findNodes(nodes, 4);
		assertTrue(foundNodes.size() == 2);

	}
	
	@Test
	public void testLookup_subs() {
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
		
		TreeSet<UBTreeNode<Integer>> nodes = new TreeSet<UBTreeNode<Integer>>();
		n0.determineDistanceToNextEOP();
		n3.determineDistanceToNextEOP();
		nodes.add(n0);
		nodes.add(n3);
	
		TreeSet<Integer> set = new TreeSet<Integer>();
		set.add(0);
		set.add(1);
		set.add(2);
		set.add(3);
		set.add(4);
		set.add(5);
		set.add(6);
		TreeSet<Integer> sub1 = new TreeSet<Integer>();
//		sub1.add(0);
//		sub1.add(1);
//		sub1.add(2);
//		sub1.add(3);
//		sub1.add(4);
		sub1.add(5);
		TreeSet<Integer> sub2 = new TreeSet<Integer>();
//		sub2.add(0);
//		sub2.add(1);
//		sub2.add(2);		
		sub2.add(6);
		TreeSet<Integer> sub3 = new TreeSet<Integer>();		
//		sub3.add(3);
//		sub3.add(4);
		sub3.add(5);
		
		Collection<SortedSet<Integer>> subsets = new UBTree<Integer>().lookup_subs(nodes, set);
		
//		for (SortedSet<Integer> sub : subsets) {
//			for (Integer i : sub) {
//				System.out.print(i + " ");
//			}
//			System.out.println();
//		}
		
		assertTrue(subsets.contains(sub1));
		assertTrue(subsets.contains(sub2));
		assertTrue(subsets.contains(sub3));
		
	}

}
