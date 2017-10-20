package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javafx.geometry.Point2D;

public class BottomGapBoxParticleWriter extends AppendFileParticlesWriter {

  private static final double OVITO_PARTICLES_RADIUS = 0;
  private static final double OVITO_PARTICLES_MASS = Double.POSITIVE_INFINITY;
  private final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction;

  private final List<Particle> boxParticles;
  private final List<Particle> boxParticles1;

  public BottomGapBoxParticleWriter(final String fileName, final Point2D boxStart,
      final Point2D boxEnd, final double boxBottomGap,
      final BiFunction<Particle, Set<Neighbour>, Point2D> forceFunction) {
    super(fileName);

    boxParticles = boxParticles(boxStart, boxEnd, boxBottomGap);
    boxParticles1 = boxParticles1(boxStart, boxEnd, boxBottomGap);
    this.forceFunction = forceFunction;
  }

  @Override
  public void write(final double time, final Collection<Particle> particles) throws IOException {
    final List<Particle> particlesToWrite = new LinkedList<>(particles);
    particlesToWrite.addAll(boxParticles);
    super.write(time, particlesToWrite);
  }

  @Override
  public void write(double time, Map<Particle, Set<Neighbour>> neighbours) throws IOException {
    Map<Particle, List<Double>> map = new HashMap<>();
    for (Map.Entry<Particle, Set<Neighbour>> entry : neighbours.entrySet()) {
      List<Double> attributes = Collections
          .singletonList(forceFunction.apply(entry.getKey(), entry.getValue())
              .magnitude());
      map.put(entry.getKey(), attributes);
    }
    boxParticles.stream().forEach(bp -> map.put(bp, Collections.singletonList(0.0)));
    writeWithAttributes(time, map);
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
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(0, 0))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxEnd.getX(), 0))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());

    return boxParticles;
  }

  public List<Particle> boxParticles1(final Point2D boxStart, final Point2D boxEnd,
      final double boxMiddleGap) {
    int id = -1;
    final List<Particle> boxParticles = new LinkedList<>();

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
            new Point2D((boxEnd.getX() - boxStart.getX()) / 2 + boxMiddleGap / 2, boxStart.getY()))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(boxStart)
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
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(0, 0))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());
    boxParticles.add(ImmutableParticle.builder()
        .position(new Point2D(boxEnd.getX(), 0))
        .velocity(Point2D.ZERO)
        .id(id--)
        .radius(OVITO_PARTICLES_RADIUS)
        .mass(OVITO_PARTICLES_MASS)
        .build());

    return boxParticles;
  }
}
