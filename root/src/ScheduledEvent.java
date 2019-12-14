public class ScheduledEvent {

    private int clockTick;
    private String operation;
    private int address;
    private int value;

    public int getClockTick() {
        return clockTick;
    }

    public void setClockTick(int clockTick) {
        this.clockTick = clockTick;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ScheduledEvent{" +
                "clockTick=" + clockTick +
                ", operation='" + operation + '\'' +
                ", address=" + address +
                ", value=" + value +
                '}';
    }
}
