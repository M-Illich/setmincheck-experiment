package com.autoreason.setmincheck.setobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.autoreason.setmincheck.AbstractSetRepMatchProvider;

/**
 * An {@link ExpSetRepresent} implementation for {@link BitVectorSet}
 *
 */
public class ExpBitVectorSet implements ExpSetRepresent<BitVectorSet, long[]> {

	@Override
	public Collection<BitVectorSet> convertCollection(Collection<Set<Integer>> col) {
		// new collection for converted sets
		Collection<BitVectorSet> convertedCol = new HashSet<BitVectorSet>();
		// go through each element of col
		for (Set<Integer> set : col) {
			// add converted set to new collection
			convertedCol.add(new BitVectorSet(set));
		}

		return convertedCol;
	}

	@Override
	public BitVectorSet getSetRepresent(Set<?> set) {
		return new BitVectorSet(set);
	}

	@Override
	public void setSetRepLength(int len) {
		BitVectorSetConverter.setRepresentLength = len;
	}

	@Override
	public AbstractSetRepMatchProvider<BitVectorSet, long[]> getMatchProvider(Set<?> test) {
		return new BitVecSetMatchProvider(test);
	}

}
