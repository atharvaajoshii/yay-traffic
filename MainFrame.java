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

        setTitle("Traffic Light Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
