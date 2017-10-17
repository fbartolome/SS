package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Point2D;

public class BottomGapBoxParticleWriter extends AppendFileParticlesWriter {

  private static final double OVITO_PARTICLES_RADIUS = 0;
  private static final double OVITO_PARTICLES_MASS = Double.POSITIVE_INFINITY;

  private final List<Particle> boxParticles;

  public BottomGapBoxParticleWriter(final String fileName, final Point2D boxStart,
      final Point2D boxEnd,
      final double boxBottomGap) {
    super(fileName);

    boxParticles = boxParticles(boxStart, boxEnd, boxBottomGap);
  }

  @Override
  public void write(final double time, final Collection<Particle> particles) throws IOException {
    final List<Particle> particlesToWrite = new LinkedList<>(particles);
    particlesToWrite.addAll(boxParticles);
    super.write(time, particlesToWrite);
  }

  private List<Particle> boxParticles(final Point2D boxStart, final Point2D boxEnd,
      final double boxMiddleGap) {

    int id = -1;
    final List<Particle> boxParticles = new LinkedList<>();

    boxParticles.add(ImmutableParticle.builder()
        .position(boxStart)
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxStart.getX(), boxEnd.getY()))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(boxEnd)
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxEnd.getX(), boxStart.getY()))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(
            new Point2D((boxEnd.getX() - boxStart.getX()) / 2 - boxMiddleGap / 2, boxStart.getY()))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(
            new Point2D((boxEnd.getX() - boxStart.getX()) / 2 + boxMiddleGap / 2, boxStart.getY()))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());

    return boxParticles;
  }

  public void writeLines() {
    // TODO: Hacer
  }
}
