package ar.edu.itba.ss.model.criteria;

import ar.edu.itba.ss.model.Particle;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public class FlowCriteria implements Criteria {

  private final int dN;
  private final int times;
  private final double error;
  private final Deque<Double> lastFlowTimes;
  private final Deque<Double> lastFlows;

  private double currentFlow;

  public FlowCriteria(final int dN, final int times, final double error) {
    this.dN = dN;
    this.times = times;
    this.error = error;
    this.lastFlowTimes = new ArrayDeque<>(times);
    this.lastFlows = new ArrayDeque<>(dN);
    this.currentFlow = 0.0;
  }

  @Override
  public boolean test(final double time, final Collection<Particle> particles) {
    return false; // Ignore
  }

  @Override
  public boolean test(final double time, final Collection<Particle> particles,
      final int particlesFlowed) {

    if (particlesFlowed >= 1) {
      lastFlowTimes.addLast(time);
    }

    if (lastFlowTimes.size() == dN) {
      currentFlow = dN / (lastFlowTimes.getLast() / lastFlowTimes.getFirst());
//      System.out.println("FLOW: " + currentFlow);
      lastFlowTimes.removeFirst();
      lastFlows.addLast(currentFlow);

      if (lastFlows.size() == times) {
        final double maxFlow = lastFlows.stream().mapToDouble(p -> p).max().getAsDouble();
        final double minFlow = lastFlows.stream().mapToDouble(p -> p).min().getAsDouble();
        final double avgFlow = lastFlows.stream().mapToDouble(p -> p).average().getAsDouble();
//        System.out.println("MAX: " + maxFlow);
//        System.out.println("MIN: " + minFlow);

        if (Math.abs(currentFlow - avgFlow) <= error) {
          return true;
        }

        lastFlows.removeFirst();
      }
    }

    return false;
  }
}
