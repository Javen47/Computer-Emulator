public class AssemblyReader {

    private final static int BITMASK_THREE = 7;
    private final static int BITMASK_EIGHT = 8;
    private final static int OPERATION_LW = 5;
    private final static int OPERATION_SW = 6;
    private final static int SHIFTS_OPERATION = 17;
    private final static int SHIFTS_DESTINATION = 14;
    private final static int SHIFTS_SOURCE = 11;
    private final static int SHIFTS_TARGET = 8;

    public static Instruction createInstruction(int hexValue) {
        final Instruction instruction = new Instruction();

        instruction.setOperation(matchOperation((hexValue >> SHIFTS_OPERATION) & BITMASK_THREE));
        if (instruction.getOperation() != null) {
            switch (instruction.getOperation()) {
                case LW:
                    instruction.setDestination(matchRegister((hexValue >> SHIFTS_DESTINATION) & BITMASK_THREE));
                    instruction.setTarget(matchRegister((hexValue >> SHIFTS_TARGET) & BITMASK_THREE));
                    instruction.setSource(null);
                    break;
                case SW:
                    instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                    instruction.setTarget(matchRegister((hexValue >> SHIFTS_TARGET) & BITMASK_THREE));
                    instruction.setDestination(null);
                    break;
            }
        } else {
            return null;
        }

        final int immediateValue = (hexValue & BITMASK_EIGHT);
        if (immediateValue != 0) {
            instruction.setImmediateValue(immediateValue);
        } else {
            instruction.setImmediateValue(null);
        }

        return instruction;
    }

    private static Operation matchOperation(int operation) {
        switch (operation) {
            case OPERATION_LW:
                return Operation.LW;
            case OPERATION_SW:
                return Operation.SW;
            default:
                return null;
        }
    }

    private static Register matchRegister(int register) {
        switch (register) {
            case 0:
                return Register.RA;
            case 1:
                return Register.RB;
            case 2:
                return Register.RC;
            case 3:
                return Register.RD;
            case 4:
                return Register.RE;
            case 5:
                return Register.RF;
            case 6:
                return Register.RG;
            case 7:
                return Register.RH;
            default:
                return null;
        }
    }

}
