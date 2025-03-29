package org.example.view.components;

import org.example.view.MainFrame;

import java.awt.*;

import javax.swing.*;
import java.awt.*;

public class CursorManager {
    private static Component targetComponent;

    public static void setTarget(Component component) {
        targetComponent = component;
    }

    public static void showWaitCursor() {
        if (targetComponent != null) {
            targetComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            System.out.println("Текущий курсор: " + targetComponent.getCursor().getType());
        }
    }

    public static void defaultCursor() {
        if (targetComponent != null) {
            targetComponent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            System.out.println("Текущий курсор: " + targetComponent.getCursor().getType());
        }
    }

}
