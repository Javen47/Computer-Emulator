class CPU {

    //Constants
    private static final Boolean DEBUG_MODE = false;
    private static final int EXE_WAIT_TIME_ONE = 1;
    private static final int EXE_WAIT_TIME_TWO = 2;
    private static final int EXE_WAIT_TIME_FIVE = 5;

    //Registers
    private int PC = 0;
    private int RA = 0;
    private int RB = 0;
    private int RC = 0;
    private int RD = 0;
    private int RE = 0;
    private int RF = 0;
    private int RG = 0;
    private int RH = 0;

    //Memory
    private DataMemory dataMemory;
    private InstructionMemory instructionMemory;

    //Memory Access
    private int exeCounters = 1;
    private int exeCountersMax = -1;
    private State currentState = State.FETCH;

    CPU(InstructionMemory instructionMemory, DataMemory dataMemory) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
    }

    /**
     * Sets all register values to be 0.
     */
    void reset() {
        this.PC = 0;
        this.RA = 0;
        this.RB = 0;
        this.RC = 0;
        this.RD = 0;
        this.RE = 0;
        this.RF = 0;
        this.RG = 0;
        this.RH = 0;
    }

    /**
     * Sets a passed in value to it's respective register.
     *
     * @param register Register
     * @param hexByte int
     */
     void setRegisterValue(Register register, int hexByte) {
        switch (register) {
            case PC:
                this.PC = hexByte;
                break;
            case RA:
                this.RA = hexByte;
                break;
            case RB:
                this.RB = hexByte;
                break;
            case RC:
                this.RC = hexByte;
                break;
            case RD:
                this.RD = hexByte;
                break;
            case RE:
                this.RE = hexByte;
                break;
            case RF:
                this.RF = hexByte;
                break;
            case RG:
                this.RG = hexByte;
                break;
            case RH:
                this.RH = hexByte;
                break;
        }
    }

    /**
     * Retrieves the value stored in a given register.
     *
     * @param register Register
     * @return int
     */
    private int getRegisterValue(Register register) {
        switch (register) {
            case PC:
                return this.PC;
            case RA:
                return this.RA;
            case RB:
                return this.RB;
            case RC:
                return this.RC;
            case RD:
                return this.RD;
            case RE:
                return this.RE;
            case RF:
                return this.RF;
            case RG:
                return this.RG;
            case RH:
                return this.RH;
            default:
                return -1;
        }
    }

    /**
     * Creates a string containing all register values.
     *
     * @return String
     */
    String dump() {
        final StringBuilder builder = new StringBuilder();
        builder.append("PC: ");
        builder.append(createDumpRow(this.PC));

        builder.append("RA: ");
        builder.append(createDumpRow(this.RA));

        builder.append("RB: ");
        builder.append(createDumpRow(this.RB));

        builder.append("RC: ");
        builder.append(createDumpRow(this.RC));

        builder.append("RD: ");
        builder.append(createDumpRow(this.RD));

        builder.append("RE: ");
        builder.append(createDumpRow(this.RE));

        builder.append("RF: ");
        builder.append(createDumpRow(this.RF));

        builder.append("RG: ");
        builder.append(createDumpRow(this.RG));

        builder.append("RH: ");
        builder.append(createDumpRow(this.RH));

        return builder.toString();
    }

    /**
     * Calls appropriate instructions at PC, increments PC.
     */
    void tick() {
        //HALT instructions
        if (this.currentState == State.HALT) {
            return;
        }

        //Fetch instructions
        if (this.currentState == State.FETCH){
            final Instruction instruction = AssemblyReader.createInstruction(this.instructionMemory.get(this.PC));
            this.exeCounters = 1;
            this.currentState = State.WAIT;
            if (instruction != null) {
                this.exeCountersMax = this.determineMaxExeCounters(instruction);
            }
        }

        //Wait instructions
        if (this.exeCounters == this.exeCountersMax) {
            final Instruction instruction = AssemblyReader.createInstruction(this.instructionMemory.get(this.PC));
            this.determineOperationFunction(instruction);
        } else {
            this.exeCounters++;
        }
    }

    /**
     * Determines and calls the appropriate operation based on instruction.
     *
     * @param instruction Instruction
     */
    private void determineOperationFunction(Instruction instruction) {
        if (instruction != null) {
            switch (instruction.getOperation()) {
                case LW:
                    this.loadWord(instruction);
                    break;
                case SW:
                    this.saveWord(instruction);
                    break;
                case ADD:
                    this.add(instruction);
                    break;
                case ADDI:
                    this.addImmediate(instruction);
                    break;
                case HALT:
                    this.halt();
                    return;
                case MUL:
                    this.multiply(instruction);
                    break;
                case BEQ:
                    this.branchIfEqual(instruction);
                    return;
                case INV:
                    this.invert(instruction);
                    break;
            }
        }
        this.currentState = State.FETCH;
        this.PC++;
    }

    /**
     * Loads a value into a destination register from the DataMemory at the index stored in a target register.
     *
     * @param instruction Instruction
     */
    private void loadWord(Instruction instruction) {
        this.setRegisterValue(
                instruction.getDestination(),
                this.dataMemory.get(this.getRegisterValue(instruction.getTarget())));
    }

    /**
     * Stores a value into DataMemory from a source register at the index stored in a target register.
     * @param instruction instruction
     */
    private void saveWord(Instruction instruction) {
        this.dataMemory.set(
                this.getRegisterValue(instruction.getTarget()), 1,
                this.getRegisterValue(instruction.getSource()));
    }

    /**
     * Adds the source & target register words, storing the result in the destination register.
     *
     * @param instruction Instruction
     */
    private void add(Instruction instruction) {
        final int addedValue = this.getRegisterValue(instruction.getSource()) + this.getRegisterValue(instruction.getTarget());
        this.setRegisterValue(instruction.getDestination(), addedValue);

        if (DEBUG_MODE) {
            System.out.println("adding: " + addedValue);
        }
    }

    /**
     * Adds the source register & immediate value words, storing the result in the destination register.
     *
     * @param instruction Instruction
     */
    private void addImmediate(Instruction instruction) {
        final int addedValue = this.getRegisterValue(instruction.getSource()) + instruction.getSignedImmediateValue();
        this.setRegisterValue(instruction.getDestination(), addedValue);

        if (DEBUG_MODE) {
            System.out.println("adding i: " + addedValue);
            System.out.println("new reg value: " + getRegisterValue(instruction.getDestination()));
        }
    }

    /**
     * Takes the upper 4 bits and lower 4 bits of the source register, multiplies those values together,
     * and stores the result in the destination register.
     *
     * @param instruction Instruction
     */
    private void multiply(Instruction instruction) {
        String binString = padBinaryString(Integer.toBinaryString(getRegisterValue(instruction.getSource())));
        final int lowerNibble = Integer.parseInt(binString.substring(4, 8) ,2);
        final int upperNibble = Integer.parseInt(binString.substring(0, 4) ,2);
        this.setRegisterValue(instruction.getDestination(), lowerNibble * upperNibble);

        if (DEBUG_MODE) {
            System.out.println("multiply to int: " + lowerNibble * upperNibble);
        }
    }

    /**
     * Inverts all the bits in the source register word, storing the result in the destination register
     *
     * @param instruction Instruction
     */
    private void invert(Instruction instruction) {
        final String binString = padBinaryString(Integer.toBinaryString(getRegisterValue(instruction.getSource())));
        final StringBuilder invBuilder = new StringBuilder();
        for (int i = 0; i < binString.length(); i++) {
            if (binString.charAt(i) == '1') {
                invBuilder.append('0');
            } else {
                invBuilder.append('1');
            }
        }
        this.setRegisterValue(instruction.getDestination(), Integer.parseInt(invBuilder.toString(), 2));

        if (DEBUG_MODE) {
            System.out.println("inverting: " + binString);
        }
    }

    /**
     * If the source and target registers are equal, assign the PC to the immediate-specified
     * imemory address, otherwise increment the PC.
     *
     * @param instruction Instruction
     */
    private void branchIfEqual(Instruction instruction) {
        if (getRegisterValue(instruction.getSource()) == getRegisterValue(instruction.getTarget())) {
            this.PC = instruction.getUnsignedImmediateValue();
        } else {
            this.PC++;
        }
        if (DEBUG_MODE) {
            System.out.println("BEQ: " + getRegisterValue(instruction.getSource()) + ", " + getRegisterValue(instruction.getTarget()));
        }
    }

    /**
     * Halts execution of processor.
     */
    private void halt() {
        this.PC++;
        this.currentState = State.HALT;

        if (DEBUG_MODE) {
            System.out.println("Halting");
        }
    }

    /**
     * Converts integers into 0x## hex format.
     *
     * @param value int
     * @return Hex String
     */
    private String createDumpRow(int value) {
        return "0x" + String.format("%02x", value).toUpperCase() + "\n";
    }

    /**
     * Determines how many clock cycles a particular instruction operation will take.
     *
     * @param instruction Instruction
     * @return int of clock cycles
     */
    private int determineMaxExeCounters(Instruction instruction) {
        switch (instruction.getOperation()) {
            case ADD:
                return EXE_WAIT_TIME_ONE;
            case ADDI:
                return EXE_WAIT_TIME_ONE;
            case MUL:
                return EXE_WAIT_TIME_TWO;
            case INV:
                return EXE_WAIT_TIME_ONE;
            case BEQ:
                if (this.getRegisterValue(instruction.getSource()) == this.getRegisterValue(instruction.getTarget())) {
                    return EXE_WAIT_TIME_TWO;
                } else {
                    return EXE_WAIT_TIME_ONE;
                }
            case LW:
                return EXE_WAIT_TIME_FIVE;
            case SW:
                return EXE_WAIT_TIME_FIVE;
            case HALT:
                return EXE_WAIT_TIME_ONE;
        }
        return -1;
    }

    /**
     * Makes sure a binary string has a length of eight. Pads leading zeros.
     *
     * @param binString String
     * @return Padded string
     */
    private String padBinaryString(String binString) {
        if (binString.length() < 8) {
            for (int i = binString.length(); i < 8; i++) {
                binString = "0" + binString;
            }
        }
        return binString;
    }

}
