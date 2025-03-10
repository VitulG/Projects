package com.assessment.exceljsonparser.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

public class ExcelToJsonConvertor {
    public static Map<String, Object> parseExcelFile(MultipartFile file) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        for(Sheet sheet : workbook) {
            List<Map<String, Object>> sheetData = new ArrayList<>();
            Iterator<Row> rowIterator = sheet.iterator();

            if(!rowIterator.hasNext()) {
                continue;
            }

            Row headerRow = rowIterator.next();
            List<String> headerList = new ArrayList<>();

            for(Cell cell : headerRow) {
                headerList.add(cell.getStringCellValue());
            }

            while(rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Map<String, Object> rowData = new LinkedHashMap<>();

                for(int i = 0; i < headerList.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headerList.get(i), getCellValue(cell));
                }
                sheetData.add(rowData);
            }
            result.put(sheet.getSheetName(), sheetData);
        }
        workbook.close();
        return result;
    }

    private static Object getCellValue(Cell cell) {
        if(cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                double numericValue = cell.getNumericCellValue();
                // Convert to int if it's a whole number
                if (numericValue == Math.floor(numericValue)) {
                    return (int) numericValue;
                }
                return numericValue; // Keep decimal if necessary
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
