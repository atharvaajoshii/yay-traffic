import javax.swing.*;
import java.awt.*;

public class RoadPanel extends JPanel {

    SimulationEngine engine;

    public RoadPanel(SimulationEngine engine) {
        this.engine = engine;
        setBackground(Color.DARK_GRAY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        int roadW = w / 6;
        int roadH = h / 6;

        int cx = w / 2;
        int cy = h / 2;

        // ================= ROADS =================
        g.setColor(Color.GRAY);
        g.fillRect(cx - roadW / 2, 0, roadW, h);          // Vertical
        g.fillRect(0, cy - roadH / 2, w, roadH);          // Horizontal

        // ================= DIVIDERS =================
        g.setColor(Color.WHITE);

        // North divider
        g.fillRect(cx - 2, 0, 4, cy - roadH / 2);

        // South divider
        g.fillRect(cx - 2, cy + roadH / 2, 4, h);

        // West divider
        g.fillRect(0, cy - 2, cx - roadW / 2, 4);

        // East divider
        g.fillRect(cx + roadW / 2, cy - 2, w, 4);

        // ================= STOP LINES =================
        g.fillRect(cx - roadW / 2, cy - roadH / 2 - 6, roadW, 4); // North
        g.fillRect(cx - roadW / 2, cy + roadH / 2 + 2, roadW, 4); // South
        g.fillRect(cx - roadW / 2 - 6, cy - roadH / 2, 4, roadH); // West
        g.fillRect(cx + roadW / 2 + 2, cy - roadH / 2, 4, roadH); // East

        // ================= TRAFFIC LIGHTS =================
        drawSignal(g, engine.signals.get(Direction.NORTH),
                cx - roadW / 2 - 30, cy - roadH / 2 - 60);

        drawSignal(g, engine.signals.get(Direction.SOUTH),
                cx + roadW / 2 + 10, cy + roadH / 2 + 10);

        drawSignal(g, engine.signals.get(Direction.WEST),
                cx - roadW / 2 - 60, cy + roadH / 2 + 10);

        drawSignal(g, engine.signals.get(Direction.EAST),
                cx + roadW / 2 + 10, cy - roadH / 2 - 60);

        // ================= VEHICLES =================
        for (Vehicle v : engine.activeVehicles) {
            v.draw(g);
        }
    }

    // ================= SIGNAL DRAWING =================
    private void drawSignal(Graphics g, TrafficSignal signal, int x, int y) {

        g.setColor(Color.BLACK);
        g.fillRect(x, y, 20, 60);

        // Red
        g.setColor(signal.getState() == SignalState.RED ? Color.RED : Color.DARK_GRAY);
        g.fillOval(x + 3, y + 3, 14, 14);

        // Yellow
        g.setColor(signal.getState() == SignalState.YELLOW ? Color.YELLOW : Color.DARK_GRAY);
        g.fillOval(x + 3, y + 23, 14, 14);

        // Green
        g.setColor(signal.getState() == SignalState.GREEN ? Color.GREEN : Color.DARK_GRAY);
        g.fillOval(x + 3, y + 43, 14, 14);
    }
}
