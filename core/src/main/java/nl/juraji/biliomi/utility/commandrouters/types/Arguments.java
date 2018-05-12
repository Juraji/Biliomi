package nl.juraji.biliomi.utility.commandrouters.types;

import nl.juraji.biliomi.utility.types.collections.FastList;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Juraji on 1-5-2017.
 * Biliomi v3
 */
public final class Arguments implements Iterable<String> {
    private final String command;
    private final List<String> arguments = new FastList<>();

    public Arguments(String command, Collection<String> arguments) {
        this.command = command;
        this.arguments.addAll(arguments);
    }

    public Arguments(String command, String... arguments) {
        this(command, Arrays.asList(arguments));
    }

    /**
     * Get the command that was issued
     * If the command was an alias it will have been translated
     *
     * @return The issued command
     */
    public String getCommand() {
        if (command == null) {
            throw new IllegalStateException("No command is supplied!");
        }
        return command;
    }

    /**
     * Pop the first element from the arguments list
     *
     * @return The first argument or NULL if it doesn't exist
     */
    public String pop() {
        return (isEmpty() ? null : arguments.remove(0));
    }

    /**
     * Pop the first element from the arguments list
     *
     * @return The first argument or an empty string if it doesn't exist
     */
    public String popSafe() {
        return (isEmpty() ? "" : arguments.remove(0));
    }

    /**
     * Get the argument at a specifi position
     *
     * @param index The index of the wanted argument counting from 0
     * @return The wanted argument or NULL if it doesn't exist
     */
    public String get(int index) {
        return (arguments.size() > index ? arguments.get(index) : null);
    }

    /**
     * Get the argument at a specifi position
     *
     * @param index The index of the wanted argument counting from 0
     * @return The wanted argument or an empty string if it doesn't exist
     */
    public String getSafe(int index) {
        return (arguments.size() > index ? arguments.get(index) : "");
    }

    /**
     * Get all arguments as an array
     *
     * @return An array of strings
     */
    public String[] getArguments() {
        return arguments.toArray(new String[]{});
    }

    /**
     * @return The current argument count
     */
    public int size() {
        return arguments.size();
    }

    /**
     * Assert that the argument count is of specific length
     *
     * @param requiredSize The required size
     * @return True if the argument is of the required size else False
     */
    public boolean assertSize(int requiredSize) {
        return size() == requiredSize;
    }

    /**
     * Assert that the argument count is of specific length or larger
     *
     * @param minSize The minimal size
     * @return True if the argument is of the required size or larger else False
     */
    public boolean assertMinSize(int minSize) {
        return size() >= minSize;
    }

    /**
     * Check wether or not the arguments list is empty
     *
     * @return True if empty else false
     */
    public boolean isEmpty() {
        return arguments.isEmpty();
    }

    /**
     * Check if the argument list contains a specific argument
     *
     * @param argument The argument to search
     * @return True when argument is found else False
     */
    public boolean contains(String argument) {
        return arguments.contains(argument);
    }

    /**
     * @return A Stream of type String containing all arguments in the list
     */
    public Stream<String> stream() {
        return arguments.stream();
    }

    /**
     * @return An iterator of type String for all arguments in the list
     */
    @Override
    public Iterator<String> iterator() {
        return arguments.iterator();
    }

    /**
     * Apply an action for each argument in the list
     *
     * @param action A consumer of type String accepting the current argument
     */
    @Override
    public void forEach(Consumer<? super String> action) {
        arguments.forEach(action);
    }

    /**
     * @return An iterator of type String for all arguments in the list
     */
    @Override
    public Spliterator<String> spliterator() {
        return arguments.spliterator();
    }

    /**
     * @return A string containing all arguments in order of input separated by a space character
     */
    @Override
    public String toString() {
        return arguments.stream()
                .reduce((l, r) -> l + ' ' + r)
                .orElse("");
    }
}
