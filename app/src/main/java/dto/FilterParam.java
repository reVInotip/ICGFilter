package dto;

import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;

public class FilterParam {
    public FieldType type;
    public Integer max;
    public Integer min;
    public List<Integer> size;
    public Integer step;
    public String name;

    public FilterParam(String name, FieldType type, Integer max, Integer min, List<Integer> size, Integer step) {
        this.type = type;
        this.max = max;
        this.min = min;
        this.step = step;
        this.name = name;
        this.size = size;
    }

    public List<Object> getMinorParamsList() {
        var minorParams = new ArrayList<>();

        switch (type) {
            case INTEGER, DOUBLE -> {
                if (min == null || max == null) {
                    return null;
                }
                minorParams.add(min);
                minorParams.add(max);
                minorParams.add(step);
            }
            case MATRIX -> {
                if (min == null || max == null) {
                    return null;
                }
                minorParams.add(min);
                minorParams.add(max);
            }
            case MATRIX_DATA -> {
                if (size == null || size.isEmpty()) {
                    return null;
                }
                minorParams.add(size);
            }
        }

        return minorParams;
    }

    public boolean isValid() {
        switch (type) {
            case INTEGER, DOUBLE, MATRIX -> {
                if (min == null || max == null) {
                    return false;
                }
            }
            case MATRIX_DATA -> {
                if (size == null || size.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }
}
