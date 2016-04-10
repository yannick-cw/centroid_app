package testREST.Enums;

/**
 * Created by yannick_uni on 1/5/16.
 */
public enum TransportationModes {
    FOOT(90), CAR(5), BIKE(50), PUBLIC(20), DEFAULT(1), DECLINED(1);
    private final int weight;

    TransportationModes(int weight) {
        this.weight = weight;
    }
    public int getWeight() {
        return weight;
    }

}
