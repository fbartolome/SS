package ar.edu.itba.ss.method.movement;

import static java.util.Objects.requireNonNull;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class EulerMovementFunction implements MovementFunction {

  private final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction;

  public EulerMovementFunction(
      final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction) {

    this.forceFunction = requireNonNull(forceFunction);
  }

  public static Particle move(final Particle currentParticle, final Set<Neighbour> neighbours,
      final double dt, final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction) {

    final Point2D force = forceFunction.apply(currentParticle, neighbours);

    final Point2D newVelocity = currentParticle.velocity()
        .add(force
            .multiply(dt / currentParticle.mass()));

    final Point2D newPosition = currentParticle.position()
        .add(newVelocity.multiply(dt))
        .add(force
            .multiply(dt * dt / (2 * currentParticle.mass())));

    return ImmutableParticle.builder().from(currentParticle)
        .position(newPosition)
        .velocity(newVelocity)
        .build();
  }

  @Override
  public Particle move(final Particle currentParticle, final Set<Neighbour> neighbours,
      final double dt) {

    return move(currentParticle, neighbours, dt, forceFunction);
  }
}
