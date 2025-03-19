package model;

import event.RepaintEvent;
import event.observers.Observable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class ImageWorker extends Observable {
    private BufferedImage loadedImage = null;
    private BufferedImage filteredImage = null;

    public void load(String imagePath, String imageName) {
        try {
            loadedImage = ImageIO.read(new File(imagePath, imageName));
            filteredImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics2D g = filteredImage.createGraphics();
            g.drawImage(loadedImage, 0, 0, null);
            g.dispose();

            update(new RepaintEvent(filteredImage));
        } catch (IOException e) {
            java.lang.System.err.println("Can not load new file with name " +
                    imageName + " to directory " + imagePath + " because " + e.getMessage());
        }
    }

    public void save(String imagePath, String imageName) {
        try {
            File newImage = new File(imagePath, imageName);
            ImageIO.write(filteredImage, "png", newImage);
        } catch (IOException e) {
            java.lang.System.err.println("Can not store new file with name " +
                    imageName + " to directory " + imagePath + " because " + e.getMessage());
        }
    }

    public BufferedImage getImage() {
        return filteredImage;
    }
}
