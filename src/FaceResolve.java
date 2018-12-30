import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceResolve {

    /* User image configuration */
    private String INPUT_FILE_PATH = "D:\\Personal Coding Projects\\Face-resolve\\sample6.jpg";
    private String OUTPUT_FILE_PATH = "D:\\Personal Coding Projects\\Face-resolve\\sample6-out1.jpg";
    private String OUTPUT_FORMAT = "jpg";
    private String CASCADES_XML = "D:\\Personal Coding Projects\\Face-resolve\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface_improved.xml";
    private Double OUTPUT_ASPECT_RATIO = 1.0;
    private Double FACE_ZOOM = 0.1;
    private Double FACE_VERT_OFFSET = -0.3;
    private Double FACE_HOR_OFFSET = 0.0;

    private Headshot findFace(){
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); // Loading the OpenCV core library
        CascadeClassifier cascadeClassifier = new CascadeClassifier(this.CASCADES_XML);
        Mat source = Imgcodecs.imread(this.INPUT_FILE_PATH);
        MatOfRect faces = new MatOfRect();
        cascadeClassifier.detectMultiScale(source, faces);
        Rect face = faces.toArray()[0];
        return new Headshot(face.x, face.y, face.width, face.height, importImage(this.INPUT_FILE_PATH));
    }

    private Headshot calculateCrop(Headshot headshot) throws Exception {
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

        Double frameHeight = headHeight/this.FACE_ZOOM;
        Double frameWidth = frameHeight*this.OUTPUT_ASPECT_RATIO;
        Double frameTopLeftX = headCenterX - (frameWidth/2.0) - (this.FACE_HOR_OFFSET*headWidth);
        Double frameTopLeftY = headCenterY - (frameHeight/2.0) - (this.FACE_VERT_OFFSET*headHeight);
        if ( frameTopLeftX * frameTopLeftY < 0){
            throw new Exception("Crop specified is outside of image bounds");
            //TODO: Add bounds check for other params as well
        }
        Point frameTopLeft = new Point((int) Math.round(frameTopLeftX), (int) Math.round(frameTopLeftY));

        headshot.setFrameTopLeft(frameTopLeft);
        headshot.setFrameWidth((int) Math.round(frameWidth));
        headshot.setFrameHeight((int) Math.round(frameHeight));
        return headshot;
    }

    private BufferedImage cropImage(BufferedImage originalImage, Point topLeft, int width, int height) {
        return originalImage.getSubimage(topLeft.x, topLeft.y, width, height);
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

    /* Getters and setters */
    public String getINPUT_FILE_PATH() {
        return INPUT_FILE_PATH;
    }

    public void setINPUT_FILE_PATH(String INPUT_FILE_PATH) {
        this.INPUT_FILE_PATH = INPUT_FILE_PATH;
    }

    public String getOUTPUT_FILE_PATH() {
        return OUTPUT_FILE_PATH;
    }

    public void setOUTPUT_FILE_PATH(String OUTPUT_FILE_PATH) {
        this.OUTPUT_FILE_PATH = OUTPUT_FILE_PATH;
    }

    public String getOUTPUT_FORMAT() {
        return OUTPUT_FORMAT;
    }

    public void setOUTPUT_FORMAT(String OUTPUT_FORMAT) {
        this.OUTPUT_FORMAT = OUTPUT_FORMAT;
    }

    public String getCASCADES_XML() {
        return CASCADES_XML;
    }

    public void setCASCADES_XML(String CASCADES_XML) {
        this.CASCADES_XML = CASCADES_XML;
    }

    public Double getOUTPUT_ASPECT_RATIO() {
        return OUTPUT_ASPECT_RATIO;
    }

    public void setOUTPUT_ASPECT_RATIO(Double OUTPUT_ASPECT_RATIO) {
        this.OUTPUT_ASPECT_RATIO = OUTPUT_ASPECT_RATIO;
    }

    public Double getFACE_ZOOM() {
        return FACE_ZOOM;
    }

    public void setFACE_ZOOM(Double FACE_ZOOM) {
        this.FACE_ZOOM = FACE_ZOOM;
    }

    public Double getFACE_VERT_OFFSET() {
        return FACE_VERT_OFFSET;
    }

    public void setFACE_VERT_OFFSET(Double FACE_VERT_OFFSET) {
        this.FACE_VERT_OFFSET = FACE_VERT_OFFSET;
    }

    public Double getFACE_HOR_OFFSET() {
        return FACE_HOR_OFFSET;
    }

    public void setFACE_HOR_OFFSET(Double FACE_HOR_OFFSET) {
        this.FACE_HOR_OFFSET = FACE_HOR_OFFSET;
    }

    /* End of getters and setters */

    private void controller() throws Exception{
        Headshot headshot = findFace();
        headshot = calculateCrop(headshot);
        headshot.setCroppedImage(cropImage(headshot.getOriginalImage(), headshot.getFrameTopLeft(), headshot.getFrameWidth(), headshot.getFrameHeight()));
        exportImage(headshot.getCroppedImage(), this.OUTPUT_FORMAT, this.OUTPUT_FILE_PATH);
    }

    public static void main(String[] args) throws Exception{
        FaceResolve faceResolve = new FaceResolve();
        faceResolve.controller();
    }
}
