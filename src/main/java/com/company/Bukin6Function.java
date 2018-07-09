package com.company;

import org.apache.commons.math3.analysis.MultivariateFunction;

public class Bukin6Function implements MultivariateFunction {

    public double value(double[] doubles) {

        if (doubles.length == 2) {
            double x = doubles[0];
            double y = doubles[1];
            return 100 * Math.sqrt(Math.abs(y - 0.01 * Math.pow(x, 2))) + 0.01 * Math.abs(x + 10);
        }
        return 0;
    }
}
