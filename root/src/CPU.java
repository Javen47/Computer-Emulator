public class CPU {

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
    private int memoryAccessWaitCounters = 1;
    private State currentState = State.FETCH;

    public CPU(InstructionMemory instructionMemory, DataMemory dataMemory) {
        this.instructionMemory = instructionMemory;
        this.dataMemory = dataMemory;
    }

    /**
     * Sets all register values to be 0.
     */
    public void reset() {
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
    public void set(Register register, int hexByte) {
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
    public int get(Register register) {
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
    public String dump() {
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
    public void tick() {
        //Fetch instructions
        if (this.currentState == State.FETCH){
          this.memoryAccessWaitCounters = 1;
          this.currentState = State.WAIT;
        }

        //Wait instructions
        if (this.memoryAccessWaitCounters == 5) {
            final Instruction instruction = AssemblyReader.createInstruction(instructionMemory.get(PC));
            //System.out.println(instruction.toString());
            if (instruction != null) {
                switch (instruction.getOperation()) {
                    case LW:
                        loadWord(instruction);
                        break;
                    case SW:
                        saveWord(instruction);
                        break;
                }
            }
            this.currentState = State.FETCH;
            this.PC++;
        } else {
            this.memoryAccessWaitCounters++;
        }
    }

    /**
     * Loads a value into a destination register from the DataMemory at the index stored in a target register.
     *
     * @param instruction Instruction
     */
    private void loadWord(Instruction instruction) {
        this.set(
                instruction.getDestination(),
                this.dataMemory.get(this.get(instruction.getTarget())));
    }

    /**
     * Stores a value into DataMemory from a source register at the index stored in a target register.
     * @param instruction
     */
    private void saveWord(Instruction instruction) {
        this.dataMemory.set(
                this.get(instruction.getTarget()),
                1,
                this.get(instruction.getSource()));
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

}
