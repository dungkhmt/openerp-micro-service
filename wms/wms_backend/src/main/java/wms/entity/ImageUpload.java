package wms.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "image_upload")
public class ImageUpload {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "file_original_name", length = 500)
    private String fileOriginalName;

    @Column(name = "file_name", length = 500)
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "extension", length = 20)
    private String extension;

    @Column(name = "uploaded_by")
    private Integer uploadedBy;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

}