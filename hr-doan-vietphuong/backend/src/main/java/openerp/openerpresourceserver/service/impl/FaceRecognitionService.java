package openerp.openerpresourceserver.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.entity.Employee;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import openerp.openerpresourceserver.repo.EmployeeRepository;
import openerp.openerpresourceserver.util.SecurityUtil;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Service
@RequiredArgsConstructor
public class FaceRecognitionService {
    private LBPHFaceRecognizer faceRecognizer;
    private final EmployeeRepository employeeRepository;
    private final String trainingDir = "./faces";
    private static final String HAAR_CASCADE_FILE = "src/main/resources/haarcascade_frontalface_default.xml";

    @PostConstruct
    @Scheduled(cron = "0 0 2 * * *")
    public void init() {
        File root = new File(trainingDir);
        FilenameFilter imgFilter = (dir, name) -> {
            name = name.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
        };
        File[] imageFiles = root.listFiles(imgFilter);

        if (imageFiles == null || imageFiles.length == 0) {
            throw new RuntimeException("No training images found in " + trainingDir);
        }

        MatVector images = new MatVector(imageFiles.length);
        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.createBuffer();

        int counter = 0;
        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), IMREAD_GRAYSCALE);
            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }
        this.faceRecognizer = LBPHFaceRecognizer.create();
        this.faceRecognizer.train(images, labels);
        System.out.println("Model trained");
    }

    public int recognize(Mat faceData) {
        CascadeClassifier faceDetector = new CascadeClassifier(HAAR_CASCADE_FILE);

        cvtColor(faceData, faceData, CV_BGR2GRAY);
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(0);

        RectVector faceDetections = new RectVector();
        faceDetector.detectMultiScale(faceData, faceDetections);

        if (faceDetections.size() == 0) {
            throw new BadRequestException("No face detected in the image.");
        }

        Rect faceRect = faceDetections.get(0); // lấy khuôn mặt đầu tiên
        Mat face = new Mat(faceData, faceRect);
        resize(face, face, new Size(160, 160));
        this.faceRecognizer.predict(face, label, confidence);
        int predictedLabel = label.get(0);

        System.out.println(confidence.get(0));
        if (confidence.get(0) > 70 || predictedLabel != SecurityUtil.getEmployeeId()) {
            return -1;
        }
        return predictedLabel;
    }

    public double recognizes(Mat faceData) {
        cvtColor(faceData, faceData, CV_BGR2GRAY);
        IntPointer label = new IntPointer(1);
        DoublePointer confidence = new DoublePointer(0);
        this.faceRecognizer.predict(faceData, label, confidence);
        int predictedLabel = label.get(0);
        return confidence.get(0);
    }

    public List<String> addFaces(List<MultipartFile> files, Long id) throws IOException {
        CascadeClassifier faceDetector = new CascadeClassifier(HAAR_CASCADE_FILE);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        Integer label = employee.getEmployeeId();
        File faceDir = new File(trainingDir);
        faceDir.mkdirs();

        List<String> result = new ArrayList<>();

        for (MultipartFile file : files) {
            String tempFileName = "temp_" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path tempFilePath = Paths.get("temp", tempFileName);
            Files.createDirectories(tempFilePath.getParent());
            Files.write(tempFilePath, file.getBytes());

            Mat image = imread(tempFilePath.toString());
            Mat gray = new Mat();
            cvtColor(image, gray, COLOR_BGR2GRAY);

            RectVector faces = new RectVector();
            faceDetector.detectMultiScale(gray, faces);

            for (int i = 0; i < faces.size(); i++) {
                Rect faceRect = faces.get(i);
                Mat face = new Mat(gray, faceRect);
                resize(face, face, new Size(160, 160)); // chuẩn hoá kích thước

                String faceFileName = String.format("%s-%s.jpg", label, UUID.randomUUID());
                String faceFilePath = trainingDir + File.separator + faceFileName;
                imwrite(faceFilePath, face);
                result.add(faceFilePath);
            }

            Files.deleteIfExists(tempFilePath);
        }

        this.init(); // train lại recognizer
        return result;
    }
}