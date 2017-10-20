package ar.edu.itba.ss;

import ar.edu.itba.ss.generator.RandomParticleGenerator;
import ar.edu.itba.ss.io.writer.BottomGapBoxParticleWriter;
import ar.edu.itba.ss.io.writer.KineticEnergyWriter;
import ar.edu.itba.ss.io.writer.MultiWriter;
import ar.edu.itba.ss.io.writer.ParticlesWriter;
import ar.edu.itba.ss.method.force.ContactForceFunction;
import ar.edu.itba.ss.method.movement.GearMovementFunction;
import ar.edu.itba.ss.method.movement.MovementFunction;
import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Physics;
import ar.edu.itba.ss.model.Scatter2DChart;
import ar.edu.itba.ss.model.criteria.KineticEnergyEquilibriumCriteria;
import ar.edu.itba.ss.model.criteria.NullVelocityCriteria;
import ar.edu.itba.ss.model.criteria.TimeCriteria;
import ar.edu.itba.ss.simulator.GranularMediaSimulator;

import java.util.*;

import javafx.application.Platform;
import javafx.geometry.Point2D;

public class GranularMediaMainPart2 {

  private static final double BOX_HEIGHT = 2;
  private static final double BOX_WIDTH = 1;
  private static final double BOX_TOP = BOX_HEIGHT * 1.1;
  private static final double BOX_BOTTOM = BOX_TOP - BOX_HEIGHT;
  private static final double GAP = 0;
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
//    final List<Particle> initialParticles = testParticle();

    final Map<Particle, MovementFunction> functions = new HashMap<>();
    final ContactForceFunction forceFunction = new ContactForceFunction(KN, KT, true);
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
//      functions.put(particle, new BeemanMovementFunction(forceFunction, Point2D.ZERO));
    }

    final GranularMediaSimulator simulator = new GranularMediaSimulator(initialParticles,
        DT, WRITER_ITERATIONS, BOX_WIDTH, BOX_HEIGHT, GAP, functions);

    final TimeCriteria timeCriteria = new TimeCriteria(1 );
    final NullVelocityCriteria nullVelocityCriteria =
        new NullVelocityCriteria(0.01, 0.01);
    final KineticEnergyEquilibriumCriteria kineticCriteria =
        new KineticEnergyEquilibriumCriteria(0.01,10 * WRITER_ITERATIONS);

    final ParticlesWriter writer = new BottomGapBoxParticleWriter("kt_" + KT + "_",
        new Point2D(0, BOX_BOTTOM), new Point2D(BOX_WIDTH, BOX_TOP), GAP, forceFunction);
    final KineticEnergyWriter kineticEnergyWriter = new KineticEnergyWriter(true);
    final List<ParticlesWriter> writers = new LinkedList<>();
    writers.add(writer);
    writers.add(kineticEnergyWriter);
    final MultiWriter multiWriter = new MultiWriter(writers);

    simulator.simulate(kineticCriteria, multiWriter);
    List<Point2D> points = kineticEnergyWriter.getPoints();
    points.stream().forEach(p -> System.out.println("tiempo: " + p.getX() + "\tenergia: " + p.getY()));
    System.out.println("LISTO");
    Platform.runLater(() -> Scatter2DChart.addSeries("", points));

  }

  // TODO: Remove
  private static List<Particle> testParticle() {
    return Collections.singletonList(ImmutableParticle.builder()
        .id(1)
        .radius(MAX_RADIUS)
        .mass(MASS)
        .velocity(new Point2D(0, 0))
        .position(new Point2D(BOX_WIDTH, BOX_HEIGHT))
        .build());
  }
}
