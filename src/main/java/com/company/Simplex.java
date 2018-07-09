package com.company;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex;

import java.util.Comparator;

public class Simplex extends AbstractSimplex {

    private static final double RHO = 1.0D;
    private static final double KHI = 2.0D;
    private static final double GAMMA = 0.5D;
    private static final double SIGMA = 0.5D;
    private static final double SIDE_LENGTH = 1.0D;

    public Simplex(int n) {
        super(n, SIDE_LENGTH);
    }

    public void build(double[] startPoint) {
        super.build(startPoint);
    }

    @Override
    public PointValuePair[] getPoints() {
        return super.getPoints();
    }

    public void iterate(MultivariateFunction evaluationFunction, Comparator<PointValuePair> comparator) {

        int n = this.getDimension();
        PointValuePair best = this.getPoint(0);
        PointValuePair secondBest = this.getPoint(n - 1);
        PointValuePair worst = this.getPoint(n);
        double[] xWorst = worst.getPointRef();
        double[] centroid = new double[n];

        for (int i = 0; i < n; ++i) {
            double[] x = this.getPoint(i).getPointRef();

            for (int j = 0; j < n; ++j) {
                centroid[j] += x[j];
            }
        }

        double scaling = 1.0D / (double) n;

        for (int j = 0; j < n; ++j) {
            centroid[j] *= scaling;
        }

        double[] xR = new double[n];

        for (int j = 0; j < n; ++j) {
            xR[j] = centroid[j] + RHO * (centroid[j] - xWorst[j]);
        }

        PointValuePair reflected = new PointValuePair(xR, evaluationFunction.value(xR), false);
        if (comparator.compare(best, reflected) <= 0 && comparator.compare(reflected, secondBest) < 0) {
            this.replaceWorstPoint(reflected, comparator);
        } else {
            double[] xSmallest;

            PointValuePair inContracted;
            if (comparator.compare(reflected, best) < 0) {
                xSmallest = new double[n];

                for (int i = 0; i < n; ++i) {
                    xSmallest[i] = centroid[i] + KHI * (xR[i] - centroid[i]);
                }

                inContracted = new PointValuePair(xSmallest, evaluationFunction.value(xSmallest), false);
                if (comparator.compare(inContracted, reflected) < 0) {
                    this.replaceWorstPoint(inContracted, comparator);
                } else {
                    this.replaceWorstPoint(reflected, comparator);
                }
            } else {
                if (comparator.compare(reflected, worst) < 0) {
                    xSmallest = new double[n];

                    for (int i = 0; i < n; ++i) {
                        xSmallest[i] = centroid[i] + GAMMA * (xR[i] - centroid[i]);
                    }

                    inContracted = new PointValuePair(xSmallest, evaluationFunction.value(xSmallest), false);
                    if (comparator.compare(inContracted, reflected) <= 0) {
                        this.replaceWorstPoint(inContracted, comparator);
                        return;
                    }
                } else {
                    xSmallest = new double[n];

                    for (int i = 0; i < n; ++i) {
                        xSmallest[i] = centroid[i] - GAMMA * (centroid[i] - xWorst[i]);
                    }

                    inContracted = new PointValuePair(xSmallest, evaluationFunction.value(xSmallest), false);
                    if (comparator.compare(inContracted, worst) < 0) {
                        this.replaceWorstPoint(inContracted, comparator);
                        return;
                    }
                }

                xSmallest = this.getPoint(0).getPointRef();

                for (int i = 1; i <= n; ++i) {
                    double[] x = this.getPoint(i).getPoint();

                    for (int j = 0; j < n; ++j) {
                        x[j] = xSmallest[j] + SIGMA * (x[j] - xSmallest[j]);
                    }

                    this.setPoint(i, new PointValuePair(x, 0.0D / 0.0, false));
                }

                this.evaluate(evaluationFunction, comparator);
            }
        }
    }
}
