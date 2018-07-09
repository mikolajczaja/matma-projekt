package com.company;

import org.apache.commons.math3.optim.PointValuePair;

import java.util.Comparator;

public class CustomComparator implements Comparator<PointValuePair> {

    public int compare(PointValuePair o1, PointValuePair o2) {

        if (o1.getSecond() > o2.getSecond()) {
            return 1;
        } else if (o1.getSecond() < o2.getSecond()) {
            return -1;
        } else {
            return 0;
        }
    }
}
