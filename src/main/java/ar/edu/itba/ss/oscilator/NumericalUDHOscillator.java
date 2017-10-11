package ar.edu.itba.ss.oscilator;

import ar.edu.itba.ss.method.movement.MovementFunction;
import ar.edu.itba.ss.model.Particle;

public class NumericalUDHOscillator extends AnalyticUDHOscillator {

  private final MovementFunction movementFunction;

  public NumericalUDHOscillator(final Particle particle, final double k,
      final double b, final MovementFunction movementFunction) {
    super(particle, k, b);

    this.movementFunction = movementFunction;
  }

  @Override
  public Particle move(final Particle particle, final double dt) {
    return movementFunction.move(particle, null, dt);
  }
}
