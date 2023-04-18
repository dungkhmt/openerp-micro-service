package com.hust.baseweb.service;

import com.hust.baseweb.entity.Application;
import com.hust.baseweb.entity.SecurityPermission;
import com.hust.baseweb.repo.ApplicationRepo;
import com.hust.baseweb.repo.ApplicationTypeRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
public class ApplicationServiceImpl implements ApplicationService {

    private ApplicationRepo applicationRepo;
    private ApplicationTypeRepo applicationTypeRepo;

    @Override
    public List<Application> getListByPermissionAndType(List<SecurityPermission> permissionList, String type) {
        String permissionStr = "";
        for (SecurityPermission sp : permissionList) {
            permissionStr += sp.getPermissionId() + ",";
        }
        //log.info("getListByPermissionAndType, permissionList = " + permissionStr + ", type = " + type);

        List<Application> applicationList = applicationRepo.findByTypeAndPermissionIn(
            applicationTypeRepo.getOne(type),
            permissionList);
        /*
        log.info("getListByPermissionAndType, permissionList = " +
                 permissionStr +
                 ", type = " +
                 type +
                 ", applicationList.sz = " +
                 applicationList.size());
        */

        List<Application> applicationList1 = applicationList
            .stream()
            .map(Application::getModule)
            .collect(Collectors.toList());
        /*
        log.info("getListByPermissionAndType, permissionList = " +
                 permissionStr +
                 ", type = " +
                 type +
                 ", applicationList.sz = " +
                 applicationList.size() +
                 ", applicationList1.sz = " +
                 applicationList1.size());
*/

        for (Application a : applicationList1) {
        }
        //List<Application> applicationList2 = applicationList1.stream().map(Application::getModule).collect(Collectors.toList());
        List<Application> applicationList2 = new ArrayList<Application>();
        for (Application a : applicationList1) {
            if (a != null) {
                if (a.getModule() != null) {
                    applicationList2.add(a.getModule());
                }
            }
        }
/*
        log.info("getListByPermissionAndType, permissionList = " +
                 permissionStr +
                 ", type = " +
                 type +
                 ", applicationList.sz = " +
                 applicationList.size() +
                 ", applicationList2.sz = " +
                 applicationList2.size());
*/
        applicationList.addAll(applicationList1);
        applicationList.addAll(applicationList2);
        applicationList.removeIf(Objects::isNull);
        return applicationList;
    }

    @Override
    public Application getById(String applicationId) {
        Optional<Application> app = applicationRepo.findById(applicationId);
        if (app.isPresent()) {
            return app.get();
        }
        return null;
    }

    @Override
    public List<String> getScrSecurInfo(String userId) {
        List<String> permissions = applicationRepo.getScrSecurInfo(userId);

        if (null == permissions) {
            return new ArrayList<>();
        }

        return permissions;
    }
}
