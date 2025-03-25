package dto;

import java.util.HashMap;

public class FilterDto {
    private String name = "";
    private HashMap<String, FilterParam> filterParams = new HashMap<>();

    public FilterDto(String name) {
        this.name = name;
    }

    public HashMap<String, FilterParam> getFilterParams() {
        return filterParams;
    }

    public void setFilterParams(HashMap<String, FilterParam> filterParams) {
        this.filterParams = filterParams;
    }

    public void addFilterParam(String elName, FilterParam filterParam) {
        this.filterParams.put(elName, filterParam);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
