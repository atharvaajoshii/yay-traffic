import java.util.*;

public class SimulationEngine {

    // ================= SIGNALS =================
    public Map<Direction, TrafficSignal> signals = new HashMap<>();

    // ================= VEHICLES =================
    public List<Vehicle> activeVehicles = new ArrayList<>();

    // ================= PHASE CONTROLLER =================
    private enum Phase {
        NS_GREEN, NS_YELLOW, ALL_RED_1,
        EW_GREEN, EW_YELLOW, ALL_RED_2
    }

    private Phase currentPhase = Phase.NS_GREEN;
    private int phaseTimer = 0;

    // Timings (in ticks, 1 tick ≈ 30 ms)
    private static final int GREEN_TIME = 1000;   // ~30 sec
    private static final int YELLOW_TIME = 130;   // ~4 sec
    private static final int ALL_RED_TIME = 70;   // ~2 sec

    public SimulationEngine() {
        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficSignal());
        }
        applyPhase(); // initial state
    }

    // ================= ADD VEHICLE =================
    public void addVehicle(Vehicle v) {
        activeVehicles.add(v);

        // 🚑 Emergency override
        if (v instanceof EmergencyVehicle) {
            for (TrafficSignal s : signals.values()) {
                s.setState(SignalState.RED);
            }
            signals.get(v.direction).setState(SignalState.GREEN);
        }
    }

    // ================= UPDATE LOOP =================
    public void update(int panelW, int panelH) {

        phaseTimer++;

        switch (currentPhase) {

            case NS_GREEN:
                if (phaseTimer > GREEN_TIME) switchPhase(Phase.NS_YELLOW);
                break;

            case NS_YELLOW:
                if (phaseTimer > YELLOW_TIME) switchPhase(Phase.ALL_RED_1);
                break;

            case ALL_RED_1:
                if (phaseTimer > ALL_RED_TIME) switchPhase(Phase.EW_GREEN);
                break;

            case EW_GREEN:
                if (phaseTimer > GREEN_TIME) switchPhase(Phase.EW_YELLOW);
                break;

            case EW_YELLOW:
                if (phaseTimer > YELLOW_TIME) switchPhase(Phase.ALL_RED_2);
                break;

            case ALL_RED_2:
                if (phaseTimer > ALL_RED_TIME) switchPhase(Phase.NS_GREEN);
                break;
        }

        // ---------- STOP LINES ----------
        int stopNorth = panelH / 2 - panelH / 12;
        int stopSouth = panelH / 2 + panelH / 12;
        int stopWest  = panelW / 2 - panelW / 12;
        int stopEast  = panelW / 2 + panelW / 12;

        Iterator<Vehicle> it = activeVehicles.iterator();
        while (it.hasNext()) {
            Vehicle v = it.next();

            int stopLine = 0;
            if (v.direction == Direction.NORTH) stopLine = stopNorth;
            if (v.direction == Direction.SOUTH) stopLine = stopSouth;
            if (v.direction == Direction.WEST)  stopLine = stopWest;
            if (v.direction == Direction.EAST)  stopLine = stopEast;

            v.move(
                    signals.get(v.direction).getState(),
                    stopLine,
                    panelW / 2,
                    panelH / 2
            );


            if (v.isOut(panelW, panelH)) {
                it.remove();
            }
        }
    }

    // ================= PHASE SWITCH =================
    private void switchPhase(Phase next) {
        currentPhase = next;
        phaseTimer = 0;
        applyPhase();
    }

    // ================= APPLY SIGNAL STATES =================
    private void applyPhase() {

        // Default: all red
        for (TrafficSignal s : signals.values()) {
            s.setState(SignalState.RED);
        }

        switch (currentPhase) {

            case NS_GREEN:
                signals.get(Direction.NORTH).setState(SignalState.GREEN);
                signals.get(Direction.SOUTH).setState(SignalState.GREEN);
                break;

            case NS_YELLOW:
                signals.get(Direction.NORTH).setState(SignalState.YELLOW);
                signals.get(Direction.SOUTH).setState(SignalState.YELLOW);
                break;

            case EW_GREEN:
                signals.get(Direction.EAST).setState(SignalState.GREEN);
                signals.get(Direction.WEST).setState(SignalState.GREEN);
                break;

            case EW_YELLOW:
                signals.get(Direction.EAST).setState(SignalState.YELLOW);
                signals.get(Direction.WEST).setState(SignalState.YELLOW);
                break;

            // ALL_RED phases handled by default
        }
    }

    // ================= RESET =================
    public void reset() {
        activeVehicles.clear();
        currentPhase = Phase.NS_GREEN;
        phaseTimer = 0;
        applyPhase();
    }
}
