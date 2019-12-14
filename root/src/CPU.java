class CPU {

    //Constants
    private static final int EXE_WAIT_TIME_ONE = 1;
    private static final int EXE_WAIT_TIME_TWO = 2;

    //Registers
    private int PC = 0;
    private int TC = 0;
    private int RA = 0;
    private int RB = 0;
    private int RC = 0;
    private int RD = 0;
    private int RE = 0;
    private int RF = 0;
    private int RG = 0;
    private int RH = 0;

    //Memory
    private Cache cache;
    private InstructionMemory instructionMemory;
    private IO inputOutput;

    //Memory Access
    private int exeCounters = 1;
    private int exeCountersMax = -1;
    private State currentState = State.FETCH;

    CPU(InstructionMemory instructionMemory, Cache cache, IO inputOutput) {
        this.instructionMemory = instructionMemory;
        this.cache = cache;
        this.inputOutput = inputOutput;
    }

    /**
     * Sets all register values to be 0.
     */
    void reset() {
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Resetting CPU");
        }

        this.PC = 0;
        this.TC = 0;
        this.RA = 0;
        this.RB = 0;
        this.RC = 0;
        this.RD = 0;
        this.RE = 0;
        this.RF = 0;
        this.RG = 0;
        this.RH = 0;
        this.currentState = State.FETCH;
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
            case TC:
                this.TC = hexByte;
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
            case TC:
                return this.TC;
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
        builder.append(cs3421_emul.createDumpRow(this.PC));
        builder.append('\n');

        builder.append("RA: ");
        builder.append(cs3421_emul.createDumpRow(this.RA));
        builder.append('\n');

        builder.append("RB: ");
        builder.append(cs3421_emul.createDumpRow(this.RB));
        builder.append('\n');

        builder.append("RC: ");
        builder.append(cs3421_emul.createDumpRow(this.RC));
        builder.append('\n');

        builder.append("RD: ");
        builder.append(cs3421_emul.createDumpRow(this.RD));
        builder.append('\n');

        builder.append("RE: ");
        builder.append(cs3421_emul.createDumpRow(this.RE));
        builder.append('\n');

        builder.append("RF: ");
        builder.append(cs3421_emul.createDumpRow(this.RF));
        builder.append('\n');

        builder.append("RG: ");
        builder.append(cs3421_emul.createDumpRow(this.RG));
        builder.append('\n');

        builder.append("RH: ");
        builder.append(cs3421_emul.createDumpRow(this.RH));
        builder.append('\n');

        builder.append("TC: ");
        builder.append(this.TC);
        builder.append('\n');

        //builder.append(cache.dump());

        return builder.toString();
    }

    /**
     * Calls appropriate instructions at PC, increments PC.
     */
    void tick() {

        //Checking IO Scheduled Events
        this.inputOutput.checkEventSchedule();

        //HALT instructions
        if (this.currentState == State.HALT) {
            return;
        }

        //Increment Tick Counter if not halted
        this.TC++;

        //Instruction
        final Instruction instruction = AssemblyReader.createInstruction(this.instructionMemory.get(this.PC));

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Performing tick for operation: " + instruction.getOperation());
        }

        //Fetch instructions
        if (this.currentState == State.FETCH){
            this.exeCounters = 1;
            this.currentState = State.WAIT;
            if (instruction != null) {
                this.exeCountersMax = this.determineMaxExeCounters(instruction);
            }
        }

        //Wait instructions
        if (this.exeCounters == this.exeCountersMax) {
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

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Determining operation for instruction: " + instruction);
        }

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

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Incrementing PC for operation " + instruction.getOperation() + '\n');
        }

    }

    /**
     * Loads a value into a destination register from the DataMemory at the index stored in a target register.
     *
     * @param instruction Instruction
     */
    private void loadWord(Instruction instruction) {
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Loading word for instruction: " + instruction);
        }
        this.setRegisterValue(
                instruction.getDestination(),
                this.cache.read(this.getRegisterValue(instruction.getTarget())));
    }

    /**
     * Stores a value into DataMemory from a source register at the index stored in a target register.
     *
     * @param instruction instruction
     */
    private void saveWord(Instruction instruction) {
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Saving word for instruction: " + instruction);
        }
        this.cache.write(
                this.getRegisterValue(instruction.getTarget()),
                this.getRegisterValue(instruction.getSource()));
    }

    /**
     * Adds the source & target register words, storing the result in the destination register.
     *
     * @param instruction Instruction
     */
    private void add(Instruction instruction) {
        final int addedValue = this.getRegisterValue(instruction.getSource()) + this.getRegisterValue(instruction.getTarget());
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("adding: " + addedValue);
        }
        this.setRegisterValue(instruction.getDestination(), addedValue);
    }

    /**
     * Adds the source register & immediate value words, storing the result in the destination register.
     *
     * @param instruction Instruction
     */
    private void addImmediate(Instruction instruction) {
        int addedValue = this.getRegisterValue(instruction.getSource()) + instruction.getSignedImmediateValue();
        if (addedValue == -1) {
            addedValue = 255;
        }

        this.setRegisterValue(instruction.getDestination(), addedValue);

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Adding signed immediate: " + addedValue);
            System.out.println("New register value at " + instruction.getDestination() + ": " + getRegisterValue(instruction.getDestination()));
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

        if (cs3421_emul.DEBUG_MODE) {
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

        if (cs3421_emul.DEBUG_MODE) {
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
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("BEQ: " + getRegisterValue(instruction.getSource()) + ", " + getRegisterValue(instruction.getTarget()));
        }
    }

    /**
     * Halts execution of processor.
     */
    private void halt() {
        this.PC++;
        this.currentState = State.HALT;

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("\nHalting");
        }
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
                return this.cache.determineCacheOperationTickTime(this.getRegisterValue(instruction.getTarget()), true);
            case SW:
                return this.cache.determineCacheOperationTickTime(this.getRegisterValue(instruction.getTarget()), false);
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
