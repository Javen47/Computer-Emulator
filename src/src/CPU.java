public class CPU {

    private Memory memory;

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

    public CPU(Memory memory) {
        this.memory = memory;
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
     * Shifts register values with the value stored in memory at the PC index, increments PC.
     */
    public void tick() {
        shiftRegisterValues(this.memory.get(this.PC));
        this.PC++;
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
     * Shifts all values stored in registers to the next register. The last register value gets deleted.
     *
     * @param newValue int
     */
    private void shiftRegisterValues(int newValue) {
        this.RH = this.RG;
        this.RG = this.RF;
        this.RF = this.RE;
        this.RE = this.RD;
        this.RD = this.RC;
        this.RC = this.RB;
        this.RB = this.RA;
        this.RA = newValue;
    }

}
