package org.example.model.filters.filterModels.events;

public class UpdateMatrixEvent implements FilterModelEvent {
    public String paramName;
    public String dividerName;
    public String matrixName;

    public UpdateMatrixEvent(String paramName, String dividerName, String matrixName) {
        this.paramName = paramName;
        this.dividerName = dividerName;
        this.matrixName = matrixName;
    }
}
