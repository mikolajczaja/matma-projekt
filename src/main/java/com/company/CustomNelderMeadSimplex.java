//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.company;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.AbstractSimplex;

public class CustomNelderMeadSimplex extends AbstractSimplex {
    private static final double DEFAULT_RHO = 1.0D;
    private static final double DEFAULT_KHI = 2.0D;
    private static final double DEFAULT_GAMMA = 0.5D;
    private static final double DEFAULT_SIGMA = 0.5D;
    private final double rho;
    private final double khi;
    private final double gamma;
    private final double sigma;

    public void build(double[] startPoint){
        super.build(startPoint);
    }


    public CustomNelderMeadSimplex(int n) {
        this(n, 1.0D);
    }

    public CustomNelderMeadSimplex(int n, double sideLength) {
        this(n, sideLength, 1.0D, 2.0D, 0.5D, 0.5D);
    }

    public CustomNelderMeadSimplex(int n, double sideLength, double rho, double khi, double gamma, double sigma) {
        super(n, sideLength);
        this.rho = rho;
        this.khi = khi;
        this.gamma = gamma;
        this.sigma = sigma;
    }

    public CustomNelderMeadSimplex(int n, double rho, double khi, double gamma, double sigma) {
        this(n, 1.0D, rho, khi, gamma, sigma);
    }

    public CustomNelderMeadSimplex(double[] steps) {
        this(steps, 1.0D, 2.0D, 0.5D, 0.5D);
    }

    public CustomNelderMeadSimplex(double[] steps, double rho, double khi, double gamma, double sigma) {
        super(steps);
        this.rho = rho;
        this.khi = khi;
        this.gamma = gamma;
        this.sigma = sigma;
    }

    public CustomNelderMeadSimplex(double[][] referenceSimplex) {
        this(referenceSimplex, 1.0D, 2.0D, 0.5D, 0.5D);
    }

    public CustomNelderMeadSimplex(double[][] referenceSimplex, double rho, double khi, double gamma, double sigma) {
        super(referenceSimplex);
        this.rho = rho;
        this.khi = khi;
        this.gamma = gamma;
        this.sigma = sigma;
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

        for(int i = 0; i < n; ++i) {
            double[] x = this.getPoint(i).getPointRef();

            for(int j = 0; j < n; ++j) {
                centroid[j] += x[j];
            }
        }

        double scaling = 1.0D / (double)n;

        for(int j = 0; j < n; ++j) {
            centroid[j] *= scaling;
        }

        double[] xR = new double[n];

        for(int j = 0; j < n; ++j) {
            xR[j] = centroid[j] + this.rho * (centroid[j] - xWorst[j]);
        }

        PointValuePair reflected = new PointValuePair(xR, evaluationFunction.value(xR), false);
        if (comparator.compare(best, reflected) <= 0 && comparator.compare(reflected, secondBest) < 0) {
            this.replaceWorstPoint(reflected, comparator);
        } else {
            double[] xSmallest;

            PointValuePair inContracted;
            if (comparator.compare(reflected, best) < 0) {
                xSmallest = new double[n];

                for(int i = 0; i < n; ++i) {
                    xSmallest[i] = centroid[i] + this.khi * (xR[i] - centroid[i]);
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

                    for(int i = 0; i < n; ++i) {
                        xSmallest[i] = centroid[i] + this.gamma * (xR[i] - centroid[i]);
                    }

                    inContracted = new PointValuePair(xSmallest, evaluationFunction.value(xSmallest), false);
                    if (comparator.compare(inContracted, reflected) <= 0) {
                        this.replaceWorstPoint(inContracted, comparator);
                        return;
                    }
                } else {
                    xSmallest = new double[n];

                    for(int i = 0; i < n; ++i) {
                        xSmallest[i] = centroid[i] - this.gamma * (centroid[i] - xWorst[i]);
                    }

                    inContracted = new PointValuePair(xSmallest, evaluationFunction.value(xSmallest), false);
                    if (comparator.compare(inContracted, worst) < 0) {
                        this.replaceWorstPoint(inContracted, comparator);
                        return;
                    }
                }

                xSmallest = this.getPoint(0).getPointRef();

                for(int i = 1; i <= n; ++i) {
                    double[] x = this.getPoint(i).getPoint();

                    for(int j = 0; j < n; ++j) {
                        x[j] = xSmallest[j] + this.sigma * (x[j] - xSmallest[j]);
                    }

                    this.setPoint(i, new PointValuePair(x, 0.0D / 0.0, false));
                }

                this.evaluate(evaluationFunction, comparator);
            }
        }
      //  System.out.println(Arrays.toString(xR) +" , "+ Arrays.toString(best.getFirst())+" , "+best.getSecond());
    }
}
