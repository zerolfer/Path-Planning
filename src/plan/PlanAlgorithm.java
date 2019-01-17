package plan;

import map.RegularGridMap;
import util.Coordinate;

import java.util.List;

public interface PlanAlgorithm {
    void executePlanner();

    List<Coordinate> computePath();

    double[][] getDistToGoal();

    void reset(RegularGridMap map);
}
