package com.scaler.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchDtos {
    private String query;
    private Integer pageNumber;
    private Integer pageSize;
}
