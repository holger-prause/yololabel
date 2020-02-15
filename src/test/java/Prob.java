/**
 * Created by Holger on 23.04.2018.
 */
public class Prob {
    private final int classId;
    private final double prob;

    public Prob(int classId, double prob) {
        this.classId = classId;
        this.prob = prob;
    }

    public int getClassId() {
        return classId;
    }

    public double getProb() {
        return prob;
    }
}
