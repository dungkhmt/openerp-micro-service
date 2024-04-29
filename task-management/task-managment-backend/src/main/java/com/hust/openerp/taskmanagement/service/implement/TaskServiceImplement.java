package com.hust.openerp.taskmanagement.service.implement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.hust.openerp.taskmanagement.dto.TaskGanttDTO;
import com.hust.openerp.taskmanagement.dto.TaskHierarchyDTO;
import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskLog;
import com.hust.openerp.taskmanagement.entity.TaskLogDetail;
import com.hust.openerp.taskmanagement.entity.TaskSkill;
import com.hust.openerp.taskmanagement.entity.Task_;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.entity.UserSkill;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.repository.TaskSkillRepository;
import com.hust.openerp.taskmanagement.repository.UserSkillRepository;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.TaskLogService;
import com.hust.openerp.taskmanagement.service.TaskService;
import com.hust.openerp.taskmanagement.specification.TaskSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;
import com.hust.openerp.taskmanagement.util.SearchOperation;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TaskServiceImplement implements TaskService {
    private final ModelMapper modelMapper;

    private final TaskRepository taskRepository;

    private ProjectMemberService projectMemberService;

    private TaskSkillRepository taskSkillRepository;

    private UserSkillRepository userLoginSkillRepository;

    private TaskLogService taskLogService;

    @Override
    @Transactional
    public Task createTask(TaskForm taskForm, String creatorId) {
        List<TaskLogDetail> taskLogDetails = new ArrayList<>();
        var task = modelMapper.map(taskForm, Task.class);
        var createdTime = new Date();
        task.setCreatorId(creatorId);
        task.setCreatedStamp(createdTime);
        task.setLastUpdatedStamp(createdTime);
        task.setStatusId("TASK_OPEN");
        task.setProgress(0);

        UUID taskId = UUID.randomUUID();
        task.setId(taskId);

        if (taskForm.getParentId() != null) {
            var parent = taskRepository.findById(taskForm.getParentId()).orElseThrow();
            task.setAncestorId(parent.getAncestorId());

            // update lft, rgt of parent and ancestor
            task.setLft(parent.getRgt());
            task.setRgt(parent.getRgt() + 1);
            taskRepository.updateNestedSetsRgt(parent.getRgt(), parent.getAncestorId());
            taskRepository.updateNestedSetsLft(parent.getRgt(), parent.getAncestorId());
        } else {
            task.setLft(1);
            task.setRgt(2);
            task.setAncestorId(taskId);
        }

        var taskLog = TaskLog.builder().taskId(taskId).creatorId(creatorId).createdAt(createdTime).build();

        // build history details
        if (taskForm.getParentId() != null) {
            var parentLog = TaskLog.builder().taskId(taskForm.getParentId()).creatorId(creatorId).createdAt(createdTime)
                    .details(new ArrayList<>(
                            List.of(TaskLogDetail.builder().event("subtask").field("subtaskId").oldValue("")
                                    .newValue(taskId.toString()).build())))
                    .build();
            taskLogService.addLog(parentLog);
        }

        if (task.getName() != null && !task.getName().isEmpty()) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("name").oldValue("")
                    .newValue(task.getName()).build());
        }

        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("description").oldValue("")
                    .newValue(task.getDescription())
                    .build());
        }

        if (task.getFromDate() != null) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("fromDate").oldValue("")
                    .newValue(new SimpleDateFormat("dd-MM-yyyy").format(task.getFromDate()))
                    .build());
        }

        if (task.getDueDate() != null) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("dueDate").oldValue("")
                    .newValue(new SimpleDateFormat("dd-MM-yyyy").format(task.getDueDate()))
                    .build());
        }

        if (task.getAttachmentPaths() != null && !task.getAttachmentPaths().isEmpty()) {
            taskLogDetails.add(TaskLogDetail.builder().event("attachment").field("attachmentPaths").oldValue("")
                    .newValue(task.getAttachmentPaths()).build());
        }

        if (task.getPriorityId() != null && !task.getPriorityId().isEmpty()) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("priorityId").oldValue("")
                    .newValue(task.getPriorityId()).build());
        }

        if (task.getCategoryId() != null && !task.getCategoryId().isEmpty()) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("categoryId").oldValue("")
                    .newValue(task.getCategoryId()).build());
        }

        if (task.getEstimatedTime() != null) {
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("estimatedTime").oldValue("")
                    .newValue(task.getEstimatedTime().toString()).build());
        }

        if (taskForm.getAssigneeId() != null) {
            // TODO: check if assignee is a member of project
            taskLogDetails.add(TaskLogDetail.builder().event("set").field("assigneeId").oldValue("")
                    .newValue(task.getAssigneeId()).build());
        }
        var savedTask = taskRepository.save(task);

        taskLog.setDetails(taskLogDetails);

        taskLogService.addLog(taskLog);

        return savedTask;
    }

    @Override
    public List<Task> getAllTaskInProject(UUID projectId) {
        return taskRepository.findAllTasksByProjectId(projectId);
    }

    @Override
    public List<Object[]> getTaskStaticsCategoryInProject(UUID projectId) {
        return taskRepository.getTaskStaticsCategoryInProject(projectId);
    }

    @Override
    public List<Object[]> getTaskStaticsStatusInProject(UUID projectId) {
        return taskRepository.getTaskStaticsStatusInProject(projectId);
    }

    @Override
    public Task getTask(UUID taskId) {
        return taskRepository.findById(taskId).orElse(null);
    }

    @Override
    public void addTaskSkill(UUID taskId, String skillId) {
        TaskSkill taskSkill = new TaskSkill();
        taskSkill.setTaskId(taskId);
        taskSkill.setSkillId(skillId);
        taskSkillRepository.save(taskSkill);
    }

    @Override
    public List<User> suggestAssignTask(UUID projectId, List<String> skillIds) {
        List<User> userList = projectMemberService.getMembersOfProject(projectId).stream().map(ProjectMember::getMember)
                .toList();
        List<User> personRs = new ArrayList<>();
        List<String> userLoginRs = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        HashMap<String, Integer> rs = new HashMap<>();
        HashMap<String, List<String>> collection = new HashMap<>();
        for (int i = 0; i < userList.size(); i++) {
            userIds
                    .add(userList.get(i).getId());
            rs.put(userIds.get(i), 0);
        }

        for (int i = 0; i < userIds.size(); i++) {
            List<UserSkill> userLoginSkills = userLoginSkillRepository.getListUserLoginSkill(userIds.get(i));
            List<String> tmp = new ArrayList<>();
            for (UserSkill userloginSkill : userLoginSkills) {
                tmp.add(userloginSkill.getSkillId());
            }
            collection.put(userIds.get(i), tmp);
        }

        for (int i = 0; i < skillIds.size(); i++) {
            for (int j = 0; j < userIds.size(); j++) {
                int dem = rs.get(userIds.get(j));
                List<String> arrTmp = collection.get(userIds.get(j));
                for (int k = 0; k < arrTmp.size(); k++) {
                    if (skillIds.get(i).equals(arrTmp.get(k))) {
                        dem++;
                    }
                }
                rs.put(userIds.get(j), dem);
            }
        }

        Collection<Integer> rsArr = rs.values();
        int max = 0;
        for (Integer i : rsArr) {
            if (max < i) {
                max = i;
            }
        }

        if (max == 0) {
            return personRs;
        }

        for (Map.Entry<String, Integer> entry : rs.entrySet()) {
            if (entry.getValue() == max) {
                userLoginRs.add(entry.getKey());
            }
        }

        userList.stream().filter(user -> userLoginRs.contains(user.getId())).forEach(personRs::add);
        return personRs;
    }

    public Page<Task> getTasksAssignedToUser(Pageable pageable, String assignee, @Nullable String search) {
        // replace "assignee:value" if exist in search by "assignee:`assignee`"
        if (search != null && !search.equals("")) {
            if (search.contains("assigneeId:"))
                search = search.replaceAll("assigneeId:[^ ]+", "assigneeId:" + assignee);
            else
                search = "( " + search + " ) AND assigneeId:" + assignee;
        } else {
            search = "assigneeId:" + assignee;
        }

        // default sort by due date
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Task_.DUE_DATE).ascending());
        }

        GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
        var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
        return taskRepository.findAll(specs, pageable);
    }

    @Override
    public long countTasksByProjectId(UUID projectId) {
        return taskRepository
                .count((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("projectId"), projectId));
    }

    @Override
    public Page<Task> getTasksOfProject(Pageable pageable, UUID projectId, @Nullable String search) {
        // default sort by created date
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Task_.CREATED_STAMP).ascending());
        }

        if (search != null && !search.equals("")) {
            search = "( " + search + " ) AND projectId:" + projectId;
        } else {
            search = "projectId:" + projectId;
        }

        GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
        var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
        return taskRepository.findAll(specs, pageable);
    }

    @Override
    @Transactional
    public Task updateTask(UUID taskId, TaskForm taskForm, String updateBy) {
        List<TaskLogDetail> taskLogDetails = new ArrayList<>();
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return null;
        }
        Date updatedTime = new Date();

        TaskLog taskLog = TaskLog.builder().taskId(taskId).creatorId(updateBy).createdAt(updatedTime)
                .comment(taskForm.getNote()).build();

        if (taskForm.getName() != null && !taskForm.getName().isEmpty() && !taskForm.getName().equals(task.getName())) {
            taskLogDetails
                    .add(TaskLogDetail.builder().event("update").field("name").oldValue(task.getName())
                            .newValue(taskForm.getName()).build());
            task.setName(taskForm.getName());
        }

        if (taskForm.getDescription() != null && !taskForm.getDescription().isEmpty() && !taskForm.getDescription()
                .equals(task.getDescription())) {
            taskLogDetails.add(TaskLogDetail.builder().event(task.getDescription() == null ? "set" : "update")
                    .field("description")
                    .oldValue(task.getDescription()).newValue(taskForm.getDescription()).build());
            task.setDescription(taskForm.getDescription());
        }

        if (taskForm.getFromDate() != null && !taskForm.getFromDate().equals(task.getFromDate())) {
            taskLogDetails.add(TaskLogDetail.builder().event(task.getFromDate() == null ? "set" : "update")
                    .field("fromDate")
                    .oldValue(task.getFromDate() != null ? new SimpleDateFormat("dd-MM-yyyy").format(task.getFromDate())
                            : "")
                    .newValue(new SimpleDateFormat("dd-MM-yyyy").format(taskForm.getFromDate())).build());
            task.setFromDate(taskForm.getFromDate());
        }

        if (taskForm.getDueDate() != null && !taskForm.getDueDate().equals(task.getDueDate())) {
            taskLogDetails
                    .add(TaskLogDetail.builder().event(task.getDueDate() == null ? "set" : "update").field("dueDate")
                            .oldValue(new SimpleDateFormat("dd-MM-yyyy").format(task.getDueDate()))
                            .newValue(new SimpleDateFormat("dd-MM-yyyy").format(taskForm.getDueDate())).build());
            task.setDueDate(taskForm.getDueDate());
        }

        if (taskForm.getAttachmentPaths() != null && !taskForm.getAttachmentPaths().isEmpty()) {
            taskLogDetails.add(TaskLogDetail.builder().event("attachment").field("attachmentPaths")
                    .oldValue("").newValue(taskForm.getAttachmentPaths()).build());
        }

        if (taskForm.getStatusId() != null && !taskForm.getStatusId().isEmpty() && !taskForm.getStatusId()
                .equals(task.getStatusId())) {
            taskLogDetails.add(TaskLogDetail.builder().event("update").field("statusId")
                    .oldValue(task.getStatusId()).newValue(taskForm.getStatusId()).build());
            task.setStatusId(taskForm.getStatusId());
        }

        if (taskForm.getPriorityId() != null && !taskForm.getPriorityId().isEmpty() && !taskForm.getPriorityId()
                .equals(task.getPriorityId())) {
            taskLogDetails
                    .add(TaskLogDetail.builder().event(task.getPriorityId() == null ? "set" : "update")
                            .field("priorityId")
                            .oldValue(task.getPriorityId())
                            .newValue(taskForm.getPriorityId()).build());
            task.setPriorityId(taskForm.getPriorityId());
        }

        if (taskForm.getCategoryId() != null && !taskForm.getCategoryId().isEmpty() && !taskForm.getCategoryId()
                .equals(task.getCategoryId())) {
            taskLogDetails.add(TaskLogDetail.builder().event(taskForm.getCategoryId() == null ? "set" : "update")
                    .field("categoryId")
                    .oldValue(task.getCategoryId())
                    .newValue(taskForm.getCategoryId()).build());
            task.setCategoryId(taskForm.getCategoryId());
        }

        if (taskForm.getEstimatedTime() != null && !taskForm.getEstimatedTime().equals(task.getEstimatedTime())) {
            taskLogDetails.add(TaskLogDetail.builder().event(task.getEstimatedTime() == null ? "set" : "update")
                    .field("estimatedTime")
                    .oldValue(task.getEstimatedTime().toString())
                    .newValue(taskForm.getEstimatedTime().toString()).build());
            task.setEstimatedTime(taskForm.getEstimatedTime());
        }

        if (taskForm.getProgress() != null && !taskForm.getProgress().equals(task.getProgress())) {
            taskLogDetails.add(TaskLogDetail.builder().event("update").field("progress")
                    .oldValue(task.getProgress().toString())
                    .newValue(taskForm.getProgress().toString()).build());
            task.setProgress(taskForm.getProgress());
        }

        if (taskForm.getAssigneeId() != null && !taskForm.getAssigneeId().isEmpty() && !taskForm.getAssigneeId()
                .equals(task.getAssigneeId())) {
            // TODO: check if assignee is a member of project
            taskLogDetails.add(
                    TaskLogDetail.builder().event(task.getAssigneeId() == null ? "set" : "update").field("assigneeId")
                            .oldValue(task.getAssigneeId())
                            .newValue(taskForm.getAssigneeId()).build());
            task.setAssigneeId(taskForm.getAssigneeId());
        }

        taskLog.setDetails(taskLogDetails);

        if (taskLogDetails.size() > 0 || taskLog.getComment() != null) {
            taskLogService.addLog(taskLog);

            task.setLastUpdatedStamp(updatedTime);
            return taskRepository.save(task);
        } else {
            return null;
        }
    }

    public List<TaskHierarchyDTO> getTaskHierarchyByRoot(UUID ancestorId) {
        var ancestor = taskRepository.findById(ancestorId).orElse(null);
        if (ancestor == null) {
            return null;
        }
        var tasks = taskRepository.getTaskByAncestorIdAndLftAndRgt(ancestorId, ancestor.getLft(), ancestor.getRgt());
        var taskLevel = findTaskLevel(tasks);
        return tasks.stream().map(task -> {
            var dto = modelMapper.map(task, TaskHierarchyDTO.class);
            dto.setLevel(taskLevel.get(task.getId()));
            return dto;
        }).toList();
    }

    private Map<UUID, Integer> findTaskLevel(List<Task> tasks) {
        // Predicate: task is sorted by lft
        Map<UUID, Integer> taskLevel = new HashMap<>();
        for (Task task : tasks) {
            taskLevel.put(task.getId(), 0);
        }
        for (Task task : tasks) {
            if (task.getParentId() != null) {
                taskLevel.put(task.getId(), taskLevel.get(task.getParentId()) + 1);
            }
        }
        return taskLevel;
    }

    public List<TaskGanttDTO> getTaskGantt(UUID projectId, String from, String to, String q) {
        var unixTime = Long.parseLong(from);
        var fromDate = new Date(unixTime * 1000L);
        unixTime = Long.parseLong(to);
        var toDate = new Date(unixTime * 1000L);

        if (q != null && !q.isEmpty()) {
            // exclude fields
            if (q.contains(Task_.CREATED_DATE)) {
                // remove created_date field
                q = q.replaceAll(Task_.CREATED_DATE + "(" + Joiner.on("|")
                        .join(SearchOperation.SIMPLE_OPERATION_SET).replace("[", "\\[").replace("]", "\\]") + ")[^ ]+",
                        "");
            }

            if (q.contains(Task_.DUE_DATE)) {
                // remove due_date field
                q = q.replaceAll(Task_.DUE_DATE + "(" + Joiner.on("|")
                        .join(SearchOperation.SIMPLE_OPERATION_SET).replace("[", "\\[").replace("]", "\\]") + ")[^ ]+",
                        "");
            }

            if (q.contains(Task_.FROM_DATE)) {
                // remove progress field
                q = q.replaceAll(Task_.PROGRESS + "(" + Joiner.on("|")
                        .join(SearchOperation.SIMPLE_OPERATION_SET).replace("[", "\\[").replace("]", "\\]") + ")[^ ]+",
                        "");
            }
        } else {
            q = "";
        }

        GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
        var specs = builder.build(new CriteriaParser().parse(q), TaskSpecification::new);
        // WHERE ... AND (projectId = )
        // AND (fromDate between fromDate and toDate
        // OR (fromDate is NULL AND createdStamp between fromDate and toDate)
        // OR (dueDate between fromDate and toDate)
        // )
        specs = Specification.where(specs)
                .and((Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> criteriaBuilder
                        .equal(root.get(Task_.PROJECT_ID), projectId))
                .and(Specification.where(
                        (Root<Task> root, CriteriaQuery<?> query,
                                CriteriaBuilder criteriaBuilder) -> criteriaBuilder
                                        .between(root.get(Task_.FROM_DATE), fromDate, toDate))
                        .or(
                                (Root<Task> root, CriteriaQuery<?> query,
                                        CriteriaBuilder criteriaBuilder) -> criteriaBuilder
                                                .and(criteriaBuilder.isNull(root.get(Task_.FROM_DATE)),
                                                        criteriaBuilder.between(
                                                                root.get(Task_.CREATED_STAMP), fromDate, toDate)))
                        .or(
                                (Root<Task> root, CriteriaQuery<?> query,
                                        CriteriaBuilder criteriaBuilder) -> criteriaBuilder
                                                .between(root.get(Task_.DUE_DATE), fromDate, toDate)));
        return taskRepository.findAll(specs, Sort.by(Task_.CREATED_STAMP).ascending()).stream()
                .map(task -> modelMapper.map(task, TaskGanttDTO.class)).toList();
    }

}
