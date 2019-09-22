import java.io.File;

public class CommandRunner {

    private Clock clock;
    private Memory memory;
    private CPU cpu;

    public CommandRunner(Clock clock, CPU cpu, Memory memory) {
        this.clock = clock;
        this.memory = memory;
        this.cpu = cpu;
    }

    /**
     * Determines what functions to run for the passed in command.
     *
     * @param command Command
     * @return String of output (if any)
     */
    public String runCommand(Command command) {
        switch (command.getDevice()) {
            case CPU:
                return runCPUCommand(command);
            case MEMORY:
                return runMemoryCommand(command);
            case CLOCK:
                return runClockCommand(command);
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
                this.cpu.set(Register.valueOf(command.getParameters()[1]),
                        Integer.decode(command.getParameters()[2]));
                return "";
            case DUMP:
                return this.cpu.dump();
            default:
                return "";
        }
    }

    /**
     * Determines what option to run for given memory command.
     *
     * @param command Memory Command
     * @return String of output (if any)
     */
    private String runMemoryCommand(Command command) {
        switch (command.getOption()) {
            case CREATE:
                this.memory.create(Integer.decode(command.getParameters()[0]));
                return "";
            case RESET:
                this.memory.reset();
                return "";
            case DUMP:
                return this.memory.dump(Integer.decode(command.getParameters()[0]),
                        Integer.decode(command.getParameters()[1]));
            case SET:
                if (command.getParameters()[1].equals("file")) {
                    this.memory.set(Integer.decode(command.getParameters()[0]),
                            new File(command.getParameters()[2]));
                } else {
                    this.memory.set(Integer.decode(command.getParameters()[0]), Integer.decode(command.getParameters()[1]),
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

    private Integer[] convertStringParametersToBytes(String[] parameters) {
        final Integer[] hexBytes = new Integer[parameters.length];
        for (int i = 2; i < parameters.length; i++) {
            hexBytes[i - 2] = Integer.decode(parameters[i]);
        }
        return hexBytes;
    }

}
