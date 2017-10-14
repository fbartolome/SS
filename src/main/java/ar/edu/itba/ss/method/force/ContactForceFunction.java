package ar.edu.itba.ss.method.force;

import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import javafx.geometry.Point2D;

import java.util.Set;
import java.util.function.BiFunction;

public class ContactForceFunction implements BiFunction<Particle, Set<Neighbour>, Point2D> {

    //TODO: gravity should be here?
    private static final double GRAVITY = 9.8;

    private final double kn;
    private final double kt;
    private final boolean isGravity;

    public ContactForceFunction(double kn, double kt, boolean isGravity) {
        this.kn = kn;
        this.kt = kt;
        this.isGravity = isGravity;
    }

    @Override
    public Point2D apply(Particle particle, Set<Neighbour> neighbours) {
        double totalForceX = 0;
        double totalForceY = 0;

        for(Neighbour neighbour : neighbours){
            final double normalForce = calculateNormalForce(particle, neighbour);
            final double tangentialForce = calculateTangentialForce(particle, neighbour);

            final Point2D distanceVector = neighbour.getNeighbourParticle().position()
                    .subtract(particle.position());
            final double forceX = normalForce * distanceVector.getX()/distanceVector.magnitude()
                    + tangentialForce * -distanceVector.getY()/distanceVector.magnitude();
            final double forceY = normalForce * distanceVector.getY()/distanceVector.magnitude()
                    + tangentialForce * distanceVector.getX()/distanceVector.magnitude();

            totalForceX += forceX;
            totalForceY += forceY;
        }

        if(isGravity){
            totalForceY -= GRAVITY * particle.mass();
        }

        return new Point2D(totalForceX, totalForceY);
    }

    private double calculateTangentialForce(Particle particle, Neighbour neighbour) {
        final Point2D normalVersor = neighbour.getNeighbourParticle().position().subtract(particle.position())
                .normalize();
        final Point2D tangentialVersor = new Point2D(-normalVersor.getY(),normalVersor.getX());
        System.out.println(tangentialVersor);
        final double relativeVelocity = particle.velocity().subtract(neighbour.getNeighbourParticle().velocity())
                .dotProduct(tangentialVersor);
        return -kt * calculatePsi(particle,neighbour) * relativeVelocity;
    }

    private double calculateNormalForce(Particle particle, Neighbour neighbour) {
        return -kn * calculatePsi(particle,neighbour);
    }

    private double calculatePsi(Particle particle, Neighbour neighbour){
        return Math.abs(neighbour.getDistance());
    }
}
