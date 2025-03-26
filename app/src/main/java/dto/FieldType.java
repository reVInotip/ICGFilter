package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum FieldType {
    @JsonProperty("undefined") UNDEFINED,
    @JsonProperty("int") INTEGER,
    @JsonProperty("double") DOUBLE,
    @JsonProperty("matrix") MATRIX
}
