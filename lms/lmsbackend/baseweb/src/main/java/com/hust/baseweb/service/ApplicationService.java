package com.hust.baseweb.service;

import com.hust.baseweb.entity.Application;
import com.hust.baseweb.entity.SecurityPermission;

import java.util.List;

public interface ApplicationService {

    List<Application> getListByPermissionAndType(List<SecurityPermission> permissionList, String type);

    Application getById(String applicationId);

    List<String> getScrSecurInfo(String userId);
}
