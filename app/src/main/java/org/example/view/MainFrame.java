package org.example.view;

import dto.FilterDto;
import org.example.event.Event;
import org.example.event.StartEvent;
import org.example.event.observers.Observer;

import org.example.model.MainModel;
import org.example.model.ModelTasksManager;
import org.example.model.filters.filterModels.ModelPrototype;
import org.example.model.tasks.*;
import org.example.view.components.CursorManager;
import org.example.view.components.Frame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends Frame implements Observer {
    private static boolean isCreated = false;

    public static MainFrame create(HashMap<String, FilterDto> filterDtos, HashMap<String, ModelPrototype> filterModels) {
        if (!isCreated) {
            isCreated = true;

            return new MainFrame(filterDtos, filterModels);
        }

        return null;
    }

    private void addAboutSubmenu() {
        String descr = "nothing there";
        try (InputStream fileStream = MainFrame.class.getResourceAsStream("/description.html")) {
            if (fileStream == null) {
                java.lang.System.err.println("Description file not found!");
                throw new IOException();
            }

            descr = new String(fileStream.readAllBytes());
        } catch (Exception e) {
            java.lang.System.err.println("Can not add description for About menu item: " + e.getMessage());
        }

        JEditorPane editorPane = new JEditorPane("text/html", descr);
        editorPane.setEditable(false);
        editorPane.setOpaque(true);
        editorPane.setBackground(new Color(240, 240, 240));
        addMenuItem("Help", "About", actionEvent -> {
            JOptionPane.showMessageDialog(MainFrame.this,
                    editorPane, "О программе", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void createSaveButton(ActionListener saveListener) {
        addToolbarButton("Save", "Сохраняет изображение", "/utils/save.png", saveListener);
        addToolbarSeparator();
    }

    private void createApplyButton(ActionListener applyListener) {
        addToolbarButton("Apply", "применение фильтра", "/utils/apply.png", applyListener);
        addToolbarSeparator();
    }


    private void createLoadButton(ActionListener loadListener) {
        addToolbarButton("load", "Открывает изображение", "/utils/open.png", loadListener);
        addToolbarSeparator();
    }

    private void fullScreenButton(ActionListener loadListener) {
        addToolbarButton("fullScreen", "на весь экран", "/utils/fullScreen.png", loadListener);
        addToolbarSeparator();
    }

    private void returnToOriginalButton(ActionListener loadListener) {
        addToolbarButton("returnToOriginal", "возвращает изображение в оригинальный вид", "/utils/return_original.png", loadListener);
        addToolbarSeparator();
    }

    private void returnToFilteredButton(ActionListener loadListener) {
        addToolbarButton("returnToFiltered", "возвращает изображение, к которому применён фильтр", "/utils/return_filtered.png", loadListener);
        addToolbarSeparator();
    }

    private void createToolbarButtons(ActionListener saveListener, ActionListener loadListener
            , ActionListener applyListener, ActionListener fullScreenListener, ActionListener returnToOriginalListener,
                                      ActionListener returnToFilteredListener) {
        createSaveButton(saveListener);
        createLoadButton(loadListener);
        createApplyButton(applyListener);
        fullScreenButton(fullScreenListener);
        returnToOriginalButton(returnToOriginalListener);
        returnToFilteredButton(returnToFilteredListener);
    }

    private MainFrame(HashMap<String, FilterDto> filterDtos, HashMap<String, ModelPrototype> filterModels) {
        super(MainModel.WIDTH, MainModel.HEIGHT, filterDtos, filterModels);

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

        ActionListener applyListener = action -> {
            CursorManager.showWaitCursor();
            if (ModelTasksManager.addTask(new ApplyTask(MainModel.getSelectedFilter())) < 0) {
                CursorManager.defaultCursor();
            }
        };

        ActionListener returnToOriginalListener = action -> {
            CursorManager.showWaitCursor();
            ModelTasksManager.addTask(new ReturnToOriginalImgTask());
        };

        ActionListener returnToFilteredListener = action -> {
            CursorManager.showWaitCursor();
            ModelTasksManager.addTask(new ReturnToFilteredImgTask());
        };

        ActionListener fullScreenListener = action -> {
            ModelTasksManager.addTask(new FullScreenTask());
        };

        ActionListener setBilinearInterpolation = actionEvent -> panel.interpolationType = RenderingHints.VALUE_INTERPOLATION_BILINEAR;

        ActionListener setBicubicInterpolation = actionEvent -> panel.interpolationType = RenderingHints.VALUE_INTERPOLATION_BICUBIC;

        ActionListener setNearestNeighbourInterpolation = actionEvent -> panel.interpolationType = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

        createToolbarButtons(saveListener, loadListener, applyListener, fullScreenListener, returnToOriginalListener, returnToFilteredListener);

        addMenuItem("File", "Save", saveListener);
        addMenuItem("File", "Open", loadListener);
        addMenuItem("Modify", "Apply selected filter", applyListener);

        addMenuButtonGroup(
                "Rendering",
                new HashMap<String, ActionListener>() {{
                    put("bilinear interpolation", setBilinearInterpolation);
                    put("bicubic interpolation", setBicubicInterpolation);
                    put("nearest neighbour interpolation", setNearestNeighbourInterpolation);
                }},
                "bilinear interpolation");

        addAboutSubmenu();
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
            addDrawPanelMouseListener(startEvent.panelMouseAdapter);
            addDrawPanelMouseMotionListener(startEvent.panelMouseAdapter);
            addDrawPanelMouseWheelListener(startEvent.panelMouseAdapter);
            showFrame();
        }
    }

    public void addDrawPanelMouseListener(MouseListener l) {
        panel.addMouseListener(l);
    }

    public void addDrawPanelMouseMotionListener(MouseMotionListener l) {
        panel.addMouseMotionListener(l);
    }

    public void addDrawPanelMouseWheelListener(MouseWheelListener l) {
        panel.addMouseWheelListener(l);
    }

    public void addToolsButtons(List<String> tools, Map<String, String> toolsDescr, Map<String, String> icons) {
        addFilterGroup(tools, toolsDescr, icons);
    }
}
