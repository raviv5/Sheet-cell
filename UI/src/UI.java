import engine.api.Engine;
import engine.impl.EngineImpl;
import engine.version.manager.api.VersionManagerGetters;
import sheet.api.SheetGetters;
import sheet.cell.api.CellGetters;
import sheet.layout.api.LayoutGetters;
import sheet.layout.size.api.SizeGetters;

import java.io.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public enum UI {

    MAIN_MENU("Main Menu", null) {
        @Override
        void execute() {
            runMenu(MAIN_MENU);
        }
    },
    LOAD_XML_FILE("Load XML File", MAIN_MENU) {
        @Override
        void execute() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter file name to load: ");
            String filename = scanner.nextLine();
            engine.readXMLInitFile(filename);
            runMenu(SECOND_MENU);
        }
    },
    READ_FROM_BINARY_FILE("Read From Binary File", MAIN_MENU) {
        @Override
        void execute() {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Please enter the binary file name: ");
                String filename = scanner.nextLine();
                if (filename.equals("{BACK}")) {
                    return;
                }
                try (ObjectInputStream in =
                             new ObjectInputStream(
                                     new FileInputStream(filename))) {
                    engine = (Engine) in.readObject();
                    System.out.println("Read successfully!");
                    runMenu(SECOND_MENU);
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    System.out.println("Error reading binary file! Please try again or enter '{BACK}' to exit.");
                }
            }

        }
    },
    EXIT("Exit", MAIN_MENU) {
        @Override
        void execute() {
            engine.exit();
            System.exit(0);
        }
    },
    SECOND_MENU("Operations", LOAD_XML_FILE) {
        @Override
        void execute() {
            runMenu(SECOND_MENU);
        }
    },
    SHOW_SHEET("Show Sheet", SECOND_MENU) {
        @Override
        void execute() {
            printSheet(engine.getSheetStatus());
        }
    },
    SHOW_CELL("Show Cell", SECOND_MENU) {
        @Override
        void execute() {
            Scanner scanner = new Scanner(System.in);
            String cellName = "";

            while (true) {
                try {
                    System.out.print("Enter the wanted cell you want to show: ");
                    cellName = scanner.nextLine().trim().toUpperCase();
                    if (cellName.equals("{BACK}")) {
                        return;
                    }
                    printFullCellStatus(cellName, engine.getCellStatus(cellName));
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage() + ". Please try again or enter '{BACK}' to cancel the operation.");
                }
            }
            }
    },
    UPDATE_CELL("Update Cell", SECOND_MENU) {
        @Override
        void execute() {
            Scanner scanner = new Scanner(System.in);
            String cellName = "";

            while (true) {
                try {
                    System.out.print("Enter the cell you want to update: ");
                    cellName = scanner.nextLine().trim().toUpperCase();
                    if (cellName.equals("{BACK}")) {
                        return;
                    }
                    printCellStatus(cellName, engine.getCellStatus(cellName));
                    System.out.println();
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage() + ". Please try again or enter '{BACK}' to cancel the operation.");
                }
            }

            while (true) {
                try {
                    System.out.print("Enter the cell new value (or '{BACK}' to cancel the operation): ");
                    String value = scanner.nextLine().trim();
                    if (value.equalsIgnoreCase("{BACK}")) {
                        return;
                    }
                    engine.updateCellStatus(cellName, value);
                    System.out.println("Cell " + cellName + " updated successfully.");
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage() + ". Please try again or enter '{BACK}' to cancel the operation.");
                }
            }

        }
    },
    SHOW_VERSIONS("Show Versions", SECOND_MENU) {
        @Override
        void execute() {
            VersionManagerGetters versionManager = engine.getVersionsManagerStatus();
            printVersionsTable(versionManager);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                try {
                    System.out.println("Enter the version you want to show: ");
                    String versionStr = scanner.nextLine().trim();
                    if (versionStr.equalsIgnoreCase("{BACK}")) {
                        return;
                    }
                    int version = Integer.parseInt(versionStr);

                    printSheet(versionManager.getVersion(version));
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: please enter an integer that represent the version you want to show.");
                }
                catch (Exception e) {
                    System.out.println(e.getMessage() + ". Please try again or enter '{BACK}' to exit.");
                }
            }
        }
    },
    WRITE_TO_BINARY_FILE("Write To Binary File", SECOND_MENU) {
        @Override
        void execute() {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Please enter the binary file name: ");
                String filename = scanner.nextLine();
                if (filename.equals("{BACK}")) {
                    return;
                }
                try (ObjectOutputStream out =
                             new ObjectOutputStream(
                                     new FileOutputStream(filename))) {
                    out.writeObject(engine);
                    out.flush();
                    System.out.println("Write successfully!");
                    break;
                } catch (FileNotFoundException e) {
                    System.out.println("File not found. Please try again or enter '{BACK}' to exit.");
                }
                catch (IOException e) {
                    System.out.println("Error writing binary file! Please try again or enter '{BACK}' to exit.");
                }
            }
        }
    },
    BACK("Go Back", null) {
        @Override
        void execute() {
            // Go back is handled in the menu runner
        }
    };

    private final String description;
    private final UI parentMenu;
    private static Engine engine;

    UI(String description, UI parentMenu) {
        this.description = description;
        this.parentMenu = parentMenu;
    }

    private String getDescription() {
        return description;
    }

    private UI getParentMenu() {
        return parentMenu;
    }

    abstract void execute();

    public static void run() {
        engine = EngineImpl.create();
        UI.runMenu(UI.MAIN_MENU);
    }

    private static void runMenu(UI menu) {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("\n--- " + menu.getDescription() + " ---");
                int optionNumber = 1;
                for (UI option : UI.values()) {
                    if (option.getParentMenu() == menu) {
                        System.out.println(optionNumber++ + ". " + option.getDescription());
                    }
                }
                if (menu != MAIN_MENU) {
                    System.out.println(optionNumber + ". Go Back");
                }

                System.out.print("Please select an option: ");
                int choice = scanner.nextInt();

                    int validOptionsCount = 0;
                    for (UI option : UI.values()) {
                        if (option.getParentMenu() == menu) {
                            validOptionsCount++;
                            if (choice == validOptionsCount) {
                                option.execute();
                                break;
                            }
                        }
                    }

                    if (menu != MAIN_MENU && choice == validOptionsCount + 1) {
                        break; // Go back to the parent menu
                    } else if (choice > validOptionsCount + (menu == MAIN_MENU ? 0 : 1) || choice < 1) {
                        System.out.println("Invalid selection, please try again.");
                    }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: please enter a number corresponding to the menu option.");
                scanner.nextLine();
            }
            catch (Exception e) {
                System.out.println(e.getMessage() + ". Please try again.");
                scanner.nextLine();
            }
        }
    }

    private static void printSheet(SheetGetters sheet) {
        LayoutGetters layout = sheet.getLayout();
        SizeGetters size = layout.getSize();
        int width = size.getWidth();
        int height = size.getHeight();
        int rows = layout.getRows();
        int columns = layout.getColumns();

        StringBuilder sb = new StringBuilder();

        sb.append("Sheet Name: ").append(sheet.getName()).append("\n");
        sb.append("Version: ").append(sheet.getVersion()).append("\n");

        // Print column headers
        sb.append("   |"); // Space for row numbers
        for (int col = 0; col < columns; col++) {
            sb.append(padBothSides(Character.toString((char) ('A' + col)), width)).append("|");
        }
        sb.append("\n");

        // Print each row
        for (int row = 0; row < rows; row++) {
            sb.append(String.format("%02d |", row + 1)); // Row number with two digits
            for (int line = 0; line < height; line++) {
                for (int col = 0; col < columns; col++) {
                    CellGetters cell = engine.getCellStatus(sheet, row, col);
                    String value = (cell != null && line == height / 2) ? cell.getEffectiveValue().toString() : ""; // Centered vertically

                    // Trim value to fit column width
                    if (value.length() > width) {
                        value = value.substring(0, width);
                    }

                    sb.append(padBothSides(value, width)).append("|");
                }
                sb.append("\n");
                if (line < height - 1) { // Print additional lines only if needed
                    sb.append("   |"); // Space for row numbers
                }
            }
        }
        System.out.println(sb.toString());
    }

    private static String padBothSides(String value, int width) {
        int totalPadding = width - value.length();
        int paddingLeft = totalPadding / 2;
        int paddingRight = totalPadding - paddingLeft;
        return " ".repeat(Math.max(0, paddingLeft)) + value + " ".repeat(Math.max(0, paddingRight));
    }

    private static void printFullCellStatus(String cellName, CellGetters cell) {
        printCellStatus(cellName, cell);

        StringBuilder sbVersion = new StringBuilder();
        StringBuilder sbDependsOn = new StringBuilder();
        StringBuilder sbInfluenceOn = new StringBuilder();

        if (cell == null) {
            sbVersion.append("Version: 1\nDepends on: []\nInfluence on: []");
            System.out.println(sbVersion.toString());
            return;
        }

        else {
            sbVersion.append("Version: ").append(cell.getVersion());
            sbDependsOn.append("Depends on: [");
            cell.getInfluenceFrom().forEach(cellDep -> sbDependsOn.append(cellDep.getCoordinate()).append(", "));
            sbDependsOn.append("]");

            if (sbDependsOn.charAt(sbDependsOn.length() - 2) != '[') {
                sbDependsOn.deleteCharAt(sbDependsOn.length() - 2).deleteCharAt(sbDependsOn.length() - 2);
            }

            sbInfluenceOn.append("Influence on: [");
            cell.getInfluenceOn().forEach(cellInf -> sbInfluenceOn.append(cellInf.getCoordinate()).append(", "));
            sbInfluenceOn.append("]");

            if (sbInfluenceOn.charAt(sbInfluenceOn.length() - 2) != '[') {
                sbInfluenceOn.deleteCharAt(sbInfluenceOn.length() - 2).deleteCharAt(sbInfluenceOn.length() - 2);
            }
        }

        System.out.println(sbVersion.append("\n").append(sbDependsOn.toString()).append("\n").append(sbInfluenceOn.toString()).toString());
    }

    private static void printCellStatus(String cellName, CellGetters cell) {
        StringBuilder sb = new StringBuilder();

        if (cell == null) {
            sb.append("Cell ID: ")
                    .append(cellName)
                    .append("\nOriginal Value: ")
                    .append("\nEffective Value:");
        } else {
            sb.append("Cell ID: ").append(cellName)
                    .append("\nOriginal Value: ").append(cell.getOriginalValue())
                    .append("\nEffective Value: ").append(cell.getEffectiveValue());
        }

        System.out.println(sb.toString());
    }

    private static void printVersionsTable(VersionManagerGetters versionsManagerStatus) {

        List<SheetGetters> versions = versionsManagerStatus.getVersions();

        StringBuilder sb = new StringBuilder();

        // Table Header
        sb.append("|  Version  |  Cells Changed  |\n");
        sb.append("|-----------|-----------------|\n");

        // Table Rows
        for (int i = 0; i < versions.size(); i++) {
            sb.append("|").append(centerText(String.valueOf(versions.get(i).getVersion()), 11))
                    .append("|").append(centerText(String.valueOf(versions.get(i).getNumberOfCellsThatChanged()), 17)).append("|\n");
        }

        // Output the final table
        System.out.println(sb.toString());
    }

    private static String centerText(String text, int width) {
        int paddingLeft = (width - text.length()) / 2;
        int paddingRight = width - text.length() - paddingLeft;
        return " ".repeat(Math.max(0, paddingLeft)) + text + " ".repeat(Math.max(0, paddingRight));
    }
}
