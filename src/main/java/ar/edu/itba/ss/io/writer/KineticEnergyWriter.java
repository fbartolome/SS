package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.Particle;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class KineticEnergyWriter implements ParticlesWriter {

  private final boolean isSemiLogarithmic;
  private List<Point2D> points;

  public KineticEnergyWriter(boolean isSemiLogarithmic) {
    this.isSemiLogarithmic = isSemiLogarithmic;
    this.points = new LinkedList<>();
  }

  @Override
  public void write(double time, Collection<Particle> particles) throws IOException {
    double kinetic = particles.stream()
            .mapToDouble(this::kineticEnergy)
            .sum();
    if(isSemiLogarithmic){
      kinetic = Math.log10(kinetic);
    }
    points.add(new Point2D(time, kinetic));
  }

  private double kineticEnergy(final Particle particle) {
    return 0.5 * particle.mass()
        * particle.velocity().magnitude() * particle.velocity().magnitude();
  }

  public List<Point2D> getPoints() {
    final List<Point2D> oldPoints = points;
    points = new LinkedList<>();

    return oldPoints;
  }
}
