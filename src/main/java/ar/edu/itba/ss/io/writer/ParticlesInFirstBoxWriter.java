package ar.edu.itba.ss.io.writer;

import ar.edu.itba.ss.model.Particle;
import ar.edu.itba.ss.model.Scatter2DChart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.geometry.Point2D;

public class ParticlesInFirstBoxWriter implements ParticlesWriter{

    private final double boxWidth;
    private final double boxHeight;
    private List<Point2D> points;

    public ParticlesInFirstBoxWriter(double boxWidth, double boxHeight) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
        this.points = new ArrayList<>();
        Scatter2DChart.initialize("Fracción de particulas en el compartimiento izquierdo",
                "tiempo [s]", 0, 3000, 250,
                "fracción", 0.5, 1, 0.1);
    }

    @Override
    public void write(double time, Collection<Particle> particles) throws IOException {
        particles = particles.stream().filter(p->p.id()>0).collect(Collectors.toList());
        List<Particle> particlesInFirstBox = particles.stream()
                .filter(p -> {
                    double x = p.position().getX();
                    double y = p.position().getY();
                    return !(x > 0 && x < (boxWidth/2) && y > 0 && y < boxHeight);
                }).collect(Collectors.toList());

        points.add(new Point2D(time, 1-((double)particlesInFirstBox.size()/particles.size())));
    }

    public void addSeries(String name){
        Platform.runLater(() -> Scatter2DChart.addSeries(name, points));
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void clear(){
        points = new LinkedList<>();
    }
}
