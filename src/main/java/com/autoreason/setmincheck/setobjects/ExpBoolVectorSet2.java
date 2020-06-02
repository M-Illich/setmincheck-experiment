package com.autoreason.setmincheck.setobjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.autoreason.setmincheck.MatchProvider;

/**
 * An {@link ExpSetRepresent} implementation for {@link BoolVectorSet2}
 *
 */
public class ExpBoolVectorSet2 implements ExpSetRepresent<BoolVectorSet2> {

	@Override
	public MatchProvider<BoolVectorSet2, Set<?>> getMatchProvider() {
		return new BoolVecSet2MatchProvider();
	}

	@Override
	public Collection<BoolVectorSet2> convertCollection(Collection<Set<Integer>> col) {
		// new collection for converted sets
		Collection<BoolVectorSet2> convertedCol = new HashSet<>();
		// go through each element of col
		for (Set<?> set : col) {
			// add converted set to new collection
			convertedCol.add(new BoolVectorSet2(set));
		}

		return convertedCol;
	}

	@Override
	public BoolVectorSet2 getSetRepresent(Set<?> set) {
		return new BoolVectorSet2(set);
	}

}
