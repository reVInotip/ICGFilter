package view;

import event.Event;
import event.StartEvent;
import event.observers.Observer;
import model.MainModel;
import view.components.Frame;

public class MainFrame extends Frame implements Observer {
    private static boolean isCreated = false;

    public static MainFrame create() {
        if (!isCreated) {
            isCreated = true;

            return new MainFrame();
        }

        return null;
    }

    private MainFrame() {
        super(MainModel.WIDTH, MainModel.HEIGHT);
    }

    @Override
    public void update(Event event) {
        if (event instanceof StartEvent) {
            showFrame();
        }
    }
}
