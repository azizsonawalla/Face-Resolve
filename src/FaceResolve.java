import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceResolve implements Runnable {

    private String CASCADES_XML =
            new File("opencv/sources/data/lbpcascades/lbpcascade_frontalface_improved.xml").getAbsolutePath();

    /* User image configuration */
    private String inputPath;
    private String outputPath;
    private String outputFormat;
    private Double outputAspectRatio;
    private Double outputWidth;
    private Double outputHeight;
    private Double faceSize;
    private Double faceVerticalOffset;
    private Double faceHorizontalOffset;

    public FaceResolve(String inputPath, String outputPath, String outputFormat, Double outputAspectRatio,
                       Double outputHeight, Double outputWidth, Double faceSize, Double vertOffset, Double horOffset) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.outputFormat = outputFormat;
        if (outputWidth >=0 && outputHeight >= 0) {
            this.outputHeight = outputHeight;
            this.outputWidth = outputWidth;
            this.outputAspectRatio = outputWidth/outputHeight;
        } else {
            this.outputAspectRatio = outputAspectRatio;
        }
        this.faceSize = faceSize;
        this.faceVerticalOffset = vertOffset;
        this.faceHorizontalOffset = horOffset;
    }

    private Headshot findFace(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); // Loading the OpenCV core library
        CascadeClassifier cascadeClassifier = new CascadeClassifier(this.CASCADES_XML);
        Mat source = Imgcodecs.imread(this.inputPath);
        Rect[] facesArray = new Rect[0];
        for (int i=0; i<100; i++){
            MatOfRect faces = new MatOfRect();
            MatOfInt reject = new MatOfInt();
            MatOfDouble weight = new MatOfDouble();
            cascadeClassifier.detectMultiScale3(source, faces, reject, weight, 1.1, i,0, new Size(), new Size(), true);
            facesArray = faces.toArray();
            if (facesArray.length == 1) {
                break;
            }
            if (facesArray.length == 0) {
                cascadeClassifier.detectMultiScale3(source, faces, reject, weight, 1.1, i+1,0, new Size(), new Size(), true);
                break;
            }
        }
        Rect face = facesArray[0];
        return new Headshot(face.x, face.y, face.width, face.height, importImage(this.inputPath));
    }

    private Headshot calculateCrop(Headshot headshot){
        /* Definitions */
        // Face: portion of the image detected by OpenCV, from top of eyes to bottom of lips
        // Head: from hairline to bottom of chin
        // Frame: the head along with amount of space around it that the user wants

        /* Constants for avg. facial proportions */
        Double HEAD_TO_FACE_HEIGHT_RATIO = 1.8; // height of entire head divided by height from eyes to lips
        Double HEAD_TO_FACE_WIDTH_RATIO = 1.25; // width from ear to ear divided by width from eye to eye
        Double HEAD_TO_FACE_CENTER_OFFSET = 0.25; // vertical offset b/w center of face and center of head, as a proportion of the face height

        Double faceWidth = headshot.getFaceWidth()*1.0;
        Double faceHeight = headshot.getFaceHeight()*1.0;
        Double faceCenterX = headshot.getFaceTopLeft().x + (faceWidth/2.0);
        Double faceCenterY = headshot.getFaceTopLeft().y + (faceHeight/2.0);

        Double headHeight = faceHeight*HEAD_TO_FACE_HEIGHT_RATIO;
        Double headWidth = faceWidth*HEAD_TO_FACE_WIDTH_RATIO;
        Double headCenterX = faceCenterX;
        Double headCenterY = faceCenterY - (faceHeight*HEAD_TO_FACE_CENTER_OFFSET);

        Double frameHeight = headHeight/this.faceSize;
        Double frameWidth = frameHeight*this.outputAspectRatio;
        Double frameTopLeftX = headCenterX - (frameWidth/2.0) - (this.faceHorizontalOffset *(frameWidth/2.0));
        Double frameTopLeftY = headCenterY - (frameHeight/2.0) - (this.faceVerticalOffset *(frameHeight/2.0));
        Point frameTopLeft = new Point((int) Math.round(frameTopLeftX), (int) Math.round(frameTopLeftY));

        headshot.setFrameTopLeft(frameTopLeft);
        headshot.setFrameWidth((int) Math.round(frameWidth));
        headshot.setFrameHeight((int) Math.round(frameHeight));
        return headshot;
    }

    private BufferedImage cropImage(BufferedImage originalImage, Point topLeft, int width, int height) throws Exception{
        int topLeftX = topLeft.x;
        int topLeftY = topLeft.y;
        int bottomLeftX = topLeftX + width;
        int bottomLeftY = topLeftY + height;
        Boolean xTopBoundaryCheck = outOfBounds(topLeftX, 0, originalImage.getWidth()-1);
        Boolean yTopBoundaryCheck = outOfBounds(topLeftY, 0, originalImage.getHeight()-1);
        Boolean xBottomBoundaryCheck = outOfBounds(bottomLeftX, topLeftX, originalImage.getWidth()-1);
        Boolean yBottomBoundaryCheck = outOfBounds(bottomLeftY, topLeftY, originalImage.getHeight()-1);
        if (xTopBoundaryCheck || yTopBoundaryCheck || xBottomBoundaryCheck || yBottomBoundaryCheck){
            throw new Exception("Crop Margin Error");
        }
        return originalImage.getSubimage(topLeftX, topLeftY, width, height);
    }

    private Boolean outOfBounds(int num, int min, int max) {
        return (num < min) || (num > max);
    }

    private void exportImage(BufferedImage image, String format, String path) {
        try {
            ImageIO.write(image, format, new File(path));
        } catch (Exception e) {
            System.out.println("Failed to export image");
        }
    }

    private BufferedImage importImage(String path) {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Failed to import image");
        }
        return originalImage;
    }

    public void run(){
        Headshot headshot = findFace();
        headshot = calculateCrop(headshot);
        try {
            headshot.setCroppedImage(cropImage(headshot.getOriginalImage(), headshot.getFrameTopLeft(),
                                     headshot.getFrameWidth(), headshot.getFrameHeight()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }
        exportImage(headshot.getCroppedImage(), this.outputFormat, this.outputPath);
    }
}
