package com.company;

import org.apache.commons.math3.optim.PointValuePair;
import org.jzy3d.analysis.AbstractAnalysis;
import org.jzy3d.analysis.AnalysisLauncher;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Builder;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.AbstractDrawable;
import org.jzy3d.plot3d.primitives.Point;
import org.jzy3d.plot3d.primitives.Polygon;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application extends AbstractAnalysis {

    private Bukin6Function function = new Bukin6Function();
    private Shape surface;

    private Simplex simplex;
    private CustomComparator pointValuePairComparator;
    private List<AbstractDrawable> addedPoints = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        System.out.println("initial simplex x");
        double x = scanner.nextDouble();
        System.out.println("initial simplex y");
        double y = scanner.nextDouble();
        System.out.println("number of iterations");
        int numberOfIterations = scanner.nextInt();

        Application app = new Application();
        app.initializeSimplex(new double[]{x, y});
        AnalysisLauncher.open(app);

        iterate(app, numberOfIterations);

        while (true) {
            app.chart.render();
        }
    }

    private static void iterate(Application demo, int numberOfIterations) {
        for (int i = 0; i < numberOfIterations; i++) {
            PointValuePair[] nextSimplexPoints = demo.getNextSimplexPoints();
            demo.addPoints(nextSimplexPoints);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            demo.chart.render();
        }
    }

    private void initializeSimplex(double[] startingPoints) {
        simplex = new Simplex(2);
        simplex.build(startingPoints);
        pointValuePairComparator = new CustomComparator();
    }

    @Override
    public void init() {

        Mapper mapper = new Mapper() {
            @Override
            public double f(double x, double y) {
                return function.value(new double[]{x, y});
            }
        };

        Range rangeX = new Range(-15, 5);
        Range rangeY = new Range(-3, 3);
        int steps = 100;

        surface = Builder.buildOrthonormal(new OrthonormalGrid(rangeX, steps, rangeY, steps), mapper);
        surface.setColorMapper(new ColorMapper(new ColorMapRainbow(), surface.getBounds().getZmin(), surface.getBounds().getZmax(), new Color(1, 1, 1, .5f)));
        surface.setFaceDisplayed(true);
        surface.setWireframeDisplayed(false);

        chart = AWTChartComponentFactory.chart(Quality.Advanced, getCanvasType());
        chart.getScene().getGraph().add(surface);
    }

    private void addPoints(PointValuePair[] nextSimplexPoints) {

        for (AbstractDrawable drawable : addedPoints) {
            surface.remove(drawable);
        }
        addedPoints.clear();

        List<Coord3d> simplexCoordinates = new ArrayList<>();

        int iterationLevel = 1;

        for (PointValuePair pointValuePair : nextSimplexPoints) {
            Coord3d coord3d = new Coord3d(pointValuePair.getPoint()[0], pointValuePair.getPoint()[1], pointValuePair.getSecond());
            simplexCoordinates.add(coord3d);

            Point point = new Point();
            point.setCoord(coord3d);
            point.setWidth(5);

            if (iterationLevel == 1) {
                point.setColor(Color.BLUE);
            }
            if (iterationLevel == 2) {
                point.setColor(Color.YELLOW);
            }
            if (iterationLevel == 3) {
                point.setColor(Color.GREEN);
            }
            surface.add(point);
            addedPoints.add(point);
            iterationLevel++;
        }


        AbstractDrawable polygon = new Polygon();

        for (AbstractDrawable drawable : addedPoints) {
            if (drawable instanceof Point) {
                ((Polygon) polygon).add((Point) drawable);
                ((Polygon) polygon).setColor(Color.RED);
            }
        }
        surface.add(polygon);
        addedPoints.add(polygon);
    }

    private PointValuePair[] getNextSimplexPoints() {
        simplex.iterate(new Bukin6Function(), pointValuePairComparator);
        return simplex.getPoints();
    }
}