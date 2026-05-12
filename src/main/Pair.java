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

	public static <X,Y> Map<X,Y> toMap(List<Pair<X,Y>> list){
		var map = new HashMap<X,Y>(list.size());
		list.forEach(e -> map.put(e.first, e.second));
		return map;
	}
}
