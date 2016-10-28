package fr.sf.once.report;

import java.util.Comparator;

import fr.sf.once.model.Redundancy;

class ComparatorRedundancyByTokenNumber implements Comparator<Redundancy> {
    @Override
    public int compare(Redundancy redundancy1, Redundancy redundancy2) {
        return redundancy2.getDuplicatedTokenNumber() - redundancy1.getDuplicatedTokenNumber();
    }
}