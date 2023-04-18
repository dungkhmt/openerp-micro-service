package com.hust.baseweb.applications.taskmanagement.dto.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskForm {

    private String name;
    private String description;
    private Date dueDate;
    private String attachmentPaths;
    private UUID projectId;
    private String statusId;
    private String priorityId;
    private String categoryId;
    private UUID partyId;
    private List<String> skillIds;
}
