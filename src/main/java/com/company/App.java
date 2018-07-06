package com.company;

import org.apache.commons.math3.optim.PointValuePair;

import java.util.Arrays;
import java.util.Comparator;

public class App {

    public static void main(String[] args) {

        CustomNelderMeadSimplex simplex = new CustomNelderMeadSimplex(2);
        simplex.build(new double[]{-10, 1});

        Comparator<PointValuePair> pointValuePairComparator = new Comparator<PointValuePair>() {
            public int compare(PointValuePair o1, PointValuePair o2) {
                if (o1.getSecond() > o2.getSecond()) {
                    return 1;
                } else if (o1.getSecond() < o2.getSecond()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
        for (int i = 0; i < 100; i++) {

            simplex.iterate(new Bukin6Function(), pointValuePairComparator);
            PointValuePair[] points = simplex.getPoints();
            System.out.println("---------------------");
            for (PointValuePair pointValuePair : points) {
                System.out.println(Arrays.toString(pointValuePair.getPoint()) + " , " + pointValuePair.getSecond());
            }
            //  System.out.println("points: "+Arrays.toString(points));
        }
    }


}
