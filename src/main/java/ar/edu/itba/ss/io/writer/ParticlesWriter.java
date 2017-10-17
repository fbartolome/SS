package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.Collision;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ParticlesWriter {

  void write(final double time, final Collection<Particle> particles) throws IOException;

  default void write(final double time, final Collection<Particle> particles, final Collision collision)
      throws IOException {
    write(time, particles);
  }

  default void write(final double time, final Map<Particle, Set<Neighbour>> neighbours) throws IOException {
    write(time, neighbours.keySet());
  }
}
