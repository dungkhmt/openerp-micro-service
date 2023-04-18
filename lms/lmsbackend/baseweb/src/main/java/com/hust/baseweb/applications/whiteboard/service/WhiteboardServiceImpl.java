package com.hust.baseweb.applications.whiteboard.service;

import com.hust.baseweb.applications.education.classmanagement.entity.EduClassSession;
import com.hust.baseweb.applications.education.classmanagement.repo.EduClassSessionRepo;
import com.hust.baseweb.applications.whiteboard.entity.UserWhiteboard;
import com.hust.baseweb.applications.whiteboard.entity.Whiteboard;
import com.hust.baseweb.applications.whiteboard.model.*;
import com.hust.baseweb.applications.whiteboard.repo.UserWhiteboardRepo;
import com.hust.baseweb.applications.whiteboard.repo.WhiteboardRepo;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.repo.UserLoginRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Log4j2
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WhiteboardServiceImpl implements  WhiteboardService {
    private WhiteboardRepo whiteboardRepo;
    private EduClassSessionRepo eduClassSessionRepo;
    private UserWhiteboardRepo userWhiteboardRepo;
    private UserLoginRepo userLoginRepo;

    @Override
    public void createWhiteboard(String userId, String whiteboardId, String name, UUID classSessionId) {
        Whiteboard whiteboard = new Whiteboard();
        EduClassSession eduClassSession = eduClassSessionRepo.findBySessionId(classSessionId);
        whiteboard.setId(whiteboardId);
        whiteboard.setName(name);
        whiteboard.setEduClassSession(eduClassSession);
        whiteboard.setTotalPage(1);
        whiteboard.setCreatedBy(userId);
        whiteboard.setCreatedDate(new Date());
        whiteboardRepo.save(whiteboard);
    }

    @Override
    public List<GetListWhiteboardModel> getWhiteboards(UUID sessionId) {
        EduClassSession eduClassSession = eduClassSessionRepo.findBySessionId(sessionId);
        List<Whiteboard> whiteboardList = whiteboardRepo.findAllByEduClassSession(eduClassSession);
        List<GetListWhiteboardModel> getListWhiteboardModels = new ArrayList<>();
        for (Whiteboard whiteboard: whiteboardList) {
            if (whiteboard != null) {
                GetListWhiteboardModel getListWhiteboardModel = new GetListWhiteboardModel();
                getListWhiteboardModel.setId(whiteboard.getId());
                getListWhiteboardModel.setName(whiteboard.getName());
                getListWhiteboardModel.setTotalPage(whiteboard.getTotalPage());
                getListWhiteboardModel.setCreatedBy(whiteboard.getCreatedBy());
                getListWhiteboardModel.setCreatedDate(whiteboard.getCreatedDate());

                getListWhiteboardModels.add(getListWhiteboardModel);
            }
        }

        return getListWhiteboardModels;
    }

    @Override
    public void saveWhiteboardData(SaveWhiteboardDataModel input, String userId) {
        Whiteboard whiteboard = whiteboardRepo.findWhiteboardById(input.getWhiteboardId());
        whiteboard.setLastModifiedDate(new Date());
        whiteboard.setLastModifiedBy(userId);
        whiteboard.setData(input.getData());
        whiteboard.setTotalPage(input.getTotalPage());

        whiteboardRepo.save(whiteboard);
    }

    @Override
    public WhiteboardDetailModel getWhiteboardDetail(String id) {
        WhiteboardDetailModel whiteboardDetailModel = new WhiteboardDetailModel();
        Whiteboard whiteboard = whiteboardRepo.findWhiteboardById(id);
        whiteboardDetailModel.setId(id);
        if (whiteboard == null) {
            return null;
        }
        whiteboardDetailModel.setName(whiteboard.getName());
        whiteboardDetailModel.setTotalPage(whiteboard.getTotalPage());
        whiteboardDetailModel.setData((whiteboard.getData()));

        return whiteboardDetailModel;
    }

    @Override
    public AddUserToWhiteboardResultModel addUserToWhiteboard(String whiteboardId, UserLogin userLogin, AddUserToWhiteboardModel input) {
        AddUserToWhiteboardResultModel addUserToWhiteboardResultModel = new AddUserToWhiteboardResultModel();
        UserWhiteboard userWhiteboard = new UserWhiteboard();
        List<UserWhiteboard> userWhiteboardList = userWhiteboardRepo.findAllByUserLogin(userLogin);
        boolean found = false;
        for(UserWhiteboard x : userWhiteboardList){
            if(x.getWhiteboard().getId().equals(whiteboardId)){
                if (x.getWhiteboard().getCreatedBy().equals(userLogin.getUserLoginId())) {
                    addUserToWhiteboardResultModel.setIsCreatedUser(true);
                } else {
                    addUserToWhiteboardResultModel.setIsCreatedUser(false);
                }
                found = true;
                addUserToWhiteboardResultModel.setRoleId(x.getRoleId());
                addUserToWhiteboardResultModel.setStatusId(x.getStatusId());
                break;
            }
        }

        if (userWhiteboardList.size() == 0 || !found) {
            UserLogin userLoginTest = userLoginRepo.findByUserLoginId(userLogin.getUserLoginId());
            Whiteboard whiteboard = whiteboardRepo.findWhiteboardById(whiteboardId);
            userWhiteboard.setUserLogin(userLoginTest);
            userWhiteboard.setWhiteboard(whiteboard);
            userWhiteboard.setRoleId(input.getRoleId());
            userWhiteboard.setStatusId(input.getStatusId());

            userWhiteboardRepo.save(userWhiteboard);
            addUserToWhiteboardResultModel.setRoleId(input.getRoleId());
            addUserToWhiteboardResultModel.setStatusId(input.getStatusId());
        }

        addUserToWhiteboardResultModel.setUserId(userLogin.getUserLoginId());

        return addUserToWhiteboardResultModel;
    }

    @Override
    public GetUserWhiteboardModel getUserWhiteboard(String whiteboardId, UserLogin userLogin) {
        UserWhiteboard userWhiteboard = userWhiteboardRepo.findByWhiteboardIdAndUserLogin(whiteboardId, userLogin);
        GetUserWhiteboardModel getUserWhiteboardModel = new GetUserWhiteboardModel();

        getUserWhiteboardModel.setRoleId(userWhiteboard.getRoleId());
        getUserWhiteboardModel.setStatusId(userWhiteboard.getStatusId());

        if (userWhiteboard == null) {
            return null;
        }

        return getUserWhiteboardModel;
    }

    @Override
    public ChangeRoleStatusModel changeRoleStatusUserWhiteboard(
        String whiteboardId,
        ChangeRoleStatusModel input
    ) {
        UserLogin userLogin = userLoginRepo.findByUserLoginId(input.getUserId());
        UserWhiteboard userWhiteboard = userWhiteboardRepo.findByWhiteboardIdAndUserLogin(whiteboardId, userLogin);

        userWhiteboard.setRoleId(input.getRoleId());
        userWhiteboard.setStatusId(input.getStatusId());

        userWhiteboardRepo.save(userWhiteboard);

        log.info("input = " + userLogin.getUserLoginId());

        return input;
    }

    @Override
    public ListDrawRequestPendingModel getListDrawRequestPending(String whiteboardId, UserLogin userLogin) {
        ListDrawRequestPendingModel listDrawRequestPendingModel = new ListDrawRequestPendingModel();
        Whiteboard whiteboard = whiteboardRepo.findWhiteboardById(whiteboardId);
        List<UserWhiteboard> userWhiteboardList = userWhiteboardRepo.findAllByWhiteboard(whiteboard);
        List<AddUserToWhiteboardResultModel> addUserToWhiteboardResultModelList = new ArrayList<>();

        for(UserWhiteboard userWhiteboard : userWhiteboardList){
            AddUserToWhiteboardResultModel addUserToWhiteboardResultModel = new AddUserToWhiteboardResultModel();
            log.info("x: whiteboardId = " + userWhiteboard.getWhiteboard().getId());
            if(userWhiteboard.getWhiteboard().getId().equals(whiteboardId) && userWhiteboard.getStatusId().equals("pending")){
                addUserToWhiteboardResultModel.setIsCreatedUser(false);
                addUserToWhiteboardResultModel.setRoleId(userWhiteboard.getRoleId());
                addUserToWhiteboardResultModel.setStatusId(userWhiteboard.getStatusId());
                addUserToWhiteboardResultModel.setUserId(userWhiteboard.getUserLogin().getUserLoginId());

                addUserToWhiteboardResultModelList.add(addUserToWhiteboardResultModel);
            }
        }

        listDrawRequestPendingModel.setAddUserToWhiteboardResultModelList(addUserToWhiteboardResultModelList);

        return listDrawRequestPendingModel;
    }

    @Override
    public List<UsersInWhiteboardModel> getListUsersInWhiteboard(String whiteboardId) {
        List<UsersInWhiteboardModel> usersInWhiteboardModelList = new ArrayList<>();
        Whiteboard whiteboard = whiteboardRepo.findWhiteboardById(whiteboardId);
        List<UserWhiteboard> userWhiteboardList = userWhiteboardRepo.findAllByWhiteboard(whiteboard);

        for(UserWhiteboard userWhiteboard : userWhiteboardList){
            UsersInWhiteboardModel usersInWhiteboardModel = new UsersInWhiteboardModel();
            if(userWhiteboard.getWhiteboard().getId().equals(whiteboardId)){
                usersInWhiteboardModel.setRoleId(userWhiteboard.getRoleId());
                usersInWhiteboardModel.setStatusId(userWhiteboard.getStatusId());
                usersInWhiteboardModel.setUserId(userWhiteboard.getUserLogin().getUserLoginId());
                if (userWhiteboard.getUserLogin().getUserLoginId().equals(whiteboard.getCreatedBy())) {
                    usersInWhiteboardModel.setIsCreatedUser(true);
                } else {
                    usersInWhiteboardModel.setIsCreatedUser(false);
                }

                usersInWhiteboardModelList.add(usersInWhiteboardModel);
            }
        }
        return usersInWhiteboardModelList;
    }

    @Override
    public DeleteWhiteboardResultModel deleteWhiteboard(String userId, DeleteWhiteboardModel input) {
        DeleteWhiteboardResultModel deleteWhiteboardResultModel = new DeleteWhiteboardResultModel();

        Whiteboard whiteboard = whiteboardRepo.findWhiteboardById(input.getWhiteboardId());
        if (!whiteboard.getCreatedBy().equals(userId)) {
            deleteWhiteboardResultModel.setSuccess(false);
            deleteWhiteboardResultModel.setMessage("Bạn không phải người tạo bảng viết này.");
        } else {
            List<UserWhiteboard> userWhiteboardList = userWhiteboardRepo.findAllByWhiteboard(whiteboard);
            userWhiteboardRepo.deleteAll(userWhiteboardList);
            whiteboardRepo.delete(whiteboard);
            deleteWhiteboardResultModel.setSuccess(true);
            deleteWhiteboardResultModel.setMessage("Xoá bảng viết thàng công.");
        }


        return deleteWhiteboardResultModel;
    }
}
