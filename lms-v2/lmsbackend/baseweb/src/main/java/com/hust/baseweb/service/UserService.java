package com.hust.baseweb.service;

import com.hust.baseweb.applications.programmingcontest.model.ModelSearchUserResult;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.model.ModelPageUserSearchResponse;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.model.UserLoginWithPersonModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserLogin findById(String userLoginId);

    String getUserFullName(String userId);

    Page<ModelSearchUserResult> search(String keyword, List<String> excludeIds, Pageable page);

    List<UserLogin> getAllUserLogins();

    PersonModel findPersonByUserLoginId(String userLoginId);

    Page<UserLoginWithPersonModel> findAllUserLoginWithPersonModelBySecurityGroupId(
        Collection<String> securityGroupIds, String search, Pageable pageable
    );

    List<String> findAllUserLoginIdOfGroup(String groupId);

    List<String> getAllEnabledLoginIdsContains(String partOfLoginId, Integer limit);

    ModelPageUserSearchResponse searchUser(Pageable pageable, String keyword);

    void synchronizeUser(String userId, String email, String firstName, String lastName);

}
