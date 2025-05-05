package openerp.openerpresourceserver.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import openerp.openerpresourceserver.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {
    private final Cloudinary cloudinary;

    @Override
    public String upload(byte[] bytes, String fileName, String folderName) throws IOException {
        String publicId = "attendance/" + folderName + "/" + fileName;
        Map<?,?> image = cloudinary.uploader().upload(bytes, ObjectUtils.asMap("public_id", publicId));
        return (String) image.get("url");
    }

    public String[] uploadImages(MultipartFile[] images) throws IOException, ExecutionException, InterruptedException {
        List<CompletableFuture<String>> uploadFutures = new ArrayList<>();

        for (MultipartFile image : images) {
            CompletableFuture<String> uploadFuture = CompletableFuture.supplyAsync(() -> {
                try {
                    Map uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
                    return (String) uploadResult.get("secure_url");
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });
            uploadFutures.add(uploadFuture);
        }

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                uploadFutures.toArray(new CompletableFuture[0]));
        CompletableFuture<List<String>> allCompletedFutures = allFutures.thenApply(future -> uploadFutures.stream()
                .map(CompletableFuture::join)
                .toList());

        return allCompletedFutures.get().toArray(new String[0]);
    }

    public void deleteImage(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
