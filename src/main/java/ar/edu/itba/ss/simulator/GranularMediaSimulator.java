package ar.edu.itba.ss.simulator;

import ar.edu.itba.ss.io.writer.ParticlesWriter;
import ar.edu.itba.ss.method.movement.MovementFunction;
import ar.edu.itba.ss.method.neigbour.CellIndexMethod;
import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.criteria.Criteria;
import javafx.geometry.Point2D;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GranularMediaSimulator implements Simulator{

    private final List<Particle> initialParticles;
    private final double dt;
    private final int writerIteration;
    private final double boxWidth;
    private final double boxHeight;
    private final double gap;
    private final CellIndexMethod cim;
    private final double rc;
    private final Map<Particle, MovementFunction> movementFunctions;

    public GranularMediaSimulator(double dt, int writerIteration, double boxWidth,
                                  double boxHeight, double gap, double rc,
                                  Map<Particle, MovementFunction> movementFunctions) {
        this.dt = dt;
        this.writerIteration = writerIteration;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.gap = gap;
        this.cim = new CellIndexMethod(boxHeight > boxWidth ? boxHeight : boxWidth, false);
        this.rc = rc;
        this.movementFunctions = movementFunctions;
        this.initialParticles = initiateParticles();
    }

    private List<Particle> initiateParticles() {
        //TODO
        return new LinkedList<>();
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
                .filter(n -> isOverlapped(particle, n))
                .collect(Collectors.toSet());
        addWallParticles(particle, neighbours);

        //TODO: if particle is below boxHeight/10, it should be positioned at the start of the box

        MovementFunction function = movementFunctions.get(particle);
        return function.move(particle, neighbours, dt);
    }

    private void addWallParticles(Particle particle, Set<Neighbour> neighbours) {
        int wallId = 0;

        // left wall
        double distanceToWall = particle.position().getX() - particle.radius();
        if(distanceToWall < 0){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(0, particle.position().getY()))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }

        // right wall
        distanceToWall = boxWidth - (particle.position().getX() + particle.radius());
        if(distanceToWall < 0){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(boxWidth, particle.position().getY()))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }

        // down wall
        double gapStart = boxWidth/2 - gap/2;
        double gapEnd = boxWidth - gapStart;
        distanceToWall = particle.position().getY() - particle.radius();
        if(distanceToWall < 0 && particle.position().getX() < gapStart
                && particle.position().getX() > gapEnd){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(particle.position().getX(), 0))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }

        // gap start
        distanceToWall = gapStart - (particle.position().getX() - particle.radius());
        if(distanceToWall < 0 && particle.position().getY() == 0){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(gapStart, 0))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }

        // gap end
        distanceToWall = gapEnd - (particle.position().getX() + particle.radius());
        if(distanceToWall < 0 && particle.position().getY() == 0){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(gapEnd, 0))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }
    }

    private boolean isOverlapped(Particle particle, Neighbour neighbour){
        return particle.radius() + neighbour.getNeighbourParticle().radius()
                > neighbour.getDistance();
    }
}
