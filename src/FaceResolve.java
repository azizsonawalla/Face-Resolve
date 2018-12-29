import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import javax.imageio.ImageIO;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class FaceResolve {

    public Face findFace(String file){
        // Loading the OpenCV core library
        System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
        CascadeClassifier cascadeClassifier = new CascadeClassifier("D:\\Personal Coding Projects\\Face-resolve\\opencv\\sources\\data\\lbpcascades\\lbpcascade_frontalface_improved.xml");
        // Reading the Image from the file and storing it in to a Matrix object
        Mat src = Imgcodecs.imread(file);
        // Detecting the face
        MatOfRect faces = new MatOfRect();
        cascadeClassifier.detectMultiScale(src, faces);
        Rect face = faces.toArray()[0];
        return new Face(face.x, face.y, face.width, face.height);
    }

    public static void main(String[] args) {
        String inputFile = "D:\\Personal Coding Projects\\Face-resolve\\sample6.jpg";
        String outputFile = "D:\\Personal Coding Projects\\Face-resolve\\sample6-out.jpg";
        // Detect face from image path
        FaceResolve faceResolve = new FaceResolve();
        Face face = faceResolve.findFace(inputFile);
        // Load image
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(new File(inputFile));
        } catch (Exception e) {
            return;
        }
        // Crop image
        BufferedImage croppedImage = originalImage.getSubimage(face.getTopLeft().x - (face.getWidth()/2), face.getTopLeft().y - ((int) Math.round(face.getHeight()/1.15)), (int) Math.round(face.getWidth()*2), (int) Math.round(face.getHeight()*2.25));
        // Export image
        try {
            ImageIO.write(croppedImage, "jpg", new File(outputFile));
        } catch (Exception e) {
            return;
        }
    }
}
