import java.util.Arrays;

public class Command {

    private Device device;
    private Option option;
    private String[] parameters;

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public void setParameters(String... parameters) {
        this.parameters = parameters;
    }

    public Device getDevice() {
        return device;
    }

    public Option getOption() {
        return option;
    }

    public String[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "Command{" +
                "device=" + device +
                ", option=" + option +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
