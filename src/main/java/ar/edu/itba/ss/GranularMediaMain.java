package ar.edu.itba.ss;

import ar.edu.itba.ss.generator.RandomParticleGenerator;
import ar.edu.itba.ss.io.writer.KineticEnergyWriter;
import ar.edu.itba.ss.method.Beverloo2DFlowLaw;
import ar.edu.itba.ss.method.force.ContactForceFunction;
import ar.edu.itba.ss.method.movement.GearMovementFunction;
import ar.edu.itba.ss.method.movement.MovementFunction;
import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Physics;
import ar.edu.itba.ss.model.Scatter2DChart;
import ar.edu.itba.ss.model.criteria.FlowCriteria;
import ar.edu.itba.ss.simulator.GranularMediaSimulator;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Point2D;

@SuppressWarnings("Duplicates")
public class GranularMediaMain {

  private static final double BOX_HEIGHT = 2;
  private static final double BOX_WIDTH = 1;
  private static final double BOX_TOP = BOX_HEIGHT * 1.1;
  private static final double BOX_BOTTOM = BOX_TOP - BOX_HEIGHT;
  private static final double[] GAPS = new double[]{0.15, 0.25, 0.5, 0.75};
  private static final double MAX_RADIUS = 0.015;
  private static final double MIN_RADIUS = 0.01;
  private static final double MASS = 0.01;
  private static final int N = 1000;
  private static final double KN = 100000;
  private static final double KT = 2 * KN;
  private static final double DT = 0.000005;
  //    private static final double DT = 0.1 * Math.sqrt(MASS / KN);
  private static final int WRITER_ITERATIONS = (int) (1 / DT) / 100;

  public static void main(final String[] args) {
    Scatter2DChart.initialize("", "", 0, 0, 0, "", 0, 0, 0);

    final Particle minParticle = ImmutableParticle.builder()
        .id(1)
        .radius(MIN_RADIUS)
        .mass(MASS)
        .velocity(Point2D.ZERO)
        .position(new Point2D(MAX_RADIUS, BOX_BOTTOM + MAX_RADIUS))
        .build();
    final Particle maxParticle = ImmutableParticle.builder()
        .id(N)
        .radius(MAX_RADIUS)
        .mass(MASS)
        .velocity(Point2D.ZERO)
        .position(new Point2D(BOX_WIDTH - MAX_RADIUS, BOX_TOP - MAX_RADIUS))
        .build();
    final List<Particle> initialParticles =
        RandomParticleGenerator.generateParticles(minParticle, maxParticle);

    final double gap = GAPS[1];

    System.out.println("START KINETIC");
    final ContactForceFunction forceFunction = new ContactForceFunction(KN, KT, true);

    final Map<Particle, MovementFunction> functions = new HashMap<>();
    for (final Particle particle : initialParticles) {
      final Point2D[] r = new Point2D[6];

      r[0] = particle.position();
      r[1] = particle.velocity();
      r[2] = new Point2D(0, -Physics.GRAVITY);
      for (int i = 3; i < r.length; i++) {
        r[i] = Point2D.ZERO;
      }

      functions.put(particle,
          new GearMovementFunction(forceFunction, GearMovementFunction.GEAR_5_ALPHAS, r));
    }

    final GranularMediaSimulator simulator = new GranularMediaSimulator(initialParticles,
        DT, WRITER_ITERATIONS, BOX_WIDTH, BOX_HEIGHT, gap, functions);

//    final TimeCriteria timeCriteria = new TimeCriteria(1);
    final FlowCriteria flowCriteria = new FlowCriteria(100, 10, 0.01);

//    final ParticlesWriter ovitoWriter = new BottomGapBoxParticleWriter("simulation_gm",
//        new Point2D(0, BOX_BOTTOM), new Point2D(BOX_WIDTH, BOX_TOP), gap, forceFunction);
    final KineticEnergyWriter kineticEnergyWriter = new KineticEnergyWriter(true);
//    final MultiWriter multiWriter =
//        new MultiWriter(Arrays.asList(ovitoWriter, kineticEnergyWriter));

    final List<Particle> lastParticles = simulator.simulate(flowCriteria, kineticEnergyWriter);

    List<Point2D> points = kineticEnergyWriter.getPoints();
    Platform.runLater(() -> Scatter2DChart.addSeries("", points));
    System.out.println("END KINETIC");

    System.out.println("Press \"ENTER\" to continue...");
    try {
      System.in.read(new byte[2]);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("OK");
    System.out.println("START N/T");

    final List<Double> flowTimes = simulator.getFlowTimes();
    final List<Point2D> nts = new LinkedList<>();
    for (int i = 0; i < flowTimes.size(); i++) {
      nts.add(new Point2D(i + 1, flowTimes.get(i)));
    }

    Platform.runLater(Scatter2DChart::removeFirstSeries);
    Platform.runLater(() -> Scatter2DChart.addSeries("", nts));
    System.out.println("END N/T");

    System.out.println("Press \"ENTER\" to continue...");
    try {
      System.in.read(new byte[2]);
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("OK");
    System.out.println("START FLOW");

    final int dN = 100;
    final List<Point2D> flows = new LinkedList<>();
    System.out.println("#FLOWSTIMES " + flowTimes.size());
    for (int i = 0; i < (flowTimes.size() - dN); i++) {
      final double currentFlow = dN / (flowTimes.get(i + dN - 1) - flowTimes.get(i));
      System.out.println("CURRENTFLOW " + currentFlow + " in time " + flowTimes.get(i));
      flows.add(new Point2D(flowTimes.get(i), currentFlow));
    }

    Platform.runLater(Scatter2DChart::removeFirstSeries);
    Platform.runLater(() -> Scatter2DChart.addSeries("", flows));
    System.out.println("END FLOW");

    System.out.println("Q = " + flows.get(flows.size() - 1));
    System.out
        .println("Mean Q = " + flows.stream().mapToDouble(Point2D::getY).average().getAsDouble());
    System.out.println(
        "Ley Qi = " + Beverloo2DFlowLaw.apply(initialParticles, BOX_WIDTH, BOX_HEIGHT, gap));
    System.out.println(
        "Ley Qf = " + Beverloo2DFlowLaw.apply(lastParticles, BOX_WIDTH, BOX_HEIGHT, gap));
  }
}
