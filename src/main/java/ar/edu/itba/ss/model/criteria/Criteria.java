package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import java.util.Collection;

public interface Criteria {

  boolean test(final double time, final Collection<Particle> particles);
}
