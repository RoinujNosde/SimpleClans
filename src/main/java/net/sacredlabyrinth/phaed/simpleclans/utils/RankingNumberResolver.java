package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/**
 *
 * @author RoiunjNosde
 * @param <E> the Element being ranked
 * @param <S> the Object used to sort the elements
 */
public class RankingNumberResolver<E, S extends Comparable<S>> {

    private final List<E> elements;
    private final Function<E, S> orderBy;
    private final RankingType type;
    private final boolean asc;
    private final Map<E, Integer> positions = new HashMap<>();

    public RankingNumberResolver(@NotNull List<E> elements, @NotNull Function<E, S> orderBy, boolean asc,
                                 @NotNull RankingType type) {
        this.elements = elements;
        this.orderBy = orderBy;
        this.type = type;
        this.asc = asc;
        loadPositions();
    }

    private void loadPositions() {
        Comparator<E> comparator = Comparator.comparing(orderBy);
        elements.sort(asc ? comparator : comparator.reversed());

        if (type == RankingType.DENSE) {
            E previous = null;
            int position = 1;
            for (E e : elements) {
                if (previous == null) {
                    positions.put(e, position);
                    previous = e;
                    continue;
                }
                if (comparator.compare(previous, e) != 0) {
                    position++;
                }
                previous = e;
                positions.put(e, position);
            }
        }
    }

    public int getRankingNumber(@NotNull E element) {
        if (type == RankingType.DENSE) {
            return positions.getOrDefault(element, -1);
        }
        int indexOf = elements.indexOf(element);
        return indexOf == -1 ? -1 : indexOf + 1;
    }

    public enum RankingType {
        /**
         * Example: 1223
         */
        DENSE,
        /**
         * Example: 1234
         */
        ORDINAL
    }
}
