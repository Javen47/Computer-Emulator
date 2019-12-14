import java.util.ArrayList;

/**
 * @author Javen Zamojcin
 * Class: Computer Organization
 * Date created: 9/22/2019
 */
public class cs3421_emul {

    static final boolean DEBUG_MODE = false;

    public static void main(String[] args) {

        final ArrayList<Command> commands = InputReader.parseDataFile(args[0]);

        final DataMemory dataMemory = new DataMemory();
        final InstructionMemory iMemory = new InstructionMemory();
        final IO inputOutput = new IO(dataMemory);
        final Cache cache = new Cache(dataMemory);
        final CPU cpu = new CPU(iMemory, cache, inputOutput);
        final Clock clock = new Clock(cpu);

        final CommandRunner commandRunner = new CommandRunner(clock, cpu, dataMemory, iMemory, cache, inputOutput);
        for (Command command : commands) {
            printOutput(commandRunner.runCommand(command));
        }

    }

    private static void printOutput(String dump) {
        if (!dump.isEmpty()) {
            System.out.println(dump);
        }
    }

    /**
     * Converts integers into 0x## hex format.
     *
     * @param value int
     * @return Hex String
     */
    static String createDumpRow(int value) {
        return "0x" + String.format("%02x", value).toUpperCase().substring(0, 2);
    }
}
