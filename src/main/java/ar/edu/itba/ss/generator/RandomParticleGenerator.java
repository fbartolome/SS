package ar.edu.itba.ss.generator;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.geometry.Point2D;

public abstract class RandomParticleGenerator {

  public static List<Particle> generateParticles(final Particle minParticle,
      final Particle maxParticle) {

    // TODO: validate parameters

    final List<Particle> particles = new LinkedList<>();
    int id = minParticle.id();

    while (id <= maxParticle.id()) {

      double x = minParticle.position().getX();
      if (x != maxParticle.position().getX()) {
        x = ThreadLocalRandom.current()
            .nextDouble(minParticle.position().getX(), maxParticle.position().getX());
      }

      double y = minParticle.position().getY();
      if (y != maxParticle.position().getY()) {
        y = ThreadLocalRandom.current()
            .nextDouble(minParticle.position().getY(), maxParticle.position().getY());
      }

      double r = minParticle.radius();
      if (r != maxParticle.radius()) {
        r = ThreadLocalRandom.current().nextDouble(minParticle.radius(), maxParticle.radius());
      }

      double m = minParticle.mass();
      if (m != maxParticle.mass()) {
        r = ThreadLocalRandom.current().nextDouble(minParticle.mass(), maxParticle.mass());
      }

      double desiredMagnitude = minParticle.velocity().magnitude();
      if (desiredMagnitude != maxParticle.velocity().magnitude()) {
        desiredMagnitude = ThreadLocalRandom.current()
            .nextDouble(minParticle.velocity().magnitude(), maxParticle.velocity().magnitude());
      }

      double vx = 0;
      double vy = 0;
      if (desiredMagnitude != 0) {
        vx = ThreadLocalRandom.current().nextDouble(desiredMagnitude);
        vx *= ThreadLocalRandom.current().nextBoolean() ? -1 : 1;
        vy = Math.sqrt(desiredMagnitude * desiredMagnitude - vx * vx);
        vy *= ThreadLocalRandom.current().nextBoolean() ? -1 : 1;
      }

      final Particle particle = ImmutableParticle.builder()
          .id(id)
          .position(new Point2D(x, y))
          .radius(r)
          .mass(m)
          .velocity(new Point2D(vx, vy))
          .build();

      if (validParticle(particle, particles)) {
        id++;
        particles.add(particle);
      }
    }

    return particles;
  }

  private static boolean validParticle(final Particle particle, final List<Particle> particleList) {
    return particleList.parallelStream().noneMatch(particle::collides);
  }
}
