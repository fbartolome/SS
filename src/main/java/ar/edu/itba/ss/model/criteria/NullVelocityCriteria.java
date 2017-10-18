package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import java.util.Collection;

public class NullVelocityCriteria implements Criteria {

  private final double error;
  private final double minTime;

  public NullVelocityCriteria(final double error, double minTime) {
    this.error = error;
    this.minTime = minTime;
  }

  @Override
  public boolean test(final double time, final Collection<Particle> particles) {
    if(time < minTime){
      return false;
    }

    return particles.stream().allMatch(p -> p.velocity().magnitude() < error);
  }
}
