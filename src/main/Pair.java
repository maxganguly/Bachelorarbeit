package main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple Generic Pair
 * @param <X> first
 * @param <Y> second
 */
public record Pair<X,Y> (X first, Y second) {

	/**
	 * Generates a Map from a list of Pairs
	 * @param list a list of pairs, where each "first" is unique
	 * @return a map equivalent to the given list
	 */
	public static <X,Y> Map<X,Y> toMap(List<Pair<X,Y>> list){
		var map = new HashMap<X,Y>(list.size());
		list.forEach(e -> map.put(e.first, e.second));
		return map;
	}
	
	@Override
	public final String toString() {
		return first.toString()+':'+second.toString();
	}
}
