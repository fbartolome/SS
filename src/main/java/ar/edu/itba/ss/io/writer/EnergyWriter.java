package ar.edu.itba.ss.io.writer;

import static java.lang.Math.pow;

import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.geometry.Point2D;

public class EnergyWriter implements ParticlesWriter {

  private List<Point2D> points;
  private final double epsilon;
  private final double rm;

  public EnergyWriter(double epsilon, double rm) {
    this.epsilon = epsilon;
    this.rm = rm;
    this.points = new LinkedList<>();
  }

  @Override
  public void write(double time, Collection<Particle> particles) throws IOException {
    // Ignore
  }

  @Override
  public void write(double time, Map<Particle, Set<Neighbour>> neighbours) throws IOException {
    final double kinetic = neighbours.keySet().stream()
        .mapToDouble(this::kineticEnergy)
        .sum();
    final double potential = neighbours.entrySet().stream()
        .mapToDouble(e -> potentialEnergy(e.getKey(), e.getValue()))
        .sum();

    points.add(new Point2D(time, kinetic + potential));
  }

  private double kineticEnergy(final Particle particle) {
    return 0.5 * particle.mass()
        * particle.velocity().magnitude() * particle.velocity().magnitude();
  }

  private double potentialEnergy(final Particle particle, final Set<Neighbour> neighbours) {
    double potential = 0;

    for (Neighbour neighbour : neighbours) {
      potential += epsilon *
          (pow(rm / neighbour.getDistance(), 12) - 2.0 * pow(rm / neighbour.getDistance(), 6));
      if (neighbour.getNeighbourParticle().id() < 0) {
        potential += epsilon *
            (pow(rm / neighbour.getDistance(), 12) - 2.0 * pow(rm / neighbour.getDistance(), 6));
      }
    }

    return potential;
  }

  public List<Point2D> getPoints() {
    final List<Point2D> oldPoints = points;
    points = new LinkedList<>();
    return oldPoints;
  }
}
