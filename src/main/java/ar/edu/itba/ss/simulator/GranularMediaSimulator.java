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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;

public class GranularMediaSimulator implements Simulator {

  private final List<Particle> initialParticles;
  private final int amountOfParticles;
  private final double dt;
  private final int writerIteration;
  private final double boxWidth;
  private final double boxHeight;
  private final double boxTop;
  private final double boxBottom;
  private final double gap;
  private final CellIndexMethod cim;
  private final double maxRadius;
  private Map<Particle, MovementFunction> movementFunctions;
  private final List<Double> flowTimes;
  private int count = 0;

  public GranularMediaSimulator(List<Particle> initialParticles, double dt, int writerIteration,
      double boxWidth, double boxHeight, double gap,
      Map<Particle, MovementFunction> movementFunctions) {
    this.initialParticles = initialParticles;
    this.amountOfParticles = initialParticles.size();
    this.dt = dt;
    this.writerIteration = writerIteration;
    this.boxWidth = boxWidth;
    this.boxHeight = boxHeight;
    this.boxTop = boxHeight * 1.1;
    this.boxBottom = boxTop - boxHeight;
    this.gap = gap;
    this.cim = new CellIndexMethod(boxTop > boxWidth ? boxTop : boxWidth, false);
    this.movementFunctions = movementFunctions;
    this.maxRadius = initialParticles.stream()
        .mapToDouble(Particle::radius)
        .max().orElseThrow(IllegalArgumentException::new);
    flowTimes = new LinkedList<>();
  }

  @Override
  public Set<Particle> simulate(final Criteria endCriteria, final ParticlesWriter writer) {
    List<Particle> currentParticles = initialParticles;
    int iteration = 1;
    double time = 0;

    while (!endCriteria.test(time, currentParticles)) {
      final Map<Particle, Set<Neighbour>> neighbours = cim.apply(currentParticles, maxRadius, 0);
      currentParticles = nextParticles(neighbours, time);

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
      if(count >= 1){
        System.out.println(count);
      }
      count = 0;
    }

    return new HashSet<>(currentParticles);
  }

  private List<Particle> nextParticles(final Map<Particle, Set<Neighbour>> neighbours, double time) {
    final List<Particle> nextParticles = new ArrayList<>(neighbours.size());
    final List<Particle> moveToTopParticles = new LinkedList<>();

    for (final Map.Entry<Particle, Set<Neighbour>> entry : neighbours.entrySet()) {
      final Particle movedParticle = moveParticle(entry.getKey(), entry.getValue());

      if (shouldMoveParticle(movedParticle)) {
        moveToTopParticles.add(movedParticle);
      } else {
        nextParticles.add(movedParticle);
      }
    }

    final List<Particle> topParticles = getTopParticles(nextParticles);
    for (final Particle particle : moveToTopParticles) {
      flowTimes.add(time);
      count++;
      final Particle particleMovedToTop = moveParticleToTop(particle, topParticles);
      nextParticles.add(particleMovedToTop);
      topParticles.add(particleMovedToTop);
      movementFunctions.get(particle).clearState(particleMovedToTop);
    }

    return nextParticles;
  }

  private boolean shouldMoveParticle(final Particle particle) {
    return particle.position().getY() - particle.radius() <= 0
        || (particle.position().getY() <= boxBottom
        && particle.position().getX() - particle.radius() <= 0)
        || (particle.position().getY() <= boxBottom
        && particle.position().getX() + particle.radius() >= boxWidth);
  }

  private List<Particle> getTopParticles(final List<Particle> particles) {
    return particles.stream()
        .filter(p -> p.position().getY() >= boxTop - 4 * maxRadius)
        .collect(Collectors.toList());
  }

  private Particle moveParticle(final Particle particle, final Set<Neighbour> neighbours) {
    addWallParticles(particle, neighbours);

    return movementFunctions.get(particle).move(particle, neighbours, dt);
  }

  private Particle moveParticleToTop(final Particle particle, final List<Particle> topParticles) {
    Particle newParticle;

    do {
      final Point2D newPosition = new Point2D(
          ThreadLocalRandom.current().nextDouble(particle.radius(), boxWidth - particle.radius()),
          boxTop - particle.radius());

      newParticle = ImmutableParticle.builder().from(particle)
          .position(newPosition)
          .velocity(Point2D.ZERO)
          .build();
    } while (isColliding(newParticle, topParticles));

    return newParticle;
  }

  private boolean isColliding(final Particle particle, final List<Particle> otherParticles) {
    return otherParticles.stream().anyMatch(particle::collides);
  }

  private void addWallParticles(final Particle particle, final Set<Neighbour> neighbours) {
    int wallId = -1;

    // left wall
    double distanceToWall = particle.position().getX() - particle.radius();
    if (distanceToWall < 0) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--)
          .position(new Point2D(0, particle.position().getY()))
          .mass(Double.POSITIVE_INFINITY)
          .radius(0)
          .velocity(Point2D.ZERO)
          .build(), distanceToWall));
    }

    // right wall
    distanceToWall = boxWidth - (particle.position().getX() + particle.radius());
    if (distanceToWall < 0) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--)
          .position(new Point2D(boxWidth, particle.position().getY()))
          .mass(Double.POSITIVE_INFINITY)
          .radius(0)
          .velocity(Point2D.ZERO)
          .build(), distanceToWall));
    }

    // down wall
    double gapStart = boxWidth / 2 - gap / 2;
    double gapEnd = boxWidth - gapStart;
    distanceToWall = Math.abs(particle.position().getY() - boxBottom) - particle.radius();
    if (distanceToWall < 0
        && (particle.position().getX() <= gapStart || particle.position().getX() >= gapEnd)) {
      neighbours.add(new Neighbour(ImmutableParticle.builder()
          .id(wallId--)
          .position(new Point2D(particle.position().getX(), boxBottom))
          .mass(Double.POSITIVE_INFINITY)
          .radius(0)
          .velocity(Point2D.ZERO)
          .build(), distanceToWall));
    }

    // gap
    if (particle.position().getX() > gapStart && particle.position().getX() < gapEnd) {
      final Point2D gapStartPosition = new Point2D(gapStart, boxBottom);
      final Point2D gapEndPosition = new Point2D(gapEnd, boxBottom);
      final double gapStartDistance =
          particle.position().distance(gapStartPosition) - particle.radius();
      final double gapEndDistance =
          particle.position().distance(gapEndPosition) - particle.radius();

      if (gapStartDistance < 0) {
        neighbours.add(new Neighbour(ImmutableParticle.builder()
            .id(wallId--)
            .position(gapStartPosition)
            .mass(Double.POSITIVE_INFINITY)
            .radius(0)
            .velocity(Point2D.ZERO)
            .build(), distanceToWall));
      }

      if (gapEndDistance < 0) {
        neighbours.add(new Neighbour(ImmutableParticle.builder()
            .id(wallId--)
            .position(gapEndPosition)
            .mass(Double.POSITIVE_INFINITY)
            .radius(0)
            .velocity(Point2D.ZERO)
            .build(), distanceToWall));
      }
    }
  }

  public List<Double> getFlowTimes() {
    return flowTimes;
  }
}
