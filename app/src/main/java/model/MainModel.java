package model;

import event.StartEvent;
import event.observers.Observable;
import view.MainFrame;

public class MainModel extends Observable {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;

    public static boolean isCreated = false;

    public static MainModel create() {
        if (!isCreated) {
            isCreated = true;
            return new MainModel();
        }

        return null;
    }

    private MainModel() {
        add(MainFrame.create());
    }

    public void start() {
        update(new StartEvent());
    }
}
