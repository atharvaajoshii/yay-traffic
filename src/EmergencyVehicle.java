public class EmergencyVehicle extends Vehicle {
    public EmergencyVehicle(Direction dir, TurnType turn, int w, int h) {
        super(dir, turn, w, h);
        this.emoji = "🚑";
    }
}
