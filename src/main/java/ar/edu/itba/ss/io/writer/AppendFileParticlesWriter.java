package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.io.IterativeFiles;
import ar.edu.itba.ss.io.ParticlesXYZFiles;
import ar.edu.itba.ss.model.Neighbour;
import ar.edu.itba.ss.model.Particle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppendFileParticlesWriter implements ParticlesWriter {

  private final Path filePath;

  public AppendFileParticlesWriter(final String fileName) {
    this.filePath = IterativeFiles.firstNotExists(fileName);

    try {
      Files.createFile(filePath);
    } catch (final IOException exception) {
      throw new IllegalStateException("Can't create file");
    }
  }

  @Override
  public void write(final double time, final Collection<Particle> particles) throws IOException {
    ParticlesXYZFiles.append(filePath, time, particles);
  }

  public void writeWithAttributes(final double time,
                                  final Map<Particle,List<Double>> map) throws IOException {
    ParticlesXYZFiles.appendWithOtherAttributes(filePath,time,map);
  }
}
