package testREST.centroid_algo;

import testREST.Enums.TransportationModes;

import java.util.List;

/**
 * Created by yannick_uni on 1/5/16.
 */
public class Centroid {
    public WeightedPoint getCentroid(List<WeightedPoint> weightedPoints) {
        int _length = weightedPoints.size() -1;
        double _centroidX = calculateCentroidX(weightedPoints, _length).a
                / calculateCentroidX(weightedPoints, _length).b;
        double _centroidY = calculateCentroidY(weightedPoints, _length)
                / calculateCentroidX(weightedPoints, _length).b;
        return new WeightedPoint(TransportationModes.DEFAULT, _centroidX, _centroidY);
    }

    private Tuple calculateCentroidX(List<WeightedPoint> weightedPoints, int length) {
        if(length < 0) {
            return new Tuple(0,0);
        } else {
            return new Tuple(weightedPoints.get(length).getX() * weightedPoints.get(length).getWeight()
                    + calculateCentroidX(weightedPoints, length-1).a
                    , weightedPoints.get(length).getWeight()
                    + calculateCentroidX(weightedPoints, length-1).b);
        }
    }

    private double calculateCentroidY(List<WeightedPoint> weightedPoints, int length) {
        if(length < 0) {
            return 0;
        } else {
            return (weightedPoints.get(length).getY() * weightedPoints.get(length).getWeight()
            + calculateCentroidY(weightedPoints, length-1));
        }
    }

    class Tuple {
        double a;
        int b;

        public Tuple(double a, int b) {
            this.a = a;
            this.b = b;
        }
    }
}

