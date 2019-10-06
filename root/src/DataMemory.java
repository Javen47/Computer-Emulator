public class DataMemory extends Memory {

    //TODO move to single function
    @Override
    public String dump(Integer hexAddress, Integer hexCount) {
        if (super.memory != null) {
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

                    int lastIndex = (currentHexAddress + 16);
                    if (hexCount < 16) {
                        lastIndex = (currentHexAddress + hexCount);
                    }
                    for (int j = (currentHexAddress + memoryDifference); j < lastIndex; j++) {
                        builder.append(super.createHexValue(super.memory.get(j), 2));
                    }
                } else {
                    final int lastIndex = (hexAddress + hexCount);
                    if (currentHexAddress + 16 < lastIndex) {
                        for (int j = (currentHexAddress); j < currentHexAddress + 16; j++) {
                            builder.append(super.createHexValue(super.memory.get(j), 2));
                        }
                    } else {
                        for (int j = (currentHexAddress); j < lastIndex; j++) {
                            builder.append(super.createHexValue(super.memory.get(j), 2));
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
