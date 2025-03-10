package com.assessment.exceljsonparser.service;

import com.assessment.exceljsonparser.dto.JsonToExcelDto;
import com.assessment.exceljsonparser.exception.InvalidConversionException;
import com.assessment.exceljsonparser.exception.InvalidFileFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface ExcelService {
    public Map<String, Object> convertExcelToJson(MultipartFile multipartFile) throws IOException;
    public Map<String, Map<String, Object>> generateJsonSchema(MultipartFile multipartFile) throws IOException, InvalidFileFormatException;
    public byte[] convertJsonToExcel(JsonToExcelDto jsonToExcelDto) throws InvalidConversionException, IOException;
}
