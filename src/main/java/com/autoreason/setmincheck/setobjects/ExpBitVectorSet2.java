package com.autoreason.setmincheck.setobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.autoreason.setmincheck.AbSetRepSubsetChecker;
import com.autoreason.setmincheck.MatchProvider;
import com.autoreason.setmincheck.SubsetChecker;

/**
 * An {@link ExpSetRepresent} implementation for {@link BitVectorSet2}
 *
 */
public class ExpBitVectorSet2 implements ExpSetRepresent<BitVectorSet2> {

	@Override
	public MatchProvider<BitVectorSet2, Set<?>> getMatchProvider() {
		return new BitVecSet2MatchProvider();
	}

	@Override
	public SubsetChecker<BitVectorSet2> getSubsetChecker() {
		return new AbSetRepSubsetChecker<BitVectorSet2>();
	}

	@Override
	public Collection<BitVectorSet2> convertCollection(Collection<Set<Integer>> col) {
		// new collection for converted sets
		Collection<BitVectorSet2> convertedCol = new HashSet<>();
		// go through each element of col
		for (Set<?> set : col) {
			// add converted set to new collection
			convertedCol.add(new BitVectorSet2(set));
		}

		return convertedCol;
	}
	
	@Override
	public BitVectorSet2 getSetRepresent(Set<?> set) {
		return new BitVectorSet2(set);
	}

}
