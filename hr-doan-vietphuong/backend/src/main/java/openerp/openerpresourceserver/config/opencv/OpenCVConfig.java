package openerp.openerpresourceserver.config.opencv;

import jakarta.annotation.PostConstruct;
import org.opencv.core.Core;
import org.springframework.stereotype.Component;

@Component
public class OpenCVConfig {

    @PostConstruct
    public void init() {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            System.out.println("OpenCV loaded. Version: " + Core.VERSION);
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load OpenCV native library.");
            e.printStackTrace();
        }
    }
}
