package com.nirocca.ornithoalert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxNElementsCollector<T> {
    
    private Map<T, Integer> countMap = new HashMap<>();
    
    public void add(T element) {
        if (!countMap.containsKey(element)) {
            countMap.put(element, 0);
        }
        countMap.put(element, countMap.get(element) + 1);
    }
    
    public List<ElementWithCount<T>> getMaxElements(int count) {
        List<ElementWithCount<T>> list = new ArrayList<>(countMap.size());
        countMap.entrySet().stream()
                .forEach(e -> list.add(new ElementWithCount<T>(e.getKey(), e.getValue())));
        Collections.sort(list, Collections.reverseOrder());
        return list.subList(0, Math.min(count, list.size()));
    }
    
    public static final class ElementWithCount<T> implements Comparable<ElementWithCount<T>>{
        private T element;
        private int count;
        
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
