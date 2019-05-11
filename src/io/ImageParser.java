package io;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * The type Image parser.
 */
public class ImageParser {
    /**
     * Gets image.
     *
     * @param imagePath the image
     * @return the image
     */
    public static Image getImage(String imagePath) {
        InputStream is = null;
        File file = new File(imagePath);
        try {
            if (file.exists()) {
                is = new FileInputStream(file);
            } else {
                is = ClassLoader.getSystemClassLoader().getResourceAsStream(imagePath);
            }
            Image image = ImageIO.read(is);
            return image;
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed loading image: " + imagePath, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException("Failed loading image: " + imagePath, e);
                }
            }
        }
    }
}
