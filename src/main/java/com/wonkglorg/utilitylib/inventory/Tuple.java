package com.wonkglorg.utilitylib.inventory;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Tuple<K, V> implements Serializable, Cloneable {
	private final K val1;
	private final V val2;

	public Tuple(K key, V value) {
		this.val1 = key;
		this.val2 = value;
	}


	public static <K, V> Tuple<K, V> of(K key, V value) {
		return new Tuple<>(key, value);
	}

	/**
	 * @return the first value
	 */
	public K getVal1() {
		return val1;
	}

	/**
	 * @return the second value
	 */
	public V getVal2() {
		return val2;
	}

	/**
	 * Swaps the first and second value
	 */
	public Tuple<V, K> swap() {
		return new Tuple<>(val2, val1);
	}

	/**
	 * Sets the new key for this tuple
	 *
	 * @param val2 the second value to set
	 */
	public Tuple<K, V> setVal2(V val2) {
		return new Tuple<>(val1, val2);
	}

	/**
	 * Sets the new key for this tuple
	 *
	 * @param val1 the first value to set
	 */
	public Tuple<K, V> setVal1(K val1) {
		return new Tuple<>(val1, val2);
	}

	@Override
	public String toString() {
		return "Tuple{" + "key=" + val1 + ", value=" + val2 + '}';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Tuple<?, ?> tuple)) {
			return false;
		}
		return Objects.equals(val1, tuple.val1) && Objects.equals(val2, tuple.val2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(val1, val2);
	}

	/**
	 * Creates a copy of this object (in case values are mutable use
	 * {@link #deepCopy(Function, Function)} instead
	 *
     */
	@Override
	protected Object clone() {
		return new Tuple<>(val1, val2);
	}

	/**
	 * Deep copies a tuple (in case values are mutable like lists
	 *
	 * @param keyCopyFunction key function
	 * @param valueCopyFunction value function
	 * @return the tuple copy
	 */
	public Tuple<K, V> deepCopy(Function<K, K> keyCopyFunction, Function<V, V> valueCopyFunction) {
		return new Tuple<>(keyCopyFunction.apply(val1), valueCopyFunction.apply(val2));
	}

	/**
	 * Converts a tuple to a map Entry
	 */
	public Map.Entry<K, V> toEntry() {
		return new AbstractMap.SimpleEntry<>(val1, val2);
	}

	/**
	 * @return if one or more values are null
	 */
	public boolean hasNulls() {
		return val1 == null || val2 == null;
	}

	/**
	 * @return if both values are null
	 */
	public boolean isEmpty() {
		return val1 == null && val2 == null;
	}


}
