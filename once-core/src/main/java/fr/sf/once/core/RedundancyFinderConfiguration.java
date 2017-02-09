package fr.sf.once.core;

import fr.sf.once.comparator.CodeComparator;

public interface RedundancyFinderConfiguration {

    int getMinimalTokenNumberDetection();

    Class<? extends CodeComparator> getCodeComparatorClass();

}