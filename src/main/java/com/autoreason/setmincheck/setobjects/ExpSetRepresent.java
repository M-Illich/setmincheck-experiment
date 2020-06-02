package com.autoreason.setmincheck.setobjects;

import java.util.Collection;
import java.util.Set;

import com.autoreason.setmincheck.MatchProvider;

/**
 * An interface that defines methods needed for the experiment with the
 * {@link SetRepresent} implementations from the {@code set-minimality-checking}
 * project
 *
 * @param <S> An implementation of both {@link SetRepresent} and
 *            {@link Comparable}
 * 
 */
public interface ExpSetRepresent<S extends SetRepresent<?> & Comparable<S>> {

	/**
	 * Get a {@link MatchProvider} instance for objects of the {@link SetRepresent}
	 * type {@code S}
	 * 
	 * @return An object of the {@code MatchProvider} implementation for type
	 *         {@code S}
	 */
	public MatchProvider<S, Set<?>> getMatchProvider();

	/**
	 * Convert the {@link Set} elements of a {@link Collection} into instances of
	 * the {@link SetRepresent} type {@code S}
	 * 
	 * @param col A {@link Collection} of {@link Set} elements containing
	 *            {@link Integer} values
	 * @return A {@link Collection} of {@link SetRepresent} objects that represent
	 *         the elements of {@code col}
	 */
	public Collection<S> convertCollection(Collection<Set<Integer>> col);

	/**
	 * Get an instance of the {@link SetRepresent} implementing type {@code S} for a
	 * given {@link Set}
	 * 
	 * @param set A {@link Set}
	 * @return An object of type {@code S} that represents {@code set}
	 */
	public S getSetRepresent(Set<?> set);

}
