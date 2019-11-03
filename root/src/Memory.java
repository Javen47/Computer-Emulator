import java.io.File;
import java.util.HashMap;

abstract class Memory {

    HashMap<Integer, Integer> memory;
    private int memoryCapacity = 0;

    /**
     * Initializes memory.
     *
     * @param size indicates the initial capacity of memory.
     */
    void create(int size) {
        this.memory = new HashMap<>(size);
        this.memoryCapacity = size;
        this.reset();
    }

    /**
     * Causes all allocated memory to be setRegisterValue to zero.
     */
    void reset() {
        if (this.memory != null) {
            this.memory = new HashMap<>(memoryCapacity);
            for (int i = 0; i < this.memoryCapacity; i++) {
                this.memory.put(i, 0);
            }
        }
    }

    /**
     * Retrieves value from memory.
     *
     * @param hexAddress int
     * @return int
     */
    int get(int hexAddress) {
        if (this.memory != null) {
            return this.memory.get(hexAddress);
        }
        return -1;
    }

    /**
     * Converts integers into 0x## hex format.
     *
     * @param value int
     * @return Hex String
     */
    String createHexValue(int value, int digits) {
        if (value != 0) {
            return String.format("%0" + digits + "x", value).toUpperCase() + " ";
        } else if (digits == 2){
            return "00 ";
        } else if (digits == 5) {
            return "00000 ";
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
    void set(int hexAddress, int hexCount, Integer... hexBytes) {
        if (memory != null) {
            for (int i = 0; i < hexCount; i++) {
                memory.put(hexAddress + i, hexBytes[i]);
            }
        }
    }

    /**
     * Sets initialized memory at a specified location to specified values.
     *
     * @param hexAddress Where to begin setting memory values.
     * @param dataFile File with list of Hex Bytes.
     */
    void set(int hexAddress, File dataFile) {
        final Integer[] bytes = InputReader.parseHexByteFile(dataFile);
        if (bytes != null) {
            this.set(hexAddress, bytes.length, bytes);
        }
    }

    /**
     * Shows the content of the memory starting at a specified location, and for a specified number of bytes.
     *
     * @param hexAddress Starting address.
     * @param hexCount Number of bytes.
     * @return String of formatted memory contents.
     */
    abstract String dump(Integer hexAddress, Integer hexCount);

}
