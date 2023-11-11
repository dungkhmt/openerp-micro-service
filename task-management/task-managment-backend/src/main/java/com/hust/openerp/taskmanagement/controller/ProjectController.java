/*
 * package com.hust.openerp.taskmanagement.controller;
 *
 * import java.security.Principal;
 * import java.text.ParseException;
 * import java.util.ArrayList;
 * import java.util.HashMap;
 * import java.util.List;
 * import java.util.Map;
 * import java.util.UUID;
 *
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.http.ResponseEntity;
 * import org.springframework.web.bind.annotation.CrossOrigin;
 * import org.springframework.web.bind.annotation.GetMapping;
 * import org.springframework.web.bind.annotation.PathVariable;
 * import org.springframework.web.bind.annotation.PostMapping;
 * import org.springframework.web.bind.annotation.RequestBody;
 * import org.springframework.web.bind.annotation.RestController;
 *
 * import com.hust.openerp.taskmanagement.dto.dao.PersonDao;
 * import com.hust.openerp.taskmanagement.dto.dao.ProjectDao;
 * import com.hust.openerp.taskmanagement.dto.dao.ProjectPagination;
 * import com.hust.openerp.taskmanagement.dto.dao.TaskDao;
 * import com.hust.openerp.taskmanagement.dto.form.ProjectMemberForm;
 * import com.hust.openerp.taskmanagement.dto.form.TaskForm;
 * import com.hust.openerp.taskmanagement.entity.Project;
 * import com.hust.openerp.taskmanagement.entity.ProjectMember;
 * import com.hust.openerp.taskmanagement.entity.Task;
 * import com.hust.openerp.taskmanagement.entity.TaskAssignment;
 * import com.hust.openerp.taskmanagement.entity.TaskExecution;
 * import com.hust.openerp.taskmanagement.entity.User;
 * import com.hust.openerp.taskmanagement.service.ProjectMemberService;
 * import com.hust.openerp.taskmanagement.service.ProjectService;
 * import com.hust.openerp.taskmanagement.service.TaskAssignableService;
 * import com.hust.openerp.taskmanagement.service.TaskService;
 *
 * import lombok.AllArgsConstructor;
 *
 * @RestController("/projects")
 *
 * @CrossOrigin
 *
 * @AllArgsConstructor(onConstructor = @__(@Autowired))
 * public class ProjectController {
 * private ProjectService projectService;
 *
 * private final ProjectMemberService projectMemberService;
 *
 * private final TaskService taskService;
 *
 * private final TaskAssignableService taskAssignableService;
 *
 * @GetMapping("/page={pageNo}/size={pageSize}")
 * public ResponseEntity<Object> getListProjects(
 *
 * @PathVariable("pageNo") int pageNo,
 *
 * @PathVariable("pageSize") int pageSize) {
 * ProjectPagination pagination = projectService.findPaginated(pageNo,
 * pageSize);
 * return ResponseEntity.ok().body(pagination);
 * }
 *
 * @GetMapping
 * public ResponseEntity<Object> getAllProjects() {
 * return ResponseEntity.ok().body(projectService.getAllProjects());
 * }
 *
 * @PostMapping
 * public ResponseEntity<Object> postProjects(Principal principal, @RequestBody
 * Project project) {
 * String userId = principal.getName();
 * Project projectRes = projectService.createProject(project);
 * ProjectMember projectMember = ProjectMember.builder()
 * .projectId(projectRes.getId())
 * .userId(userId)
 * .build();
 * projectMemberService.create(projectMember);
 * return ResponseEntity.ok().body(projectRes);
 * }
 *
 * @GetMapping("/projects/{projectId}")
 * public ResponseEntity<Object> getProject(@PathVariable("projectId") UUID
 * projectId) {
 * return ResponseEntity.ok(new
 * ProjectDao(projectService.getProjectById(projectId)));
 * }
 *
 * @GetMapping("/projects/{projectId}/members")
 * public ResponseEntity<List<User>>
 * getMembersJoinedProject(@PathVariable("projectId") UUID projectId) {
 * List<User> users = projectMemberService.getMemberIdJoinedProject(projectId);
 * return ResponseEntity.ok(users);
 * }
 *
 * @PostMapping("/projects/{projectId}/members")
 * public ResponseEntity<Object> addMemberToProject(
 *
 * @PathVariable("projectId") UUID projectId, @RequestBody ProjectMemberForm
 * projectMemberForm) {
 * if (projectMemberService.checkAddedMemberInProject(
 * UUID.fromString(projectMemberForm.getPartyId()),
 * UUID.fromString(projectMemberForm.getProjectId()))) {
 * Map<String, String> map = new HashMap<>();
 * map.put("error", "Thành viên đã trong dự án !");
 * return ResponseEntity.ok(map);
 * }
 * return
 * ResponseEntity.ok(projectMemberService.addMemberToProject(projectMemberForm))
 * ;
 * }
 *
 * @GetMapping("/projects/{projectId}/tasks")
 * public ResponseEntity<Object> getAllTasksInProject(@PathVariable("projectId")
 * UUID projectId) {
 * List<Task> tasks = taskService.getAllTaskInProject(projectId);
 * List<TaskDao> taskDaoList = new ArrayList<>();
 * for (Task task : tasks) {
 * TaskAssignment taskAssignment =
 * taskAssignableService.getByTaskId(task.getId());
 * String userLoginIdTemp = "";
 * String userId = null;
 * String fullName = "Không xác định";
 * if (taskAssignment != null) {
 * userId = taskAssignment.getUserId();
 * userLoginIdTemp = projectMemberService.getUserLoginById(userId).getId();
 * fullName = new PersonDao(personService.findByPartyId(userId),
 * userLoginIdTemp).getFullName();
 * }
 *
 * taskDaoList.add(new TaskDao(task, userLoginIdTemp + " (" + fullName + ")",
 * userId));
 * }
 *
 * return ResponseEntity.ok(taskDaoList);
 * }
 *
 * @PostMapping("/projects/{projectId}/tasks")
 * public ResponseEntity<Object> createNewTask(
 * Principal principal,
 *
 * @RequestBody TaskForm taskForm,
 *
 * @PathVariable("projectId") UUID projectId) throws ParseException {
 * Task task = new Task();
 * task.setName(taskForm.getName());
 * task.setDescription(taskForm.getDescription());
 * task.setAttachmentPaths(taskForm.getAttachmentPaths());
 * task.setDueDate(taskForm.getDueDate());
 * task.setProject(projectService.getProjectById(projectId));
 * task.setStatusItem(taskService.getStatusItemByStatusId(taskForm.getStatusId()
 * ));
 * task.setTaskCategory(taskCategoryService.getTaskCategory(taskForm.
 * getCategoryId()));
 * task.setTaskPriority(taskPriorityService.getTaskPriorityById(taskForm.
 * getPriorityId()));
 * task.setCreatedByUserLoginId(principal.getName());
 * Task taskRes = taskService.createTask(task);
 *
 * TaskAssignment taskAssignment = new TaskAssignment();
 * taskAssignment.setTask(task);
 * taskAssignment.setUserId(taskForm.getPartyId());
 * taskAssignableService.create(taskAssignment);
 *
 * TaskExecution taskExecution = new TaskExecution();
 * taskExecution.setTask(taskRes);
 * taskExecution.setCreatedByUserLoginId(principal.getName());
 * taskExecution.setExecutionTags("issue");
 * taskExecution.setProjectId(projectId);
 * taskExecutionService.create(taskExecution);
 *
 * for (String skillId : taskForm.getSkillIds()) {
 * taskService.addTaskSkill(taskRes.getId(), skillId);
 * }
 * return ResponseEntity.ok(taskRes);
 * }
 *
 * }
 */
