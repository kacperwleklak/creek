package pl.poznan.put.kacperwleklak.common.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
@Slf4j
public class CollectionUtils {

    public static <T> List<T> concatLists(List<T> list1, List<T> list2) {
        return Stream
                .concat(list1.stream(), list2.stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T> CommonPrefixResult<T> longestCommonPrefix(List<T> list1, List<T> list2) {
        if (list1 == null || list2 == null) {
            return CommonPrefixResult.empty();
        }
        int shorterSize = Math.min(list1.size(), list2.size());
        List<T> commonPrefix = new ArrayList<>();
        int i = 0;
        for (;i < shorterSize; i++) {
            var l1obj = list1.get(i);
            var l2obj = list2.get(i);
            if (l1obj.equals(l2obj)) {
                commonPrefix.add(l1obj);
            } else {
                break;
            }
        }
        return new CommonPrefixResult<>(commonPrefix, list1.subList(i, list1.size()), list2.subList(i, list2.size()));
    }

    /**
     * Returns difference between two lists
     * List1: [E, G, H, L, O]
     * List2: [I, K, L, E, G]
     * Returns: [H, O]
     * @param list1 - primary list
     * @param list2 - secondary list
     * @return elements that are in list1, but not in list2
     */
    public static <T> List<T> differenceToList(Collection<T> list1, Collection<T> list2) {
        if (list1 == null || list2 == null) {
            return Collections.emptyList();
        }
        List<T> diffList1 = new ArrayList<>(list1);
        diffList1.removeAll(list2);
        return new ArrayList<>(diffList1);
    }

    public static <T> Set<T> differenceToSet(Collection<T> collection1, Collection<T> collection2) {
        if (collection1 == null || collection2 == null) {
            return Collections.emptySet();
        }
        Set<T> diffList = new HashSet<>(collection1);
        diffList.removeAll(collection2);
        return diffList;
    }
}

