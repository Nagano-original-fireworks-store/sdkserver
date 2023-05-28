package org.nofs.utils;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/* loaded from: sdkserver.jar:emu/grasscutter/utils/WeightedList.class */
public class WeightedList<E> {
    private final NavigableMap<Double, E> map = new TreeMap();
    private double total = 0.0d;

    public WeightedList<E> add(double weight, E result) {
        if (weight <= 0.0d) {
            return this;
        }
        this.total += weight;
        this.map.put(Double.valueOf(this.total), result);
        return this;
    }

    public E next() {
        double value = ThreadLocalRandom.current().nextDouble() * this.total;
        return this.map.higherEntry(Double.valueOf(value)).getValue();
    }

    public int size() {
        return this.map.size();
    }
}
