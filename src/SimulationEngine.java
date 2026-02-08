import java.util.*;

public class SimulationEngine {

    // ================= SIGNALS =================
    public Map<Direction, TrafficSignal> signals = new HashMap<>();

    // ================= VEHICLES =================
    public List<Vehicle> activeVehicles = new ArrayList<>();
    private Map<Direction, LinkedList<Vehicle>> laneQueues = new HashMap<>();

    // ================= PHASE CONTROLLER =================
    private enum Phase {
        NS_GREEN, NS_YELLOW,
        EW_GREEN, EW_YELLOW
    }

    private Phase currentPhase = Phase.NS_GREEN;
    private int phaseTimer = 0;

    private static final int GREEN_TIME = 100;
    private static final int YELLOW_TIME = 10;
    private static final int MIN_GAP = 45;

    // ================= COUNTERS =================
    private int totalVehiclesPassed = 0;
    private Map<Direction, Integer> totalLanePassed = new HashMap<>();

    // ================= CONFLICT =================
    private Direction conflictPriorityLane = null;

    public SimulationEngine() {
        for (Direction d : Direction.values()) {
            signals.put(d, new TrafficSignal());
            laneQueues.put(d, new LinkedList<>());
            totalLanePassed.put(d, 0);
        }
        applyPhase();
    }

    public void addVehicle(Vehicle v) {
        activeVehicles.add(v);
        laneQueues.get(v.direction).add(v);
    }

    public void update(int panelW, int panelH) {

        Direction emergencyLane = detectEmergencyLane();
        if (emergencyLane != null) {
            forceEmergencyGreen(emergencyLane);
        } else {
            updateSignals();
        }

        int cx = panelW / 2;
        int cy = panelH / 2;

        int stopNorth = cy - panelH / 12;
        int stopSouth = cy + panelH / 12;
        int stopWest  = cx - panelW / 12;
        int stopEast  = cx + panelW / 12;

        conflictPriorityLane = determineConflictPriorityLane();

        for (Direction dir : Direction.values()) {

            LinkedList<Vehicle> queue = laneQueues.get(dir);
            Vehicle front = null;

            Iterator<Vehicle> it = queue.iterator();
            while (it.hasNext()) {

                Vehicle v = it.next();

                int stopLine = switch (dir) {
                    case NORTH -> stopNorth;
                    case SOUTH -> stopSouth;
                    case WEST  -> stopWest;
                    case EAST  -> stopEast;
                };

                boolean losingLane =
                        conflictPriorityLane != null &&
                                conflictPriorityLane != dir &&
                                isConflictingTurn(v);

                // ✅ ISSUE 3 FIX: move till stop line, don’t freeze
                if (losingLane) {
                    v.move(SignalState.RED, stopLine, cx, cy);
                    front = v;
                    continue;
                }

                if (front != null && v.distanceFrom(front) < MIN_GAP) {
                    break;
                }

                v.move(signals.get(dir).getState(), stopLine, cx, cy);
                front = v;

                if (v.isOut(panelW, panelH)) {
                    it.remove();
                    activeVehicles.remove(v);
                    totalVehiclesPassed++;
                    totalLanePassed.put(dir, totalLanePassed.get(dir) + 1);
                }
            }
        }
    }

    private boolean isConflictingTurn(Vehicle v) {
        return v.turnType == TurnType.LEFT || v.turnType == TurnType.UTURN;
    }

    private Direction determineConflictPriorityLane() {
        Direction best = null;
        int max = 0;

        for (Direction d : Direction.values()) {
            int count = 0;
            for (Vehicle v : laneQueues.get(d))
                if (isConflictingTurn(v)) count++;

            if (count > max) {
                max = count;
                best = d;
            }
        }
        return max > 0 ? best : null;
    }

    private void updateSignals() {
        phaseTimer++;
        switch (currentPhase) {
            case NS_GREEN -> { if (phaseTimer > GREEN_TIME) switchPhase(Phase.NS_YELLOW); }
            case NS_YELLOW -> { if (phaseTimer > YELLOW_TIME) switchPhase(Phase.EW_GREEN); }
            case EW_GREEN -> { if (phaseTimer > GREEN_TIME) switchPhase(Phase.EW_YELLOW); }
            case EW_YELLOW -> { if (phaseTimer > YELLOW_TIME) switchPhase(Phase.NS_GREEN); }
        }
    }

    private void switchPhase(Phase p) {
        currentPhase = p;
        phaseTimer = 0;
        applyPhase();
    }

    private void applyPhase() {
        for (TrafficSignal s : signals.values())
            s.setState(SignalState.RED);

        if (currentPhase == Phase.NS_GREEN) {
            signals.get(Direction.NORTH).setState(SignalState.GREEN);
            signals.get(Direction.SOUTH).setState(SignalState.GREEN);
        } else if (currentPhase == Phase.NS_YELLOW) {
            signals.get(Direction.NORTH).setState(SignalState.YELLOW);
            signals.get(Direction.SOUTH).setState(SignalState.YELLOW);
        } else if (currentPhase == Phase.EW_GREEN) {
            signals.get(Direction.EAST).setState(SignalState.GREEN);
            signals.get(Direction.WEST).setState(SignalState.GREEN);
        } else {
            signals.get(Direction.EAST).setState(SignalState.YELLOW);
            signals.get(Direction.WEST).setState(SignalState.YELLOW);
        }
    }

    private Direction detectEmergencyLane() {
        for (Direction d : Direction.values())
            for (Vehicle v : laneQueues.get(d))
                if (v instanceof EmergencyVehicle)
                    return d;
        return null;
    }

    private void forceEmergencyGreen(Direction d) {
        for (TrafficSignal s : signals.values())
            s.setState(SignalState.RED);
        signals.get(d).setState(SignalState.GREEN);
    }

    // ===== REQUIRED PUBLIC METHODS =====
    public int getLaneVehicleCount(Direction dir) {
        return laneQueues.get(dir).size();
    }

    public int getTotalLaneVehiclesPassed(Direction dir) {
        return totalLanePassed.get(dir);
    }

    public int getTotalVehiclesPassed() {
        return totalVehiclesPassed;
    }

    public void reset() {
        activeVehicles.clear();
        for (LinkedList<Vehicle> q : laneQueues.values())
            q.clear();

        totalVehiclesPassed = 0;
        for (Direction d : Direction.values())
            totalLanePassed.put(d, 0);

        conflictPriorityLane = null;
        currentPhase = Phase.NS_GREEN;
        phaseTimer = 0;
        applyPhase();
    }
}