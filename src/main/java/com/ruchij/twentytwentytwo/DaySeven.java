package com.ruchij.twentytwentytwo;

import com.ruchij.JavaSolution;

import java.util.*;
import java.util.stream.Stream;

public class DaySeven implements JavaSolution {
    interface Item {
        long getSize();

        Directory parent();

        record Directory(Directory parent, String name, Map<String, Item> items) implements Item {
            @Override
            public long getSize() {
                return items.values().stream().map(Item::getSize).reduce(Long::sum).orElse(0L);
            }

            @Override
            public String toString() {
                return "Directory{" +
                        "name='" + name + '\'' +
                        ", items=" + items +
                        '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Directory directory = (Directory) o;
                return Objects.equals(name, directory.name) && Objects.equals(items, directory.items);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, items);
            }
        }

        record File(Directory parent, String name, long size) implements Item {
            @Override
            public long getSize() {
                return size;
            }

            @Override
            public String toString() {
                return "File{" +
                        "name='" + name + '\'' +
                        ", size=" + size +
                        '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                File file = (File) o;
                return size == file.size && Objects.equals(name, file.name);
            }

            @Override
            public int hashCode() {
                return Objects.hash(name, size);
            }
        }
    }

    interface Input {
        interface DirectoryCommand {
            record Root() implements DirectoryCommand {
            }

            record Up() implements DirectoryCommand {
            }

            record Goto(String name) implements DirectoryCommand {
            }
        }

        record ChangeDirectory(DirectoryCommand directoryCommand) implements Input {
        }

        interface Output {
            record Directory(String name) implements Output {
            }

            record File(String name, long size) implements Output {
            }
        }

        record ListItems(Set<Output> output) implements Input {
        }
    }


    @Override
    public Object solve(Stream<String> input) {
        List<Input> inputs = parse(input);

        Item.Directory root = new Item.Directory(null, null, new HashMap<>());
        traverse(root, inputs);

        return calculate(new HashSet<>(), root, 100_000, 0);
    }

    long calculate(Set<Item.Directory> directories, Item.Directory directory, long max, long size) {
        long directorySize = directory.getSize();

        if (directorySize <= max) {
            directories.add(directory);
            size += directorySize;
        }

        for (Item item : directory.items.values()) {
            if (item instanceof Item.Directory current) {
                size = calculate(directories, current, max, size);
            }
        }

        return size;
    }

    private final String CHANGE_DIRECTORY = "$ cd";
    private final String LIST = "$ ls";
    private final String DIRECTORY = "dir";

    void traverse(Item.Directory directory, List<Input> inputs) {
        for (Input input : inputs) {
            if (input instanceof Input.ChangeDirectory) {
                Input.ChangeDirectory changeDirectory = (Input.ChangeDirectory) input;
                Input.DirectoryCommand directoryCommand = changeDirectory.directoryCommand;

                if (directoryCommand instanceof Input.DirectoryCommand.Root) {
                    while (directory.parent != null) {
                        directory = directory.parent;
                    }
                } else if (directoryCommand instanceof Input.DirectoryCommand.Up) {
                    directory = directory.parent;
                } else {
                    Input.DirectoryCommand.Goto go = (Input.DirectoryCommand.Goto) directoryCommand;
                    Item item = directory.items.get(go.name);

                    if (item != null) {
                        directory = (Item.Directory) item;
                    } else {
                        Item.Directory newDirectory = new Item.Directory(directory, go.name, new HashMap<>());
                        directory.items.put(go.name, newDirectory);

                        directory = newDirectory;
                    }
                }
            } else if (input instanceof Input.ListItems) {
                Input.ListItems listItems = (Input.ListItems) input;

                for (Input.Output output : listItems.output) {
                    if (output instanceof Input.Output.Directory) {
                        Input.Output.Directory dir = (Input.Output.Directory) output;
                        Item item = directory.items.get(dir.name);

                        if (item == null) {
                            directory.items.put(dir.name, new Item.Directory(directory, dir.name, new HashMap<>()));
                        }
                    } else {
                        Input.Output.File outputFile = (Input.Output.File) output;

                        directory.items.put(outputFile.name, new Item.File(directory, outputFile.name, outputFile.size));
                    }
                }

            }
        }
    }

    List<Input> parse(Stream<String> input) {
        ArrayList<Input> inputs = new ArrayList<>();
        Iterator<String> iterator = input.iterator();
        String line = null;

        while (iterator.hasNext() || line != null) {
            if (line == null) {
                line = iterator.next();
            }

            if (line.startsWith(CHANGE_DIRECTORY)) {
                String substring = line.substring(CHANGE_DIRECTORY.length()).trim();

                if (substring.startsWith("/")) {
                    inputs.add(new Input.ChangeDirectory(new Input.DirectoryCommand.Root()));
                } else if (substring.startsWith("..")) {
                    inputs.add(new Input.ChangeDirectory(new Input.DirectoryCommand.Up()));
                } else {
                    inputs.add(new Input.ChangeDirectory(new Input.DirectoryCommand.Goto(substring)));
                }

                line = null;
            } else if (line.equalsIgnoreCase(LIST)) {
                Set<Input.Output> outputs = new HashSet<>();

                while (iterator.hasNext()) {
                    line = iterator.next().trim();

                    if (line.startsWith("$")) {
                        inputs.add(new Input.ListItems(outputs));
                        outputs = null;
                        break;
                    } else if (line.startsWith(DIRECTORY)) {
                        outputs.add(new Input.Output.Directory(line.substring(DIRECTORY.length()).trim()));
                    } else {
                        String[] split = line.split(" ");
                        long size = Long.parseLong(split[0].trim());
                        String name = split[1].trim();

                        outputs.add(new Input.Output.File(name, size));
                    }
                }

                if (outputs != null) {
                    line = null;
                    inputs.add(new Input.ListItems(outputs));
                }

            }
        }

        return inputs;
    }


}
