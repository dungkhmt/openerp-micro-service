package com.hust.openerp.taskmanagement.controller;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hust.openerp.taskmanagement.dto.dao.AssignedTaskPagination;
import com.hust.openerp.taskmanagement.dto.dao.CommentDao;
import com.hust.openerp.taskmanagement.dto.dao.HistoryDao;
import com.hust.openerp.taskmanagement.dto.dao.ProjectDao;
import com.hust.openerp.taskmanagement.dto.dao.ProjectPagination;
import com.hust.openerp.taskmanagement.dto.dao.TaskDao;
import com.hust.openerp.taskmanagement.dto.form.BoardFilterInputForm;
import com.hust.openerp.taskmanagement.dto.form.CategoryForm;
import com.hust.openerp.taskmanagement.dto.form.CommentForm;
import com.hust.openerp.taskmanagement.dto.form.PriorityForm;
import com.hust.openerp.taskmanagement.dto.form.ProjectForm;
import com.hust.openerp.taskmanagement.dto.form.ProjectMemberForm;
import com.hust.openerp.taskmanagement.dto.form.SkillForm;
import com.hust.openerp.taskmanagement.dto.form.SuggestForm;
import com.hust.openerp.taskmanagement.dto.form.TaskForm;
import com.hust.openerp.taskmanagement.dto.form.TaskStatusForm;
import com.hust.openerp.taskmanagement.entity.Comment;
import com.hust.openerp.taskmanagement.entity.Project;
import com.hust.openerp.taskmanagement.entity.ProjectMember;
import com.hust.openerp.taskmanagement.entity.Skill;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskAssignment;
import com.hust.openerp.taskmanagement.entity.TaskCategory;
import com.hust.openerp.taskmanagement.entity.TaskExecution;
import com.hust.openerp.taskmanagement.entity.TaskPriority;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.service.MailService;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.ProjectService;
import com.hust.openerp.taskmanagement.service.SkillService;
import com.hust.openerp.taskmanagement.service.TaskAssignableService;
import com.hust.openerp.taskmanagement.service.TaskCategoryService;
import com.hust.openerp.taskmanagement.service.TaskExecutionService;
import com.hust.openerp.taskmanagement.service.TaskPriorityService;
import com.hust.openerp.taskmanagement.service.TaskService;
import com.hust.openerp.taskmanagement.service.TaskStatusService;
import com.hust.openerp.taskmanagement.service.UserService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NghiaLMController {

    private final ProjectService projectService;

    private final ProjectMemberService projectMemberService;

    private final TaskService taskService;

    private final TaskCategoryService taskCategoryService;

    private final TaskPriorityService taskPriorityService;

    private final TaskAssignableService taskAssignableService;

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final TaskExecutionService taskExecutionService;

    private final SkillService skillService;

    private final NotificationService notificationsService;

    private final MailService mailService;

    @GetMapping
    public void syncUser(JwtAuthenticationToken token) {
        Jwt principal = (Jwt) token.getPrincipal();
        userService.synchronizeUser(
                principal.getClaim("preferred_username"),
                principal.getClaim("email"),
                principal.getClaim("given_name"),
                principal.getClaim("family_name"));
    }

    @GetMapping("/nghialm")
    public ResponseEntity<?> testController() {
        String bodyResponse = "Hello Nghia Le Minh, test spring boot api";
        return ResponseEntity.ok().body(bodyResponse);
    }

    @GetMapping("/projects/page={pageNo}/size={pageSize}")
    public ResponseEntity<Object> getListProjects(
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize) {
        ProjectPagination pagination = projectService.findPaginated(pageNo, pageSize);
        return ResponseEntity.ok().body(pagination);
    }

    @GetMapping("/projects")
    public ResponseEntity<Object> getAllProjects() {
        return ResponseEntity.ok().body(projectService.getAllProjects());
    }

    @PostMapping("/projects")
    public ResponseEntity<Object> postProjects(Principal principal, @RequestBody Project project) {
        String userId = principal.getName();
        Project projectRes = projectService.createProject(project);
        ProjectMember projectMember = ProjectMember.builder()
                .projectId(projectRes.getId())
                .userId(userId)
                .roleId("maintainer")
                .build();
        projectMemberService.create(projectMember);
        return ResponseEntity.ok().body(projectRes);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Object> getProject(@PathVariable("projectId") UUID projectId) {
        return ResponseEntity.ok(new ProjectDao(projectService.getProjectById(projectId)));
    }

    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<List<User>> getMembersJoinedProject(@PathVariable("projectId") UUID projectId) {
        List<User> users = projectMemberService.getMemberIdJoinedProject(projectId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/projects/{projectId}/members")
    public ResponseEntity<Object> addMemberToProject(
            @PathVariable("projectId") UUID projectId, @RequestBody ProjectMemberForm projectMemberForm) {
        if (projectMemberService.checkAddedMemberInProject(
                projectMemberForm.getMemberId(),
                UUID.fromString(projectMemberForm.getProjectId()))) {
            Map<String, String> map = new HashMap<>();
            map.put("error", "Thành viên đã trong dự án !");
            return ResponseEntity.ok(map);
        }
        return ResponseEntity.ok(projectMemberService.addMemberToProject(projectMemberForm));
    }

    @GetMapping("/tasks")
    public ResponseEntity<Object> getAllTasks() {
        List<Task> tasks = taskService.getAllTasks();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Object> getAllTasksInProject(@PathVariable("projectId") UUID projectId) {
        List<Task> tasks = taskService.getAllTaskInProject(projectId);
        List<TaskDao> taskDaoList = new ArrayList<>();
        for (Task task : tasks) {
            TaskAssignment taskAssignment = taskAssignableService.getByTaskId(task.getId());
            String fullName = "Không xác định";
            if (taskAssignment != null) {
                User user = userService.findById(taskAssignment.getAssigneeId());
                fullName = taskAssignment.getAssigneeId() + " (" + user.getFirstName() + " " + user.getLastName() + ")";
            }
            taskDaoList.add(new TaskDao(task, fullName));
        }

        return ResponseEntity.ok(taskDaoList);
    }

    @PostMapping("/projects/{projectId}/tasks")
    @Transactional
    public ResponseEntity<Object> createNewTask(
            Principal principal,
            @RequestBody TaskForm taskForm,
            @PathVariable("projectId") UUID projectId) throws ParseException {
        String userId = principal.getName();
        User assignee = userService.findById(taskForm.getAssigneeId());

        if (assignee == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy người được giao nhiệm vụ");
        }

        Task task = new Task();
        task.setName(taskForm.getName());
        task.setDescription(taskForm.getDescription());
        task.setAttachmentPaths(taskForm.getAttachmentPaths());
        task.setDueDate(taskForm.getDueDate());
        task.setStatusId(taskForm.getStatusId());
        task.setProjectId(projectId);
        task.setCategoryId(taskForm.getCategoryId());
        task.setPriorityId(taskForm.getPriorityId());
        task.setCreatedByUserId(userId);
        Task taskRes = taskService.createTask(task);

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTask(task);
        taskAssignment.setAssigneeId(taskForm.getAssigneeId());
        taskAssignableService.create(taskAssignment);

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(taskRes);
        taskExecution.setCreatedByUserId(userId);
        taskExecution.setExecutionTags("issue");
        taskExecution.setProjectId(projectId);
        taskExecution.setCategory(taskForm.getCategoryId());
        taskExecution.setTaskName(taskForm.getName());
        taskExecution.setTaskDescription(taskForm.getDescription());
        taskExecution.setStatus(taskForm.getStatusId());
        taskExecution.setPriority(taskForm.getPriorityId());
        taskExecution.setDueDate(taskForm.getDueDate().toString());

        taskExecutionService.create(taskExecution);

        for (String skillId : taskForm.getSkillIds()) {
            taskService.addTaskSkill(taskRes.getId(), skillId);
        }

        // push notification and send mail
        // TODO: refactor this into a service

        try {
            notificationsService.sendNotification(
                    "admin",
                    assignee.getId(),
                    "Bạn được giao nhiệm vụ mới: " + taskRes.getName(),
                    "/project/" + projectId + "/tasks");

            String assigneeMail = assignee.getEmail();
            // FIXME: this is a hack, we should use a template engine
            mailService.sendSimpleMail(
                    new String[] { assigneeMail },
                    "OPEN ERP - Thông báo bạn đã được giao nhiệm vụ mới",
                    "Bạn đã được giao nhiệm vụ mới: " +
                            taskRes.getName() +
                            ". Đây là email tự động, bạn không trả lời lại email này!",
                    "OpenERP");
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle async exception
        }

        return ResponseEntity.ok(taskRes);
    }

    @GetMapping("/task-categories")
    public ResponseEntity<Object> getListCategories() {
        return ResponseEntity.ok(taskCategoryService.getAllTaskCategory());
    }

    @GetMapping("/task-priorities")
    public ResponseEntity<Object> getListPriorities() {
        return ResponseEntity.ok(taskPriorityService.getAll());
    }

    @GetMapping("/task-persons")
    public ResponseEntity<Object> getListPersons() {
        List<User> persons = userService.getAllUsers();
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/assigned-tasks-user-login/page={pageNo}/size={pageSize}")
    public ResponseEntity<Object> getAssignedTasksUserLogin(
            Principal principal,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize") int pageSize) {
        String userLoginId = principal.getName();
        AssignedTaskPagination assignedTaskPagination = taskAssignableService.getAssignedTaskPaginated(
                userLoginId,
                pageNo,
                pageSize);
        return ResponseEntity.ok(assignedTaskPagination);
    }

    @GetMapping("/projects/{projectId}/statics/{type}")
    public ResponseEntity<Object> getTasksStaticsInProject(
            @PathVariable("projectId") UUID projectID,
            @PathVariable("type") String type) {
        int sumTasks = 0;
        List<Object[]> listTasks = null;

        if (type.equals("category")) {
            listTasks = taskService.getTaskStaticsCategoryInProject(projectID);
        } else if (type.equals("status")) {
            listTasks = taskService.getTaskStaticsStatusInProject(projectID);
        }

        List<Map<String, String>> result = new ArrayList<>();
        if (listTasks != null && !listTasks.isEmpty()) {
            for (Object[] objects : listTasks) {
                sumTasks += (int) objects[2];
            }
            for (Object[] objects : listTasks) {
                Map<String, String> temp = new HashMap<>();
                temp.put("id", (String) objects[0]);
                temp.put("name", (String) objects[1]);
                temp.put(
                        "value",
                        String.valueOf(Math.round(((int) objects[2] * 100 / (sumTasks * 1.0)) * 100.0) / 100.0));
                result.add(temp);
            }
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/task-status-list")
    public ResponseEntity<Object> getListTaskStatus() {
        return ResponseEntity.ok(taskStatusService.getAllTaskStatus());
    }

    @GetMapping("/tasks/{taskId}/status/{statusId}")
    public ResponseEntity<Object> updateStatusTask(
            @PathVariable("taskId") UUID taskId,
            @PathVariable("statusId") String statusId) {
        Task task = taskService.getTask(taskId);
        task.setStatusItem(taskService.getStatusItemByStatusId(statusId));
        return ResponseEntity.ok(taskService.updateTasks(task));
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Object> updateProject(
            @PathVariable("projectId") UUID projectId,
            @RequestBody ProjectForm projectForm) {
        Project project = projectService.getProjectById(projectId);
        project.setName(projectForm.getName());
        project.setCode(projectForm.getCode());
        return ResponseEntity.ok(projectService.save(project));
    }

    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Object> deleteProject(@PathVariable("projectId") UUID projectId) {
        projectService.deleteProjectById(projectId);
        return ResponseEntity.ok("delete success!");
    }

    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<Object> showTask(@PathVariable("taskId") UUID taskId) {
        Task task = taskService.getTask(taskId);
        String assignee = "";
        String userId = null;
        if (taskAssignableService.getByTaskId(taskId) != null) {
            userId = taskAssignableService.getByTaskId(taskId).getAssigneeId();
            User assignUser = userService.findById(userId);
            assignee = assignUser.getId() + " (" + assignUser.getFirstName() + " " + assignUser.getLastName() + ")";
        }

        return ResponseEntity.ok(new TaskDao(task, assignee));
    }

    @PostMapping("/tasks/{taskId}/comment")
    public ResponseEntity<Object> commentOnTask(
            Principal principal,
            @PathVariable("taskId") UUID taskId,
            @RequestBody CommentForm commentForm) {
        Task task = taskService.getTask(taskId);
        String userLoginId = principal.getName();
        return ResponseEntity.ok(taskExecutionService.createTaskComment(task, commentForm, userLoginId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable("commentId") UUID commentId) {
        taskExecutionService.deleteComment(commentId);
        return ResponseEntity.ok("success");
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Object> updateCommentOnTask(
            @PathVariable("commentId") UUID commentId,
            @RequestBody CommentForm commentForm) {
        taskExecutionService.updateTaskComment(commentId, commentForm);
        return ResponseEntity.ok("update comment success!");
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Object> getAllComments(@PathVariable("taskId") UUID taskId, Principal principal) {
        List<Comment> comments = taskExecutionService.getAllCommentsByTaskId(taskId);
        List<CommentDao> commentDaos = new ArrayList<>();
        String userLoginId = principal.getName();
        for (Comment comment : comments) {
            boolean isModify = userLoginId.equals(comment.getCreatedByUserLoginId());
            commentDaos.add(new CommentDao(comment, isModify));
        }
        return ResponseEntity.ok(commentDaos);
    }

    @GetMapping("/projects/{projectId}/history")
    public ResponseEntity<Object> getHistory(@PathVariable("projectId") UUID projectId) {
        List<HistoryDao> historyDaos = new ArrayList<>();
        List<Object[]> objects = taskExecutionService.getAllDistinctDay(projectId);
        for (Object[] object : objects) {
            Date date = (Date) object[0];
            String dateStr = new SimpleDateFormat("E, dd MMM yyyy").format(date);
            HistoryDao historyDao = new HistoryDao();
            historyDao.setDate(dateStr);
            historyDao.setTaskExecutionList(taskExecutionService.getAllTaskExecutionByDate(date, projectId));
            historyDaos.add(historyDao);
        }
        return ResponseEntity.ok(historyDaos);
    }

    @PutMapping("/tasks/{taskId}/status")
    public ResponseEntity<Object> updateStatusTask(
            @PathVariable("taskId") UUID taskId,
            @RequestBody TaskStatusForm taskStatusForm,
            Principal principal) {
        String userLoginId = principal.getName();
        return ResponseEntity.ok(taskService.updateStatusTask(taskId, taskStatusForm, userLoginId));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(
            @PathVariable("taskId") UUID taskId,
            @RequestBody TaskForm taskForm,
            Principal principal) {
        String createdByUserLoginId = principal.getName();
        taskService.updateTask(taskId, taskForm, createdByUserLoginId);
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/board")
    public ResponseEntity<Object> board(@RequestBody BoardFilterInputForm boardFilterInputForm) {
        return ResponseEntity.ok(projectService.getDataBoardWithFilters(boardFilterInputForm));
    }

    @GetMapping("/skills")
    public ResponseEntity<Object> getList() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    // Quản lý danh mục

    @PostMapping("/task-categories")
    public ResponseEntity<Object> postCategory(@RequestBody CategoryForm categoryForm) {
        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setCategoryId(categoryForm.getCategoryId());
        taskCategory.setCategoryName(categoryForm.getCategoryName());
        return ResponseEntity.ok(taskCategoryService.create(taskCategory));
    }

    @DeleteMapping("/task-category/{categoryId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("categoryId") String categoryId) {
        taskCategoryService.delete(categoryId);
        return ResponseEntity.ok("delete category success!");
    }

    // Quản lý độ ưu tiên

    @PostMapping("/task-priorities")
    public ResponseEntity<Object> postPriority(@RequestBody PriorityForm priorityForm) {
        TaskPriority taskPriority = new TaskPriority();
        taskPriority.setPriorityId(priorityForm.getPriorityId());
        taskPriority.setPriorityName(priorityForm.getPriorityName());
        return ResponseEntity.ok(taskPriorityService.create(taskPriority));
    }

    @DeleteMapping("/task-priority/{priorityId}")
    public ResponseEntity<Object> deletePriority(@PathVariable("priorityId") String priorityId) {
        taskPriorityService.delete(priorityId);
        return ResponseEntity.ok("delete priority success!");
    }

    // Quản lý kỹ năng

    @PostMapping("/skills")
    public ResponseEntity<Object> postSkill(@RequestBody SkillForm skillForm) {
        Skill skill = new Skill();
        skill.setSkillId(skillForm.getSkillId());
        skill.setName(skillForm.getSkillName());
        skill.setDescription(skillForm.getSkillDescription());
        return ResponseEntity.ok(skillService.create(skill));
    }

    @DeleteMapping("/skill/{skillId}")
    public ResponseEntity<Object> deleteSkill(@PathVariable("skillId") String skillId) {
        skillService.delete(skillId);
        return ResponseEntity.ok("delete skill success!");
    }

    @PostMapping("/suggest-assign-task")
    public ResponseEntity<Object> suggestAssignTask(@RequestBody SuggestForm suggestForm) {
        return ResponseEntity.ok(taskService.suggestAssignTask(suggestForm.getProjectId(), suggestForm.getSkillIds()));
    }
}
