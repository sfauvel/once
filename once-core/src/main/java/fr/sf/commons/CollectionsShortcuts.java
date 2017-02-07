package fr.sf.commons;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Methods to write easily some simple collection transformations.
 */
public final class CollectionsShortcuts {
    
    private CollectionsShortcuts() {
        
    }
    
    public static <T, R> Set<R> mapToSet(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    public static <T, R> List<R> mapToList(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return list.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

}
