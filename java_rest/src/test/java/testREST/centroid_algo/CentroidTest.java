package testREST.centroid_algo;

import org.junit.Test;
import testREST.Enums.TransportationModes;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by yannick_uni on 1/5/16.
 */
public class CentroidTest {

    private Centroid centroid = new Centroid();

    @Test
    public void testCentroid() {
        List<WeightedPoint> weightedPoints = new ArrayList<>();
        weightedPoints.add(new WeightedPoint(TransportationModes.DEFAULT,1,0));
        weightedPoints.add(new WeightedPoint(TransportationModes.DEFAULT,0,1));
        assertEquals(0.5, centroid.getCentroid(weightedPoints).getX(), 0.01);
    }

    @Test
    public void testFourPeopleMeet() {
        List<WeightedPoint> weightedPoints = new ArrayList<>();
        weightedPoints.add(new WeightedPoint(TransportationModes.DEFAULT,0,0));
        weightedPoints.add(new WeightedPoint(TransportationModes.DEFAULT,0,1));
        weightedPoints.add(new WeightedPoint(TransportationModes.DEFAULT,1,0));
        weightedPoints.add(new WeightedPoint(TransportationModes.DEFAULT,1,1));
        assertEquals(0.5, centroid.getCentroid(weightedPoints).getX(), 0.1);
        assertEquals(0.5, centroid.getCentroid(weightedPoints).getY(), 0.1);
    }

    @Test
    public void testRealisticScenario() {
        List<WeightedPoint> weightedPoints = new ArrayList<>();
        weightedPoints.add(new WeightedPoint(TransportationModes.BIKE,53.1203388,8.455425));
        weightedPoints.add(new WeightedPoint(TransportationModes.BIKE,48.1550268,11.2609725));
        assertEquals(50.6376828, centroid.getCentroid(weightedPoints).getX(), 0.0001);
        assertEquals(9.85819875, centroid.getCentroid(weightedPoints).getY(), 0.0001);
    }

}