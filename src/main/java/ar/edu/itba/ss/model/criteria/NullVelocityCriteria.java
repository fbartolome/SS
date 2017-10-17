package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import java.util.Collection;

public class NullVelocityCriteria implements Criteria {

  private final double error;

  public NullVelocityCriteria(final double error) {
    this.error = error;
  }

  @Override
  public boolean test(final double time, final Collection<Particle> particles) {
    return particles.stream().allMatch(p -> p.velocity().magnitude() < error);
  }
}
