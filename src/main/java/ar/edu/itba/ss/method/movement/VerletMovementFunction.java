package ar.edu.itba.ss.method.movement;

import static java.util.Objects.requireNonNull;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class VerletMovementFunction implements MovementFunction {

  private final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction;
  private Point2D previousPosition;

  public VerletMovementFunction(final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction,
      final Point2D previousPosition) {

    this.forceFunction = requireNonNull(forceFunction);
    this.previousPosition = requireNonNull(previousPosition);
  }

  @Override
  public Particle move(final Particle currentParticle, final Set<Neighbour> neighbours,
      final double dt) {

    final Point2D currentForce = forceFunction.apply(currentParticle, neighbours);

    final Point2D predictedPosition = currentParticle.position()
        .multiply(2)
        .subtract(previousPosition)
        .add(currentForce.multiply(dt * dt / currentParticle.mass()));

    final Point2D predictedVelocity = predictedPosition
        .subtract(previousPosition)
        .multiply(1.0 / (2.0 * dt));

    previousPosition = currentParticle.position();

    return ImmutableParticle.builder().from(currentParticle)
        .position(predictedPosition)
        .velocity(predictedVelocity)
        .build();
  }

  @Override
  public void clearState(Particle particle) {
    previousPosition = particle.position();
  }
}
