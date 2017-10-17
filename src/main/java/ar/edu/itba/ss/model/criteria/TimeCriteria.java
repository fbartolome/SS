package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import java.util.Collection;

public class TimeCriteria implements Criteria {

  private final double limit;

  public TimeCriteria(double limit) {
    this.limit = limit;
  }

  @Override
  public boolean test(double time, Collection<Particle> particles) {
    return time > limit;
  }
}
