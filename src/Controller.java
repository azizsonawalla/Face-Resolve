import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    public static void run() throws InterruptedException {
        List<String> inputFiles = new ArrayList<>();
        for (int i=1; i <= 5; i++) {
            inputFiles.add(String.format("\\sample(%d).jpg", i));
        }
        String inputPathTemplate = new File("samples/").getAbsolutePath();
        String outputPathTemplate = new File("samples/out/").getAbsolutePath();
        String outputFormat = "jpg";
        Double outputAspectRatio = 1.0;
        Double faceSize = 0.5;
        Double faceVerticalOffset = -0.3;
        Double faceHorizontalOffset = 0.0;

        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (String filename : inputFiles) {
            System.out.println(inputPathTemplate + filename);
            FaceResolve faceResolve = new FaceResolve(inputPathTemplate + filename, outputPathTemplate + filename,
                    outputFormat, outputAspectRatio, -1.0, -1.0, faceSize, faceVerticalOffset, faceHorizontalOffset);
            pool.execute(faceResolve);
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.MINUTES);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
