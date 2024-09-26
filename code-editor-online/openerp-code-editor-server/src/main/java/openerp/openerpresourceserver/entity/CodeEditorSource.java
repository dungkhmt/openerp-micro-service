package openerp.openerpresourceserver.entity;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import openerp.openerpresourceserver.entity.enumeration.ProgrammingLanguage;

@Entity
@Table(name = "code_editor_source")
@Getter
@Setter
public class CodeEditorSource {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private ProgrammingLanguage language;

    @Column(name = "source")
    private String source;

    @ManyToOne
    @JoinColumn(name = "room_id", referencedColumnName = "id")
    private CodeEditorRoom room;

    @Column(name = "create_date")
    @JsonIgnore
    @CreatedDate
    private Instant createDate = Instant.now();

    @Column(name = "edit_date")
    @JsonIgnore
    @LastModifiedDate
    private Instant editDate = Instant.now();

    @JsonIgnore
    @Column(name = "edit_by")
    @LastModifiedBy
    private String editBy;
}
