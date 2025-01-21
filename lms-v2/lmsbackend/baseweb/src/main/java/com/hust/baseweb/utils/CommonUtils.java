package com.hust.baseweb.utils;

import com.hust.baseweb.model.querydsl.SortCriteria;
import org.javatuples.Pair;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.*;

public class CommonUtils {

    public static int SEQ_ID_LEN = 6;

    public static Sort buildSortBySortCriteria(List<SortCriteria> sort) {
        if (sort.size() == 0) {
            return null;
        }
        List<Order> orders = new ArrayList<>();
        for (SortCriteria sortCriteria : sort) {
            if (sortCriteria.isAsc()) {
                orders.add(new Order(Sort.Direction.ASC, sortCriteria.getField()));
            } else {
                orders.add(new Order(Sort.Direction.DESC, sortCriteria.getField()));
            }
        }
        return Sort.by(orders);

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String buildQueryDslPath(Pair<Map<String, String>, Map<String, Pair>> pair, String field) {
        String[] elm = field.split("\\.");
        StringBuilder result = new StringBuilder();
        Pair<Map<String, String>, Map<String, Pair>> pairTmp = pair;
        for (int i = 0; i < elm.length - 1; i++) {
            result.append(pairTmp.getValue0().get(elm[i]).length() != 0 ? (pairTmp.getValue0().get(elm[i]) + ".") : "");
            pairTmp = pairTmp.getValue1().get(elm[i]);
        }
        result.append(pairTmp.getValue0().get(elm[elm.length - 1]));
        return result.toString();
    }

    public static String buildSeqId(int idx) {
        StringBuilder stringBuilder = new StringBuilder(idx + "");
        while (stringBuilder.length() < SEQ_ID_LEN) {
            stringBuilder.insert(0, "0");
        }
        return stringBuilder.toString();
    }

    public static int convertStr2Int(String s) {
        try {
            //s.replaceAll("0"," ");
            System.out.println("convertStr2Int, s = " + s);

            s = s.trim();
            int num = Integer.valueOf(s);
            return num;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String[] generateNextSeqId(String[] seqIds, int sz) {
        String[] res = new String[sz];
        int[] idx = new int[seqIds.length];
        try {
            int minValue = 0;
            HashSet<Integer> S = new HashSet<Integer>();
            for (int i = 0; i < seqIds.length; i++) {

                idx[i] = convertStr2Int(seqIds[i]);
                System.out.println("generateNextSeqId, convert get " + idx[i]);
                if (idx[i] < 0) {
                    return null;
                }
                if (i == 0) {
                    minValue = idx[i];
                } else {
                    if (minValue > idx[i]) {
                        minValue = idx[i];
                    }
                }
                S.add(idx[i]);
            }
            for (int i = 0; i < sz; i++) {
                int n = minValue;
                do {
                    n = n + 1;
                } while (S.contains(n));
                S.add(n);
                res[i] = buildSeqId(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return res;
    }

    public static int[] genRandom(int a, int n, Random R) {
        // return a random elements from 0,...,n-1
        if (a > n) {
            return null;
        }
        int[] idx = new int[n];
        for (int j = 0; j < n; j++) {
            idx[j] = j;
        }
        int[] ans = new int[a];
        for (int j = 0; j < a; j++) {
            int k = R.nextInt(n);
            ans[j] = idx[k];
            // remove the kth element by swapping idx[k] with idx[n-1]
            int tmp = idx[k];
            idx[k] = idx[n - 1];
            idx[n - 1] = tmp;
            n = n - 1;
        }
        return ans;
    }

    public static Pageable getPageable(Integer page, Integer size, Sort sort) {
        if (page == null || page < 0) {
            page = 0;
        }

        if (size == null || size < 1) {
            size = Integer.MAX_VALUE;
        }

        return PageRequest.of(page, size, sort);
    }

}
