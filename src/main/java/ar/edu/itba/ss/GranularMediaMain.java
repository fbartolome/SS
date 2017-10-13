package ar.edu.itba.ss;

import ar.edu.itba.ss.generator.RandomParticleGenerator;
import ar.edu.itba.ss.io.writer.BottomGapBoxParticleWriter;
import ar.edu.itba.ss.io.writer.ParticlesWriter;
import ar.edu.itba.ss.method.force.ContactForceFunction;
import ar.edu.itba.ss.method.movement.BeemanMovementFunction;
import ar.edu.itba.ss.method.movement.MovementFunction;
import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.criteria.TimeCriteria;
import ar.edu.itba.ss.simulator.GranularMediaSimulator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;

public class GranularMediaMain {

  private static final double BOX_HEIGHT = 10;
  private static final double BOX_WIDTH = 1;
  private static final double BOX_TOP = BOX_HEIGHT * 1.1;
  private static final double BOX_BOTTOM = BOX_TOP - BOX_HEIGHT;
  private static final double GAP = 0;
  private static final double MAX_RADIUS = 0.015;
  private static final double MIN_RADIUS = 0.01;
  private static final double MASS = 0.01;
  private static final int N = 1;
  private static final double KN = 100000;
  private static final double KT = 2 * KN;
  private static final double DT = 0.00001;
  //  private static final double DT = 0.1 * Math.sqrt(MASS / KN);
  private static final int WRITER_ITERATIONS = (int) (1 / DT) / 10;

  public static void main(String[] args) {
    Particle minParticle = ImmutableParticle.builder()
        .id(1)
        .radius(MIN_RADIUS)
        .mass(MASS)
        .velocity(Point2D.ZERO)
        .position(new Point2D(0, BOX_BOTTOM))
        .build();
    Particle maxParticle = ImmutableParticle.builder()
        .id(N)
        .radius(MAX_RADIUS)
        .mass(MASS)
        .velocity(Point2D.ZERO)
        .position(new Point2D(BOX_WIDTH, BOX_TOP))
        .build();
    List<Particle> initialParticles = RandomParticleGenerator.generateParticles(minParticle,
        maxParticle);

    Map<Particle, MovementFunction> functions = new HashMap<>();
    ContactForceFunction forceFunction = new ContactForceFunction(KN, KT, true);
    for (Particle particle : initialParticles) {
      functions.put(particle, new BeemanMovementFunction(forceFunction, Point2D.ZERO));
    }

    GranularMediaSimulator simulator = new GranularMediaSimulator(initialParticles,
        DT, WRITER_ITERATIONS, BOX_WIDTH, BOX_HEIGHT, GAP, functions);

    TimeCriteria timeCriteria = new TimeCriteria(10);
    ParticlesWriter writer = new BottomGapBoxParticleWriter("simulation_gm",
        new Point2D(0, BOX_BOTTOM), new Point2D(BOX_WIDTH, BOX_TOP), GAP);
    simulator.simulate(timeCriteria, writer);
  }
}
