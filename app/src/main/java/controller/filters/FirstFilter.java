package controller.filters;

import controller.ToolView;

@ToolView(name = "First", descr = "это первая проба пера")
public class FirstFilter extends IFilter {
    @Override
    public void sendToModel() {
        return;
    }
}
