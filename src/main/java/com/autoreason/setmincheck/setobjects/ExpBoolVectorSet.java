package com.autoreason.setmincheck.setobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.autoreason.setmincheck.AbSetRepSubsetChecker;
import com.autoreason.setmincheck.MatchProvider;
import com.autoreason.setmincheck.SubsetChecker;

/**
 * An {@link ExpSetRepresent} implementation for {@link BoolVectorSet}
 *
 */
public class ExpBoolVectorSet implements ExpSetRepresent<BoolVectorSet> {

	@Override
	public MatchProvider<BoolVectorSet, Set<?>> getMatchProvider() {
		return new BoolVecSetMatchProvider();
	}

	@Override
	public SubsetChecker<BoolVectorSet> getSubsetChecker() {
		return new AbSetRepSubsetChecker<BoolVectorSet>();
	}

	@Override
	public Collection<BoolVectorSet> convertCollection(Collection<Set<Integer>> col) {
		// new collection for converted sets
		Collection<BoolVectorSet> convertedCol = new HashSet<>();
		// go through each element of col
		for (Set<?> set : col) {
			// add converted set to new collection
			convertedCol.add(new BoolVectorSet(set));
		}

		return convertedCol;
	}

	@Override
	public BoolVectorSet getSetRepresent(Set<?> set) {
		return new BoolVectorSet(set);
	}

}
