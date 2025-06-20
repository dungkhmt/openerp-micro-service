package com.hust.openerp.taskmanagement.model;

import com.hust.openerp.taskmanagement.exception.ApiException;
import com.hust.openerp.taskmanagement.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrgInvitationStatusEnum {
	ACCEPTED("ORG_ACCEPTED"),
	EXPIRED("ORG_EXPIRED"),
	PENDING("ORG_PENDING"),
	DECLINED("ORG_DECLINED");

	private final String statusId;
}
