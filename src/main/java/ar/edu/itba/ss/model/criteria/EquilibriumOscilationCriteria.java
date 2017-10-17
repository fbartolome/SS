package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Points;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;

public class EquilibriumOscilationCriteria implements Criteria {

  private final Point2D start;
  private final Point2D end;
  private final int iterationsInEquilibrium;
  private final double error;
  private int iterationsInEquilibriumCounter;

  public EquilibriumOscilationCriteria(Point2D start, Point2D end,
      int iterationsInEquilibrium, double error) {
    this.start = start;
    this.end = end;
    this.iterationsInEquilibrium = iterationsInEquilibrium;
    this.error = error;
    this.iterationsInEquilibriumCounter = 0;
  }

  @Override
  public boolean test(double time, Collection<Particle> particles) {
    final List<Point2D> positions = particles.stream().filter(p -> p.id() > 0)
        .map(Particle::position).collect(Collectors.toList());
    final double fraction =
        (double) Points.between(positions, start, end) / positions.size();

    if (fraction >= 0.5 - error && fraction <= 0.5 + error) {
      iterationsInEquilibriumCounter++;
      if (iterationsInEquilibriumCounter == iterationsInEquilibrium) {
        return true;
      }
    } else {
      iterationsInEquilibriumCounter = 0;
    }
    return false;
  }
}
