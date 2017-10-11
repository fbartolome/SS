package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.io.writer.ParticlesWriter;
import ar.edu.itba.ss.method.movement.MovementFunction;
import ar.edu.itba.ss.method.neigbour.CellIndexMethod;
import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.criteria.Criteria;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;

public class LennardJonesGasSimulator implements Simulator {

  private final List<Particle> initialParticles;
  private final double dt;
  private final int writerIteration;
  private final double boxWidth;
  private final double boxHeight;
  private final double middleGap;
  private final CellIndexMethod cim;
  private final double rc;
  private final Map<Particle, MovementFunction> movementFunctions;

  public LennardJonesGasSimulator(final List<Particle> initialParticles, final double boxWidth,
      final double boxHeight, final double middleGap, final double dt, final int writerIteration,
      double rc,
      final Map<Particle, MovementFunction> movementFunctions) {
    this.initialParticles = initialParticles;
    this.dt = dt;
    this.writerIteration = writerIteration;
    this.boxWidth = boxWidth;
    this.boxHeight = boxHeight;
    this.middleGap = middleGap;
    this.rc = rc;
    this.cim = new CellIndexMethod(boxHeight > boxWidth ? boxHeight : boxWidth, false);
    this.movementFunctions = movementFunctions;
  }

  @Override
  public Set<Particle> simulate(Criteria endCriteria, ParticlesWriter writer) {
    double time = 0;
    int iteration = 1;
    List<Particle> particles = initialParticles;

    while (!endCriteria.test(time, particles)) {
      Map<Particle, Set<Neighbour>> neighbours = cim
          .apply(particles, particles.get(0).radius(), rc);
      particles = nextParticles(neighbours);

      if (iteration == writerIteration) {
        iteration = 0;
        try {
          writer.write(time, neighbours);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      time += dt;
      iteration++;
    }

    return new HashSet<>(particles);
  }

  private List<Particle> nextParticles(Map<Particle, Set<Neighbour>> neighbours) {
    List<Particle> nextParticles = new ArrayList<>(neighbours.size());

    for (Map.Entry<Particle, Set<Neighbour>> entry : neighbours.entrySet()) {
      nextParticles.add(moveParticle(entry.getKey(), entry.getValue()));
    }

    return nextParticles;
  }

  private Particle moveParticle(Particle particle, Set<Neighbour> neighbours) {
    neighbours = neighbours.stream()
        .filter(n -> !isWallBetween(particle, n.getNeighbourParticle()))
        .collect(Collectors.toSet());
    addWallParticles(particle, neighbours);

    MovementFunction function = movementFunctions.get(particle);
    return function.move(particle, neighbours, dt);
  }

  private void addWallParticles(Particle particle, Set<Neighbour> neighbours) {
    int wallId = -1;
    final double gapStart = (boxHeight / 2) - (middleGap / 2);
    final double gapEnd = boxHeight - gapStart;
    // up wall
    double distanceToExtremeWall = boxHeight - particle.position().getY();
    double distanceToMiddleWall = gapEnd - particle.position().getY();
    if (particle.position().getX() == boxWidth / 2 && distanceToMiddleWall <= rc
        && distanceToMiddleWall >= 0) {
    } else if (distanceToExtremeWall <= rc) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--).position(new Point2D(particle.position().getX(), boxHeight))
          .mass(Double.POSITIVE_INFINITY)
          .velocity(Point2D.ZERO).build(), distanceToExtremeWall));
    }

    // down wall
    distanceToExtremeWall = particle.position().getY();
    distanceToMiddleWall = particle.position().getY() - gapStart;
    if (particle.position().getX() == boxWidth / 2 && distanceToMiddleWall <= rc
        && distanceToMiddleWall >= 0) {
    } else if (distanceToExtremeWall <= rc) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--).position(new Point2D(particle.position().getX(), 0))
          .mass(Double.POSITIVE_INFINITY)
          .velocity(Point2D.ZERO).build(), distanceToExtremeWall));
    }

    // left wall
    distanceToExtremeWall = particle.position().getX();
    distanceToMiddleWall = distanceToExtremeWall - boxWidth / 2;
    if (distanceToMiddleWall > 0 && distanceToMiddleWall <= rc &&
        (particle.position().getY() <= gapStart || particle.position().getY() >= gapEnd)
        ) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--).position(new Point2D(boxWidth / 2, particle.position().getY()))
          .mass(Double.POSITIVE_INFINITY)
          .velocity(Point2D.ZERO).build(), distanceToMiddleWall));
    } else if (distanceToExtremeWall <= rc) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--).position(new Point2D(0, particle.position().getY()))
          .mass(Double.POSITIVE_INFINITY)
          .velocity(Point2D.ZERO).build(), distanceToExtremeWall));
    }

    // right wall
    distanceToExtremeWall = boxWidth - particle.position().getX();
    distanceToMiddleWall = distanceToExtremeWall - boxWidth / 2;
    if (distanceToMiddleWall > 0 && distanceToMiddleWall <= rc &&
        (particle.position().getY() <= gapStart || particle.position().getY() >= gapEnd)
        ) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--).position(new Point2D(boxWidth / 2, particle.position().getY()))
          .mass(Double.POSITIVE_INFINITY)
          .velocity(Point2D.ZERO).build(), distanceToMiddleWall));
    } else if (distanceToExtremeWall <= rc) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--).position(new Point2D(boxWidth, particle.position().getY()))
          .mass(Double.POSITIVE_INFINITY)
          .velocity(Point2D.ZERO).build(), distanceToExtremeWall));
    }

    if (particle.position().getY() > gapStart && particle.position().getY() < gapEnd) {
      final Point2D gapStartPosition = new Point2D(boxWidth / 2, gapStart);
      final Point2D gapEndPosition = new Point2D(boxWidth / 2, gapEnd);
      final double gapStartDistance = particle.position().distance(gapStartPosition);
      final double gapEndDistance = particle.position().distance(gapEndPosition);

      if (gapStartDistance <= rc) {
        neighbours.add(new Neighbour(ImmutableParticle.builder()
            .id(wallId--)
            .position(gapStartPosition)
            .mass(Double.POSITIVE_INFINITY)
            .build(), gapStartDistance));
      }

      if (gapEndDistance <= rc) {
        neighbours.add(new Neighbour(ImmutableParticle.builder()
            .id(wallId--)
            .position(gapEndPosition)
            .mass(Double.POSITIVE_INFINITY)
            .build(), gapEndDistance));
      }
    }
  }

  private boolean isWallBetween(Particle particle1, Particle particle2) {
    final double x1 = particle1.position().getX();
    final double x2 = particle2.position().getX();
    final double y1 = particle1.position().getY();
    final double y2 = particle2.position().getY();

    if (x1 == x2) {
      return false;
    }

    final double m = (y2 - y1) / (x2 - x1);
    final double b = y1 - m * x1;
    final double xp = boxWidth / 2;
    final double yp = m * xp + b;

    final double gapStart = (boxHeight / 2) - (middleGap / 2);
    final double gapEnd = boxHeight - gapStart;

    return yp > gapStart && yp < gapEnd &&
        ((x1 > boxWidth / 2 && x2 < boxWidth / 2) ||
            (x1 < boxWidth / 2 && x2 > boxWidth / 2));
  }
}
