class AssemblyReader {

    private final static int BITMASK_THREE = 7;
    private final static int BITMASK_EIGHT = 255;

    private final static int OPERATION_ADD = 0;
    private final static int OPERATION_ADDI = 1;
    private final static int OPERATION_MUL = 2;
    private final static int OPERATION_INV = 3;
    private final static int OPERATION_BEQ = 4;
    private final static int OPERATION_LW = 5;
    private final static int OPERATION_SW = 6;
    private final static int OPERATION_HALT = 7;

    private final static int SHIFTS_OPERATION = 17;
    private final static int SHIFTS_DESTINATION = 14;
    private final static int SHIFTS_SOURCE = 11;
    private final static int SHIFTS_TARGET = 8;

    /**
     * Creates instruction from hex-based assembly instruction.
     *
     * @param hexValue int
     * @return Instruction
     */
    static Instruction createInstruction(int hexValue) {
        final Instruction instruction = new Instruction();

        instruction.setOperation(matchOperation((hexValue >> SHIFTS_OPERATION) & BITMASK_THREE));
        if (instruction.getOperation() != null) {
            setInstructionRegisters(instruction, hexValue);
        } else {
            return null;
        }

        final byte signedImmediateValue = (byte) (hexValue & BITMASK_EIGHT);
        if (signedImmediateValue != 0) {
            instruction.setSignedImmediateValue((int) signedImmediateValue);
        } else {
            instruction.setSignedImmediateValue(null);
        }

        final int unsignedImmediateValue = (hexValue & BITMASK_EIGHT);
        if (unsignedImmediateValue != 0) {
            instruction.setUnsignedImmediateValue(unsignedImmediateValue);
        } else {
            instruction.setUnsignedImmediateValue(null);
        }

        if (1 == 0) {
            System.out.println();
            String binString = Integer.toBinaryString(hexValue);
            if (binString.length() < 20) {
                for (int i = binString.length(); i < 20; i++) {
                    binString = "0" + binString;
                }
            }
            System.out.println(binString);
            System.out.println(instruction.toString());
        }

        return instruction;
    }

    /**
     * Determines which registers to use and assigns their appropriate values.
     *
     * @param instruction Instruction
     * @param hexValue int
     */
    private static void setInstructionRegisters(Instruction instruction, int hexValue) {
        switch (instruction.getOperation()) {
            case ADD:
                instruction.setDestination(matchRegister((hexValue >> SHIFTS_DESTINATION) & BITMASK_THREE));
                instruction.setTarget(matchRegister((hexValue >> SHIFTS_TARGET) & BITMASK_THREE));
                instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                break;
            case ADDI:
                instruction.setDestination(matchRegister((hexValue >> SHIFTS_DESTINATION) & BITMASK_THREE));
                instruction.setTarget(null);
                instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                break;
            case MUL:
                instruction.setDestination(matchRegister((hexValue >> SHIFTS_DESTINATION) & BITMASK_THREE));
                instruction.setTarget(null);
                instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                break;
            case INV:
                instruction.setDestination(matchRegister((hexValue >> SHIFTS_DESTINATION) & BITMASK_THREE));
                instruction.setTarget(null);
                instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                break;
            case BEQ:
                instruction.setDestination(null);
                instruction.setTarget(matchRegister((hexValue >> SHIFTS_TARGET) & BITMASK_THREE));
                instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                break;
            case LW:
                instruction.setDestination(matchRegister((hexValue >> SHIFTS_DESTINATION) & BITMASK_THREE));
                instruction.setTarget(matchRegister((hexValue >> SHIFTS_TARGET) & BITMASK_THREE));
                instruction.setSource(null);
                break;
            case SW:
                instruction.setDestination(null);
                instruction.setSource(matchRegister((hexValue >> SHIFTS_SOURCE) & BITMASK_THREE));
                instruction.setTarget(matchRegister((hexValue >> SHIFTS_TARGET) & BITMASK_THREE));
                break;
            case HALT:
                instruction.setDestination(null);
                instruction.setSource(null);
                instruction.setTarget(null);
                break;
        }
    }

    /**
     * Determines which Operation to use based on operator code.
     *
     * @param operation int
     * @return Operation
     */
    private static Operation matchOperation(int operation) {
        switch (operation) {
            case OPERATION_ADD:
                return Operation.ADD;
            case OPERATION_ADDI:
                return Operation.ADDI;
            case OPERATION_MUL:
                return Operation.MUL;
            case OPERATION_INV:
                return Operation.INV;
            case OPERATION_BEQ:
                return Operation.BEQ;
            case OPERATION_LW:
                return Operation.LW;
            case OPERATION_SW:
                return Operation.SW;
            case OPERATION_HALT:
                return Operation.HALT;
            default:
                return null;
        }
    }

    /**
     * Determines which register to use based on register code.
     *
     * @param register int
     * @return Register
     */
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
