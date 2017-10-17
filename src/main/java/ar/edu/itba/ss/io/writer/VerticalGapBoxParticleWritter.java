package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Point2D;

public class VerticalGapBoxParticleWritter extends AppendFileParticlesWriter {

  private static final double OVITO_PARTICLES_RADIUS = 0;
  private static final double OVITO_PARTICLES_MASS = Double.POSITIVE_INFINITY;

  private final List<Particle> boxParticles;

  public VerticalGapBoxParticleWritter(final String fileName, final double boxWidth, final double boxHeight,
      final double boxMiddleGap) {
    super(fileName);

    boxParticles = boxParticles(boxWidth, boxHeight, boxMiddleGap);
  }

  @Override
  public void write(final double time, final Collection<Particle> particles) throws IOException {
    final List<Particle> particlesToWrite = new LinkedList<>(particles);
    particlesToWrite.addAll(boxParticles);
    super.write(time, particlesToWrite);
  }

  private List<Particle> boxParticles(final double boxWidth, final double boxHeight,
      final double boxMiddleGap) {

    int id = -1;
    final List<Particle> boxParticles = new LinkedList<>();

    boxParticles.add(ImmutableParticle.builder()
        .position(Point2D.ZERO)
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(0, boxHeight))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxWidth, 0))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxWidth, boxHeight))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxWidth / 2, 0))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxWidth / 2, boxHeight / 2 - boxMiddleGap / 2))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxWidth / 2, boxHeight / 2 + boxMiddleGap / 2))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxWidth / 2, boxHeight))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());

    return boxParticles;
  }
}
