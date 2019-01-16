package plan;

import map.RegularGridMap;
import util.Coordinate;

import java.util.*;

public class AStarAlgorithm implements PlanAlgorithm {

    private class Node {

        Coordinate coordinate;
        Node parent;
        double f; // f(x)=g(x)+h(x)
        double g;

        public Node(Coordinate coordinate) {
            this.coordinate = coordinate;
        }

        public Node(Coordinate coordinate, Node parent) {
            this(coordinate);
            this.parent = parent;
        }

        /**
         * heuristic function as the Euclidean Distance
         */
        private double h() {
            return getEuclideanDistance(this, goal);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(coordinate, node.coordinate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(coordinate);
        }

        @Override
        public String toString() {
            String s = "";
            if (parent != null) s = "(x=" + parent.coordinate.getX() + ", y=" + parent.coordinate.getY() + ")";
            return "Node{" +
                    "coordinate=" + coordinate +
                    ", parent=" + s +
                    ", f=" + f +
                    ", g=" + g +
                    ", h=" + h() +
                    '}';
        }

        public double getF() {
            return f;
        }

        public void setF(double g) {
            this.f = g + h();
            distToGoal[coordinate.getX()][coordinate.getY()] = f;
        }
    }

    private double getEuclideanDistance(Node from, Node to) {
        return Math.sqrt(Math.pow(from.coordinate.getX() - to.coordinate.getX(), 2)
                + Math.pow(from.coordinate.getY() - to.coordinate.getY(), 2));
    }

    private Node goal;
    private final Node start;
    private final RegularGridMap map;
    private double[][] distToGoal;

    public AStarAlgorithm(RegularGridMap map) {
        this.goal = new Node(map.getGoal());
        this.start = new Node(map.getStart());
        this.map = map;
        distToGoal = new double[map.getGrid().length][map.getGrid()[0].length];
        for (int i = 0; i < distToGoal.length; i++) {
            for (int j = 0; j < distToGoal[0].length; j++) {
                if (i == start.coordinate.getX() && j == start.coordinate.getY())
                    distToGoal[i][j] = start.h();
                distToGoal[i][j] = Double.MAX_VALUE;
            }
        }
    }

    @Override
    public void executePlanner() {
        PriorityQueue<Node> OPEN = new PriorityQueue<>((o1, o2) -> {
            if (o1.getF() < o2.getF())
                return -1;
            else return +1;
        }); // queue of the visited but not expanded nodes
        List<Node> CLOSED = new ArrayList<>(); // set of the visited AND expanded nodes

        OPEN.add(start);


        while (!OPEN.isEmpty()) {
            Node current = OPEN.poll();
            CLOSED.add(current);

            if (current.equals(goal)) {
                this.goal = current; // update info
                return;
            }
            for (Node child : getChildren(current)) {
                if (CLOSED.contains(child) || !map.isFree(child.coordinate))
                    continue;
                child.g = current.g + getEuclideanDistance(child, current);
                child.setF(child.g);

                for (Node openNode : OPEN) {
                    if (openNode.equals(child) && child.g > openNode.g)
                        continue;
                }
                OPEN.add(child);
            }
        }
    }

    private List<Node> getChildren(Node parent) {
        List<Node> result = new ArrayList<>();
        int x = parent.coordinate.getX(), y = parent.coordinate.getY();
        for (int xshift = -1; xshift <= 1; xshift++) {
            for (int yshift = -1; yshift <= 1; yshift++) {
                if (xshift == 0 && yshift == 0) continue;
                if (x + xshift >= map.getGrid().length || y + yshift >= map.getGrid()[0].length ||
                        x + xshift < 0 || y + yshift < 0) continue;
                result.add(new Node(new Coordinate(x + xshift, y + yshift), parent));
            }
        }
        return result;
    }


    @Override
    public List<Coordinate> computePath() {
        List<Coordinate> path = new ArrayList<>();
        Node current = goal;
        while (current != null) {
            path.add(current.coordinate);
            current = current.parent;
        }
        return path; // THE PATH IS IN REVERSE ORDER BUT FOR PAINTING DOENSEN'T MATTER
    }

    @Override
    public double[][] getDistToGoal() {
        return this.distToGoal;
    }

    /**
     * THIS IS FOR THE USER INTERFACE JCOMBOBOX
     */
    @Override
    public String toString() {
        return "A-Star Algorithm";
    }

}
