package mara.mybox.fxml;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import mara.mybox.objects.IntCircle;
import mara.mybox.objects.ImageScope;
import mara.mybox.objects.IntPoint;
import mara.mybox.objects.IntRectangle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author Mara
 * @CreateDate 2018-11-10 19:51:22
 * @Version 1.0
 * @Description
 * @License Apache License Version 2.0
 */
public class FxmlReplaceColorTools {

    private static final Logger logger = LogManager.getLogger();

    public static Image replaceColors(Image image, Color newColor, ImageScope scope) {
        PixelReader pixelReader = image.getPixelReader();
        WritableImage newImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = newImage.getPixelWriter();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                if (scope.inScope(x, y, color)) {
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    pixelWriter.setColor(x, y, color);
                }
            }
        }
        return newImage;
    }

    public static Image replaceColorsRectangle(Image image, Color newColor, IntRectangle rect) {
        PixelReader pixelReader = image.getPixelReader();
        WritableImage newImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = newImage.getPixelWriter();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (rect.include(x, y)) {
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
                }
            }
        }
        return newImage;
    }

    public static Image replaceColorsCircle(Image image, Color newColor, IntCircle circle) {
        PixelReader pixelReader = image.getPixelReader();
        WritableImage newImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        PixelWriter pixelWriter = newImage.getPixelWriter();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if (circle.include(x, y)) {
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    pixelWriter.setColor(x, y, pixelReader.getColor(x, y));
                }
            }
        }
        return newImage;
    }

    public static Image replaceColorsMatting(Image source, Color newColor,
            List<IntPoint> points, double distance) {
        try {
            if (source == null
                    || points == null || points.isEmpty()
                    || distance < 0 || distance > 1) {
                return source;
            }
            int width = (int) source.getWidth();
            int height = (int) source.getHeight();
            PixelReader pixelReader = source.getPixelReader();
            WritableImage newImage = new WritableImage(width, height);
            PixelWriter pixelWriter = newImage.getPixelWriter();
            pixelWriter.setPixels(0, 0, width, height, pixelReader, 0, 0);

            boolean[][] visited = new boolean[height][width];
            Queue<IntPoint> queue = new LinkedList<>();
            for (IntPoint point : points) {
                Color startColor = pixelReader.getColor(point.getX(), point.getY());
                queue.add(point);
                while (!queue.isEmpty()) {
                    IntPoint p = queue.remove();
                    int x = p.getX(), y = p.getY();
                    if (x < 0 || x >= width || y < 0 || y >= height
                            || visited[y][x]) {
                        continue;
                    }
                    visited[y][x] = true;
                    Color pixelColor = pixelReader.getColor(x, y);
                    if (FxmlColorTools.isColorMatch(pixelColor, startColor, distance)) {
                        pixelWriter.setColor(x, y, newColor);
                        queue.add(new IntPoint(x + 1, y));
                        queue.add(new IntPoint(x - 1, y));
                        queue.add(new IntPoint(x, y + 1));
                        queue.add(new IntPoint(x, y - 1));
                    }
                }
            }

            return newImage;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }

    }

}
