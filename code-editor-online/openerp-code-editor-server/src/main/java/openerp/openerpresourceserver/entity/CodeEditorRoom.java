package openerp.openerpresourceserver.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "code_editor_room")
@Getter
@Setter
public class CodeEditorRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "room_master_id")
    private String roomMasterId;

    @Column(name = "create_date")
    @JsonIgnore
    @CreatedDate
    private Instant createDate = Instant.now();

    @Column(name = "edit_date")
    @JsonIgnore
    @LastModifiedDate
    private Instant editDate = Instant.now();

}
