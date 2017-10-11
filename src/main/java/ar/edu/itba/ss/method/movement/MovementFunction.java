package ar.edu.itba.ss.method.movement;

import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;

public interface MovementFunction {

  Particle move(final Particle currentParticle, final Set<Neighbour> neighbours, final double dt);
}
