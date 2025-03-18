package view;

import event.Event;
import event.StartEvent;
import event.observers.Observer;
import model.MainModel;
import model.ModelTasksManager;
import model.tasks.LoadTask;
import model.tasks.SaveTask;
import view.components.Frame;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import java.util.Map;

public class MainFrame extends Frame implements Observer {
    private static boolean isCreated = false;

    public static MainFrame create() {
        if (!isCreated) {
            isCreated = true;

            return new MainFrame();
        }

        return null;
    }

    private void createSaveButton(ActionListener saveListener) {
        addToolbarButton("Save", "Сохраняет изображение", "/utils/save.png", saveListener);
        addToolbarSeparator();
    }

    private void createLoadButton(ActionListener loadListener) {
        addToolbarButton("load", "Открывает изображение", "/utils/open.png", loadListener);
        addToolbarSeparator();
    }

    private void createToolbarButtons(ActionListener saveListener, ActionListener loadListener) {
        createSaveButton(saveListener);
        createLoadButton(loadListener);
    }

    private MainFrame() {
        super(MainModel.WIDTH, MainModel.HEIGHT);

        addMenu("File");
        addMenu("Modify");
        addMenu("Filter");
        addMenu("Rendering");
        addMenu("Help");

        ActionListener saveListener = action -> {
            FileDialog fd = new FileDialog(MainFrame.this, "Сохранить изображение", FileDialog.SAVE);
            fd.setFile("*.png");
            fd.setName("Image.png");
            fd.setVisible(true);
            if (fd.getFile() != null && fd.getDirectory() != null) {
                ModelTasksManager.addTask(new SaveTask(fd.getDirectory(), fd.getFile()));
            }
        };

        ActionListener loadListener = action -> {
            FileDialog fd = new FileDialog(MainFrame.this, "Открыть изображение", FileDialog.LOAD);
            fd.setFile("*.png; *.jpg; *.jpeg; *.gif; *.bmp");
            fd.setName("Image.png");
            fd.setVisible(true);
            if (fd.getFile() != null && fd.getDirectory() != null) {
                ModelTasksManager.addTask(new LoadTask(fd.getDirectory(), fd.getFile()));
            }
        };

        createToolbarButtons(saveListener, loadListener);
    }

    public List<Observer> getInternalObservers() {
        List<Observer> observers = new ArrayList<>();
        observers.add(panel);

        return observers;
    }

    @Override
    public void update(Event event) {
        if (event instanceof StartEvent startEvent) {
            addComponentListener(startEvent.stateChangeAdapter);
            showFrame();
        }
    }

    public void addToolsButtons(String[] tools, Map<String, String> toolsDescr, Map<String, String> icons) {
        addFilterGroup(tools, toolsDescr, icons);
    }
}
