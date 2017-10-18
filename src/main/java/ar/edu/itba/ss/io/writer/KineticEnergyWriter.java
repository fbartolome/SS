package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.Particle;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class KineticEnergyWriter implements ParticlesWriter {

  private List<Point2D> points;

  public KineticEnergyWriter() {
    this.points = new LinkedList<>();
  }

  @Override
  public void write(double time, Collection<Particle> particles) throws IOException {
    final double kinetic = particles.stream()
            .mapToDouble(this::kineticEnergy)
            .sum();
    points.add(new Point2D(time, kinetic));
  }

  public double kineticEnergy(final Particle particle) {
    return 0.5 * particle.mass()
        * particle.velocity().magnitude() * particle.velocity().magnitude();
  }

  public List<Point2D> getPoints() {
    final List<Point2D> oldPoints = points;
    points = new LinkedList<>();

    return oldPoints;
  }
}
