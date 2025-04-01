package com.hust.baseweb.service;

import com.hust.baseweb.applications.programmingcontest.model.ModelSearchUserResult;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.entity.UserRegister;
import com.hust.baseweb.model.ModelPageUserSearchResponse;
import com.hust.baseweb.model.PersonModel;
import com.hust.baseweb.model.UserLoginWithPersonModel;
import com.hust.baseweb.repo.UserLoginRepo;
import com.hust.baseweb.repo.UserRegisterRepo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Slf4j
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional
@javax.transaction.Transactional
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserLoginRepo userLoginRepo;

    UserRegisterRepo userRegisterRepo;


    @Override
    public UserLogin findById(String userLoginId) {
        return userLoginRepo.findByUserLoginId(userLoginId);
    }

    private String getUserFullName(ModelSearchUserResult user) {
        if (user == null) {
            return "";
        }
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return firstName + " " + lastName;
    }

    @Override
    public String getUserFullName(String userId) {
        ModelSearchUserResult user = userLoginRepo.getUserGeneralInfo(userId);
        return getUserFullName(user);
    }

    public List<UserLogin> getAllUserLogins() {
        return userLoginRepo.findAll();
    }

    @Override
    public Page<ModelSearchUserResult> search(String keyword, List<String> excludeIds, Pageable page) {
        if (excludeIds.isEmpty()) {
            excludeIds = null;
        }
        return userLoginRepo.search(keyword, excludeIds, page);
    }


    @Override
    public PersonModel findPersonByUserLoginId(String userLoginId) {
        /*
        updated: 2021-10-26 (by PQD)
        use Caching
         */
        //if(mId2Person == null){
        //    mId2Person = new HashMap();
        //}
        //if(mId2Person.get(userLoginId) == null) {
        UserLogin userLogin = userLoginRepo.findByUserLoginId(userLoginId);
        UserRegister userRegister = userRegisterRepo.findById(userLoginId).orElse(null);
        String affiliations = "";
        if (userRegister != null) {
            affiliations = userRegister.getAffiliations();
        }
        //log.info("findPersonByUserLoginId, affiliations of " + userLoginId + " = " + affiliations);
        //Person person = personRepo.findByPartyId(userLogin.getParty().getPartyId());
        //if (person == null) {
        //    log.info("findPersonByUserLoginId, person of " + userLoginId + " not exists");
        //    return new PersonModel();
        //}
        //log.info("findPersonByUserLoginId, found person {}", person);
        PersonModel personModel = new PersonModel();
        personModel.setUserName(userLoginId);
        if (userRegister != null) {
            personModel.setEmail(userRegister.getEmail());
        }
        //personModel.setGender(person.getGender());
        //personModel.setBirthDate(person.getBirthDate());
        //personModel.setFirstName(person.getFirstName());
        //personModel.setMiddleName(person.getMiddleName());
        //personModel.setLastName(person.getLastName());
        //personModel.setAffiliations(affiliations);
        personModel.setLastName(userLogin.getLastName());
        personModel.setFirstName(userLogin.getFirstName());

        return personModel;
        //mId2Person.put(userLoginId, personModel);
        //}
        //return mId2Person.get(userLoginId);

        /*
            UserLogin userLogin = userLoginRepo.findByUserLoginId(userLoginId);
            Person person = personRepo.findByPartyId(userLogin.getParty().getPartyId());
            if (person == null) {
                log.info("findPersonByUserLoginId, person of " + userLoginId + " not exists");
                return new PersonModel();
            }

            PersonModel personModel = new PersonModel();
            personModel.setFirstName(person.getFirstName());
            personModel.setMiddleName(person.getMiddleName());
            personModel.setLastName(person.getLastName());


        }
        return personModel;
         */
    }

    @Override
    public Page<UserLoginWithPersonModel> findAllUserLoginWithPersonModelBySecurityGroupId(
        Collection<String> securityGroupIds, String search, Pageable pageable
    ) {
        return userLoginRepo.findUserLoginWithPersonModelBySecurityGroupId(securityGroupIds, search, pageable);
    }

    @Override
    public List<String> findAllUserLoginIdOfGroup(String groupId) {
        return userLoginRepo.findAllUserLoginOfGroup(groupId);
    }

    @Override
    public List<String> getAllEnabledLoginIdsContains(String partOfLoginId, Integer limit) {
        return userLoginRepo.findByEnabledLoginIdContains(partOfLoginId, limit);
    }

    @Override
    public ModelPageUserSearchResponse searchUser(Pageable pageable, String keyword) {
        Page<PersonModel> list = userLoginRepo.searchUser(pageable, keyword);
        return ModelPageUserSearchResponse.builder().contents(list).build();
    }

    @Override
    public void synchronizeUser(String userId, String email, String firstName, String lastName) {
        UserLogin ul = userLoginRepo.findByUserLoginId(userId);
        if (ul == null) {
            userLoginRepo.save(UserLogin.builder()
                                        .userLoginId(userId)
                                        .email(email)
                                        .firstName(firstName)
                                        .lastName(lastName)
                                        .enabled(true)
                                        .build());
        } else if (StringUtils.compareIgnoreCase(email, ul.getEmail()) != 0 ||
                   StringUtils.compareIgnoreCase(firstName, ul.getFirstName()) != 0 ||
                   StringUtils.compareIgnoreCase(lastName, ul.getLastName()) != 0) {

            ul.setEmail(email);
            ul.setFirstName(firstName);
            ul.setLastName(lastName);

            userLoginRepo.save(ul);
        }
    }
}
