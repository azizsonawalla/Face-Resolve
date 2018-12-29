import java.awt.*;

public class Face {

    private Point topLeft;
    private Point bottomRight;
    private int width;
    private int height;

    public Face(int topLeftX, int topLeftY, int width, int height) {
        this.width = width;
        this.height = height;
        this.topLeft = new Point(topLeftX, topLeftY);
        this.bottomRight = new Point(topLeft.x + width, topLeft.y + height);
    }

    public Point getTopLeft(){
        return this.topLeft;
    }

    public Point getBottomRight() {
        return this.bottomRight;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
