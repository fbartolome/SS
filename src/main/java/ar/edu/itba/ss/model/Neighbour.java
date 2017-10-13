package ar.edu.itba.ss.model;

import java.util.Objects;

public class Neighbour {

  private final Particle neighbourParticle;
  private final double distance;

  public Neighbour(final Particle neighbourParticle, final double distance) {
//    if (distance < 0) {
//      throw new IllegalArgumentException("Distance can't be less than 0");
//    }

    this.neighbourParticle = Objects.requireNonNull(neighbourParticle);
    this.distance = distance;
  }

  public Particle getNeighbourParticle() {
    return neighbourParticle;
  }

  public double getDistance() {
    return distance;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof Neighbour)) {
      return false;
    }

    final Neighbour neighbour = (Neighbour) o;

    if (Double.compare(neighbour.distance, distance) != 0) {
      return false;
    }

    return neighbourParticle.equals(neighbour.neighbourParticle);
  }

  @Override
  public int hashCode() {
    int result = neighbourParticle.hashCode();
    final long temp = Double.doubleToLongBits(distance);

    result = 31 * result + (int) (temp ^ (temp >>> 32));

    return result;
  }
}
