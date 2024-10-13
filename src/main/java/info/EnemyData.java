package info;

import bwapi.Unit;
import info.opponent.ObservedUnit;

import java.util.HashMap;
import java.util.HashSet;

/**
 * What we know about our enemy.
 */
public class EnemyData {
    private HashMap<Unit, ObservedUnit> observedUnitLookup = new HashMap();
    private HashSet<ObservedUnit> observedUnits = new HashSet<>();

    public void newObservedUnit(Unit unit, int currentFrame) {
        ObservedUnit observedUnit = new ObservedUnit(unit, currentFrame);
        observedUnitLookup.put(unit, observedUnit);
        observedUnits.add(observedUnit);
    }

    /**
     * Update enemy unit state
     * @param unit
     */
    public void onUnitDestroy(Unit unit) {
        ObservedUnit observedUnit = observedUnitLookup.get(unit);
        if (observedUnit == null) {
            return;
        }
        observedUnit.setAlive(false);
    }

    public boolean isObserved(Unit unit) {
        return observedUnitLookup.containsKey(unit);
    }
}
