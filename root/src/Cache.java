import java.util.ArrayList;

class Cache {

    //Cache Line Offset
    private int CLO = 0;
    private int virtualCLO = 0;

    //Constants
    private static final int CLOCK_TICKS_1 = 1;
    private static final int CLOCK_TICKS_5 = 5;

    //Cache booleans
    private boolean cache_enabled = false;
    private boolean valid_data = true;

    //Cache storage
    private ArrayList<CacheEntry> cacheData = new ArrayList<>(8);

    //Reference to data memory
    private DataMemory dataMemory;

    //Constructor
    Cache(DataMemory dataMemory) {
        this.dataMemory = dataMemory;
        this.clearCache();
    }


    /**
     * Reads from cache. Called from LW instruction.
     *
     * @param address Hex Integer
     * @return Stored byte
     */
    Integer read(int address) {
        if (!cache_enabled) {
            return this.dataMemory.get(address);
        }

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("Reading from cache at address: " + calculateCacheOffset(address));
        }

        if (address == 0xFF) {
            this.invalidateData();
            return 0;
        }

        if (determineCacheReadHit(address)) {
            //cache hit
            if (cs3421_emul.DEBUG_MODE) {
                System.out.println("cache data[" + calculateCacheOffset(address) + "]: " + this.cacheData.get(calculateCacheOffset(address)));
            }

            final CacheEntry retrievedEntry = cacheData.get(calculateCacheOffset(address));

            if (!retrievedEntry.getWritten()) {
                //return this.readCacheMiss(address);
            }

            return retrievedEntry.getValue();
        } else {
            //Cache misses
            return this.readCacheMiss(address);
        }
    }

    private Integer readCacheMiss(int address) {
        //this.flushCache(false);
        this.clearCache();
        final int calculatedCacheLine = calculateCacheLine(address);
        for (int i = 0; i < 8; i++) {
            int dataMemoryIndex = (CLO * 8) + i;
            this.cacheData.set(i, new CacheEntry(dataMemory.get(dataMemoryIndex), false));

            if (cs3421_emul.DEBUG_MODE) {
                System.out.println("cache data[" + i + "]: " + this.cacheData.get(i));
            }
        }
        this.setCLO(calculatedCacheLine);
        this.valid_data = true;
        return cacheData.get(calculateCacheOffset(address)).getValue();
    }


    /**
     * Writes to cache. Called from SW instruction.
     *
     * @param address int
     */
    void write(int address, int value) {
        if (!cache_enabled) {
            this.dataMemory.set(address, 1, value);
            this.invalidateData();
            return;
        }

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("\nWriting to cache[" + this.calculateCacheOffset(address) + "] with value: " + value);
        }

        if (address == 0xFF) {
            this.flushCache(false);
            this.clearCacheWrittenFlags();
            return;
        }

        final int calculatedCacheLine = calculateCacheLine(address);
        if ( ((calculatedCacheLine == CLO)) && (address != -1)) {
            //cache hit
            this.cacheData.set(calculateCacheOffset(address), new CacheEntry(value, true));
            this.setCLO(calculateCacheLine(address));
        } else {
            //Cache miss
            this.flushCache(false);
            this.clearCacheWrittenFlags();
            this.setCLO(calculatedCacheLine);
            this.cacheData.set(calculateCacheOffset(address), new CacheEntry(value, true));

        }
    }

    /**
     * Determines how many clock ticks a certain cache operation will take.
     *
     * @param address Hex Address
     * @param isReadOperation Whether or not it is for a read operation
     * @return tick int value
     */
    int determineCacheOperationTickTime(int address, boolean isReadOperation) {
        if (!cache_enabled) {
            return CLOCK_TICKS_5;
        }

        if (address == 255) {
            if (!isReadOperation) {
                //write operation
                if (!this.flushCache(true)) {
                    return CLOCK_TICKS_1;
                } else {
                    return CLOCK_TICKS_5;
                }
            } else {
                //read operation
                return CLOCK_TICKS_1;
            }
        }

        if (isReadOperation) {
            if (determineCacheReadHit(address)) {
                //cache hit
//                final CacheEntry retrievedEntry = cacheData.get(calculateCacheOffset(address));
//                if (!retrievedEntry.getWritten()) {
//                    return CLOCK_TICKS_5;
//                }
                return CLOCK_TICKS_1;

            } else {
                //cache miss
                return CLOCK_TICKS_5;
            }
        } else {
            //write operation
            final int calculatedCacheLine = calculateCacheLine(address);
            if ((calculatedCacheLine == this.CLO) ) {
                return CLOCK_TICKS_1;
            } else {
                this.virtualCLO = calculatedCacheLine;
                if (!this.flushCache(true)) {
                    return CLOCK_TICKS_1;
                } else {
                    return CLOCK_TICKS_5;
                }
            }
        }

    }

    /**
     * If any value has a true "written" flag, write to data memory.
     *
     * @return is there was any data in cache with "written" flag set to true.
     */
    private boolean flushCache(boolean isVirtualFlush) {
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("\nFlushing cache with size of " + this.cacheData.size());
        }

        boolean modifiedData = false;
        for (int i = 0; i < 8; i++) {
            final CacheEntry e = this.cacheData.get(i);
            if (e.getWritten()) {
                modifiedData = true;
                if (!isVirtualFlush) {
                    final int dataMemoryAddress = (this.CLO * 8) + i;
                    this.dataMemory.set(dataMemoryAddress, 1, e.getValue());
                }
            }
        }
        return modifiedData;
    }

    private int calculateCacheOffset(int sourceAddress) {
        if (sourceAddress == -1) {
            return 0;
        }
        return (sourceAddress % 8);
    }

    /**
     * Sets the cache to disabled, CLO to zero and invalidates cacheData.
     */
    void reset() {
        this.off();
        this.CLO = 0;
        this.invalidateData();

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("\nCache resetting");
        }
    }

    /**
     * Enables the cache, causing it keep copies of cacheData transferred between the
     * CPU and Data Memory
     */
    void on() {
        this.cache_enabled = true;

        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("\nCache enabled");
            System.out.println("dumping cache:" + this.dump() + '\n');
        }
    }

    /**
     * Disables the cache, causing the system to act as if no cache were present.
     */
    void off() {
        if (cs3421_emul.DEBUG_MODE) {
            System.out.println("\nCache disabled");
        }

        this.flushCache(false);
        this.cache_enabled = false;
    }

    /**
     * Creates dump of cache information.
     *
     * @return String
     */
    String dump() {
        final StringBuilder builder = new StringBuilder();
        builder.append("CLO        : ");
        builder.append(cs3421_emul.createDumpRow(this.virtualCLO));
        builder.append('\n');

        builder.append("cache data :");
        for (CacheEntry e : this.cacheData) {
            builder.append(" ");
            builder.append(cs3421_emul.createDumpRow(e.getValue()));
        }
        builder.append("\n");

        builder.append("Flags      :");
//        for (CacheEntry e : this.cacheData) {
//            builder.append("   ");
//            if (e.getWritten()) {
//                builder.append('W');
//            } else {
//                if (this.isSynchronized()) {
//                    builder.append('V');
//                } else {
//                    builder.append('I');
//                }
//            }
//            builder.append(' ');
//        }
        for (int i = 0; i < 8; i++) {
            builder.append("   ");
            if (this.cacheData.get(i).getWritten()) {
                builder.append('W');
            } else {
                if (this.isSynchronized(i) && (this.cacheData.get(i).getValue() != 0) )  {
                    builder.append('V');
                } else {
                    builder.append('I');
                }
            }
            builder.append(' ');
        }

        builder.append('\n');
        return builder.toString();
    }

    private void invalidateData() {
        this.valid_data = false;
    }

    private int calculateCacheLine(int value) {
        for (int i = 0; i < 256; i = i + 8) {
            if (i <= value && value <= (i + 7)) {
                return (i / 8);
            }
        }
        return 0;
    }

    private boolean determineCacheReadHit(int address) {
        return (this.calculateCacheLine(address) == this.CLO && valid_data);
    }

    private void clearCache() {
        this.cacheData = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            this.cacheData.add(new CacheEntry(0, false));
        }
    }

    private void clearCacheWrittenFlags() {
        for (CacheEntry e : this.cacheData) {
            e.setWritten(false);
        }
    }

    private void setCLO(int calculatedCacheLine) {

        if (calculatedCacheLine != this.CLO) {
            invalidateData();
        }

        this.CLO = calculatedCacheLine;
        this.virtualCLO = calculatedCacheLine;
    }

    private boolean isSynchronized(int address) {
        return (this.cacheData.get(calculateCacheOffset(address)).getValue() == this.dataMemory.get(address));
    }

}
