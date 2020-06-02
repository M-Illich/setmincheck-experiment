package com.autoreason.setmincheck.setobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.autoreason.setmincheck.MatchProvider;

/**
 * An {@link ExpSetRepresent} implementation for {@link SumVectorSet}
 *
 */
public class ExpSumVectorSet implements ExpSetRepresent<SumVectorSet> {

	@Override
	public MatchProvider<SumVectorSet, Set<?>> getMatchProvider() {
		return new SumVecSetMatchProvider();
	}

	@Override
	public Collection<SumVectorSet> convertCollection(Collection<Set<Integer>> col) {
		// new collection for converted sets
		Collection<SumVectorSet> convertedCol = new HashSet<>();
		// go through each element of col
		for (Set<?> set : col) {
			// add converted set to new collection
			convertedCol.add(new SumVectorSet(set));
		}

		return convertedCol;
	}

	@Override
	public SumVectorSet getSetRepresent(Set<?> set) {
		return new SumVectorSet(set);
	}

}
