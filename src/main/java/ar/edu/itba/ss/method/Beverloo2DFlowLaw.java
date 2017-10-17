package ar.edu.itba.ss.method;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Physics;
import java.util.List;

public final class Beverloo2DFlowLaw {

  public static double apply(final List<Particle> particles, final double boxWidth,
      final double boxHeight, final double boxGap) {
    final double np = particles.size() / (boxWidth * boxHeight);
    final double cr = particles.stream()
        .mapToDouble(Particle::radius)
        .average()
        .orElseThrow(IllegalArgumentException::new);

    return apply(np, boxGap, cr);
  }

  public static double apply(final double np, final double d, final double cr) {
    return np * sqrt(Physics.GRAVITY) * pow(d - cr, 1.5);
  }
}
