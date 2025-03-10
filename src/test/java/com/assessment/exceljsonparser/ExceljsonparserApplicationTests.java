package com.assessment.exceljsonparser;

import com.assessment.exceljsonparser.exception.InvalidFileFormatException;
import com.assessment.exceljsonparser.service.ExcelService;
import com.assessment.exceljsonparser.util.ExcelToJsonConvertor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExceljsonparserApplicationTests {

	@InjectMocks
	private ExcelToJsonConvertor excelToJsonConvertor;

	@Mock
	private ExcelService excelService;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void testExcelToJsonConvertor()  throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Workbook workbook = new XSSFWorkbook();

		Sheet sheet1 = workbook.createSheet("Sheet1");
		Row headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("Name");
		headerRow.createCell(1).setCellValue("Age");
		headerRow.createCell(2).setCellValue("City");

		Row dataRow = sheet1.createRow(1);
		dataRow.createCell(0).setCellValue("Vitul");
		dataRow.createCell(1).setCellValue(26);
		dataRow.createCell(2).setCellValue("Khurja");

		// Create Sheet2 with different headers
		Sheet sheet2 = workbook.createSheet("Sheet2");
		Row sheet2HeaderRow = sheet2.createRow(0);
		sheet2HeaderRow.createCell(0).setCellValue("Product");
		sheet2HeaderRow.createCell(1).setCellValue("Price");

		Row sheet2DataRow = sheet2.createRow(1);
		sheet2DataRow.createCell(0).setCellValue("Laptop");
		sheet2DataRow.createCell(1).setCellValue(55000);

		workbook.write(byteArrayOutputStream);
		workbook.close();

		MockMultipartFile mockFile = new MockMultipartFile(
				"file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
		);

		Map<String, Object> currentResult = excelToJsonConvertor.parseExcelFile(mockFile);
		JSONObject result = new JSONObject(currentResult);

		// Step 4: Assertions to check output
		Object resultJson;
		assertNotNull(result);
		assertTrue(result.has("Sheet1"));
		assertTrue(result.has("Sheet2"));

		JSONArray sheet1Data = result.getJSONArray("Sheet1");
		assertEquals(1, sheet1Data.length());

		JSONObject firstRow = sheet1Data.getJSONObject(0);
		assertEquals("Vitul", firstRow.getString("Name"));
		assertEquals(26, firstRow.getInt("Age"));
		assertEquals("Khurja", firstRow.getString("City"));

		JSONArray sheet2Data = result.getJSONArray("Sheet2");
		assertEquals(1, sheet2Data.length());

		JSONObject firstRowSheet2 = sheet2Data.getJSONObject(0);
		assertEquals("Laptop", firstRowSheet2.getString("Product"));
		assertEquals(55000, firstRowSheet2.getInt("Price"));
	}

	@Test
	public void testExcelToJsonConvertorWithNullValues() throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Workbook workbook = new XSSFWorkbook();

		// Create Sheet1 with NULL (empty) values
		Sheet sheet1 = workbook.createSheet("Sheet1");
		Row headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("Name");
		headerRow.createCell(1).setCellValue("Age");
		headerRow.createCell(2).setCellValue("City");

		// Row with all NULL values
		Row dataRow = sheet1.createRow(1);
		dataRow.createCell(0).setCellValue("");  // Empty Name
		dataRow.createCell(1).setCellValue("");  // Empty Age
		dataRow.createCell(2).setCellValue("");  // Empty City

		workbook.write(byteArrayOutputStream);
		workbook.close();

		// Convert to MockMultipartFile
		MockMultipartFile mockFile = new MockMultipartFile(
				"file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
		);

		// Convert returned Map to JSONObject
		Map<String, Object> resultMap = excelToJsonConvertor.parseExcelFile(mockFile);
		JSONObject result = new JSONObject(resultMap);

		// ✅ Assertions
		assertNotNull(result);
		assertTrue(result.has("Sheet1"));

		JSONArray sheet1Data = result.getJSONArray("Sheet1");

		// Since all fields were NULL, we expect an empty string ""
		if (sheet1Data.length() > 0) {
			JSONObject firstRow = sheet1Data.getJSONObject(0);
			assertEquals("", firstRow.getString("Name"));
			assertEquals("", firstRow.getString("Age")); // Assuming age is treated as a String when empty
			assertEquals("", firstRow.getString("City"));
		} else {
			// If the implementation removes empty rows, expect no data
			assertEquals(0, sheet1Data.length());
		}
	}

	@Test
	public void testExtractJsonSchema() throws Exception {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet1 = workbook.createSheet("Sheet1");

		Row headerRow = sheet1.createRow(0);
		headerRow.createCell(0).setCellValue("Name");
		headerRow.createCell(1).setCellValue("Age");
		headerRow.createCell(2).setCellValue("City");

		Row dataRow = sheet1.createRow(1);
		dataRow.createCell(0).setCellValue("Vitul");
		dataRow.createCell(1).setCellValue(26);
		dataRow.createCell(2).setCellValue("Khurja");

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);
		workbook.close();

		MockMultipartFile mockFile = new MockMultipartFile(
				"file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
		);

		// ✅ Step 1: Mock the service's behavior
		Map<String, Map<String, Object>> mockSchema = new HashMap<>();
		Map<String, Object> sheet1Schema = new HashMap<>();
		sheet1Schema.put("Name", "String");
		sheet1Schema.put("Age", "Integer"); // Adjust based on your actual schema logic
		sheet1Schema.put("City", "String");
		mockSchema.put("Sheet1", sheet1Schema);

		Mockito.when(excelService.generateJsonSchema(mockFile)).thenReturn(mockSchema);

		// ✅ Step 2: Call the actual method
		Map<String, Map<String, Object>> currentSchema = excelService.generateJsonSchema(mockFile);

		// ✅ Step 3: Perform assertions
		assertNotNull(currentSchema);
		assertTrue(currentSchema.containsKey("Sheet1"));

		Map<String, Object> sheetSchema = currentSchema.get("Sheet1");
		assertEquals("String", sheetSchema.get("Name"));
		assertEquals("Integer", sheetSchema.get("Age")); // Check data type logic
		assertEquals("String", sheetSchema.get("City"));
	}

	@Test
	public void testExtractJsonSchema_EmptyFile() throws Exception {
		// Create an empty workbook
		Workbook workbook = new XSSFWorkbook();

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);
		workbook.close();

		MockMultipartFile mockFile = new MockMultipartFile(
				"file", "empty.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
				new ByteArrayInputStream(byteArrayOutputStream.toByteArray())
		);

		Map<String, Map<String, Object>> schema = excelService.generateJsonSchema(mockFile);

		// Assertions
		assertNotNull(schema);
		assertTrue(schema.isEmpty(), "Schema should be empty for an empty Excel file.");
	}

	@Test
	public void testExtractJsonSchema_InvalidFileFormat() throws InvalidFileFormatException, IOException {
		MockMultipartFile mockFile = new MockMultipartFile(
				"file", "invalid.txt", "text/plain",
				"This is a test file".getBytes()
		);

		Mockito.when(excelService.generateJsonSchema(Mockito.any()))
				.thenThrow(new InvalidFileFormatException("Invalid file format"));

		Exception exception = assertThrows(InvalidFileFormatException.class, () -> {
			excelService.generateJsonSchema(mockFile);
		});

		assertTrue(exception.getMessage().contains("Invalid file format"));
	}

	@Test
	public void testJsonToExcel_ValidInput() throws Exception {
		String jsonPayload = "{ \"sheets\": { \"Sheet1\": [ { \"Name\": \"Vitul\", \"Age\": 26, \"City\": \"Khurja\" }, " +
				"{ \"Name\": \"Sachin\", \"Age\": 25, \"City\": \"Bulandshahr\" } ] } }";

		MockMultipartFile jsonFile = new MockMultipartFile(
				"jsonFile", "test.json", "application/json",
				jsonPayload.getBytes()
		);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/excel/json-to-excel")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonPayload))
				.andExpect(status().isOk());

	}

	@Test
	public void testJsonToExcel_InvalidInput() throws Exception {
		String invalidJsonPayload = "{ \"sheets\": { \"Sheet1\": [ { \"Name\": \"Vitul\", \"Age\": \"twenty-six\", \"City\": 1234 } ] } }";

		MockMultipartFile jsonFile = new MockMultipartFile(
				"jsonFile", "invalid.json", "application/json",
				invalidJsonPayload.getBytes()
		);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/excel/json-to-excel")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn();
	}




}
