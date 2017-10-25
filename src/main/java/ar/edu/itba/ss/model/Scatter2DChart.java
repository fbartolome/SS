package ar.edu.itba.ss.model;

import java.util.List;
import java.util.Objects;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Scatter2DChart extends Application {

  private static String name;
  private static String xLabel;
  private static double xMin;
  private static double xMax;
  private static double xStep;
  private static String yLabel;
  private static double yMin;
  private static double yMax;
  private static double yStep;

  private static ScatterChart<Number, Number> chart;

  public static void initialize(final String name, String xLabel, double xMin, double xMax,
      double xStep, String yLabel, double yMin, double yMax, double yStep) {

    Scatter2DChart.name = name;
    Scatter2DChart.xLabel = xLabel;
    Scatter2DChart.xMin = xMin;
    Scatter2DChart.xMax = xMax;
    Scatter2DChart.xStep = xStep;
    Scatter2DChart.yLabel = yLabel;
    Scatter2DChart.yMin = yMin;
    Scatter2DChart.yMax = yMax;
    Scatter2DChart.yStep = yStep;

    new Thread(() -> Application.launch(Scatter2DChart.class, null)).start();
  }

  public static void addSeries(final String name, final List<Point2D> points) {
    final Series<Number, Number> series = new Series<>();

    series.setName(Objects.requireNonNull(name));
    for (final Point2D point : points) {
      series.getData().add(new Data<>(point.getX(), point.getY()));
    }
    Scatter2DChart.chart.getData().add(series);
  }

  public static void removeFirstSeries() {
    Scatter2DChart.chart.getData().remove(0);
  }

  @Override
  public void start(final Stage primaryStage) throws Exception {
    final NumberAxis xAxis = new NumberAxis();
    final NumberAxis yAxis = new NumberAxis();
    xAxis.tickLabelFontProperty().set(Font.font(14));
    yAxis.tickLabelFontProperty().set(Font.font(14));

    Scatter2DChart.chart = new ScatterChart<>(xAxis, yAxis);
    Scatter2DChart.chart.setTitle(Objects.requireNonNull(name));
    Scatter2DChart.chart.setPrefSize(500, 400); // TODO
    primaryStage.setTitle(chart.getTitle());
    primaryStage.setScene(new Scene(chart));
    primaryStage.show();
  }
}
