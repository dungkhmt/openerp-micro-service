package com.hust.baseweb.service;

import com.hust.baseweb.entity.SecurityGroup;
import com.hust.baseweb.model.GetAllRolesOM;

import java.util.List;
import java.util.Set;

public interface SecurityGroupService {

    List<SecurityGroup> findAll();

    Set<GetAllRolesOM> getRoles();
}
