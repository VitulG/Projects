package com.assessment.exceljsonparser.controller;

import com.assessment.exceljsonparser.dto.JsonToExcelDto;
import com.assessment.exceljsonparser.exception.EmptyFileException;
import com.assessment.exceljsonparser.exception.InvalidConversionException;
import com.assessment.exceljsonparser.exception.InvalidFileFormatException;
import com.assessment.exceljsonparser.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    private final ExcelService excelService;

    @Autowired
    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcelFile(@RequestParam("file") MultipartFile multipartFile) {
        try {
            if(multipartFile.isEmpty()) {
                throw new EmptyFileException("Given file is empty");
            }

            if(!multipartFile.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    && !multipartFile.getContentType().equals("application/vnd.ms-excel")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Invalid file format. " +
                        "Only Excel (.xlsx) and (.xls) files are supported");
            }
            Map<String, Object> jsonResponse = excelService.convertExcelToJson(multipartFile);
            return ResponseEntity.ok(jsonResponse);
        } catch (EmptyFileException emptyFileException) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emptyFileException.getMessage());
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }
    }

    @PostMapping("/schema")
    public ResponseEntity<Map<String, Map<String, Object>>> generateJsonSchema(@RequestParam("file")
                                                                               MultipartFile file) {
        try {
            Map<String, Map<String, Object>> jsonSchema = excelService.generateJsonSchema(file);
            return ResponseEntity.ok(jsonSchema);
        } catch (InvalidFileFormatException invalidFileFormatException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error",
                    Map.of("message", "Invalid file format")));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error",
                    Map.of("message", "failed to proceed")));
        }
    }

    @PostMapping("/json-to-excel")
    public ResponseEntity<byte[]> convertJsonToExcel(@RequestBody JsonToExcelDto jsonToExcelDto) {
        try {
            byte[] excelFile = excelService.convertJsonToExcel(jsonToExcelDto);
            return ResponseEntity.ok().body(excelFile);
        } catch (InvalidConversionException invalidConversionException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new byte[]{});
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new byte[]{});
        }
    }
}
