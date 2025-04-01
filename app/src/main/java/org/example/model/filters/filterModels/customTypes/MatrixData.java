package org.example.model.filters.filterModels.customTypes;

import java.util.List;

public class MatrixData {
    private int currSizeForRedChannel;
    private int currSizeForGreenChannel;
    private int currSizeForBlueChannel;
    private final List<Integer> availableSizes;

    public MatrixData(int currSizeForRedChannel, int currSizeForGreenChannel, int currSizeForBlueChannel, List<Integer> availableSizes) {
        this.currSizeForRedChannel = currSizeForRedChannel;
        this.currSizeForGreenChannel = currSizeForGreenChannel;
        this.currSizeForBlueChannel = currSizeForBlueChannel;
        this.availableSizes = availableSizes;
    }

    public List<Integer> getAvailableSizes() {
        return availableSizes;
    }

    public void setCurrSizeForRedChannel(int size) {
        currSizeForRedChannel = size;
    }

    public int getCurrSizeForRedChannel() {
        return currSizeForRedChannel;
    }

    public int getCurrSizeForBlueChannel() {
        return currSizeForBlueChannel;
    }

    public void setCurrSizeForBlueChannel(int currSizeForBlueChannel) {
        this.currSizeForBlueChannel = currSizeForBlueChannel;
    }

    public void setCurrSizeForGreenChannel(int currSizeForGreenChannel) {
        this.currSizeForGreenChannel = currSizeForGreenChannel;
    }

    public int getCurrSizeForGreenChannel() {
        return currSizeForGreenChannel;
    }
}
