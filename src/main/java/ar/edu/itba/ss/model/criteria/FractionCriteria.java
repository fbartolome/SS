package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Points;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.DoublePredicate;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;

public class FractionCriteria implements Criteria {

  private final Point2D start;
  private final Point2D end;
  private final DoublePredicate condition;

  public FractionCriteria(final Point2D start, final Point2D end, final DoublePredicate condition) {
    this.start = Objects.requireNonNull(start);
    this.end = Objects.requireNonNull(end);
    this.condition = Objects.requireNonNull(condition);
  }

  @Override
  public boolean test(final double time, final Collection<Particle> originalParticles) {
    final List<Point2D> positions = originalParticles.stream().filter(p -> p.id() > 0)
        .map(Particle::position).collect(Collectors.toList());
    final double fraction =
        (double) Points.between(positions, start, end) / positions.size();

    return condition.test(fraction);
  }
}
