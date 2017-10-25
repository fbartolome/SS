package ar.edu.itba.ss;

import ar.edu.itba.ss.model.Scatter2DChart;
import java.util.LinkedList;
import java.util.List;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class BeverlooErrorMain {

  public static void main(String[] args) {
    Scatter2DChart.initialize("", "",
        0, 0, 0, "", 0, 0, 0);

    double b = 750 * Math.sqrt(9.8);
    double r = (0.015 + 0.01) / 2;
    double d015 = 0.15;
    double q015 = 122.094;
    double maxC = 5;
    List<Point2D> points015 = new LinkedList<>();
    for (double i = 0; i < maxC; i += 0.01) {
      double error = Math.abs(q015 - beverloo(i, b, d015, r));
      points015.add(new Point2D(i, error));
    }

    double d020 = 0.20;
    double q020 = 181.9;
    List<Point2D> points020 = new LinkedList<>();
    for (double i = 0; i < maxC; i += 0.01) {
      double error = Math.abs(q020 - beverloo(i, b, d020, r));
      points020.add(new Point2D(i, error));
    }

    double d0225 = 0.225;
    double q0225 = 239.830;
    List<Point2D> points0225 = new LinkedList<>();
    for (double i = 0; i < maxC; i += 0.01) {
      double error = Math.abs(q0225 - beverloo(i, b, d0225, r));
      points0225.add(new Point2D(i, error));
    }

    double d025 = 0.25;
    double q025 = 342.635;
    List<Point2D> points025 = new LinkedList<>();
    for (double i = 0; i < maxC; i += 0.01) {
      double error = Math.abs(q025 - beverloo(i, b, d025, r));
      points025.add(new Point2D(i, error));
    }

    List<Point2D> sumPoints = new LinkedList<>();
    for (int i = 0; i < 100 * maxC; i += 0.01) {
      double sumError = points015.get(i).getY() + points0225.get(i).getY()
          + points020.get(i).getY() + points025.get(i).getY();
      sumPoints.add(new Point2D(i, sumError));
    }

    Platform.runLater(() -> Scatter2DChart.addSeries("d = 0.15m", points015));
    Platform.runLater(() -> Scatter2DChart.addSeries("d = 0.20m", points020));
    Platform.runLater(() -> Scatter2DChart.addSeries("d = 0.225m", points0225));
    Platform.runLater(() -> Scatter2DChart.addSeries("d = 0.25m", points025));
    Platform.runLater(() -> Scatter2DChart.addSeries("Error acumulado", sumPoints));

//    List<Point2D> pointsBeverloo = new LinkedList<>();
//    for (double i = 0.01; i < 0.5; i += 0.001) {
//      pointsBeverloo.add(new Point2D(i, beverloo(2.6, b, i, r)));
//    }
//    List<Point2D> pointsQ = new LinkedList<>();
//    pointsQ.add(new Point2D(0.15, 122.094));
//    pointsQ.add(new Point2D(0.20, 181.9));
//    pointsQ.add(new Point2D(0.225, 239.83));
//    pointsQ.add(new Point2D(0.25, 342.635));
//    Platform.runLater(() -> Scatter2DChart.addSeries("Beverloo", pointsBeverloo));
//    Platform.runLater(() -> Scatter2DChart.addSeries("Empírico", pointsQ));
//
//    System.out.println(sumPoints.stream().min(Comparator.comparingDouble(Point2D::getY)).get().getX());
  }


  private static double beverloo(double c, double b, double d, double r) {
    return b * Math.pow(d - c * r, 1.5);
  }
}
