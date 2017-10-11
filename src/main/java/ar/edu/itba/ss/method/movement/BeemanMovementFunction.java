package ar.edu.itba.ss.method.movement;

import static java.util.Objects.requireNonNull;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class BeemanMovementFunction implements MovementFunction {

  private final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction;
  private Point2D previousAcceleration;

  public BeemanMovementFunction(final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction,
      final Point2D previousAcceleration) {

    this.forceFunction = requireNonNull(forceFunction);
    this.previousAcceleration = requireNonNull(previousAcceleration);
  }

  @Override
  public Particle move(final Particle currentParticle, final Set<Neighbour> neighbours,
      final double dt) {

    final Point2D currentAcceleration = forceFunction.apply(currentParticle, neighbours)
        .multiply(1.0 / currentParticle.mass());

    final Point2D predictedPosition = currentParticle.position()
        .add(currentParticle.velocity()
            .multiply(dt))
        .add(currentAcceleration
            .multiply(dt * dt * 2.0 / 3.0))
        .subtract(previousAcceleration
            .multiply(dt * dt / 6.0));

    final Particle predictedParticle = ImmutableParticle.builder()
        .from(currentParticle)
        .position(predictedPosition)
        .build();

    final Point2D predictedAcceleration = forceFunction.apply(predictedParticle, neighbours)
        .multiply(1.0 / predictedParticle.mass());

    final Point2D predictedVelocity = currentParticle.velocity()
        .add(predictedAcceleration
            .multiply(dt / 3.0))
        .add(currentAcceleration
            .multiply(dt * 5.0 / 6.0))
        .subtract(previousAcceleration
            .multiply(dt / 6.0));

    previousAcceleration = currentAcceleration;

    return ImmutableParticle.builder()
        .from(currentParticle)
        .position(predictedPosition)
        .velocity(predictedVelocity)
        .build();
  }
}
