package ar.edu.itba.ss.oscilator;

import static java.lang.Math.cos;
import static java.lang.Math.exp;
import static java.lang.Math.sqrt;
import static java.util.Objects.requireNonNull;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class AnalyticUDHOscillator {

  private final double b;
  private final double k;

  private Particle particle;
  private double time;

  public AnalyticUDHOscillator(final Particle particle, final double k, final double b) {

    if (requireNonNull(particle).velocity().getY() != 0) {
      throw new IllegalArgumentException("Movement has to be un 1D");
    }

    this.particle = particle;
    this.k = k;
    this.b = b;
    this.time = 0;
  }

  public Particle move(final Particle particle, final double dt) {
    time += dt;

    final double gamma = b / (2 * particle.mass());
    final double wa = sqrt(k / particle.mass() - gamma * gamma);

    final Point2D newPosition = new Point2D(exp(-gamma * time) * cos(wa * time),
        particle.position().getY());

    return ImmutableParticle.builder().from(particle)
        .position(newPosition)
//        .velocity()
        .build();
  }

  public static class UnderdampedOscillatorForceFunction implements
      BiFunction<Particle, Set<Neighbour>, Point2D> {

    private final double k;
    private final double b;

    public UnderdampedOscillatorForceFunction(final double k, final double b) {
      this.k = k;
      this.b = b;
    }

    public static Point2D apply(final Particle particle, final double k, final double b) {
      return particle.position()
          .multiply(-k)
          .subtract(particle.velocity()
              .multiply(b));
    }

    @Override
    public Point2D apply(final Particle particle, final Set<Neighbour> neighbours) {
      return apply(particle, k, b);
    }
  }
}
