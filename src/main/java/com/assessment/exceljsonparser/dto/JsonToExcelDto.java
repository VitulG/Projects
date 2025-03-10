package com.assessment.exceljsonparser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JsonToExcelDto {
    private Map<String, List<Map<String, Object>>> sheets;
}
