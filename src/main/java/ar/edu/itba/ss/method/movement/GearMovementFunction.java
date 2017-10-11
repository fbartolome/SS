package ar.edu.itba.ss.method.movement;

import static java.lang.Math.pow;
import static java.util.Objects.requireNonNull;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class GearMovementFunction implements MovementFunction {

  public static final double[] GEAR_5_VD_ALPHAS = new double[]{
      3.0 / 16.0,
      251.0 / 360.0,
      1.0,
      11.0 / 18.0,
      1.0 / 6.0,
      1.0 / 60.0,
  };
  public static final double[] GEAR_5_ALPHAS = new double[]{
      3.0 / 20.0,
      251.0 / 360.0,
      1.0,
      11.0 / 18.0,
      1.0 / 6.0,
      1.0 / 60.0,
  };
  private static final double[] factorials = new double[]{
      1,
      1,
      2,
      6,
      24,
      120
  };

  private final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction;
  private final int order;
  private final double[] alphas;
  private final Point2D[] r;
  private final Point2D[] rp;

  public GearMovementFunction(final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction,
      final double[] alphas, final Point2D[] r) {

    if (alphas.length != r.length) {
      throw new IllegalArgumentException("Dimensions don't match");
    }

    if (alphas.length > factorials.length) {
      throw new IllegalArgumentException("Order not supported");
    }

    this.forceFunction = requireNonNull(forceFunction);
    this.order = alphas.length - 1;
    this.alphas = alphas;
    this.r = r;
    this.rp = new Point2D[order + 1];
  }

  @Override
  public Particle move(final Particle currentParticle, final Set<Neighbour> neighbours,
      final double dt) {

    for (int i = order; i >= 0; i--) {
      rp[i] = r[i];

      for (int j = i + 1, l = 1; j < order + 1; j++, l++) {
        rp[i] = rp[i].add(r[j].multiply(pow(dt, l) / factorials[l]));
      }
    }

    final Particle predictedParticle = ImmutableParticle.builder()
        .from(currentParticle)
        .position(rp[0])
        .velocity(rp[1])
        .build();
    final Point2D deltaR2 = forceFunction.apply(predictedParticle, neighbours)
        .multiply(1.0 / currentParticle.mass())
        .subtract(rp[2])
        .multiply(dt * dt / 2);

    for (int i = 0; i < order + 1; i++) {
      r[i] = rp[i].add(deltaR2.multiply(alphas[i] * factorials[i] / pow(dt, i)));
    }

    return ImmutableParticle.builder()
        .from(currentParticle)
        .position(r[0])
        .velocity(r[1])
        .build();
  }
}
