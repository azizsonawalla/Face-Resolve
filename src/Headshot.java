import java.awt.*;
import java.awt.image.BufferedImage;

public class Headshot {

    private BufferedImage originalImage;
    private BufferedImage croppedImage;

    private Point faceTopLeft;
    private int faceWidth;
    private int faceHeight;

    private Point frameTopLeft;
    private int frameWidth;
    private int frameHeight;

    public Headshot(int topLeftX, int topLeftY, int width, int height, BufferedImage image) {
        this.faceWidth = width;
        this.faceHeight = height;
        this.originalImage = image;
        this.faceTopLeft = new Point(topLeftX, topLeftY);
    }

    public Point getFaceTopLeft(){
        return this.faceTopLeft;
    }

    public int getFaceWidth() {
        return this.faceWidth;
    }

    public int getFaceHeight() {
        return this.faceHeight;
    }

    public BufferedImage getOriginalImage() {
        return this.originalImage;
    }

    public Point getFrameTopLeft() {
        return frameTopLeft;
    }

    public void setFrameTopLeft(Point frameTopLeft) {
        this.frameTopLeft = frameTopLeft;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }

    public BufferedImage getCroppedImage() {
        return croppedImage;
    }

    public void setCroppedImage(BufferedImage croppedImage) {
        this.croppedImage = croppedImage;
    }
}
