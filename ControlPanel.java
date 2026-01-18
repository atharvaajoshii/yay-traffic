import javax.swing.*;

public class ControlPanel extends JPanel {

    public ControlPanel(SimulationEngine engine, RoadPanel road) {

        JComboBox<String> type = new JComboBox<>(new String[]{"Normal", "Emergency"});
        JComboBox<Direction> dir = new JComboBox<>(Direction.values());
        JComboBox<TurnType> turn = new JComboBox<>(TurnType.values());

        JButton add = new JButton("Add Vehicle");
        JButton play = new JButton("Play");
        JButton pause = new JButton("Pause");
        JButton reset = new JButton("Reset");

        Timer timer = new Timer(30, e -> {
            engine.update(road.getWidth(), road.getHeight());
            road.repaint();
        });

        add.addActionListener(e -> {
            int w = road.getWidth();
            int h = road.getHeight();

            Vehicle v;
            if (type.getSelectedItem().equals("Emergency")) {
                v = new EmergencyVehicle(
                        (Direction) dir.getSelectedItem(),
                        (TurnType) turn.getSelectedItem(),
                        w, h
                );
            } else {
                v = new Vehicle(
                        (Direction) dir.getSelectedItem(),
                        (TurnType) turn.getSelectedItem(),
                        w, h
                );
            }
            engine.addVehicle(v);
        });

        play.addActionListener(e -> timer.start());
        pause.addActionListener(e -> timer.stop());
        reset.addActionListener(e -> engine.reset());

        add(type);
        add(dir);
        add(turn);
        add(add);
        add(play);
        add(pause);
        add(reset);
    }
}
