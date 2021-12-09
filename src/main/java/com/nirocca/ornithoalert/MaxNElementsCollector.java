package com.nirocca.ornithoalert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxNElementsCollector<T> {
    
    private final Map<T, Integer> countMap = new HashMap<>();
    
    public void add(T element) {
        if (!countMap.containsKey(element)) {
            countMap.put(element, 0);
        }
        countMap.put(element, countMap.get(element) + 1);
    }
    
    public List<ElementWithCount<T>> getMaxElements(int count) {
        List<ElementWithCount<T>> list = new ArrayList<>(countMap.size());
        countMap.forEach((key, value) -> list.add(new ElementWithCount<>(key, value)));
        list.sort(Collections.reverseOrder());
        return list.subList(0, Math.min(count, list.size()));
    }
    
    public static final class ElementWithCount<T> implements Comparable<ElementWithCount<T>>{
        private final T element;
        private final int count;
        
        public ElementWithCount(T element, int count) {
            this.element = element;
            this.count = count;
        }

        public T getElement() {
            return element;
        }

        @Override
        public int compareTo(ElementWithCount<T> o) {
            return Integer.compare(count, o.count);
        }

        @Override
        public String toString() {
            return count + "\t" + element.toString();
        }
    }

}
