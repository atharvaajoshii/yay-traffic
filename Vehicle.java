import java.awt.*;

public class Vehicle {

    protected Direction direction;
    protected TurnType turnType;

    protected int x, y;
    protected int speed = 2;

    protected boolean crossedStopLine = false;

    // Movement state
    protected enum MoveState {
        APPROACHING,
        TURNING,              // for LEFT / RIGHT
        UTURN_FIRST_TURN,
        UTURN_STRAIGHT,
        UTURN_SECOND_TURN,
        EXITING
    }

    protected MoveState moveState = MoveState.APPROACHING;

    protected String emoji = "🚗";
    private int uTurnStep = 0; // 0 = not started, 1 = first left done


    private static final int LANE_PADDING = 18;

    public Vehicle(Direction dir, TurnType turn, int w, int h) {
        this.direction = dir;
        this.turnType = turn;

        int cx = w / 2;
        int cy = h / 2;

        if (dir == Direction.NORTH) {
            x = cx - (LANE_PADDING + 40);
            y = 0;
        }
        if (dir == Direction.SOUTH) {
            x = cx + LANE_PADDING;
            y = h;
        }
        if (dir == Direction.WEST) {
            x = 0;
            y = cy + LANE_PADDING+30;
        }
        if (dir == Direction.EAST) {
            x = w;
            y = cy - LANE_PADDING;
        }
    }
    private boolean enteredOutgoingLane(int cx, int cy) {

        int offset = 25; // how far vehicle must move after first turn

        if (direction == Direction.EAST && x > cx + offset) return true;
        if (direction == Direction.WEST && x < cx - offset) return true;
        if (direction == Direction.NORTH && y < cy - offset) return true;
        if (direction == Direction.SOUTH && y > cy + offset) return true;

        return false;
    }

    // ================= MOVE =================
    public void move(SignalState state, int stopLine, int centerX, int centerY) {

        // Stop-line rule
// -------- STOP LINE CHECK --------
        if (!crossedStopLine) {

            if (direction == Direction.NORTH && y + speed >= stopLine) {
                crossedStopLine = true;
            }
            else if (direction == Direction.SOUTH && y - speed <= stopLine) {
                crossedStopLine = true;
            }
            else if (direction == Direction.WEST && x + speed >= stopLine) {
                crossedStopLine = true;
            }
            else if (direction == Direction.EAST && x - speed <= stopLine) {
                crossedStopLine = true;
            }
        }

// If NOT crossed stop line yet and signal is RED → stop
        if (!crossedStopLine && state == SignalState.RED) {
            return;
        }


        // -------- APPROACHING --------
        if (moveState == MoveState.APPROACHING) {
            moveStraight();

            if (shouldTurn(centerX, centerY)) {

                if (turnType == TurnType.STRAIGHT) {
                    moveState = MoveState.EXITING;
                }
                else if (turnType == TurnType.UTURN) {
                    moveState = MoveState.UTURN_FIRST_TURN;
                }
                else {
                    moveState = MoveState.TURNING; // LEFT / RIGHT
                }
            }

            return;
        }

        // -------- TURNING --------
// ---------- LEFT / RIGHT ----------
        if (moveState == MoveState.TURNING) {
            applyTurn();
            moveState = MoveState.EXITING;
            return;
        }

// ---------- U-TURN : FIRST LEFT ----------
        if (moveState == MoveState.UTURN_FIRST_TURN) {
            direction = turnLeft(direction);
            moveState = MoveState.UTURN_STRAIGHT;
            return;
        }

// ---------- U-TURN : STRAIGHT INTO OUTGOING LANE ----------
        if (moveState == MoveState.UTURN_STRAIGHT) {
            moveStraight();

            if (enteredOutgoingLane(centerX, centerY)) {
                moveState = MoveState.UTURN_SECOND_TURN;
            }
            return;
        }

// ---------- U-TURN : SECOND LEFT ----------
        if (moveState == MoveState.UTURN_SECOND_TURN) {
            direction = turnLeft(direction);
            moveState = MoveState.EXITING;
            return;
        }


        // -------- EXITING --------
        moveStraight();
    }

    // ================= HELPERS =================

    private void moveStraight() {
        if (direction == Direction.NORTH) y += speed;
        if (direction == Direction.SOUTH) y -= speed;
        if (direction == Direction.WEST)  x += speed;
        if (direction == Direction.EAST)  x -= speed;
    }

    private boolean shouldTurn(int cx, int cy) {

        if (direction == Direction.NORTH && y >= cy) return true;
        if (direction == Direction.SOUTH && y <= cy) return true;
        if (direction == Direction.WEST  && x >= cx) return true;
        if (direction == Direction.EAST  && x <= cx) return true;

        return false;
    }
    private void applyTurn() {

        if (turnType == TurnType.LEFT) {
            direction = turnLeft(direction);
        }
        else if (turnType == TurnType.RIGHT) {
            direction = turnRight(direction);
        }
        else if (turnType == TurnType.UTURN) {
            direction = turnLeft(turnLeft(direction));
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

    // ================= EXIT CHECK =================
    public boolean isOut(int w, int h) {
        return x < -60 || x > w + 60 || y < -60 || y > h + 60;
    }
}
