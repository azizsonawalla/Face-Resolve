import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceResolve {

    private Headshot findFace(String file, String cascadesXml){
        // Loading the OpenCV core library
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        CascadeClassifier cascadeClassifier = new CascadeClassifier(cascadesXml);
        // Reading the Image from the file and storing it in to a Matrix object
        Mat source = Imgcodecs.imread(file);
        // Detecting the face
        MatOfRect faces = new MatOfRect();
        cascadeClassifier.detectMultiScale(source, faces);
        Rect face = faces.toArray()[0];
        return new Headshot(face.x, face.y, face.width, face.height, importImage(file));
    }

    private BufferedImage cropImage(BufferedImage originalImage, Point topLeft, int width, int height) {
        BufferedImage croppedImage = originalImage.getSubimage(topLeft.x, topLeft.y, width, height);
        return croppedImage;
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

    private Headshot calculateCrop(Headshot headshot, Double aspectRatio, Double headHeightProportion, Double vertOffset, Double horOffset) {
        Double faceWidth = headshot.getFaceWidth()*1.0;
        Double faceHeight = headshot.getFaceHeight()*1.0;
        Double faceCenterX = headshot.getFaceTopLeft().x + (faceWidth/2.0);
        Double faceCenterY = headshot.getFaceTopLeft().y + (faceHeight/2.0);

        Double headHeight = faceHeight*1.8;
        Double headWidth = faceWidth*1.25;
        Double headCenterX = faceCenterX;
        Double headCenterY = faceCenterY - (faceHeight/4);
        Point headTopLeft = new Point((int) Math.round(headCenterX-0.5*headWidth), (int) Math.round(headCenterY-0.5*headHeight));

        Double frameHeight = headHeight/headHeightProportion;
        Double frameWidth = frameHeight*aspectRatio;
        Double frameTopLeftX = headCenterX - (frameWidth/2.0) + (horOffset*headWidth);
        Double frameTopLeftY = headCenterY - (frameHeight/2.0) + (vertOffset*headHeight);
        Point frameTopLeft = new Point((int) Math.round(frameTopLeftX), (int) Math.round(frameTopLeftY));

        headshot.setFrameTopLeft(frameTopLeft);
        headshot.setFrameWidth((int) Math.round(frameWidth));
        headshot.setFrameHeight((int) Math.round(frameHeight));
        return headshot;
    }

    public static void main(String[] args) {
        String inputFile = "D:\\Personal Coding Projects\\Face-resolve\\sample1.jpg";
        String outputFile = "D:\\Personal Coding Projects\\Face-resolve\\sample1-out2.jpg";
        String outputFormat = "jpg";
        String cascadesXml = "D:\\Personal Coding Projects\\Face-resolve\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface_improved.xml";
        int outputWidth = 3;
        int outputHeight = 4;
        Double outputAspectRatio = (outputWidth*1.0)/(outputHeight*1.0);
        Double faceHeightProportion = 0.8;
        Double vertOffset = 0.0;
        Double horOffset = 0.0;

        FaceResolve faceResolve = new FaceResolve();
        Headshot headshot = faceResolve.findFace(inputFile, cascadesXml);
        headshot = faceResolve.calculateCrop(headshot, outputAspectRatio, faceHeightProportion, vertOffset, horOffset);
        headshot.setCroppedImage(faceResolve.cropImage(headshot.getOriginalImage(), headshot.getFrameTopLeft(), headshot.getFrameWidth(), headshot.getFrameHeight()));
        faceResolve.exportImage(headshot.getCroppedImage(), outputFormat, outputFile);
    }
}
