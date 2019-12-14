import java.io.File;

class CommandRunner {

    private Clock clock;
    private DataMemory dataMemory;
    private InstructionMemory instructionMemory;
    private CPU cpu;
    private Cache cache;
    private IO inputOutput;

    CommandRunner(Clock clock, CPU cpu, DataMemory dataMemory, InstructionMemory instructionMemory, Cache cache, IO inputOutput) {
        this.clock = clock;
        this.dataMemory = dataMemory;
        this.instructionMemory = instructionMemory;
        this.cpu = cpu;
        this.cache = cache;
        this.inputOutput = inputOutput;
    }

    /**
     * Determines what functions to run for the passed in command.
     *
     * @param command Command
     * @return String of output (if any)
     */
    String runCommand(Command command) {
        switch (command.getDevice()) {
            case CPU:
                return runCPUCommand(command);
            case DATA_MEMORY:
                return runDataMemoryCommand(command);
            case INSTRUCTION_MEMORY:
                return runInstructionMemoryCommand(command);
            case CLOCK:
                return runClockCommand(command);
            case CACHE:
                return runCacheCommand(command);
            case IO:
                return runIOCommand(command);
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given IO command.
     *
     * @param command IO Command
     * @return String of output (if any)
     */
    private String runIOCommand(Command command) {
        switch (command.getOption()) {
            case RESET:
                this.inputOutput.reset();
                return "";
            case LOAD:
                this.inputOutput.load(new File(command.getParameters()[0]));
                return "";
            case DUMP:
                return this.inputOutput.dump();
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given Cache command.
     *
     * @param command Cache Command
     * @return String of output (if any)
     */
    private String runCacheCommand(Command command) {
        switch (command.getOption()) {
            case ON:
                this.cache.on();
                return "";
            case OFF:
                this.cache.off();
                return "";
            case RESET:
                this.cache.reset();
                return "";
            case DUMP:
                return this.cache.dump();
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given CPU command.
     *
     * @param command CPU Command
     * @return String of output (if any)
     */
    private String runCPUCommand(Command command) {
        switch (command.getOption()) {
            case RESET:
                this.cpu.reset();
                return "";
            case SET:
                this.cpu.setRegisterValue(Register.valueOf(command.getParameters()[1]),
                        Integer.decode(command.getParameters()[2]));
                return "";
            case DUMP:
                return this.cpu.dump();
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given dataMemory command.
     *
     * @param command DataMemory Command
     * @return String of output (if any)
     */
    private String runDataMemoryCommand(Command command) {
        switch (command.getOption()) {
            case CREATE:
                this.dataMemory.create(Integer.decode(command.getParameters()[0]));
                return "";
            case RESET:
                this.dataMemory.reset();
                return "";
            case DUMP:
                return this.dataMemory.dump(Integer.decode(command.getParameters()[0]),
                        Integer.decode(command.getParameters()[1]));
            case SET:
                if (command.getParameters()[1].equals("file")) {
                    this.dataMemory.set(Integer.decode(command.getParameters()[0]),
                            new File(command.getParameters()[2]));
                } else {
                    this.dataMemory.set(Integer.decode(command.getParameters()[0]),
                            Integer.decode(command.getParameters()[1]),
                            convertStringParametersToBytes(command.getParameters()));
                }
                return "";
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given clock command.
     *
     * @param command Clock Command
     * @return String of output (if any)
     */
    private String runClockCommand(Command command) {
        switch (command.getOption()) {
            case RESET:
                this.clock.reset();
                return "";
            case TICK:
                this.clock.tick(Long.valueOf(command.getParameters()[0]));
                return "";
            case DUMP:
                return this.clock.dump();
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given instruction memory command.
     *
     * @param command InstructionMemory Command
     * @return String of output (if any)
     */
    private String runInstructionMemoryCommand(Command command) {
        switch (command.getOption()) {
            case CREATE:
                this.instructionMemory.create(Integer.decode(command.getParameters()[0]));
                return "";
            case RESET:
                this.instructionMemory.reset();
                return "";
            case DUMP:
                return this.instructionMemory.dump(Integer.decode(command.getParameters()[0]),
                        Integer.decode(command.getParameters()[1]));
            case SET:
                if (command.getParameters()[1].equals("file")) {
                    this.instructionMemory.set(Integer.decode(command.getParameters()[0]),
                            new File(command.getParameters()[2]));
                } else {
                    this.instructionMemory.set(Integer.decode(command.getParameters()[0]),
                            Integer.decode(command.getParameters()[1]),
                            convertStringParametersToBytes(command.getParameters()));
                }
                return "";
            default:
                return "";
        }
    }

    private Integer[] convertStringParametersToBytes(String[] parameters) {
        final Integer[] hexBytes = new Integer[parameters.length];
        for (int i = 2; i < parameters.length; i++) {
            hexBytes[i - 2] = Integer.decode(parameters[i]);
        }
        return hexBytes;
    }

}
