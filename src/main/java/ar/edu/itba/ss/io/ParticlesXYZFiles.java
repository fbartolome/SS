package ar.edu.itba.ss.io;

import ar.edu.itba.ss.model.ImmutableParticle;
import ar.edu.itba.ss.model.Particle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javafx.geometry.Point2D;

public class ParticlesXYZFiles {

  public static void append(final Path path, final double time,
      final Collection<Particle> particles)
      throws IOException {

    try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
      writer.write(particles.size() + "\n");
      writer.write(time + "\n");
      for (final Particle particle : particles) {
        writer.write(particle.id() + "  " + particle.radius() + "  " + particle.position().getX()
            + "  " + particle.position().getY() + "  " + particle.velocity().getX() + "  "
            + particle.velocity().getY() + "\n");
      }
    }
  }

  public static void appendWithOtherAttributes(final Path path, final double time,
      final Map<Particle, List<Double>> map) throws IOException {

    try (final BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
      writer.write(map.size() + "\n");
      writer.write(time + "\n");
      for (final Map.Entry entry : map.entrySet()) {
        final Particle particle = (Particle) entry.getKey();
        final List<Double> attributes = (List<Double>) entry.getValue();
        writer.write(particle.id() + "  " + particle.radius() + "  " + particle.position().getX()
            + "  " + particle.position().getY() + "  " + particle.velocity().getX() + "  "
            + particle.velocity().getY());
        for (Double attribute : attributes) {
          writer.write("  " + attribute);
        }
        writer.write("\n");
      }
    }
  }

  public static List<Particle> read(final Path path) throws IOException {
    final List<Particle> particles;

    try (final BufferedReader reader = Files.newBufferedReader(path)) {
      String line = reader.readLine();

      if (line == null) {
        throw new IllegalArgumentException("Missing size");
      }

      final int initialParticlesSize;
      try {
        initialParticlesSize = Integer.parseInt(line.trim());
      } catch (final NumberFormatException exception) {
        throw new IllegalArgumentException("Invalid initial size");
      }

      line = reader.readLine();
      if (line == null) {
        throw new IllegalArgumentException("Missing initial time");
      }

      final double initialTime;
      try {
        initialTime = Double.parseDouble(line.trim());
      } catch (final NumberFormatException exception) {
        throw new IllegalArgumentException("Invalid initial time");
      }

      particles = new ArrayList<>(initialParticlesSize);

      for (int i = 0; i < initialParticlesSize; i++) {
        line = reader.readLine();
        if (line == null) {
          throw new IllegalArgumentException("Missing particles");
        }

        final String[] properties = line.trim().split("\\s+", 6);
        if (properties.length != 6) {
          throw new IllegalArgumentException("Invalid particle's properties");
        }

        final int id;
        final double r;
        final double x;
        final double y;
        final double vx;
        final double vy;
        try {
          id = Integer.parseInt(properties[0]);
          r = Double.parseDouble(properties[1]);
          x = Double.parseDouble(properties[2]);
          y = Double.parseDouble(properties[3]);
          vx = Double.parseDouble(properties[4]);
          vy = Double.parseDouble(properties[5]);
        } catch (final NumberFormatException exception) {
          exception.printStackTrace();
          throw new IllegalArgumentException("Invalid particle's properties");
        }

        particles.add(
            ImmutableParticle.builder().id(id).radius(r).position(new Point2D(x, y))
                .velocity(new Point2D(vx, vy)).build());
      }
    }

    return particles;
  }
}
