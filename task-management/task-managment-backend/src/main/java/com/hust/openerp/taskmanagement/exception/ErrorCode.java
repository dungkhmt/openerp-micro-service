package com.hust.openerp.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	UNKNOWN_SERVER_ERROR("E0000", "Unknown server error", HttpStatus.INTERNAL_SERVER_ERROR),

	// 400

	/* Common */
	INVALID_REQUEST("E0001", "Request is invalid", HttpStatus.BAD_REQUEST),
	INVALID_REQUEST_BODY_PARAMETER("E0004", "Request body parameter is invalid", HttpStatus.BAD_REQUEST),
	MISSING_REQUEST_PART("E0005", "Request part is missing", HttpStatus.BAD_REQUEST),
	MISSING_REQUEST_PARAMETER("E0006", "Request parameter is missing", HttpStatus.BAD_REQUEST),
	INVALID_REQUEST_PARAMETER_TYPE("E0007", "Request parameter type is invalid", HttpStatus.BAD_REQUEST),
	INVALID_REQUEST_PARAMETER_VALUE("E0008", "Request parameter value is invalid", HttpStatus.BAD_REQUEST),
	INVALID_JSON("E0009", "Invalid JSON", HttpStatus.BAD_REQUEST),

	/* User */
	PROJECT_NOT_EXIST("E1000", "The project does not exist", HttpStatus.BAD_REQUEST),
	USER_NOT_EXIST("E1002", "The user does not exist", HttpStatus.BAD_REQUEST),
	TASK_NOT_EXIST("E1003", "The task does not exist", HttpStatus.BAD_REQUEST),
	FILE_NOT_EXIST("E1004", "The file does not exist", HttpStatus.BAD_REQUEST),
	INVALID_STATUS_TRANSITION("E1005", "Invalid meeting plan status transition", HttpStatus.BAD_REQUEST),

	// 401

	UNAUTHORIZED("E0100", "Unauthorized", HttpStatus.UNAUTHORIZED),
	INVALID_CREDENTIALS("E0101", "Invalid credentials", HttpStatus.UNAUTHORIZED),

	// 403

	ACCESS_DENIED("E0200", "Access denied", HttpStatus.FORBIDDEN),
	NOT_A_MEMBER_OF_PROJECT("E0201",
			"You are not a member of this project, please contact the administrator to be added to the project",
			HttpStatus.FORBIDDEN),
	INSUFFICIENT_PERMISSIONS("E0202", "You do not have sufficient permissions to perform this action",
			HttpStatus.FORBIDDEN),
	LAST_OWNER_CANNOT_LEAVE("E0203", "You are the last owner, please assign a new owner before leaving the project",
			HttpStatus.FORBIDDEN),
	NOT_A_MEMBER_OF_MEETING_PLAN("E0204",
			"You are not a member of this meeting, please contact the administrator to be added to the meeting",
			HttpStatus.FORBIDDEN),
	REGISTRATION_CLOSED("E0205", "Registration deadline has passed. You can no longer update sessions",
			HttpStatus.FORBIDDEN),

	// 404

	NO_HANDLER_FOUND("E0300", "No handler found", HttpStatus.NOT_FOUND),
	ITEM_NOT_FOUND("E0301", "Item not found", HttpStatus.NOT_FOUND),
	PROJECT_NOT_FOUND("E0302", "Project not found", HttpStatus.NOT_FOUND),
	TASK_NOT_FOUND("E0303", "Task not found", HttpStatus.NOT_FOUND),
	EVENT_NOT_FOUND("E0304", "Event not found", HttpStatus.NOT_FOUND),
	MEETING_PLAN_NOT_FOUND("E0305", "Meeting plan not found", HttpStatus.NOT_FOUND),
	MEETING_PLAN_USER_NOT_FOUND("E0306", "The meeting plan member not found", HttpStatus.NOT_FOUND),
	STATUS_NOT_FOUND("E0307", "The status not found", HttpStatus.NOT_FOUND),
	ROLE_NOT_FOUND("E0308", "The role not found", HttpStatus.NOT_FOUND),
	SKILL_NOT_FOUND("E0309", "The skill not found", HttpStatus.NOT_FOUND),

	// 405

	METHOD_NOT_ALLOWED("E0400", "Method not allowed", HttpStatus.METHOD_NOT_ALLOWED),

	// 409

	PROJECT_CODE_EXIST("E0501", "The project code already exists", HttpStatus.CONFLICT),
	SKILL_EXIST("E0502", "The skill already exists. Please check the skill list before adding a new one",
			HttpStatus.CONFLICT),
	PRIORITY_EXIST("E0503", "The priority already exists. Please check the priority list before adding a new one",
			HttpStatus.CONFLICT),
	STATUS_EXIST("E0504", "The status already exists. Please check the status list before adding a new one",
			HttpStatus.CONFLICT),
	CATEGORY_EXIST("E0505", "The category already exists. Please check the category list before adding a new one",
			HttpStatus.CONFLICT),

	// 415

	UNSUPPORTED_MEDIA_TYPE("E0500", "Unsupported media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE);

	private final String code;
	private final String message;
	private final HttpStatus status;

	ErrorCode(String code, String message, HttpStatus status) {
		this.code = code;
		this.message = message;
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
