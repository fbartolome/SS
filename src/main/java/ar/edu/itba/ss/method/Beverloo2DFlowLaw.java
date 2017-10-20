package ar.edu.itba.ss.method;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Physics;
import java.util.List;

public final class Beverloo2DFlowLaw {

  private static final double C = 1.0;

  public static double apply(final List<Particle> particles, final double boxWidth,
      final double boxHeight, final double boxGap) {
    final double maxWidth = particles.stream()
            .mapToDouble(p -> p.position().getX())
            .max()
            .orElseThrow(IllegalArgumentException::new);
    final double minWidth = particles.stream()
        .mapToDouble(p -> p.position().getX())
        .min()
        .orElseThrow(IllegalArgumentException::new);
    final double maxHeight = particles.stream()
        .mapToDouble(p -> p.position().getY())
        .max()
        .orElseThrow(IllegalArgumentException::new);
    final double minHeight = particles.stream()
        .mapToDouble(p -> p.position().getY())
        .min()
        .orElseThrow(IllegalArgumentException::new);
    final double np = particles.size() / ((maxWidth - minWidth) * (maxHeight - minHeight));
    final double r = particles.stream()
        .mapToDouble(Particle::radius)
        .average()
        .orElseThrow(IllegalArgumentException::new);

    return apply(np, boxGap, r);
  }

  public static double apply(final double np, final double d, final double r) {
    return np * sqrt(Physics.GRAVITY) * pow(d - C * r, 1.5);
  }
}
