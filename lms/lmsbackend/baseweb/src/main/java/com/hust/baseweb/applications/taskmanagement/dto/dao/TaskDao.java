package com.hust.baseweb.applications.taskmanagement.dto.dao;

import com.hust.baseweb.applications.taskmanagement.entity.Project;
import com.hust.baseweb.applications.taskmanagement.entity.Task;
import com.hust.baseweb.applications.taskmanagement.entity.TaskCategory;
import com.hust.baseweb.applications.taskmanagement.entity.TaskPriority;
import com.hust.baseweb.entity.StatusItem;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class TaskDao {

    private static final String STATUS_DONE = "TASK_RESOLVED";

    private UUID id;

    private String name;

    private String description;

    private Project project;

    private TaskCategory taskCategory;

    private StatusItem statusItem;

    private TaskPriority taskPriority;

    private String createdByUserLoginId;

    private String assignee;

    private UUID PartyId;

    private String createdStamp;

    private String lastUpdatedStamp;

    private String fileName;

    private String fileId;

    private String dueDate;

    private Date dueDateOrigin;

    private String timeRemaining;

    private boolean outOfDate;

    public TaskDao(Task task, String assignee, UUID partyId) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        this.setId(task.getId());
        this.setName(task.getName());
        this.setProject(task.getProject());
        this.setTaskCategory(task.getTaskCategory());
        this.setDescription(task.getDescription() != null ? task.getDescription() : "Không có mô tả");
        this.setStatusItem(task.getStatusItem());
        this.setTaskPriority(task.getTaskPriority());
        this.setDueDate(sdf.format(task.getDueDate()));
        this.setCreatedStamp(task.getCreatedStamp() != null ? sdf.format(task.getCreatedStamp()) : null);
        this.setLastUpdatedStamp(task.getLastUpdatedStamp() != null ? sdf.format(task.getLastUpdatedStamp()) : null);
        try {
            Date d1 = task.getDueDate();
            Date d2 = new Date();
            long differenceInTime = d1.getTime() - d2.getTime();
            long differenceInSeconds
                = TimeUnit.MILLISECONDS
                      .toSeconds(differenceInTime)
                  % 60;

            long differenceInMinutes
                = TimeUnit
                      .MILLISECONDS
                      .toMinutes(differenceInTime)
                  % 60;

            long differenceInHours
                = TimeUnit
                      .MILLISECONDS
                      .toHours(differenceInTime)
                  % 24;

            long differenceInDays
                = TimeUnit
                      .MILLISECONDS
                      .toDays(differenceInTime)
                  % 365;
            if (differenceInSeconds <= 0) {
                this.setTimeRemaining("Quá hạn " + String.valueOf(-differenceInDays) + "ngày, "
                                      + String.valueOf(-differenceInHours) + "giờ, "
                                      + String.valueOf(-differenceInMinutes) + "phút, "
                                      + String.valueOf(-differenceInSeconds) + "giây");
                this.setOutOfDate(true);
            } else {
                this.setTimeRemaining("Còn " + String.valueOf(differenceInDays) + "ngày, "
                                      + String.valueOf(differenceInHours) + "giờ, "
                                      + String.valueOf(differenceInMinutes) + "phút, "
                                      + String.valueOf(differenceInSeconds) + "giây tới hạn!");
                this.setOutOfDate(false);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (this.getStatusItem() != null && (this.getStatusItem().getStatusId().equals(STATUS_DONE))) {
            this.setOutOfDate(false);
            this.setTimeRemaining("");
        }

        String[] arrOfStr = task.getAttachmentPaths().split(",");
        this.setFileName(arrOfStr[0] != "" ? arrOfStr[0] : "Không có tệp đính kèm");
        this.setFileId(arrOfStr.length > 1 ? (!arrOfStr[1].equals("null") && !arrOfStr[1].equals("undefined") ? arrOfStr[1] : null) : null);
        this.setAssignee(assignee);
        this.setPartyId(partyId);
        this.setDueDateOrigin(task.getDueDate());
        this.setCreatedByUserLoginId(task.getCreatedByUserLoginId());
    }
}
