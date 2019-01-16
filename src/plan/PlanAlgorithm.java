package plan;

import util.Coordinate;

import java.util.List;

public interface PlanAlgorithm {
    void executePlanner();

    List<Coordinate> computePath();

    double[][] getDistToGoal();
}
