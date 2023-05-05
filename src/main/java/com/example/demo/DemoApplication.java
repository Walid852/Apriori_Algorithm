package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.*;
@SpringBootApplication
class Apriori {
    public static void main(String[] args) {

        ExcelReader reader = new ExcelReader();
        List<List<String>> transactions =reader.readExcelFile("CoffeeShopTransactions.xlsx");
        Scanner input = new Scanner(System.in);
        input.useLocale(Locale.ROOT);
        System.out.println("Enter minSupport ");
        int minSupport = input.nextInt();
        System.out.println("Enter minConfidence ");
        int minConfidence = input.nextInt();

        // Generate frequent itemises of candidate 1
        Map<String, Integer> itemsetes = new HashMap<String, Integer>();
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                itemsetes.put(item, itemsetes.getOrDefault(item,0) + 1);
            }
        }
        // Filter frequent itemsetes of size 1 by elmainate Less than min support
        Map<String, Integer> frequentItemsets = new HashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : itemsetes.entrySet()) {
            if (entry.getValue() >= minSupport) {
                frequentItemsets.put(entry.getKey(), entry.getValue());
            }
        }
        int k = 2;
        while (!frequentItemsets.isEmpty()) {
            // Generate candidate itemsetes of size k
            Map<String, Integer> candidates = new HashMap<String, Integer>();
            for (Map.Entry<String, Integer> entry1 : frequentItemsets.entrySet()) {
                for (Map.Entry<String, Integer> entry2 : frequentItemsets.entrySet()) {
                    if (!entry1.getKey().equals(entry2.getKey())) {
                        String[] items1 = entry1.getKey().split(",");
                        String[] items2 = entry2.getKey().split(",");
                        Set<String> union = new HashSet<String>(Arrays.asList(items1));
                        union.addAll(Arrays.asList(items2));
                        if (union.size() == k) {
                            String candidate = String.join(",", union);
                            candidates.put(candidate, 0);
                        }
                    }
                }
            }

            // Count frequency of candidate itemsetes
            for (List<String> transaction : transactions) {
                for (String candidate : candidates.keySet()) {
                    boolean transactionContainsAllItemsInCandidate = true;
                    for (String item : candidate.split(",")) {
                        if (!transaction.contains(item)) {
                            transactionContainsAllItemsInCandidate = false;
                            break;
                        }
                    }
                    if (transactionContainsAllItemsInCandidate) {
                        candidates.put(candidate, candidates.get(candidate) + 1);
                    }
                }
            }

            //temp frequentItemsets
            Map<String, Integer> frequentItemsetss = new HashMap<String, Integer>();
            // filter by eliminate candidates less than minSupport
            for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
                if (entry.getValue() >= minSupport) {
                    frequentItemsetss.put(entry.getKey(), entry.getValue());
                }
            }
            //after filter check if frequentItemsetss empty or not
            if (!frequentItemsetss.isEmpty()){
            frequentItemsets.clear();
            for (Map.Entry<String, Integer> entry : candidates.entrySet()) {
                if (entry.getValue() >= minSupport) {
                    frequentItemsets.put(entry.getKey(), entry.getValue());
                }

            }
            }
            else {
                break;
            }
            k++;
        }
        System.out.println(frequentItemsets);
        //Calculate Confidence and eliminate frequentItemsets less than min confidence
        for (Map.Entry<String, Integer> entry : frequentItemsets.entrySet()) {
            //Generate combinations
            List<String> items = Arrays.stream(entry.getKey().split(",")).toList();
            List<List<String>> combinations = generateAllCombinationsForFrequentItemSets(items);

            //Generate candidates
            Map<String, Integer> candidates = new HashMap<String, Integer>();
            for (List<String> combination:combinations) {
                if (combination.isEmpty())continue;
                if (combination.size()==items.size())continue;
                    String candidate = String.join(",", combination);
                    candidates.put(candidate, 0);
            }

            // Count frequency of candidate itemsetes
            for (List<String> transaction : transactions) {
                for (String candidate : candidates.keySet()) {
                    boolean transactionContainsAllItemsInCandidate = true;
                    for (String item : candidate.split(",")) {
                        if (!transaction.contains(item)) {
                            transactionContainsAllItemsInCandidate = false;
                            break;
                        }
                    }
                    if (transactionContainsAllItemsInCandidate) {
                        candidates.put(candidate, candidates.get(candidate) + 1);
                    }
                }
            }

            // check it with minConfidence
            for (Map.Entry<String, Integer> candidate : candidates.entrySet()) {
                double v= entry.getValue().doubleValue() / candidate.getValue().doubleValue();

                if(v*100>=minConfidence){
                    System.out.println('{'+candidate.getKey()+'}'+" --> "+'{'+entry.getKey()+'}'+"--------"+v);
                }
            }

        }

    }
    public static List<List<String>> generateAllCombinationsForFrequentItemSets(List<String> list) {
        List<List<String>> result = new ArrayList<>();
        generateCombinationsManagerForCandidate(result, new ArrayList<>(), list, 0);
        return result;
    }

    private static void generateCombinationsManagerForCandidate(List<List<String>> result, List<String> tempList, List<String> list, int start) {
        result.add(new ArrayList<>(tempList));
        for (int i = start; i < list.size(); i++) {
            tempList.add(list.get(i));
            generateCombinationsManagerForCandidate(result, tempList, list, i + 1);
            tempList.remove(tempList.size() - 1);
        }
    }
}


