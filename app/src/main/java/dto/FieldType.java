package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FieldType {
    @JsonProperty("int") INTEGER,
    @JsonProperty("double") DOUBLE,
    @JsonProperty("matrix") MATRIX,
    @JsonProperty("list") LIST,
    @JsonProperty("matrixData") MATRIX_DATA
}
