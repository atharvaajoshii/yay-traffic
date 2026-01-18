import java.util.*;

public class Lane {
    Queue<Vehicle> queue = new LinkedList<>();

    public void addVehicle(Vehicle v) {
        queue.offer(v);
    }

    public Queue<Vehicle> getVehicles() {
        return queue;
    }
}
