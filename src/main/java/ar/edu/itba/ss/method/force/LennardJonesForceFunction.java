package ar.edu.itba.ss.method.force;

import static java.lang.Math.pow;

import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class LennardJonesForceFunction implements BiFunction<Particle, Set<Neighbour>, Point2D> {

  private final double epsilon;
  private final double rm;

  public LennardJonesForceFunction(final double epsilon, final double rm) {
    this.epsilon = epsilon;
    this.rm = rm;
  }

  private static double forceMagnitude(final double epsilon, final double rm, final double r) {
    return 12 * epsilon * (pow(rm / r, 13) - pow(rm / r, 7)) / rm;
  }

  @Override
  public Point2D apply(final Particle particle, final Set<Neighbour> neighbours) {

    double totalForceX = 0;
    double totalForceY = 0;

    for (final Neighbour neighbour : neighbours) {
      final double magnitude = forceMagnitude(epsilon, rm, neighbour.getDistance());
      final Point2D distanceVector = particle.position()
          .subtract(neighbour.getNeighbourParticle().position());
      totalForceX += magnitude * (distanceVector.getX()) / distanceVector.magnitude();
      totalForceY += magnitude * (distanceVector.getY()) / distanceVector.magnitude();
    }

    return new Point2D(totalForceX, totalForceY);
  }
}
