package ar.edu.itba.ss.model;

import javafx.geometry.Point2D;
import org.immutables.value.Value;

@Value.Immutable
public interface Particle {

  int id();

  @Value.Default
  default double radius() {
    return 0;
  }

  @Value.Auxiliary
  Point2D position();

  @Value.Auxiliary
  @Value.Default
  default double mass() {
    return 1;
  }

  @Value.Auxiliary
  @Value.Default
  default Point2D velocity() {
    return Point2D.ZERO;
  }

  default boolean collides(final Particle other) {
    return this.position().distance(other.position()) - this.radius() - other.radius() < 0;
  }
}
