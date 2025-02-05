import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileFilter {

    private static final String DEFAULT_INT_FILE = "integers.txt";
    private static final String DEFAULT_FLOAT_FILE = "floats.txt";
    private static final String DEFAULT_STRING_FILE = "strings.txt";

    // Паттерны для проверки типов данных
    private static final Pattern INT_PATTERN = Pattern.compile("-?[0-9]+");
    private static final Pattern FLOAT_PATTERN = Pattern.compile("-?[0-9]*\\.[0-9]+([eE][+-]?[0-9]+)?");

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java -jar util.jar [-options] input_file1 [input_file2 ...]");
            return;
        }

        List<String> inputFiles = new ArrayList<>();
        boolean appendMode = false;
        String outputPath = ".";
        String filePrefix = "";
        boolean fullStats = false;

        int i = 0;
        while (i < args.length && args[i].startsWith("-")) {
            switch (args[i]) {
                case "-a":
                    appendMode = true;
                    break;
                case "-o":
                    if (++i >= args.length) {
                        throw new IllegalArgumentException("Option '-o' requires a path argument.");
                    }
                    outputPath = args[i];
                    break;
                case "-p":
                    if (++i >= args.length) {
                        throw new IllegalArgumentException("Option '-p' requires a prefix argument.");
                    }
                    filePrefix = args[i];
                    break;
                case "-s":
                    fullStats = false;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown option: " + args[i]);
            }
            ++i;
        }

        for (; i < args.length; ++i) {
            inputFiles.add(args[i]);
        }

        if (inputFiles.isEmpty()) {
            System.out.println("No input files specified.");
            return;
        }

        processFiles(inputFiles, outputPath, filePrefix, appendMode, fullStats);
    }

    private static void processFiles(List<String> inputFiles, String outputPath, String filePrefix,
                                     boolean appendMode, boolean fullStats) throws IOException {

        try {

            // Переменные для хранения статистики
            Stats intStats = new Stats();
            Stats floatStats = new Stats();
            Stats stringStats = new Stats();

            String intFileName = filePrefix + DEFAULT_INT_FILE;
            String floatFileName = filePrefix + DEFAULT_FLOAT_FILE;
            String stringFileName = filePrefix + DEFAULT_STRING_FILE;

            BufferedWriter bwInt = new BufferedWriter(new FileWriter(intFileName));
            BufferedWriter bwFloat = new BufferedWriter(new FileWriter(floatFileName));
            BufferedWriter bwString = new BufferedWriter(new FileWriter(stringFileName));

            // Читаем все файлы построчно
            for (String inputFile : inputFiles) {
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (INT_PATTERN.matcher(line).matches()) {
                            try  {
                                bwInt.append(line);
                                bwInt.newLine();

                            } catch (IOException ex) {

                                System.out.println(ex.getMessage());
                            }
                            updateIntStats(intStats, Integer.parseInt(line));
                        } else if (FLOAT_PATTERN.matcher(line).matches()) {
                            try  {
                                bwFloat.append(line);
                                bwFloat.newLine();

                            } catch (IOException ex) {

                                System.out.println(ex.getMessage());
                            }
                            updateFloatStats(floatStats, Double.parseDouble(line));
                        } else {
                            try  {
                                bwString.append(line);
                                bwString.newLine();

                            } catch (IOException ex) {

                                System.out.println(ex.getMessage());
                            }
                            updateStringStats(stringStats, line);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error reading file " + inputFile + ": " + e.getMessage());
                }
            }

            bwInt.close();
            bwFloat.close();
            bwString.close();

            // Выводим статистику
            printStats(intStats, floatStats, stringStats, fullStats);
        } finally {


        }
    }





    private static void updateIntStats(Stats stats, int value) {
        stats.count++;
        if (stats.intMin == null || value < stats.intMin) {
            stats.intMin = value;
        }
        if (stats.intMax == null || value > stats.intMax) {
            stats.intMax = value;
        }
        if (stats.intSum == null) {
            stats.intSum = value;
        } else {
            stats.intSum += value;
        }
    }

    private static void updateFloatStats(Stats stats, double value) {
        stats.count++;
        if (stats.floatMin == null || value < stats.floatMin) {
            stats.floatMin = value;
        }
        if (stats.floatMax == null || value > stats.floatMax) {
            stats.floatMax = value;
        }
        if (stats.floatSum == null) {
            stats.floatSum = value;
        } else {
            stats.floatSum += value;
        }
    }

    private static void updateStringStats(Stats stats, String str) {
        stats.count++;
        if (stats.minLength == null || str.length() < stats.minLength) {
            stats.minLength = str.length();
        }
        if (stats.maxLength == null || str.length() > stats.maxLength) {
            stats.maxLength = str.length();
        }
    }

    private static void printStats(Stats intStats, Stats floatStats, Stats stringStats, boolean fullStats) {
        System.out.println("Integer statistics:");
        printStats(intStats, fullStats);

        System.out.println("\nFloat statistics:");
        printStats(floatStats, fullStats);

        System.out.println("\nString statistics:");
        printStats(stringStats, fullStats);
    }

    private static void printStats(Stats stats, boolean fullStats) {
        if (stats.count == 0) {
            System.out.println("No data found.");
            return;
        }

        System.out.println("Count: " + stats.count);
        if (fullStats) {
            if (stats.intMin != null) {
                System.out.println("Min: " + stats.intMin);
            }
            if (stats.intMax != null) {
                System.out.println("Max: " + stats.intMax);
            }
            if (stats.intSum != null) {
                System.out.println("Sum: " + stats.intSum);
                System.out.println("Average: " + ((double) stats.intSum / stats.count));
            }
            if (stats.floatMin != null) {
                System.out.println("Min: " + stats.floatMin);
            }
            if (stats.floatMax != null) {
                System.out.println("Max: " + stats.floatMax);
            }
            if (stats.floatSum != null) {
                System.out.println("Sum: " + stats.floatSum);
                System.out.println("Average: " + (stats.floatSum / stats.count));
            }
            if (stats.minLength != null) {
                System.out.println("Shortest length: " + stats.minLength);
            }
            if (stats.maxLength != null) {
                System.out.println("Longest length: " + stats.maxLength);
            }
        }
    }

    private static class Stats {
        int count = 0;
        Integer intMin;
        Integer intMax;
        Integer intSum;
        Double floatMin;
        Double floatMax;
        Double floatSum;
        Integer minLength;
        Integer maxLength;
    }
}