package com.assessment.exceljsonparser.service;

import com.assessment.exceljsonparser.dto.JsonToExcelDto;
import com.assessment.exceljsonparser.exception.InvalidConversionException;
import com.assessment.exceljsonparser.exception.InvalidFileFormatException;
import com.assessment.exceljsonparser.util.ExcelToJsonConvertor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Override
    public Map<String, Object> convertExcelToJson(MultipartFile multipartFile) throws IOException {
        return ExcelToJsonConvertor.parseExcelFile(multipartFile);
    }

    @Override
    public Map<String, Map<String, Object>> generateJsonSchema(MultipartFile multipartFile) throws IOException, InvalidFileFormatException {

        if(!multipartFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                && !multipartFile.getContentType().equals("application/vnd.ms-excel")) {
            throw new InvalidFileFormatException("Invalid file format");
        }

        Map<String, Map<String, Object>> schema = new LinkedHashMap<>();

        Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream());

        for(Sheet sheet : workbook) {
            Map<String, Object> currentSheet = new LinkedHashMap<>();
            Row rowHeader = sheet.getRow(0);

            if(rowHeader != null) {
                for(Cell cell : rowHeader) {
                    String columnName = cell.getStringCellValue();
                    String columnType = inferColumnType(sheet, cell.getColumnIndex());
                    currentSheet.put(columnName, columnType);
                }
            }
            schema.put(sheet.getSheetName(), currentSheet);
        }
        return schema;
    }

    @Override
    public byte[] convertJsonToExcel(JsonToExcelDto jsonToExcelDto) throws InvalidConversionException, IOException {
        Workbook workbook = new XSSFWorkbook();

        if(jsonToExcelDto == null) {
            throw new InvalidConversionException("Given JASON is empty");
        }

        for(Map.Entry<String, List<Map<String, Object>>> sheetEntry : jsonToExcelDto.getSheets().entrySet()) {
            String sheetName = sheetEntry.getKey();
            List<Map<String, Object>> rows = sheetEntry.getValue();

            Sheet newSheet = workbook.createSheet(sheetName);

            if(!rows.isEmpty()) {
                Row rowHeader = newSheet.createRow(0);
                int columnIndex = 0;

                for(String columnName : rows.get(0).keySet()) {
                    rowHeader.createCell(columnIndex++).setCellValue(columnName);
                }

                int rowIndex = 1;
                for(Map<String, Object> rowData : rows) {
                    Row newRow = newSheet.createRow(rowIndex++);
                    columnIndex = 0;

                    for(Object value : rowData.values()) {
                        Cell cell = newRow.createCell(columnIndex++);
                        if(value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        }else {
                            cell.setCellValue(value.toString());
                        }
                    }
                }
            }
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    private String inferColumnType(Sheet sheet, int columnIndex) {
        for(int i=1; i<=sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if(row != null) {
                Cell cell = row.getCell(columnIndex);
                if(cell != null) {
                    return switch (cell.getCellType()) {
                        case STRING -> "String";
                        case NUMERIC -> "Integer";
                        case BOOLEAN -> "Boolean";
                        default -> "Unknown";
                    };
                }
            }
        }
        return "String";
    }
}
