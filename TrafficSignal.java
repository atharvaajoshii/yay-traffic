public class TrafficSignal {

    private SignalState state = SignalState.RED;

    public SignalState getState() {
        return state;
    }

    public void setState(SignalState state) {
        this.state = state;
    }
}
