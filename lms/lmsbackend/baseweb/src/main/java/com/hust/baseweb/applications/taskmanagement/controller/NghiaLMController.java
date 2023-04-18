package com.hust.baseweb.applications.taskmanagement.controller;

import com.hust.baseweb.applications.contentmanager.model.ContentModel;
import com.hust.baseweb.applications.taskmanagement.dto.dao.*;
import com.hust.baseweb.applications.taskmanagement.dto.form.*;
import com.hust.baseweb.applications.taskmanagement.entity.*;
import com.hust.baseweb.applications.taskmanagement.repository.ProjectMemberRepository;
import com.hust.baseweb.applications.taskmanagement.service.*;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.entity.Person;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.service.PartyService;
import com.hust.baseweb.service.UserService;
import com.hust.baseweb.service.PersonService;
import lombok.AllArgsConstructor;
import okhttp3.Response;
import org.bouncycastle.cert.ocsp.RespID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@CrossOrigin
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class NghiaLMController {

    private final ProjectService projectService;

    private final ProjectMemberService projectMemberService;

    private final ProjectMemberRepository projectMemberRepository;

    private final PartyService partyService;

    private final PersonService personService;

    private final TaskService taskService;

    private final TaskCategoryService taskCategoryService;

    private final TaskPriorityService taskPriorityService;

    private final TaskAssignableService taskAssignableService;

    private final UserService userService;

    private final TaskStatusService taskStatusService;

    private final TaskExecutionService taskExecutionService;

    private final NotificationsService notificationsService;

    private final SkillService skillService;

    @GetMapping("/nghialm")
    public ResponseEntity<?> testController() {
        String bodyResponse = "Hello Nghia Le Minh, test spring boot api";
        return ResponseEntity.ok().body(bodyResponse);
    }

    @GetMapping("/projects/page={pageNo}/size={pageSize}")
    public ResponseEntity<Object> getListProjects(
        @PathVariable("pageNo") int pageNo,
        @PathVariable("pageSize") int pageSize
    ) {
        ProjectPagination pagination = projectService.findPaginated(pageNo, pageSize);
        return ResponseEntity.ok().body(pagination);
    }

    @GetMapping("/projects")
    public ResponseEntity<Object> getAllProjects() {
        return ResponseEntity.ok().body(projectService.getAllProjects());
    }

    @PostMapping("/projects")
    public ResponseEntity<Object> postProjects(Principal principal, @RequestBody Project project) {
        String userLoginId = principal.getName();
        UserLogin userLogin = userService.findById(userLoginId);
        UUID partyId = userLogin.getParty().getPartyId();
        Project projectRes = projectService.createProject(project);
        ProjectMember projectMember = new ProjectMember();
        ProjectMemberId projectMemberId = new ProjectMemberId();
        projectMemberId.setProjectId(projectRes.getId());
        projectMemberId.setPartyID(partyId);
        projectMember.setId(projectMemberId);
        projectMemberService.create(projectMember);
        return ResponseEntity.ok().body(projectRes);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Object> getProject(@PathVariable("projectId") UUID projectId) {
        return ResponseEntity.ok(new ProjectDao(projectService.getProjectById(projectId)));
    }

    @GetMapping("/projects/{projectId}/members")
    public ResponseEntity<Object> getMembersJoinedProject(@PathVariable("projectId") UUID projectId) {
        List<PersonDao> personDaos = new ArrayList<>();
        List<Person> persons = projectMemberService.getMemberIdJoinedProject(projectId);
        for (Person person : persons) {
            String userLoginIdTemp = projectMemberService.getUserLoginByPartyId(person.getPartyId()).getUserLoginId();
            personDaos.add(new PersonDao(person, userLoginIdTemp));
        }
        return ResponseEntity.ok(personDaos);
    }

    @PostMapping("/projects/{projectId}/members")
    public ResponseEntity<Object> addMemberToProject(
        @PathVariable("projectId") UUID projectId, @RequestBody
        ProjectMemberForm projectMemberForm
    ) {
        if (projectMemberService.checkAddedMemberInProject(
            UUID.fromString(projectMemberForm.getPartyId()),
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
            String userLoginIdTemp = "";
            UUID partyId = null;
            String fullName = "Không xác định";
            if (taskAssignment != null) {
                partyId = taskAssignment.getPartyId();
                userLoginIdTemp = projectMemberService.getUserLoginByPartyId(partyId).getUserLoginId();
                fullName = new PersonDao(personService.findByPartyId(partyId), userLoginIdTemp).getFullName();
            }

            taskDaoList.add(new TaskDao(task, userLoginIdTemp + " (" + fullName + ")", partyId));
        }

        return ResponseEntity.ok(taskDaoList);
    }

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<Object> createNewTask(
        Principal principal,
        @RequestBody TaskForm taskForm,
        @PathVariable("projectId") UUID projectId
    ) throws ParseException {
        Task task = new Task();
        task.setName(taskForm.getName());
        task.setDescription(taskForm.getDescription());
        task.setAttachmentPaths(taskForm.getAttachmentPaths());
        task.setDueDate(taskForm.getDueDate());
        task.setProject(projectService.getProjectById(projectId));
        task.setStatusItem(taskService.getStatusItemByStatusId(taskForm.getStatusId()));
        task.setTaskCategory(taskCategoryService.getTaskCategory(taskForm.getCategoryId()));
        task.setTaskPriority(taskPriorityService.getTaskPriorityById(taskForm.getPriorityId()));
        task.setCreatedByUserLoginId(principal.getName());
        Task taskRes = taskService.createTask(task);

        TaskAssignment taskAssignment = new TaskAssignment();
        taskAssignment.setTask(task);
        taskAssignment.setPartyId(taskForm.getPartyId());
        taskAssignableService.create(taskAssignment);

        TaskExecution taskExecution = new TaskExecution();
        taskExecution.setTask(taskRes);
        taskExecution.setCreatedByUserLoginId(principal.getName());
        taskExecution.setExecutionTags("issue");
        taskExecution.setProjectId(projectId);
        taskExecutionService.create(taskExecution);

        for (String skillId : taskForm.getSkillIds()) {
            taskService.addTaskSkill(taskRes.getId(), skillId);
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
        List<PersonDao> personDaos = new ArrayList<>();
        List<Person> persons = personService.getALL();
        for (Person person : persons) {
            String userLoginIdTemp = projectMemberService.getUserLoginByPartyId(person.getPartyId()).getUserLoginId();
            personDaos.add(new PersonDao(person, userLoginIdTemp));
        }
        return ResponseEntity.ok(personDaos);
    }

    @GetMapping("/assigned-tasks-user-login/page={pageNo}/size={pageSize}")
    public ResponseEntity<Object> getAssignedTasksUserLogin(
        Principal principal,
        @PathVariable("pageNo") int pageNo,
        @PathVariable("pageSize") int pageSize
    ) {
        String userLoginId = principal.getName();
        UserLogin userLogin = userService.findById(userLoginId);
        UUID partyId = userLogin.getParty().getPartyId();
        AssignedTaskPagination assignedTaskPagination = taskAssignableService.getAssignedTaskPaginated(
            partyId,
            pageNo,
            pageSize);
        return ResponseEntity.ok(assignedTaskPagination);
    }

    @GetMapping("/projects/{projectId}/statics/{type}")
    public ResponseEntity<Object> getTasksStaticsInProject(
        @PathVariable("projectId") UUID projectID,
        @PathVariable("type") String type
    ) {
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
        @PathVariable("statusId") String statusId
    ) {
        Task task = taskService.getTask(taskId);
        task.setStatusItem(taskService.getStatusItemByStatusId(statusId));
        return ResponseEntity.ok(taskService.updateTasks(task));
    }

    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Object> updateProject(
        @PathVariable("projectId") UUID projectId,
        @RequestBody ProjectForm projectForm
    ) {
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
        UUID partyId = null;
        if (taskAssignableService.getByTaskId(taskId) != null) {
            partyId = taskAssignableService.getByTaskId(taskId).getPartyId();
            assignee = projectMemberService.getUserLoginByPartyId(partyId).getUserLoginId();
        }

        return ResponseEntity.ok(new TaskDao(task, assignee, partyId));
    }

    @PostMapping("/tasks/{taskId}/comment")
    public ResponseEntity<Object> commentOnTask(
        Principal principal,
        @PathVariable("taskId") UUID taskId,
        @RequestBody CommentForm commentForm
    ) {
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
        @RequestBody CommentForm commentForm
    ) {
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
        Principal principal
    ) {
        String userLoginId = principal.getName();
        return ResponseEntity.ok(taskService.updateStatusTask(taskId, taskStatusForm, userLoginId));
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<Object> updateTask(
        @PathVariable("taskId") UUID taskId,
        @RequestBody TaskForm taskForm,
        Principal principal
    ) {
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
    public ResponseEntity<Object> postCategory(@RequestBody CategoryForm categoryForm){
        TaskCategory taskCategory = new TaskCategory();
        taskCategory.setCategoryId(categoryForm.getCategoryId());
        taskCategory.setCategoryName(categoryForm.getCategoryName());
        return ResponseEntity.ok(taskCategoryService.create(taskCategory));
    }

    @DeleteMapping("/task-category/{categoryId}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("categoryId") String categoryId){
        taskCategoryService.delete(categoryId);
        return ResponseEntity.ok("delete category success!");
    }

    // Quản lý độ ưu tiên

    @PostMapping("/task-priorities")
    public ResponseEntity<Object> postPriority(@RequestBody PriorityForm priorityForm){
        TaskPriority taskPriority = new TaskPriority();
        taskPriority.setPriorityId(priorityForm.getPriorityId());
        taskPriority.setPriorityName(priorityForm.getPriorityName());
        return ResponseEntity.ok(taskPriorityService.create(taskPriority));
    }

    @DeleteMapping("/task-priority/{priorityId}")
    public ResponseEntity<Object> deletePriority(@PathVariable("priorityId") String priorityId){
        taskPriorityService.delete(priorityId);
        return ResponseEntity.ok("delete priority success!");
    }

    //Quản lý kỹ năng

    @PostMapping("/skills")
    public ResponseEntity<Object> postSkill(@RequestBody SkillForm skillForm){
        Skill skill = new Skill();
        skill.setSkillId(skillForm.getSkillId());
        skill.setName(skillForm.getSkillName());
        skill.setDescription(skillForm.getSkillDescription());
        return ResponseEntity.ok(skillService.create(skill));
    }

    @DeleteMapping("/skill/{skillId}")
    public ResponseEntity<Object> deleteSkill(@PathVariable("skillId") String skillId){
        skillService.delete(skillId);
        return ResponseEntity.ok("delete skill success!");
    }

    @PostMapping("/suggest-assign-task")
    public ResponseEntity<Object> suggestAssignTask(@RequestBody SuggestForm suggestForm){
        return ResponseEntity.ok(taskService.suggestAssignTask(suggestForm.getProjectId(), suggestForm.getSkillIds()));
    }
}
