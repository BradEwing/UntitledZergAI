package info.opponent;

import bwapi.Unit;

public class ObservedUnit {

    private Unit unit;
    private int firstObservedFrame;
    private int lastObservedFrame;
    private boolean alive = true;

    public ObservedUnit(Unit unit, int firstObservedFrame)  {
        this.unit = unit;
        this.firstObservedFrame = firstObservedFrame;
        this.lastObservedFrame = firstObservedFrame;
    }

    public int getFirstObservedFrame() { return this.firstObservedFrame; }

    public int getLastObservedFrame() { return this.lastObservedFrame; }

    public void updateLastObservedFrame(int frame) { this.lastObservedFrame = frame; }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ObservedUnit)) {
            return false;
        }

        ObservedUnit u = (ObservedUnit) o;

        return this.unit.equals(u.hashCode());
    }

    @Override
    public int hashCode() {
        return this.unit.getID();
    }

    public void setAlive(boolean isAlive) {
        this.alive = isAlive;
    }
}
