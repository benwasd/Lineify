package ch.bfh.ti.lineify.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListUtils {
    public static <T> List<T> filter(List<T> list, rx.functions.Func1<T, Boolean> predicate) {
        List<T> filteredList = new ArrayList();

        Iterator<T> i = list.iterator();
        while (i.hasNext()) {
            T element = i.next();
            if (predicate.call(element)) {
                filteredList.add(element);
            }
        }

        return filteredList;
    }
}
