package info;

import bwapi.Player;
import bwapi.Unit;
import bwapi.UnitType;
import bwem.BWEM;
import bwem.Base;
import bwem.Mineral;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import planner.PlannedItem;
import strategy.openers.Opener;
import strategy.strategies.Strategy;
import strategy.strategies.UnitWeights;
import unit.managed.ManagedUnit;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Class to handle global state that is shared among various managers.
 *
 * Break this into subclasses if too big or need util functions around subsets.
 */
@Data
public class GameState {
    private Player self;
    private BWEM bwem;

    private int mineralWorkers;
    private int geyserWorkers;
    private int plannedSupply;
    private int larvaDeadlockDetectedFrame;

    private HashSet<ManagedUnit> gatherers = new HashSet<>();
    private HashSet<ManagedUnit> mineralGatherers = new HashSet<>();
    private HashSet<ManagedUnit> gasGatherers = new HashSet<>();

    private HashMap<Unit, HashSet<ManagedUnit>> geyserAssignments = new HashMap<>();
    private HashMap<Unit, HashSet<ManagedUnit>> mineralAssignments = new HashMap<>();

    private HashSet<ManagedUnit> larva = new HashSet<>();

    private boolean enemyHasCloakedUnits = false;
    private boolean enemyHasHostileFlyers = false;
    private boolean isLarvaDeadlocked = false;
    private boolean isAllIn = false;

    private HashSet<PlannedItem> plansScheduled = new HashSet<>();
    private HashSet<PlannedItem> plansBuilding = new HashSet<>();
    private HashSet<PlannedItem> plansMorphing = new HashSet<>();
    private HashSet<PlannedItem> plansComplete = new HashSet<>();
    private HashMap<Unit, PlannedItem> assignedPlannedItems = new HashMap<>();

    private HashMap<Base, HashSet<ManagedUnit>> gatherersAssignedToBase = new HashMap<>();

    private HashMap<Base, HashSet<Unit>> baseToThreatLookup = new HashMap<>();

    private Opener activeOpener;
    @Setter(AccessLevel.NONE)
    private Strategy activeStrategy;
    private UnitWeights unitWeights;

    private UnitTypeCount unitTypeCount = new UnitTypeCount();

    private TechProgression techProgression = new TechProgression();

    private ResourceCount resourceCount;

    private BaseData baseData;

    public GameState(Player self, BWEM bwem) {
        this.self = self;
        this.bwem = bwem;
        this.resourceCount = new ResourceCount(self);
        this.baseData = new BaseData(bwem.getMap().getBases());
    }

    public void setActiveStrategy(Strategy activeStrategy) {
        this.activeStrategy = activeStrategy;
        this.unitWeights = activeStrategy.getUnitWeights();
    }

    public int numGatherers() {
        return gatherers.size();
    }

    public int numLarva() { return larva.size(); }

    public int frameCanAffordUnit(UnitType unit, int currentFrame) {
        return this.resourceCount.frameCanAffordUnit(unit, currentFrame, mineralGatherers.size(), gasGatherers.size());
    }

    public Base reserveBase() {
        final Base newBase = baseData.findNewBase();
        return newBase;
    }

    public void claimBase(Unit hatchery) {
        if (this.baseData.isBase(hatchery)) {
            return;
        }
        final Base newBase = baseData.findNewBase();
        addBaseToGameState(hatchery, newBase);
    }

    public void addBaseToGameState(Unit hatchery, Base base) {
        gatherersAssignedToBase.put(base, new HashSet<>());
        this.baseData.addBase(hatchery, base);

        for (Mineral mineral: base.getMinerals()) {
            mineralAssignments.put(mineral.getUnit(), new HashSet<>());
        }
    }

    public void addMainBase(Unit hatchery, Base base) {
        this.baseData.initializeMainBase(base);
        addBaseToGameState(hatchery, base);
    }

    public void addMacroHatchery(Unit hatchery) {
        this.baseData.addMacroHatchery(hatchery);
    }

    // TODO: Refactor base lookups into baseData?
    // TODO: Reassign gatherers
    public void removeHatchery(Unit hatchery) {
        if (this.baseData.isBase(hatchery)) {
            Base base = this.baseData.get(hatchery);
            gatherersAssignedToBase.remove(base);
            baseToThreatLookup.remove(base);
        }
        this.baseData.removeHatchery(hatchery);
    }
}
