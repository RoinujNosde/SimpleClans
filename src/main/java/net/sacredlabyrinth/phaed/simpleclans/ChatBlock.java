package net.sacredlabyrinth.phaed.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class ChatBlock {
    private static final int LINE_LENGTH = 319;
    private final ArrayList<Boolean> columnFlexes = new ArrayList<>();
    private final ArrayList<Integer> columnSizes = new ArrayList<>();
    private final ArrayList<String> columnAlignments = new ArrayList<>();
    private final LinkedList<String[]> rows = new LinkedList<>();
    private String color = "";
    private boolean cropRight = true;
    private boolean padRight = true;

    public void setCropRight(boolean cropRight) {
    	this.cropRight = cropRight;
    }
    
    public void setPadRight(boolean padRight) {
    	this.padRight = padRight;
    }
    
    /**
     * @param columnAlignment
     */
    public void setAlignment(String... columnAlignment) {
        columnAlignments.addAll(Arrays.asList(columnAlignment));
    }

    /**
     * @param columnFlex
     */
    public void setFlexibility(boolean... columnFlex) {
        for (boolean flex : columnFlex) {
            columnFlexes.add(flex);
        }
    }

    /**
     * @param columnPercentages
     * @param prefix
     */
    public void setColumnSizes(String prefix, double... columnPercentages) {
        int ll = LINE_LENGTH;

        if (prefix != null) {
            ll = LINE_LENGTH - (int) msgLength(prefix);
        }

        for (double percentage : columnPercentages) {
            columnSizes.add((int) Math.floor((percentage / 100) * ll));
        }
    }

    /**
     * @return
     */
    public boolean hasContent() {
        return !rows.isEmpty();
    }

    /**
     * @param contents
     */
    public void addRow(String... contents) {
        rows.add(contents);
    }

    /**
     * @return
     */
    public int size() {
        return rows.size();
    }

    /**
     * @return
     */
    public boolean isEmpty() {
        return rows.isEmpty();
    }

    /**
     *
     */
    public void clear() {
        rows.clear();
    }

    /**
     * @param player
     * @return
     */
    public boolean sendBlock(CommandSender player) {
        return sendBlock(player, null, 0);
    }

    /**
     * @param player
     * @param prefix
     * @return
     */
    public boolean sendBlock(CommandSender player, String prefix) {
        return sendBlock(player, prefix, 0);
    }

    /**
     * @param player
     * @param amount
     * @return
     */
    public boolean sendBlock(CommandSender player, int amount) {
        return sendBlock(player, null, amount);
    }

    /**
     * @param player
     * @param prefix
     * @param amount
     * @return
     */
    boolean sendBlock(CommandSender player, String prefix, int amount) {
        if (player == null) {
            return false;
        }

        if (rows.isEmpty()) {
            return false;
        }

        if (amount == 0) {
            amount = rows.size();
        }

        boolean prefix_used = prefix == null;

        String empty_prefix = ChatBlock.makeEmpty(prefix);

        // if no column sizes provided then
        // make some up based on the data

        if (columnSizes.isEmpty()) {
            // generate columns sizes

            int colCount = rows.get(0).length;

            if (colCount > 1) {
                for (int i = 0; i < colCount; i++) {
                    columnSizes.add(getMaxWidth(i) + 4);
                }
            } else {
                columnSizes.add(LINE_LENGTH);
            }
        }


        List<String> messages = new ArrayList<>();
        // size up all sections

        for (int i = 0; i < amount; i++) {
            if (rows.isEmpty()) {
                continue;
            }

            List<String> measuredCols = new ArrayList<>();
            String row[] = rows.pollFirst();

            for (int sid = 0; sid < row.length; sid++) {
                String col = "";
                String section = row[sid];
                double colsize = (columnSizes.size() >= (sid + 1)) ? columnSizes.get(sid) : 0;
                String align = (columnAlignments.size() >= (sid + 1)) ? columnAlignments.get(sid) : "l";

                if (align.equalsIgnoreCase("r")) {
                    if (msgLength(section) > colsize) {
                        col = cropLeftToFit(section, colsize);
                    } else if (msgLength(section) < colsize) {
                        col = paddLeftToFit(section, colsize);
                    }
                } else if (align.equalsIgnoreCase("l")) {
                    if (msgLength(section) > colsize) {
                    	if (cropRight) {
                    		col = cropRightToFit(section, colsize);
                        } else {
                        	col = section;
                        }
                    } else if (msgLength(section) < colsize) {
                    	if (padRight) {
                    		col = paddRightToFit(section, colsize);
                    	} else {
                    		col = section;
                    	}
                    }
                } else if (align.equalsIgnoreCase("c")) {
                    if (msgLength(section) > colsize) {
                    	if (cropRight) {
                    		col = cropRightToFit(section, colsize);
                    	} else {
                    		col = section;
                    	}
                    } else if (msgLength(section) < colsize) {
                    	col = centerInLineOf(section, colsize);
                    }
                }

                measuredCols.add(col);
            }

            // add in spacings

            int availableSpacing = 12;

            while (calculatedRowSize(measuredCols) < LINE_LENGTH && availableSpacing > 0) {
                for (int j = 0; j < measuredCols.size(); j++) {
                    String col = measuredCols.get(j);
                    measuredCols.set(j, col + " ");

                    if (calculatedRowSize(measuredCols) >= LINE_LENGTH) {
                        break;
                    }

                }

                availableSpacing -= 4;
            }

            // cut off from flexible columns if too big

            if (columnFlexes.size() == measuredCols.size()) {
                while (calculatedRowSize(measuredCols) > LINE_LENGTH) {
                    boolean didFlex = false;

                    for (int j = 0; j < measuredCols.size(); j++) {
                        boolean flex = columnFlexes.get(j);

                        if (flex) {
                            String col = measuredCols.get(j);

                            if (col.length() > 0) {
                                measuredCols.set(j, col.substring(0, col.length() - 1));
                                didFlex = true;
                            }
                        }

                        if (calculatedRowSize(measuredCols) <= LINE_LENGTH) {
                            break;
                        }
                    }

                    if (!didFlex) {
                        break;
                    }
                }
            }

            // concatenate final strings

            String finalString = "";

            for (String measured : measuredCols) {
                finalString += measured;
            }

            // crop and print out
            String msg = (prefix_used ? empty_prefix : prefix + " ") + finalString;
            if (cropRight) {
            	msg = cropRightToFit(msg, LINE_LENGTH);
            }
            if (color.length() > 0) {
                msg = color + msg;
            }

            messages.add(msg);

            prefix_used = true;
        }

        player.sendMessage(messages.toArray(new String[0]));
        return !rows.isEmpty();
    }

    private int calculatedRowSize(List<String> cols) {
        int out = 0;

        for (String col : cols) {
            out += msgLength(col);
        }

        return out;
    }

    /**
     * @param col
     * @return
     */
    int getMaxWidth(int col) {
        double maxWidth = 0;

        for (String[] row : rows) {
            maxWidth = Math.max(maxWidth, msgLength(row[col]));
        }

        return (int) maxWidth;
    }

    /**
     * @param msg
     * @return
     */
    public static String centerInLine(String msg) {
        return centerInLineOf(msg, LINE_LENGTH);
    }

    /**
     * @param msg
     * @param lineLength
     * @return
     */
    private static String centerInLineOf(String msg, double lineLength) {
        double length = msgLength(msg);
        double diff = lineLength - length;

        // if too big for line return it as is

        if (diff < 0) {
            return msg;
        }

        double sideSpace = diff / 2;

        // pad the left with space

        msg = paddLeftToFit(msg, lineLength - Math.floor(sideSpace));

        // padd the right with space

        msg = paddRightToFit(msg, lineLength);


        return msg;
    }

    /**
     * @param str
     * @return
     */
    public static String makeEmpty(String str) {
        if (str == null) {
            return "";
        }

        return paddLeftToFit("", msgLength(str));
    }

    /**
     * @param msg
     * @param length
     * @return
     */
    private static String cropRightToFit(String msg, double length) {
        if (msg == null || msg.length() == 0 || length == 0) {
            return "";
        }

        while (msgLength(msg) > length) {
            msg = msg.substring(0, msg.length() - 2);
        }

        return msg;
    }

    /**
     * @param msg
     * @param length
     * @return
     */
    private static String cropLeftToFit(String msg, double length) {
        if (msg == null || msg.length() == 0 || length == 0) {
            return "";
        }

        while (msgLength(msg) >= length) {
            msg = msg.substring(1);
        }

        return msg;
    }

    /**
     * Padds left til the string is a certain size
     *
     * @param msg
     * @param length
     * @return
     */
    private static String paddLeftToFit(String msg, double length) {
        if (msgLength(msg) >= length) {
            return msg;
        }

        while (msgLength(msg) < length) {
            msg = " " + msg;
        }

        return msg;
    }

    /**
     * Padds right til the string is a certain size
     *
     * @param msg
     * @param length
     * @return
     */
    private static String paddRightToFit(String msg, double length) {
        if (msgLength(msg) >= length) {
            return msg;
        }

        while (msgLength(msg) < length) {
            msg += " ";
        }

        return msg;
    }

    /**
     * Finds the length on the screen of a string. Ignores colors.
     *
     * @param str
     * @return
     */
    private static double msgLength(String str) {
        double length = 0;
        str = ChatColor.stripColor(str);

        // Loop through all the characters, skipping any color characters and their following color codes

        for (int x = 0; x < str.length(); x++) {
            int len = charLength(str.charAt(x));
            if (len > 0) {
                length += len;
            } else {
                x++;
            }
        }
        return length;
    }

    /**
     * Finds the visual length of the character on the screen.
     *
     * @param x
     * @return
     */
    private static int charLength(char x) {
        String normalized = StringSimplifier.simplifiedString(x + "");

        if ("i.:,;|!".contains(normalized)) {
            return 2;
        } else if ("l'".contains(normalized)) {
            return 3;
        } else if ("tI[]".contains(normalized)) {
            return 4;
        } else if ("fk{}<>\"*()".contains(normalized)) {
            return 5;
        } else if ("abcdeghjmnopqrsuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ1234567890\\/#?$%-=_+&^".contains(normalized)) {
            return 6;
        } else if ("@~".contains(normalized)) {
            return 7;
        } else if (normalized.equals(" ")) {
            return 4;
        } else {
            return 7;
        }
    }

    /**
     * Cuts the message apart into whole words short enough to fit on one line
     *
     */
    private static String[] wordWrap(String msg) {
        // Split each word apart

        ArrayList<String> split = new ArrayList<>(Arrays.asList(msg.split(" ")));

        // Create an array list for the output

        ArrayList<String> out = new ArrayList<>();

        // While i is less than the length of the array of words

        while (!split.isEmpty()) {
            int len = 0;

            // Create an array list to hold individual words

            ArrayList<String> words = new ArrayList<>();

            // go through the split array containing all the words, and adding them to the words array
            // until reaching the point where their width no longer fits on a chat line

            while (!split.isEmpty() && split.get(0) != null && len <= LINE_LENGTH) {
                double wordLength = msgLength(split.get(0)) + 4;

                // If a word is too long for a line

                if (wordLength > LINE_LENGTH) {
                    String[] tempArray = wordCut(len, split.remove(0));
                    words.add(tempArray[0]);
                    split.add(tempArray[1]);
                }

                // If the word is not too long to fit

                len += wordLength;

                if (len < LINE_LENGTH) {
                    words.add(split.remove(0));
                }
            }

            // Merge the words into a sentence (that now fits into a single chat line) and add them to the output array.

            String merged = combineSplit(words.toArray(new String[0]));
            out.add(merged);
        }

        // Convert to an array and return

        return out.toArray(new String[0]);
    }

    /**
     */
    private static String combineSplit(String[] string) {
        StringBuilder builder = new StringBuilder();
        for (String aString : string) {
            builder.append(aString);
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - " ".length());

        return builder.toString();
    }

    /**
     * Cuts apart a word that is too long to fit on one line
     *
     */
    private static String[] wordCut(int lengthBefore, String str) {
        int length = lengthBefore;

        // Loop through all the characters, skipping any color characters and their following color codes

        String[] output = new String[2];
        int x = 0;
        while (length < LINE_LENGTH && x < str.length()) {
            int len = charLength(str.charAt(x));
            if (len > 0) {
                length += len;
            } else {
                x++;
            }
            x++;
        }
        if (x > str.length()) {
            x = str.length();
        }

        // Add the substring to the output after cutting it

        output[0] = str.substring(0, x);

        // Add the last of the string to the output.

        output[1] = str.substring(x);
        return output;
    }

    /**
     * Outputs a single line out, crops overflow
     *
     */
    public static void saySingle(CommandSender receiver, String msg) {
        if (receiver == null) {
            return;
        }

        receiver.sendMessage(colorize(new String[]{cropRightToFit(msg, LINE_LENGTH)})[0]);
    }

    /**
     * Outputs a message to a user
     *
     */
    public static void sendMessage(@Nullable CommandSender receiver, @NotNull String msg) {
        if (receiver == null) {
            return;
        }

        receiver.sendMessage(ChatUtils.parseColors(msg));
//        String[] message = colorize(wordWrap(msg));
//
//        for (String out : message) {
//            receiver.sendMessage(out);
//        }
    }

    public static void sendMessageKey(@Nullable CommandSender receiver, @NotNull String key, @NotNull Object... args) {
        sendMessage(receiver, lang(key, receiver, args));
    }

    public void startColor(String color) {
        this.color = color;
    }

    /**
     * Send blank line
     *
     */
    public static void sendBlank(CommandSender receiver) {
        if (receiver == null) {
            return;
        }

        receiver.sendMessage(" ");
    }

    /**
     * Colors each line
     *
     */
    public static String[] say(String message) {
        return colorize(wordWrap(message));
    }
    
    public static String[] getColorizedMessage(String msg) {
        return colorize(wordWrap(msg));
    }

    private static String[] colorize(String[] message) {
        try {
            return colorizeBase(message);
        } catch (Exception ex) {
            return message;
        }
    }

    public static @Nullable String colorize(String message) {
        return colorizeBase(new String[]{ message })[0];
    }


    private static @Nullable String[] colorizeBase(String[] message) {
        if (message != null && message[0] != null && !message[0].isEmpty()) {
            // Go through each line

            String prevColor = "";
            String lastColor = "";

            int counter = 0;
            for (String msg : message) {
                // Loop through looking for a color code
                for (int x = 0; x < msg.length(); x++) {
                    // If the char is color code
                    if (msg.codePointAt(x) == 167) {
                        // advance x to the next character
                        x += 1;

                        if (x < msg.length()) {
                            lastColor = ChatColor.COLOR_CHAR + "" + msg.charAt(x);
                        }
                    }
                }
                // Replace the message with the colorful message

                message[counter] = prevColor + msg;
                prevColor = lastColor;
                counter++;
            }
        }

        //noinspection ConstantConditions
        return message;
    }
}