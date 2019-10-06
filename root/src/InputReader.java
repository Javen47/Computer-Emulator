import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class InputReader {

    /**
     * Parses strings out of an input text file.
     *
     * @param dataFile String
     * @return ArrayList<Command>
     */
    public static ArrayList<Command> parseDataFile(String dataFile) {
        final ArrayList<Command> commands = new ArrayList<>();

        try {
            final Scanner reader = new Scanner(new File(dataFile));
            while (reader.hasNextLine()) {
                commands.add(parseDataLine(reader.nextLine()));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return commands;
    }

    /**
     * Parses hex byte strings out of a passed in file.
     *
     * @param dataFile File
     * @return Integer[] of converted bytes
     */
    public static Integer[] parseHexByteFile(File dataFile) {
        try {
            final Scanner reader = new Scanner(dataFile);
            final ArrayList<Integer> bytes = new ArrayList<>();
            while (reader.hasNext()) {
                String nextByte = reader.next();
                if (nextByte.contains("0x")) {
                    bytes.add(Integer.decode(nextByte));
                } else {
                    nextByte = "0x" + nextByte;
                    bytes.add(Integer.decode(nextByte));
                }
            }
            return bytes.toArray(new Integer[bytes.size()]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Converts strings parsed out of an input file to command objects.
     *
     * @param line String
     * @return Command
     */
    private static Command parseDataLine(String line) {
        final Command command = new Command();
        String[] words = line.split(" ");

        switch (words[0]) {
            case ("clock"):
                command.setDevice(Device.CLOCK);
                break;
            case ("memory"):
                command.setDevice(Device.DATA_MEMORY);
                break;
            case ("imemory"):
                command.setDevice(Device.INSTRUCTION_MEMORY);
                break;
            case ("cpu"):
                command.setDevice(Device.CPU);
                break;
        }

        command.setOption(Option.valueOf(words[1].toUpperCase()));

        if (words.length > 2) {
            command.setParameters(Arrays.copyOfRange(words, 2, words.length));
        }

        return command;
    }

}
