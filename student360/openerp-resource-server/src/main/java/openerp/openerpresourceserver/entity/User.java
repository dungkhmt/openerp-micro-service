package openerp.openerpresourceserver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_login")
public class User implements Serializable {

    @Id
    @Column(name = "user_login_id", updatable = false, nullable = false)
    private String id;

    private String email;

    private String firstName;

    private String lastName;

    @CreatedDate
    @Column(name = "created_stamp")
    private Date createdDate;


    public String getFullName() {
        StringBuilder fullNameBuilder = new StringBuilder();

        // Thêm firstName vào fullName nếu có
        if (firstName != null && !firstName.isEmpty()) {
            fullNameBuilder.append(firstName);
            fullNameBuilder.append(" ");
        }

        // Thêm lastName vào fullName nếu có
        if (lastName != null && !lastName.isEmpty()) {
            fullNameBuilder.append(lastName);
        }

        // Trả về chuỗi tên đầy đủ
        return fullNameBuilder.toString();
    }
}
