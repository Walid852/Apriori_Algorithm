package com.example.demo;
// Input data
        /*String[][] transactions = {
                {"milk", "bread", "butter", "jam"},
                {"milk", "bread", "butter"},
                {"milk", "bread"},
                {"milk", "egg", "bread", "butter", "jam"},
                {"egg", "bread", "butter"}
        };*/
        /*
        * List<List<String>> transactions = new ArrayList<>(List.of(
                new ArrayList<>(List.of("milk", "bread", "butter", "jam")),
                new ArrayList<>(List.of("milk", "bread", "butter")),
                new ArrayList<>(List.of("milk", "bread")),
                new ArrayList<>(List.of("milk", "egg", "bread", "butter", "jam")),
                new ArrayList<>(List.of("egg", "bread", "butter"))

        ));*/
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelReader {

    public List<List<String>> readExcelFile(String filePath) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            List<List<String>> transactions=new LinkedList<>();
            for (Row row : sheet) {
                int x=0;
                List<String> transaction=new LinkedList<>();
                for (Cell cell : row) {
                    x++;
                    if (x >= 4) {
                        transaction.add(cell.getStringCellValue().replace(" ","").toLowerCase(Locale.ROOT));
                        //System.out.print(cell.getStringCellValue() + "\t");
                    }
                }
                transactions.add(transaction);
            }
            /*for (List<String> transactionR : transactions) {
                for (String item : transactionR) {
                    System.out.print(item + "\t");
                }
                System.out.println();
            }*/
            workbook.close();
            inputStream.close();
            return transactions;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}

