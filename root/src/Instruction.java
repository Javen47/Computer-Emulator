public class Instruction {

    private Operation operation;
    private Register destination;
    private Register source;
    private Register target;
    private Integer immediateValue;

    public Instruction() {

    }

    public Instruction(Operation operation, Register target) {
        this.operation = operation;
        this.target = target;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Register getDestination() {
        return destination;
    }

    public void setDestination(Register destination) {
        this.destination = destination;
    }

    public Register getSource() {
        return source;
    }

    public void setSource(Register source) {
        this.source = source;
    }

    public Register getTarget() {
        return target;
    }

    public void setTarget(Register target) {
        this.target = target;
    }

    public Integer getImmediateValue() {
        return immediateValue;
    }

    public void setImmediateValue(Integer immediateValue) {
        this.immediateValue = immediateValue;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "operation=" + operation +
                ", destination=" + destination +
                ", source=" + source +
                ", target=" + target +
                ", immediateValue=" + immediateValue +
                '}';
    }
}
