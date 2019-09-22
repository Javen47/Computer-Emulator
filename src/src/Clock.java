public class Clock {

    private CPU cpu;
    private long tickCount = 0;

    public Clock(CPU cpu) {
        this.cpu = cpu;
    }

    /**
     * Communicates with CPU to perform a cycle.
     *
     * @param ticks Long
     */
    public void tick(long ticks) {
        this.tickCount += ticks;
        for (long i = 0; i < ticks; i++) {
            this.cpu.tick();
        }
    }

    /**
     * Sets the clock tick count to 0.
     */
    public void reset() {
        this.tickCount = 0;
    }

    /**
     * Returns the total clock tick count.
     * @return String
     */
    public String dump() {
        return "Clock: " + tickCount;
    }



}
