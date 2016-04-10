package testREST.centroid_algo;

import testREST.Enums.TransportationModes;

/**
 * Created by yannick_uni on 1/5/16.
 */
public class WeightedPoint {
    private final int weight;
    private final double x;
    private final double y;

    public WeightedPoint(TransportationModes transportationModes, double x, double y) {
        this.weight = transportationModes.getWeight();
        this.x = x;
        this.y = y;
    }

    public int getWeight() {
        return weight;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
