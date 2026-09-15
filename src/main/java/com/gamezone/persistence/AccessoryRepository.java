package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves and loads accessories from a CSV file.
 */
public class AccessoryRepository {

    private static final String FILE_PATH = "data/accessories.csv";
    private static final String SEPARATOR = ",";
    private static final String CONSOLE_IDS_SEPARATOR = "\\|";
    private static final String CONSOLE_IDS_JOINER = "|";

    /**
     * Saves the given accessories to the data file, replacing its current content.
     *
     * @param accessories accessories to persist
     */
    public void saveAll(List<Accessory> accessories) {
        List<String> lines = new ArrayList<>();
        for (Accessory accessory : accessories) {
            lines.add(toLine(accessory));
        }
        try {
            Files.write(Path.of(FILE_PATH), lines);
        } catch (IOException e) {
            throw new UncheckedIOException("Error writing accessories file", e);
        }
    }

    /**
     * @return the accessories currently stored in the data file, or an empty list if it does not exist yet
     */
    public List<Accessory> loadAll() {
        Path path = Path.of(FILE_PATH);
        List<Accessory> accessories = new ArrayList<>();
        if (!Files.exists(path)) {
            return accessories;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                accessories.add(fromLine(line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Error reading accessories file", e);
        }
        return accessories;
    }

    private String toLine(Accessory accessory) {
        String compatibleConsoleIds = String.join(CONSOLE_IDS_JOINER, accessory.getCompatibleConsoleIds());
        if (accessory instanceof Controller controller) {
            return "CONTROLLER" + SEPARATOR + controller.getId() + SEPARATOR + controller.getTitle() + SEPARATOR
                    + controller.getPrice() + SEPARATOR + controller.getQuantity() + SEPARATOR
                    + controller.getConnectionType() + SEPARATOR + compatibleConsoleIds;
        } else if (accessory instanceof Cable cable) {
            return "CABLE" + SEPARATOR + cable.getId() + SEPARATOR + cable.getTitle() + SEPARATOR
                    + cable.getPrice() + SEPARATOR + cable.getQuantity() + SEPARATOR
                    + cable.getLengthMeters() + SEPARATOR + cable.getConnectorType() + SEPARATOR
                    + compatibleConsoleIds;
        } else if (accessory instanceof Memory memory) {
            return "MEMORY" + SEPARATOR + memory.getId() + SEPARATOR + memory.getTitle() + SEPARATOR
                    + memory.getPrice() + SEPARATOR + memory.getQuantity() + SEPARATOR
                    + memory.getCapacityGb() + SEPARATOR + memory.getMemoryType() + SEPARATOR
                    + compatibleConsoleIds;
        }
        throw new IllegalArgumentException("Unknown accessory type: " + accessory.getClass());
    }

    private Accessory fromLine(String line) {
        String[] fields = line.split(SEPARATOR, -1);
        String type = fields[0];
        String id = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int quantity = Integer.parseInt(fields[4]);

        Accessory accessory;
        String compatibleConsoleIds;
        if (type.equals("CONTROLLER")) {
            accessory = new Controller(id, title, price, quantity, fields[5]);
            compatibleConsoleIds = fields[6];
        } else if (type.equals("CABLE")) {
            accessory = new Cable(id, title, price, quantity, Double.parseDouble(fields[5]), fields[6]);
            compatibleConsoleIds = fields[7];
        } else if (type.equals("MEMORY")) {
            accessory = new Memory(id, title, price, quantity, Integer.parseInt(fields[5]), fields[6]);
            compatibleConsoleIds = fields[7];
        } else {
            throw new IllegalArgumentException("Unknown accessory type: " + type);
        }

        if (!compatibleConsoleIds.isEmpty()) {
            for (String consoleId : compatibleConsoleIds.split(CONSOLE_IDS_SEPARATOR)) {
                accessory.addCompatibleConsole(consoleId);
            }
        }
        return accessory;
    }
}
