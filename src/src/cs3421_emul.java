import java.util.ArrayList;

/**
 * @author Javen Zamojcin
 * Class: Computer Organization
 * Date Last Modified: 9/22/2019
 */
public class cs3421_emul {

    public static void main(String[] args) {

        final ArrayList<Command> commands = InputReader.parseDataFile(args[0]);

        final Memory memory = new Memory();
        final CPU cpu = new CPU(memory);
        final Clock clock = new Clock(cpu);

        final CommandRunner commandRunner = new CommandRunner(clock, cpu, memory);
        for (Command command : commands) {
            printOutput(commandRunner.runCommand(command));
        }

    }

    private static void printOutput(String dump) {
        if (!dump.isEmpty()) {
            System.out.println(dump);
        }
    }
}
