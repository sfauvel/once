package fr.sf.once.report;

import java.util.Comparator;

import fr.sf.once.model.Redundancy;

class ComparatorRedundancyByTokenNumber implements Comparator<Redundancy> {
    @Override
    public int compare(Redundancy redondance1, Redundancy redondance2) {
        return redondance2.getDuplicatedTokenNumber() - redondance1.getDuplicatedTokenNumber();
    }
}