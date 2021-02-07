package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static net.md_5.bungee.api.ChatColor.COLOR_CHAR;
import static net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils.applyLastColorToFollowingLines;

public class WordWrapper {

    private final String rawString;
    private final int lineLength;
    char[] rawChars;
    StringBuilder word = new StringBuilder();
    StringBuilder line = new StringBuilder();
    List<String> lines = new LinkedList<>();
    int lineColorChars = 0;

    public WordWrapper(@NotNull String rawString, int lineLength) {
        this.rawString = rawString;
        this.lineLength = lineLength > 0 ? lineLength : 319;
        rawChars = (rawString + ' ').toCharArray(); // add a trailing space to trigger pagination
    }

    @NotNull
    public String[] wrap() {
        // A string shorter than the lineWidth is a single line
        if (rawString.length() <= lineLength && !rawString.contains("\n")) {
            return new String[]{rawString};
        }

        for (int i = 0; i < rawChars.length; i++) {
            char c = rawChars[i];
            // skip chat color modifiers
            if (c == COLOR_CHAR) {
                processChatColorModifiers(rawChars[i + 1]);
                i++; // Eat the next character as we have already processed it
                continue;
            }
            if (c == ' ' || c == '\n') {
                processSpace();
                processLineBreak(c);
            } else {
                word.append(c);
            }
        }
        addLastLine();
        // Iterate over the wrapped lines, applying the last color from one line to the beginning of the next
        applyLastColorToFollowingLines(lines);

        return lines.toArray(new String[0]);
    }

    private void addLastLine() {
        if (line.length() > 0) { // Only add the last line if there is anything to add
            lines.add(line.toString());
        }
    }

    private void processLineBreak(char c) {
        if (c == '\n') { // Newline forces the line to flush
            lines.add(line.toString());
            line = new StringBuilder();
        }
    }

    private void processSpace() {
        if (line.length() == 0 && word.length() - lineColorChars > lineLength) { // special case: extremely long word begins a line
            lines.addAll(split(word.toString(), lineLength));
        } else if (line.length() + 1 + word.length() - lineColorChars == lineLength) { // Line exactly the correct length...newline
            processLineCorrectLength();
        } else if (line.length() + 1 + word.length() - lineColorChars > lineLength) { // Line too long...break the line
            processLineTooLong();
        } else {
            appendWord();
        }
        word = new StringBuilder();
    }

    private void appendWord() {
        if (line.length() > 0) {
            line.append(' ');
        }
        line.append(word);
    }

    private void processLineTooLong() {
        for (String partialWord : split(word.toString(), lineLength)) {
            if (line.length() > 0) {
                lines.add(line.toString());
            }
            line = new StringBuilder(partialWord);
        }
        lineColorChars = 0;
    }

    private void processLineCorrectLength() {
        appendWord();
        lines.add(line.toString());
        line = new StringBuilder();
        lineColorChars = 0;
    }

    private void processChatColorModifiers(char rawChar) {
        word.append(COLOR_CHAR);
        word.append(rawChar);
        lineColorChars += 2;
    }

    /**
     * Breaks a String, using the passed length and respecting color codes, into a List of Strings
     *
     * @param input the input
     * @return the split input
     */
    @NotNull
    public static List<String> split(@NotNull String input, int length) {
        List<String> split = new ArrayList<>();
        int nonColorCount = 0;
        for (int i = 0; i < input.length(); i++) {
            if (nonColorCount == length) {
                split.add(input.substring(0, i));
                input = input.substring(i);
                nonColorCount = 0;
                i = 0;
            }
            char c = input.charAt(i);
            if (c == COLOR_CHAR) {
                if (i + 1 < input.length()) {
                    String s = String.valueOf(input.charAt(i + 1));
                    if (ChatColor.ALL_CODES.contains(s)) {
                        i++;
                        continue;
                    }
                }
            }
            nonColorCount++;
        }
        if (!input.isEmpty()) {
            split.add(input);
        }
        return split;
    }


}
