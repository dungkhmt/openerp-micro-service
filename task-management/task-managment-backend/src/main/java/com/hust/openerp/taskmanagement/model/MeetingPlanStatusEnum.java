package com.hust.openerp.taskmanagement.model;

import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeetingPlanStatusEnum {
	PLAN_DRAFT("PLAN_DRAFT"), 
	PLAN_REG_OPEN("PLAN_REG_OPEN"), 
	PLAN_REG_CLOSED("PLAN_REG_CLOSED"),
	PLAN_ASSIGNED("PLAN_ASSIGNED"), 
	PLAN_IN_PROGRESS("PLAN_IN_PROGRESS"), 
	PLAN_COMPLETED("PLAN_COMPLETED"),
	PLAN_CANCELED("PLAN_CANCELED");

	private final String statusId;

	// Find Enum from statusId
	public static MeetingPlanStatusEnum fromStatusId(String statusId) {
		for (MeetingPlanStatusEnum status : values()) {
			if (status.getStatusId().equals(statusId)) {
				return status;
			}
		}
		throw new ApiException(ErrorCode.STATUS_NOT_FOUND);
	}
}
