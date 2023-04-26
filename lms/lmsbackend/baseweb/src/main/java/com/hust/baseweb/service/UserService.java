package com.hust.baseweb.service;

import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.ModelPageUserSearchResponse;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.model.UserLoginWithPersonModel;
import com.hust.baseweb.rest.user.DPerson;
import com.hust.baseweb.rest.user.UserRestBriefProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserLogin findById(String userLoginId);

    DPerson findByPartyId(String partyId);

//    Page<DPerson> findAllPerson(Pageable page, SortAndFiltersInput query);

    Page<UserRestBriefProjection> findPersonByFullName(Pageable page, String sString);

    Page<UserRestBriefProjection> findUsersByUserLoginId(Pageable page, String sString);

    List<UserLogin> getAllUserLogins();

//    UserLogin createAndSaveUserLogin(String userName, String password);
//
//    UserLogin updatePassword(UserLogin user, String password);
//
//    Party createAndSaveUserLogin(PersonModel personModel) throws Exception;
//    Party createAndSaveUserLoginNotYetActivated(PersonModel personModel);
//
//    Party update(PersonUpdateModel personUpdateModel, UUID partyId);
//
//    UserLogin findUserLoginByPartyId(UUID partyId);
//
//    SimpleResponse register(RegisterIM im);
//
//    GetAllRegistsOM getAllRegists();
//
//    SimpleResponse approve(ApproveRegistrationIM im);
//
//    SimpleResponse disableUserRegistration(DisableUserRegistrationIM im);
//
//    UserLogin updatePassword2(String userLoginId, String password);
//
//    List<UserLogin> getALlUserLoginsByGroupId(String groupId);
//
//    List<String> getGroupPermsByUserLoginId(String userLoginId);

    PersonModel findPersonByUserLoginId(String userLoginId);

    Page<UserLoginWithPersonModel> findAllUserLoginWithPersonModelBySecurityGroupId(
        Collection<String> securityGroupIds, String search, Pageable pageable
    );

    List<String> findAllUserLoginIdOfGroup(String groupId);

//    SimpleResponse approveCreateAccountActivationSendEmail(ApproveRegistrationIM im);
//
//    SimpleResponse activateAccount(UUID activationId);
//
//    SimpleResponse resetPassword(String userLoginId);
//
//    SimpleResponse assignGroup2AllUsers(ModelAssignGroupAllUsersInput I);

    List<String> getAllEnabledLoginIdsContains(String partOfLoginId, Integer limit);

    ModelPageUserSearchResponse searchUser(Pageable pageable, String keyword);

    void synchronizeUser(String userId, String email, String firstName, String lastName);

//    UserRegister.OutputModel registerUser(UserRegister.InputModel inputModel);
//
//    boolean approveRegisterUser(String userLoginId);
//
//    List<UserRegister.OutputModel> findAllRegisterUser();

}
