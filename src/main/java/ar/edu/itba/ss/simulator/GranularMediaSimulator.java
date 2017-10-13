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
    private final int amountOfParticles;
    private final double dt;
    private final int writerIteration;
    private final double boxWidth;
    private final double boxHeight;
    private final double boxTop;
    private final double boxBottom;
    private final double gap;
    private final CellIndexMethod cim;
    private Map<Particle, MovementFunction> movementFunctions;

    public GranularMediaSimulator(List<Particle> initialParticles, double dt, int writerIteration, double boxWidth,
                                  double boxHeight, double gap,
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
        this.cim = new CellIndexMethod(boxTop, false);
        this.movementFunctions = movementFunctions;
    }

    @Override
    public Set<Particle> simulate(Criteria endCriteria, ParticlesWriter writer) {
        double time = 0;
        int iteration = 1;
        List<Particle> particles = initialParticles;
        double maxRadius = particles.stream().mapToDouble(Particle::radius).max().getAsDouble();

        while (!endCriteria.test(time, particles)) {
            Map<Particle, Set<Neighbour>> neighbours = cim
                    .apply(particles, maxRadius, 0);
            particles = nextParticles(neighbours);

            if (iteration == writerIteration) {
                System.out.println("AVG VELOCITY: " + particles.stream().mapToDouble(p -> p.velocity().magnitude()).average().getAsDouble());
                System.out.println("MIN POSITION: " + particles.stream().mapToDouble(p -> p.position().getX()).min().getAsDouble() + ", " + particles.stream().mapToDouble(p -> p.position().getY()).min().getAsDouble());
                System.out.println("MAX POSITION: " + particles.stream().mapToDouble(p -> p.position().getX()).max().getAsDouble() + ", " + particles.stream().mapToDouble(p -> p.position().getY()).max().getAsDouble());

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
        addWallParticles(particle, neighbours);

        MovementFunction function = movementFunctions.get(particle);
        Particle movedParticle = function.move(particle, neighbours, dt);
        if(movedParticle.position().getY() < 0){
            return particleToTop(movedParticle);
        }
        return movedParticle;
    }

    private Particle particleToTop(Particle particle) {
        Random r = new Random();
        Particle topParticle = ImmutableParticle.builder()
                .from(particle)
                .position(new Point2D(r.nextDouble() * boxWidth,boxTop))
                .velocity(Point2D.ZERO)
                .build();
        //TODO: check overlapping
        movementFunctions.get(topParticle).clearState(topParticle);
        movementFunctions.put(topParticle,
                movementFunctions.get(topParticle));
        return topParticle;
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
        distanceToWall = particle.position().getY() - particle.radius() - boxBottom;
        if(distanceToWall < 0 && (particle.position().getX() < gapStart
                || particle.position().getX() > gapEnd)){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(particle.position().getX(), boxBottom))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }

        // gap start
        distanceToWall = gapStart - (particle.position().getX() - particle.radius());
        if(distanceToWall < 0 && particle.position().getY() == boxBottom){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(gapStart, 0))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }

        // gap end
        distanceToWall = gapEnd - (particle.position().getX() + particle.radius());
        if(distanceToWall < 0 && particle.position().getY() == boxBottom){
            neighbours.add(new Neighbour(ImmutableParticle.builder()
                    .id(wallId--)
                    .position(new Point2D(gapEnd, 0))
                    .mass(Double.POSITIVE_INFINITY)
                    .radius(0)
                    .velocity(Point2D.ZERO).build(), -distanceToWall));
        }
    }
}
