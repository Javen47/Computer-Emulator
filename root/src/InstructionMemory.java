public class InstructionMemory extends Memory {

    @Override
    public String dump(Integer hexAddress, Integer hexCount) {
        if (super.memory != null) {
            final StringBuilder builder = new StringBuilder();
            builder.append("Addr       0     1     2     3     4     5     6     7\n");

            Integer adjustedHexAddress = hexAddress;
            if (hexAddress % 8 != 0) {
                adjustedHexAddress = (hexAddress - (hexAddress % 8));
            }

            final int memoryDifference = hexAddress - adjustedHexAddress;
            final int rowCount = (int) Math.ceil( (( hexCount.doubleValue() + memoryDifference ) / 8) );

            for (int row = 0; row < rowCount; row++) {

                final int currentHexAddress = (adjustedHexAddress + (8 * row));
                final String rowAddress = "0x" + String.format("%04x", currentHexAddress).toUpperCase() + " ";
                builder.append(rowAddress);

                if (row == 0) {
                    for (int i = 0; i < memoryDifference; i++) {
                        builder.append("      ");
                    }

                    int lastIndex = (currentHexAddress + 8);
                    if (hexCount < 8) {
                        lastIndex = (currentHexAddress + hexCount);
                    }
                    for (int j = (currentHexAddress + memoryDifference); j < lastIndex; j++) {
                        builder.append(super.createHexValue(super.memory.get(j), 5));
                    }
                } else {
                    final int lastIndex = (hexAddress + hexCount);
                    if (currentHexAddress + 8 < lastIndex) {
                        for (int j = (currentHexAddress); j < currentHexAddress + 8; j++) {
                            builder.append(super.createHexValue(super.memory.get(j), 5));
                        }
                    } else {
                        for (int j = (currentHexAddress); j < lastIndex; j++) {
                            builder.append(super.createHexValue(super.memory.get(j), 5));
                        }
                    }
                }

                builder.append("\n");
            }

            return builder.toString();
        }
        return "";
    }

}
