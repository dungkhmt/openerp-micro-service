package openerp.openerpresourceserver.service.mongodb;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.client.gridfs.model.GridFSFile;

@Service
public class ImageService {

    @Autowired
    private GridFsOperations gridFsOperations;

    /**
     * Lưu ảnh vào MongoDB GridFS.
     *
     * @param imageFile Tệp ảnh cần lưu.
     * @return ID của ảnh đã lưu.
     */
    public String saveImage(MultipartFile imageFile) throws IOException {
        ObjectId imageId = gridFsOperations.store(
                imageFile.getInputStream(),
                imageFile.getOriginalFilename(),
                imageFile.getContentType()
        );
        return imageId.toString();
    }

    /**
     * Xóa ảnh từ MongoDB GridFS.
     *
     * @param imageId ID của ảnh cần xóa.
     */
    public void deleteImage(String imageId) {
        gridFsOperations.delete(new Query(Criteria.where("_id").is(imageId)));
    }

    /**
     * Lấy nội dung ảnh từ MongoDB GridFS.
     *
     * @param imageId ID của ảnh.
     * @return Dữ liệu của ảnh dưới dạng mảng byte.
     */
    public byte[] getImage(String id) {
        try {
            // Lấy nội dung tệp bằng ID
            GridFsResource content = getById(id);
            if (content != null) {
                try (InputStream inputStream = content.getInputStream()) {
                    // Chuyển InputStream thành mảng byte
                    return IOUtils.toByteArray(inputStream);
                }
            } else {
                throw new RuntimeException("File not found with id: " + id);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file with id: " + id, e);
        }
    }

    // Hàm hỗ trợ lấy GridFsResource bằng ID
    public GridFsResource getById(String id) {
        // Tìm file trong GridFS bằng ID
        GridFSFile fID = gridFsOperations.findOne(Query.query(Criteria.where("_id").is(id)));
        if (fID == null) {
            return null;
        }
        // Lấy tài nguyên từ GridFS
        return gridFsOperations.getResource(fID);
    }

}

