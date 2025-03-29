package com.hust.openerp.taskmanagement.service.implement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Joiner;
import com.hust.openerp.taskmanagement.dto.ProjectDTO;
import com.hust.openerp.taskmanagement.dto.TaskDTO;
import com.hust.openerp.taskmanagement.dto.TaskGanttDTO;
import com.hust.openerp.taskmanagement.dto.TaskHierarchyDTO;
import com.hust.openerp.taskmanagement.dto.form.CreateTaskForm;
import com.hust.openerp.taskmanagement.dto.form.UpdateTaskForm;
import com.hust.openerp.taskmanagement.entity.Task;
import com.hust.openerp.taskmanagement.entity.TaskLog;
import com.hust.openerp.taskmanagement.entity.TaskLogDetail;
import com.hust.openerp.taskmanagement.entity.Task_;
import com.hust.openerp.taskmanagement.entity.User;
import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import com.hust.openerp.taskmanagement.repository.EventRepository;
import com.hust.openerp.taskmanagement.repository.TaskRepository;
import com.hust.openerp.taskmanagement.service.NotificationService;
import com.hust.openerp.taskmanagement.service.ProjectMemberService;
import com.hust.openerp.taskmanagement.service.TaskLogService;
import com.hust.openerp.taskmanagement.service.TaskService;
import com.hust.openerp.taskmanagement.service.UserService;
import com.hust.openerp.taskmanagement.specification.TaskSpecification;
import com.hust.openerp.taskmanagement.specification.builder.GenericSpecificationsBuilder;
import com.hust.openerp.taskmanagement.util.CriteriaParser;
import com.hust.openerp.taskmanagement.util.SearchOperation;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Service
public class TaskServiceImplement implements TaskService {
	private final ModelMapper modelMapper;

	private final TaskRepository taskRepository;

	private final EventRepository eventRepository;

	private ProjectMemberService projectMemberService;

	private TaskLogService taskLogService;

	private NotificationService notiService;

	private UserService userService;

	@Value("${app.endpoint.client}")
	private String clientEndpoint;

	@Autowired
	public TaskServiceImplement(ModelMapper modelMapper, TaskRepository taskRepository, EventRepository eventRepository,
			ProjectMemberService projectMemberService, TaskLogService taskLogService, NotificationService notiService,
			UserService userService) {
		this.modelMapper = modelMapper;
		this.taskRepository = taskRepository;
		this.eventRepository = eventRepository;
		this.projectMemberService = projectMemberService;
		this.taskLogService = taskLogService;
		this.notiService = notiService;
		this.userService = userService;
	}

	@Override
	@Transactional
	public TaskDTO createTask(CreateTaskForm taskForm, String creatorId) {
		List<TaskLogDetail> taskLogDetails = new ArrayList<>();
		var task = modelMapper.map(taskForm, Task.class);
		var createdTime = new Date();
		var dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

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
					.details(new ArrayList<>(List.of(TaskLogDetail.builder().event("subtask").field("subtaskId")
							.oldValue("").newValue(taskId.toString()).build())))
					.build();
			taskLogService.addLog(parentLog);
		}

		if (task.getName() != null && !task.getName().isEmpty()) {
			taskLogDetails.add(
					TaskLogDetail.builder().event("set").field("name").oldValue("").newValue(task.getName()).build());
		}

		if (task.getDescription() != null && !task.getDescription().isEmpty()) {
			taskLogDetails.add(TaskLogDetail.builder().event("set").field("description").oldValue("")
					.newValue(task.getDescription()).build());
		}

		if (task.getFromDate() != null) {
			taskLogDetails.add(TaskLogDetail.builder().event("set").field("fromDate").oldValue("")
					.newValue(dateFormatter.format(task.getFromDate())).build());
		}

		if (task.getDueDate() != null) {
			taskLogDetails.add(TaskLogDetail.builder().event("set").field("dueDate").oldValue("")
					.newValue(dateFormatter.format(task.getDueDate())).build());
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

		if (task.getEventId() != null) {
			taskLogDetails.add(TaskLogDetail.builder().event("set").field("eventId").oldValue("")
					.newValue(task.getEventId().toString()).build());
		}

		var savedTask = taskRepository.save(task);

		taskLog.setDetails(taskLogDetails);

		taskLogService.addLog(taskLog);

		if (task.getAssigneeId() != null && !task.getAssigneeId().equals(creatorId)) {
			sendNotification(userService.findById(creatorId), userService.findById(taskForm.getAssigneeId()), task);
		}

		return convertToDto(savedTask);
	}

	@Override
	public TaskDTO getTask(UUID taskId, String getterId) {
		var task = taskRepository.findById(taskId).orElseThrow(() -> new ApiException(ErrorCode.TASK_NOT_FOUND));

		if (!projectMemberService.checkAddedMemberInProject(getterId, task.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

		var dto = convertToDto(task);
		dto.setHierarchies(this.getTaskHierarchyByRoot(task.getAncestorId()));

		return dto;
	}

	public Page<TaskDTO> getTasksAssignedToUser(Pageable pageable, String assignee, @Nullable String search) {
		// replace "assignee:value" if exist in search by "assignee:`assignee`"
		if (search != null && !search.equals("")) {
			if (search.contains(Task_.ASSIGNEE_ID + ":"))
				search = search.replaceAll(Task_.ASSIGNEE_ID + ":[^ ]+", Task_.ASSIGNEE_ID + ":" + assignee);
			else
				search = "( " + search + " ) AND " + Task_.ASSIGNEE_ID + ":" + assignee;
		} else {
			search = Task_.ASSIGNEE_ID + ":" + assignee;
		}

		// default sort by due date
		if (pageable.getSort().isUnsorted()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
					Sort.by(Task_.DUE_DATE).ascending());
		}

		GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
		var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
		return taskRepository.findAll(specs, pageable).map(entity -> {
			var dto = modelMapper.map(entity, TaskDTO.class);
			dto.setProject(modelMapper.map(entity.getProject(), ProjectDTO.class));
			return dto;
		});
	}

	public List<TaskDTO> getTasksForMemberInProject(UUID projectId, String assigneeId) {
		// Build the search query based on assigneeId and projectId
		String search = Task_.ASSIGNEE_ID + ":" + assigneeId + " AND " + Task_.PROJECT_ID + ":" + projectId;

		// Default sort by due date
		Sort dateSort = Sort.by(Task_.DUE_DATE).ascending();

		GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
		var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
		List<Task> tasks = taskRepository.findAll(specs, dateSort);

		return tasks.stream().map(entity -> {
			var dto = modelMapper.map(entity, TaskDTO.class);
			dto.setProject(modelMapper.map(entity.getProject(), ProjectDTO.class));
			return dto;
		}).collect(Collectors.toList());
	}

	public List<TaskDTO> getEventTasks(String userId, UUID eventId) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_FOUND));
		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		// Build the search query based on eventId
		String search = Task_.EVENT_ID + ":" + eventId;
		// Default sort by due date
		Sort dateSort = Sort.by(Task_.DUE_DATE).ascending();

		GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
		var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
		List<Task> tasks = taskRepository.findAll(specs, dateSort);

		return tasks.stream().map(entity -> {
			var dto = modelMapper.map(entity, TaskDTO.class);
			return dto;
		}).collect(Collectors.toList());
	}
	
	public void addExistingTasksToEvent(String userId, UUID eventId, List<UUID> taskIds) {
		var event = eventRepository.findById(eventId).orElseThrow(() -> new ApiException(ErrorCode.EVENT_NOT_EXIST));
		if (!projectMemberService.checkAddedMemberInProject(userId, event.getProjectId())) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		
		List<Task> tasks = taskRepository.findAllById(taskIds);
        for (Task task : tasks) {
            task.setEventId(eventId);
        }
        taskRepository.saveAll(tasks);
	}
	
	@Override
	public List<TaskDTO> getTasksWithoutEvent(String userId, UUID projectId) {
		if (!projectMemberService.checkAddedMemberInProject(userId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}
		
		List<Task> tasks = taskRepository.findByProjectIdAndEventIdIsNull(projectId);
		return tasks.stream().map(entity -> {
			var dto = modelMapper.map(entity, TaskDTO.class);
			return dto;
		}).collect(Collectors.toList());
	}

	@Override
	public Page<TaskDTO> getTasksOfProject(Pageable pageable, UUID projectId, @Nullable String search,
			String getterId) {
		if (!projectMemberService.checkAddedMemberInProject(getterId, projectId)) {
			throw new ApiException(ErrorCode.NOT_A_MEMBER_OF_PROJECT);
		}

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
		return taskRepository.findAll(specs, pageable).map(this::convertToDto);
	}

	@Override
    @Transactional
	public TaskDTO updateTask(UUID taskId, UpdateTaskForm taskForm, String updateBy) {
		List<TaskLogDetail> taskLogDetails = new ArrayList<>();
		Task task = taskRepository.findById(taskId).orElseThrow(
				() -> new ApiException(ErrorCode.TASK_NOT_EXIST));

		Date updatedTime = new Date();
		boolean isPushNoti = false;

		var dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

		TaskLog taskLog = TaskLog.builder().taskId(taskId).creatorId(updateBy).createdAt(updatedTime)
				.comment(taskForm.getNote()).build();

		if (taskForm.getName() != null && !taskForm.getName().isEmpty() && !taskForm.getName().equals(task.getName())) {
			taskLogDetails.add(TaskLogDetail.builder().event("update").field("name").oldValue(task.getName())
					.newValue(taskForm.getName()).build());
			task.setName(taskForm.getName());
		}

		if (taskForm.getDescription() != null && !taskForm.getDescription().isEmpty()
				&& !taskForm.getDescription().equals(task.getDescription())) {
			taskLogDetails.add(TaskLogDetail.builder().event(task.getDescription() == null ? "set" : "update")
					.field("description").oldValue(task.getDescription()).newValue(taskForm.getDescription()).build());
			task.setDescription(taskForm.getDescription());
		}

		if (taskForm.getFromDate() != null && !taskForm.getFromDate().equals(task.getFromDate())) {
			taskLogDetails
					.add(TaskLogDetail.builder().event(task.getFromDate() == null ? "set" : "update").field("fromDate")
							.oldValue(task.getFromDate() != null ? dateFormatter.format(task.getFromDate()) : "")
							.newValue(dateFormatter.format(taskForm.getFromDate())).build());
			task.setFromDate(taskForm.getFromDate());
		}

		if (taskForm.getDueDate() != null && !taskForm.getDueDate().equals(task.getDueDate())) {
			taskLogDetails.add(TaskLogDetail.builder().event(task.getDueDate() == null ? "set" : "update")
					.field("dueDate").oldValue(task.getDueDate() != null ? dateFormatter.format(task.getDueDate()) : "")
					.newValue(dateFormatter.format(taskForm.getDueDate())).build());
			task.setDueDate(taskForm.getDueDate());
		}

		if (taskForm.getAttachmentPaths() != null && !taskForm.getAttachmentPaths().isEmpty()) {
			taskLogDetails.add(TaskLogDetail.builder().event("attachment").field("attachmentPaths").oldValue("")
					.newValue(taskForm.getAttachmentPaths()).build());
		}

		if (taskForm.getStatusId() != null && !taskForm.getStatusId().isEmpty()
				&& !taskForm.getStatusId().equals(task.getStatusId())) {
			taskLogDetails.add(TaskLogDetail.builder().event("update").field("statusId").oldValue(task.getStatusId())
					.newValue(taskForm.getStatusId()).build());
			task.setStatusId(taskForm.getStatusId());
		}

		if (taskForm.getPriorityId() != null && !taskForm.getPriorityId().isEmpty()
				&& !taskForm.getPriorityId().equals(task.getPriorityId())) {
			taskLogDetails.add(TaskLogDetail.builder().event(task.getPriorityId() == null ? "set" : "update")
					.field("priorityId").oldValue(task.getPriorityId()).newValue(taskForm.getPriorityId()).build());
			task.setPriorityId(taskForm.getPriorityId());
		}

		if (taskForm.getCategoryId() != null && !taskForm.getCategoryId().isEmpty()
				&& !taskForm.getCategoryId().equals(task.getCategoryId())) {
			taskLogDetails.add(TaskLogDetail.builder().event(taskForm.getCategoryId() == null ? "set" : "update")
					.field("categoryId").oldValue(task.getCategoryId()).newValue(taskForm.getCategoryId()).build());
			task.setCategoryId(taskForm.getCategoryId());
		}

		if (taskForm.getEstimatedTime() != null && !taskForm.getEstimatedTime().equals(task.getEstimatedTime())) {
			taskLogDetails.add(TaskLogDetail.builder().event(task.getEstimatedTime() == null ? "set" : "update")
					.field("estimatedTime").oldValue(task.getEstimatedTime() == null ? "" : task.getEstimatedTime().toString())
					.newValue(taskForm.getEstimatedTime().toString()).build());
			task.setEstimatedTime(taskForm.getEstimatedTime());
		}

		if (taskForm.getProgress() != null && !taskForm.getProgress().equals(task.getProgress())) {
			taskLogDetails.add(TaskLogDetail.builder().event("update").field("progress")
					.oldValue(task.getProgress().toString()).newValue(taskForm.getProgress().toString()).build());
			task.setProgress(taskForm.getProgress());
		}

		if (taskForm.getAssigneeId() != null && !taskForm.getAssigneeId().isEmpty()
				&& !taskForm.getAssigneeId().equals(task.getAssigneeId())) {
			// TODO: check if assignee is a member of project
			taskLogDetails.add(TaskLogDetail.builder().event(task.getAssigneeId() == null ? "set" : "update")
					.field("assigneeId").oldValue(task.getAssigneeId()).newValue(taskForm.getAssigneeId()).build());
			task.setAssigneeId(taskForm.getAssigneeId());
			isPushNoti = true;
		}

		if ((taskForm.getEventId() != null && !taskForm.getEventId().equals(task.getEventId()))
				|| (taskForm.getEventId() == null && task.getEventId() != null)) {
			taskLogDetails.add(TaskLogDetail.builder().event(task.getEventId() == null ? "set" : "update")
					.field("eventId").oldValue(task.getEventId() == null ? "" : task.getEventId().toString())
					.newValue(taskForm.getEventId() == null ? "" : taskForm.getEventId().toString()).build());
			task.setEventId(taskForm.getEventId());
		}

		taskLog.setDetails(taskLogDetails);

		if (!taskLogDetails.isEmpty() || taskLog.getComment() != null) {
			taskLogService.addLog(taskLog);
			task.setLastUpdatedStamp(updatedTime);
			var savedTask = taskRepository.save(task);

			if (isPushNoti) {
				sendNotification(userService.findById(updateBy), userService.findById(taskForm.getAssigneeId()),
						savedTask);
			}

			return convertToDto(taskRepository.save(task));
		} else {
			return null;
		}
	}

	private List<TaskHierarchyDTO> getTaskHierarchyByRoot(UUID ancestorId) {
		var ancestor = taskRepository.findById(ancestorId).orElse(null);
		if (ancestor == null) {
			return List.of();
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
				q = q.replaceAll(Task_.CREATED_DATE + "(" + Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET)
						.replace("[", "\\[").replace("]", "\\]") + ")[^ ]+", "");
			}

			if (q.contains(Task_.DUE_DATE)) {
				// remove due_date field
				q = q.replaceAll(Task_.DUE_DATE + "(" + Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET)
						.replace("[", "\\[").replace("]", "\\]") + ")[^ ]+", "");
			}

			if (q.contains(Task_.FROM_DATE)) {
				// remove progress field
				q = q.replaceAll(Task_.PROGRESS + "(" + Joiner.on("|").join(SearchOperation.SIMPLE_OPERATION_SET)
						.replace("[", "\\[").replace("]", "\\]") + ")[^ ]+", "");
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
						(Root<Task> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> criteriaBuilder
								.between(root.get(Task_.FROM_DATE), fromDate, toDate))
						.or((Root<Task> root, CriteriaQuery<?> query,
								CriteriaBuilder criteriaBuilder) -> criteriaBuilder.and(
										criteriaBuilder.isNull(root.get(Task_.FROM_DATE)),
										criteriaBuilder.between(root.get(Task_.CREATED_STAMP), fromDate, toDate)))
						.or((Root<Task> root, CriteriaQuery<?> query,
								CriteriaBuilder criteriaBuilder) -> criteriaBuilder.between(root.get(Task_.DUE_DATE),
										fromDate, toDate)));
		return taskRepository.findAll(specs, Sort.by(Task_.CREATED_STAMP).ascending()).stream()
				.map(task -> modelMapper.map(task, TaskGanttDTO.class)).toList();
	}

	public Page<TaskDTO> getTasksCreatedByUser(Pageable pageable, String creator, @Nullable String search) {
		// replace "creatorId:value" if exist in search by "creatorId:`creator`"
		if (search != null && !search.equals("")) {
			if (search.contains(Task_.CREATOR_ID))
				search = search.replaceAll(Task_.CREATOR_ID + ":[^ ]+", Task_.CREATOR_ID + ":" + creator);
			else
				search = "( " + search + " ) AND " + Task_.CREATOR_ID + ":" + creator;
		} else {
			search = Task_.CREATOR_ID + ":" + creator;
		}

		// default sort by due date
		if (pageable.getSort().isUnsorted()) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
					Sort.by(Task_.DUE_DATE).ascending());
		}

		GenericSpecificationsBuilder<Task> builder = new GenericSpecificationsBuilder<>();
		var specs = builder.build(new CriteriaParser().parse(search), TaskSpecification::new);
		return taskRepository.findAll(specs, pageable).map(entity -> {
			var dto = modelMapper.map(entity, TaskDTO.class);
			dto.setProject(modelMapper.map(entity.getProject(), ProjectDTO.class));
			return dto;
		});
	}

	private TaskDTO convertToDto(Task task) {
		return modelMapper.map(task, TaskDTO.class);
	}

	private void sendNotification(User from, User to, Task task) {
		try {
			if (to == null || (from != null && from.getId().equals(to.getId()))) {
				return;
			}

			String subject = "Bạn được phân công nhiệm vụ mới: " + task.getName();

			notiService.createInAppNotification(Optional.of(from).map(f -> f.getId()).orElse(null), to.getId(), subject,
					"/project/" + task.getProjectId() + "/task/" + task.getId());

			if (to.getEmail() == null || to.getEmail().isEmpty()) {
				return;
			}

			Map<String, Object> model = new HashMap<>();
			model.put("assigneeName", to.getFirstName() == null && to.getLastName() == null ? to.getId()
					: to.getFirstName() + " " + to.getLastName());
			model.put("taskName", task.getName());
			model.put("assignedBy", from.getFirstName() == null && from.getLastName() == null ? from.getId()
					: from.getFirstName() + " " + from.getLastName());
			model.put("dueDate", task.getDueDate() == null ? "Không xác định"
					: new SimpleDateFormat("dd-MM-yyyy HH:mm").format(task.getDueDate()));
			model.put("link", clientEndpoint + "/project/" + task.getProjectId() + "/task/" + task.getId());
			notiService.createMailNotification(from.getEmail(), to.getEmail(), subject, "new-assign", model);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
