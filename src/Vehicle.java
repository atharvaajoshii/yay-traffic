import java.awt.*;

public class Vehicle {

    protected Direction direction;
    protected TurnType turnType;

    protected Direction entryDirection;

    protected int x, y;
    protected int speed = 3;

    protected boolean crossedStopLine = false;
    protected boolean hasTurned = false;

    // U-turn control
    protected int uTurnStage = 0;
    // 0 = before first turn
    // 1 = after first left
    // 2 = after second left (done)

    protected String emoji = "🚗";
    private static final int LANE_PADDING = 18;

    public Vehicle(Direction dir, TurnType turn, int w, int h) {
        this.direction = dir;
        this.entryDirection = dir;
        this.turnType = turn;

        int cx = w / 2;
        int cy = h / 2;

        if (dir == Direction.NORTH) { x = cx - 55; y = 0; }
        if (dir == Direction.SOUTH) { x = cx + 5; y = h; }
        if (dir == Direction.WEST)  { x = 0; y = cy + 40; }
        if (dir == Direction.EAST)  { x = w; y = cy - 20; }
    }

    // ================= MOVE =================
    public void move(SignalState state, int stopLine, int cx, int cy) {

        // -------- STOP LINE LOGIC --------
        if (!crossedStopLine) {
            if (direction == Direction.NORTH && y + speed >= stopLine) crossedStopLine = true;
            if (direction == Direction.SOUTH && y - speed <= stopLine) crossedStopLine = true;
            if (direction == Direction.WEST  && x + speed >= stopLine) crossedStopLine = true;
            if (direction == Direction.EAST  && x - speed <= stopLine) crossedStopLine = true;
        }

        if (!crossedStopLine && state == SignalState.RED) return;

        // =================================================
        // ===================== U TURN ====================
        // =================================================

        if (turnType == TurnType.UTURN) {

            // FIRST TURN at center
            if (uTurnStage == 0 && reachedCenter(cx, cy)) {
                direction = turnLeft(direction);
                uTurnStage = 1;
            }

            // MOVE SIDEWAYS INTO OUTGOING LANE
            else if (uTurnStage == 1 && crossedDivider(cx, cy)) {
                direction = turnLeft(direction); // SECOND TURN = complete U-turn
//                snapToOppositeLane();            // SHIFT to opposite lane
                alignToLane(cx, cy);
                uTurnStage = 2;
            }

            moveStraight();
            return;
        }


        // =================================================
        // ================ NORMAL TURNS ===================
        // =================================================
        if (!hasTurned && turnType != TurnType.STRAIGHT && reachedCenter(cx, cy)) {

            if (turnType == TurnType.LEFT) {
                direction = turnLeft(direction);
            } else if (turnType == TurnType.RIGHT) {
                direction = turnRight(direction);
            }
            alignToLane(cx, cy);
            hasTurned = true;
        }

        moveStraight();
    }

    // ================= HELPERS =================

    private void moveStraight() {
        if (direction == Direction.NORTH) y += speed;
        if (direction == Direction.SOUTH) y -= speed;
        if (direction == Direction.WEST)  x += speed;
        if (direction == Direction.EAST)  x -= speed;
    }

    private boolean reachedCenter(int cx, int cy) {
        return Math.abs(x - cx) < 4 || Math.abs(y - cy) < 4;
    }

    // Checks if vehicle has crossed the center divider of the opposite road
// Checks divider crossing based on ORIGINAL entry direction
    private boolean crossedDivider(int cx, int cy) {

        switch (entryDirection) {

            case NORTH:
            case SOUTH:
                // Must cross vertical divider
                return Math.abs(x - cx) > 20;

            case EAST:
            case WEST:
                // Must cross horizontal divider
                return Math.abs(y - cy) > 20;
        }
        return false;
    }



    private void snapToOppositeLane() {

        int laneGap = 55;

        switch (direction) {

            case NORTH:
                x -= laneGap; // snap to opposite vertical lane
                break;

            case SOUTH:
                x += laneGap;
                break;

            case EAST:
                y -= laneGap; // snap to opposite horizontal lane
                break;

            case WEST:
                y += laneGap;
                break;
        }
    }



    private Direction turnLeft(Direction d) {
        switch (d) {
            case NORTH: return Direction.WEST;
            case WEST:  return Direction.SOUTH;
            case SOUTH: return Direction.EAST;
            case EAST:  return Direction.NORTH;
        }
        return d;
    }

    private Direction turnRight(Direction d) {
        switch (d) {
            case NORTH: return Direction.EAST;
            case EAST:  return Direction.SOUTH;
            case SOUTH: return Direction.WEST;
            case WEST:  return Direction.NORTH;
        }
        return d;
    }

    // ================= DRAW =================
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        g.drawString(emoji, x, y);
    }

    // ================= EXIT =================
    public boolean isOut(int w, int h) {
        return x < -60 || x > w + 60 || y < -60 || y > h + 60;
    }
    private void alignToLane(int cx, int cy) {
        switch (direction) {
            case NORTH:
                x = cx - 55;
                break;
            case SOUTH:
                x = cx + 5;
                break;
            case WEST:
                y = cy + 40;
                break;
            case EAST:
                y = cy - 20;
                break;
        }
    }





    public int distanceFrom(Vehicle v) {
        return Math.abs(this.x - v.x) + Math.abs(this.y - v.y);
    }

}