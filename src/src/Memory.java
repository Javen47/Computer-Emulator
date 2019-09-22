import java.io.File;
import java.util.HashMap;

public class Memory {

    private HashMap<Integer, Integer> memory;
    private int memoryCapacity = 0;

    /**
     * Initializes memory.
     *
     * @param size indicates the initial capacity of memory.
     */
    public void create(int size) {
        this.memory = new HashMap<>(size);
        this.memoryCapacity = size;
    }

    /**
     * Causes all allocated memory to be set to zero.
     */
    public void reset() {
        if (this.memory != null) {
            this.memory = new HashMap<>(memoryCapacity);
            for (int i = 0; i < this.memoryCapacity; i++) {
                this.memory.put(i, 0);
            }
        }
    }

    /**
     * Shows the content of the memory starting at a specified location, and for a specified number of bytes.
     *
     * @param hexAddress Starting address.
     * @param hexCount Number of bytes.
     * @return String of formatted memory contents.
     */
    public String dump(Integer hexAddress, Integer hexCount) {
        if (this.memory != null) {
            final StringBuilder builder = new StringBuilder();
            builder.append("Addr   00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F\n");

            Integer adjustedHexAddress = hexAddress;
            if (hexAddress % 16 != 0) {
                adjustedHexAddress = (hexAddress - (hexAddress % 16));
            }

            final int memoryDifference = hexAddress - adjustedHexAddress;
            final int rowCount = (int) Math.ceil( (( hexCount.doubleValue() + memoryDifference ) / 16) );

            for (int row = 0; row < rowCount; row++) {

                final int currentHexAddress = (adjustedHexAddress + (16 * row));
                final String rowAddress = "0x" + String.format("%04x", currentHexAddress).toUpperCase() + " ";
                builder.append(rowAddress);

                if (row == 0) {
                    for (int i = 0; i < memoryDifference; i++) {
                        builder.append("   ");
                    }
                    for (int j = (currentHexAddress + memoryDifference); j < currentHexAddress + 16; j++) {
                        builder.append(createHexValue(memory.get(j)));
                    }
                } else {
                    final int lastIndex = (hexAddress + hexCount);
                    if (currentHexAddress + 16 < lastIndex) {
                        for (int j = (currentHexAddress); j < currentHexAddress + 16; j++) {
                            builder.append(createHexValue(memory.get(j)));
                        }
                    } else {
                        for (int j = (currentHexAddress); j < lastIndex; j++) {
                            builder.append(createHexValue(memory.get(j)));
                        }
                    }
                }

                builder.append("\n");
            }

            return builder.toString();
        }
        return "";
    }

    private String createHexValue(int value) {
        if (value != 0) {
            return String.format("%02x", value).toUpperCase() + " ";
        } else {
            return "";
        }
    }

    /**
     * Sets initialized memory at a specified location to specified values.
     *
     * @param hexAddress Where to begin setting memory values.
     * @param hexCount How many hex values will be assigned.
     * @param hexBytes Values of hex bytes to be assigned.
     */
    public void set(int hexAddress, int hexCount, Integer[] hexBytes) {
        if (this.memory != null) {
            for (int i = 0; i < hexCount; i++) {
                this.memory.put(hexAddress + i, hexBytes[i]);
            }
        }
    }

    /**
     * Sets initialized memory at a specified location to specified values.
     *
     * @param hexAddress Where to begin setting memory values.
     * @param dataFile File with list of Hex Bytes.
     */
    public void set(int hexAddress, File dataFile) {
        final Integer[] bytes = InputReader.parseHexByteFile(dataFile);
        if (bytes != null) {
            this.set(hexAddress, bytes.length, bytes);
        }
    }

    /**
     * Retrieves value from memory.
     *
     * @param hexAddress int
     * @return int
     */
    public int get(int hexAddress) {
        if (this.memory != null) {
            return this.memory.get(hexAddress);
        }
        return -1;
    }

}
