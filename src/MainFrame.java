import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        SimulationEngine engine = new SimulationEngine();
        RoadPanel road = new RoadPanel(engine);
        ControlPanel controls = new ControlPanel(engine, road);

        setLayout(new BorderLayout());

        add(road, BorderLayout.CENTER);
        add(controls, BorderLayout.SOUTH);

        // Base window size 
        setSize(900, 900);

        // Minimum size safety
        setMinimumSize(new Dimension(700, 700));

        // Allow resizing
        setResizable(true);

        // Center on screen
        setLocationRelativeTo(null);

        setTitle("Traffic Light Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        setTitle("Traffic Light Simulator");

    }
}