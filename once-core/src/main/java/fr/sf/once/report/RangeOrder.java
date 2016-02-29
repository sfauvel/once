package fr.sf.once.report;

import java.util.Comparator;

import org.apache.commons.lang.math.IntRange;

class RangeOrder implements Comparator<IntRange> {
    @Override
    public int compare(IntRange o1, IntRange o2) {
        int compare = o1.getMinimumInteger() - o2.getMinimumInteger();
        if (compare == 0) {
            compare = o1.getMaximumInteger() - o2.getMaximumInteger();
        }
        return compare;
    }
}