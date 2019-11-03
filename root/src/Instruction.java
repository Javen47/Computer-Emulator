public class Instruction {

    private Operation operation;
    private Register destination;
    private Register source;
    private Register target;
    private Integer signedImmediateValue;
    private Integer unsignedImmediateValue;

    Instruction() {

    }

    public Instruction(Operation operation, Register target) {
        this.operation = operation;
        this.target = target;
    }

    Operation getOperation() {
        return operation;
    }

    void setOperation(Operation operation) {
        this.operation = operation;
    }

    Register getDestination() {
        return destination;
    }

    void setDestination(Register destination) {
        this.destination = destination;
    }

    Register getSource() {
        return source;
    }

    void setSource(Register source) {
        this.source = source;
    }

    Register getTarget() {
        return target;
    }

    void setTarget(Register target) {
        this.target = target;
    }

    public Integer getSignedImmediateValue() {
        return signedImmediateValue;
    }

    public void setSignedImmediateValue(Integer signedImmediateValue) {
        this.signedImmediateValue = signedImmediateValue;
    }

    public Integer getUnsignedImmediateValue() {
        return unsignedImmediateValue;
    }

    public void setUnsignedImmediateValue(Integer unsignedImmediateValue) {
        this.unsignedImmediateValue = unsignedImmediateValue;
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "operation=" + operation +
                ", destination=" + destination +
                ", source=" + source +
                ", target=" + target +
                ", signedImmediateValue=" + signedImmediateValue +
                ", unsignedImmediateValue=" + unsignedImmediateValue +
                '}';
    }


}
