package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Stack;

public class KineticEnergyEquilibriumCriteria implements Criteria {

    private final double error;
    private final int times;
    private final Deque<Double> energies;

    public KineticEnergyEquilibriumCriteria(double error, int times) {
        this.error = error;
        this.times = times;
        energies = new LinkedList<>();
    }

    @Override
    public boolean test(double time, Collection<Particle> particles) {
        final double kinetic = particles.stream()
                .mapToDouble(this::kineticEnergy)
                .sum();

        energies.add(kinetic);
        if(energies.size() > times){
            energies.poll();
        }

        if(energies.size() == times){
            final double maxKinetic = energies.stream().mapToDouble(e -> e).max().getAsDouble();
            final double minKinetic = energies.stream().mapToDouble(e -> e).min().getAsDouble();
            if(maxKinetic - minKinetic <= error){
                return true;
            }
        }
        return false;
    }

    private double kineticEnergy(final Particle particle) {
        return 0.5 * particle.mass()
                * particle.velocity().magnitude() * particle.velocity().magnitude();
    }
}
