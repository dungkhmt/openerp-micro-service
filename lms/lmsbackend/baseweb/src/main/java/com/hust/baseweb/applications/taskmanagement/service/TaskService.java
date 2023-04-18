package com.hust.baseweb.applications.taskmanagement.service;

import com.hust.baseweb.applications.taskmanagement.dto.dao.PersonDao;
import com.hust.baseweb.applications.taskmanagement.dto.form.TaskForm;
import com.hust.baseweb.applications.taskmanagement.dto.form.TaskStatusForm;
import com.hust.baseweb.applications.taskmanagement.entity.Task;
import com.hust.baseweb.entity.StatusItem;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TaskService {
    Task createTask(Task task);

    List<Task> getAllTasks();

    List<Task> getAllTaskInProject(UUID projectId);

    List<Object[]> getTaskStaticsCategoryInProject(UUID projectId);

    List<Object[]> getTaskStaticsStatusInProject(UUID projectId);

    StatusItem getStatusItemByStatusId(String statusId);

    Task getTask(UUID taskId);

    Task updateTasks(Task task);

    Task updateStatusTask(UUID taskId, TaskStatusForm taskStatusForm, String userLoginId);

    Task updateTask(UUID taskId, TaskForm taskForm, String createdByUserLoginId);

    void addTaskSkill(UUID taskId, String skillId);

    List<PersonDao> suggestAssignTask(UUID projectId, List<String> skillIds);
}
