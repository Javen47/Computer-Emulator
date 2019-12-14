class CacheEntry {

    private Integer value;
    private Boolean isWritten;

    CacheEntry(Integer value, Boolean isWritten) {
        this.value = value;
        this.isWritten = isWritten;
    }

    Integer getValue() {
        return value;
    }

    void setValue(Integer value) {
        this.value = value;
    }

    Boolean getWritten() {
        return isWritten;
    }

    void setWritten(Boolean written) {
        isWritten = written;
    }

    @Override
    public String toString() {
        return "CacheEntry{" +
                "value=" + value +
                ", isWritten=" + isWritten +
                '}';
    }
}
