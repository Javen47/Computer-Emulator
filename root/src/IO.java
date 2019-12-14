import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class IO {

    //IO Register
    private int R0 = 0;
    private int clockTick = 0;

    //Reference to Data Memory
    private DataMemory dataMemory;

    //Scheduled Events Map
    private HashMap<Integer, ScheduledEvent> scheduledEventMap;

    IO(DataMemory dataMemory) {
        this.dataMemory = dataMemory;
        this.scheduledEventMap = new HashMap<>();
    }

    /**
     * Resets IO device, setting IO Register to 0.
     */
    void reset() {
        this.R0 = 0;
        this.scheduledEventMap = new HashMap<>();
    }

    /**
     * Takes a file that contains a list of I/O schedule events, with a max of 100.
     * Places scheduled event into a schedule map.
     *
     * @param eventsFile File
     */
    void load(File eventsFile) {
        try {
            final Scanner scanner = new Scanner(eventsFile);
            while (scanner.hasNextLine()) {

                final String line = scanner.nextLine();
                final String[] scheduleEventInfo = line.split(" ");
                final ScheduledEvent scheduledEvent = new ScheduledEvent();

                scheduledEvent.setClockTick(Integer.parseInt(scheduleEventInfo[0]));
                scheduledEvent.setOperation(scheduleEventInfo[1]);
                scheduledEvent.setAddress(Integer.decode(scheduleEventInfo[2]));
                if (scheduleEventInfo.length > 3) {
                    scheduledEvent.setValue(Integer.decode(scheduleEventInfo[3]));
                }

                this.scheduledEventMap.put(scheduledEvent.getClockTick() + 5, scheduledEvent);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Dumps the content of the IO Register.
     *
     * @return String
     */
    String dump() {
        return "IO Device: " + cs3421_emul.createDumpRow(this.R0) + '\n';
    }


    /**
     * Determines if it is appropriate to execute a scheduled event based on the passed in clock tick the CPU is on.
     *
     * @return boolean of whether or not a scheduled event was executed.
     */
    boolean checkEventSchedule() {
        final ScheduledEvent scheduledEvent = this.scheduledEventMap.get(clockTick);
        this.clockTick++;
        if (scheduledEvent != null) {
            if (scheduledEvent.getOperation().equals("write")) {
                this.dataMemory.set(scheduledEvent.getAddress(), 1, scheduledEvent.getValue());
            } else {
                this.R0 = this.dataMemory.get(scheduledEvent.getAddress());
            }
            return true;
        }
        return false;
    }

}
