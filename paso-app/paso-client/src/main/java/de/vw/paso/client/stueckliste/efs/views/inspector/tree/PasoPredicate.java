package de.vw.paso.client.stueckliste.efs.views.inspector.tree;

import java.util.Map;
import java.util.function.Predicate;

import javafx.scene.control.TableColumnBase;

@FunctionalInterface
public interface PasoPredicate<T> extends Predicate<T> {

    default PasoPredicate<T> andAll(Map<TableColumnBase<T, ?>, ? extends Predicate<? super T>> predicateMap) {
        return t -> {
            boolean fitsPredicate = test(t);

            for (TableColumnBase<T, ?> key : predicateMap.keySet()) {
                if (!predicateMap.get(key).equals(this)) {
                    fitsPredicate = fitsPredicate && predicateMap.get(key).test(t);
                }
            }

            return fitsPredicate;
        };
    }
}