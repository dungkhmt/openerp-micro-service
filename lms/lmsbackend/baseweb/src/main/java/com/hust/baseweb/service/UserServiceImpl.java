package com.hust.baseweb.service;

import com.hust.baseweb.applications.education.exception.SimpleResponse;
import com.hust.baseweb.applications.mail.service.MailService;
import com.hust.baseweb.applications.notifications.service.NotificationsService;
import com.hust.baseweb.entity.*;
import com.hust.baseweb.entity.PartyType.PartyTypeEnum;
import com.hust.baseweb.entity.Status.StatusEnum;
import com.hust.baseweb.model.*;
import com.hust.baseweb.model.getregists.GetAllRegistsOM;
import com.hust.baseweb.model.getregists.RegistsOM;
import com.hust.baseweb.model.querydsl.SearchCriteria;
import com.hust.baseweb.model.querydsl.SortAndFiltersInput;
import com.hust.baseweb.repo.*;
import com.hust.baseweb.rest.user.DPerson;
import com.hust.baseweb.rest.user.PredicateBuilder;
import com.hust.baseweb.rest.user.UserRestBriefProjection;
import com.hust.baseweb.rest.user.UserRestRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.ResourceUtils;

import javax.persistence.EntityExistsException;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Log4j2
@Transactional
@javax.transaction.Transactional
public class UserServiceImpl implements UserService {

    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static final String module = UserService.class.getName();

    private AccountActivationRepo accountActivationRepo;

    private final UserLoginRepo userLoginRepo;

    private final UserRestRepository userRestRepository;

    private final PartyService partyService;

    private final PartyTypeRepo partyTypeRepo;

    private final PartyRepo partyRepo;

    private final StatusRepo statusRepo;

    private final PersonRepo personRepo;

    private final SecurityGroupRepo securityGroupRepo;

    private final UserRegisterRepo userRegisterRepo;

    private final StatusItemRepo statusItemRepo;

    private final JavaMailSender javaMailSender;

    private final MailService mailService;

    private final SecurityGroupService groupService;

    private final static ExecutorService EMAIL_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

    private final NotificationsService notificationsService;

    private final Configuration config;

    public static Map<String, PersonModel> mId2Person = null;


    @Override
    public UserLogin findById(String userLoginId) {
        return userLoginRepo.findByUserLoginId(userLoginId);
    }

    public List<UserLogin> getAllUserLogins() {
        return userLoginRepo.findAll();
    }

    @Override
    @Transactional
    public UserLogin createAndSaveUserLogin(String userName, String password) {

        Party party = partyService.save("PERSON");
        UserLogin userLogin = new UserLogin(userName, password, null, true);
        userLogin.setParty(party);
        if (userLoginRepo.existsById(userName)) {
//            System.out.println(module + "::save, userName " + userName + " EXISTS!!!");
            throw new EntityExistsException("userLoginId = " + userLogin.getUserLoginId() + ", already exist!");
        }
        return userLoginRepo.save(userLogin);
    }

    @Override
    public UserLogin updatePassword(UserLogin user, String password) {
        user.setPassword(password);
        return userLoginRepo.save(user);
    }

    @Override
    @Transactional
    public Party createAndSaveUserLogin(PersonModel personModel) {
        Party party = partyRepo.save(new Party(
            personModel.getPartyCode(),
            partyTypeRepo.getOne(PartyTypeEnum.PERSON.name()),
            "",
            statusRepo
                .findById(StatusEnum.PARTY_ENABLED.name())
                .orElseThrow(NoSuchElementException::new),
            false));

        personRepo.save(new Person(
            party.getPartyId(),
            personModel.getFirstName(),
            personModel.getMiddleName(),
            personModel.getLastName(),
            personModel.getGender(),
            personModel.getBirthDate()));

        Set<SecurityGroup> roles = securityGroupRepo.findAllByGroupIdIn(personModel.getRoles());
        UserLogin userLogin = new UserLogin(personModel.getUserName(), personModel.getPassword(), roles, true);

        //log.info("save, roles = " + personModel.getRoles().size());
        if (userLoginRepo.existsById(personModel.getUserName())) {
            throw new RuntimeException();
        }

        userLogin.setParty(party);
        userLoginRepo.save(userLogin);

        return party;
    }
    @Override
    @Transactional
    public Party createAndSaveUserLoginNotYetActivated(PersonModel personModel) {
        Party party = partyRepo.save(new Party(
            personModel.getPartyCode(),
            partyTypeRepo.getOne(PartyTypeEnum.PERSON.name()),
            "",
            statusRepo
                .findById(StatusEnum.PARTY_ENABLED.name())
                .orElseThrow(NoSuchElementException::new),
            false));

        personRepo.save(new Person(
            party.getPartyId(),
            personModel.getFirstName(),
            personModel.getMiddleName(),
            personModel.getLastName(),
            personModel.getGender(),
            personModel.getBirthDate()));

        Set<SecurityGroup> roles = securityGroupRepo.findAllByGroupIdIn(personModel.getRoles());
        UserLogin userLogin = new UserLogin(personModel.getUserName(), personModel.getPassword(), roles, true);

       // log.info("save, roles = " + personModel.getRoles().size());
        if (userLoginRepo.existsById(personModel.getUserName())) {
            throw new RuntimeException();
        }

        userLogin.setParty(party);
        userLoginRepo.save(userLogin);

        return party;
    }

    @Override
    public Page<DPerson> findAllPerson(Pageable page, SortAndFiltersInput query) {
        BooleanExpression expression = null;
        List<SearchCriteria> fNew = new ArrayList<>();

        fNew.add(new SearchCriteria("type.id", ":", PartyTypeEnum.PERSON.name()));
        if (query != null) {
            // SortCriteria [] sorts= query.getSort();
            SearchCriteria[] filters = query.getFilters();
            fNew.addAll(Arrays.asList(filters));
        }
        PredicateBuilder builder = new PredicateBuilder();
        for (SearchCriteria sc : fNew) {
            builder.with(sc.getKey(), sc.getOperation(), sc.getValue());
        }
        expression = builder.build();
        // SortBuilder driverSortBuilder = new SortBuilder();
        // for (int i = 0; i < sorts.length; i++) {
        // driverSortBuilder.add(sorts[i].getField(), sorts[i].isAsc());
        // }
        // Sort sort = driverSortBuilder.build();
        return userRestRepository.findAll(expression, page);
    }

    @Override
    public Page<UserRestBriefProjection> findPersonByFullName(Pageable page, String sString) {
        return userRestRepository.findByTypeAndStatusAndFullNameLike(page, PartyTypeEnum.PERSON.name(),
                                                                     StatusEnum.PARTY_ENABLED.name(), sString);
    }

    @Override
    public Page<UserRestBriefProjection> findUsersByUserLoginId(Pageable page, String sString) {
        return userRestRepository.findByLoginUserId(page, sString);
    }

    @Override
    public DPerson findByPartyId(String partyId) {
        return userRestRepository.findById(UUID.fromString(partyId)).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Party update(PersonUpdateModel personUpdateModel, UUID partyId) {
        Person person = personRepo.getOne(partyId);
        person.setBirthDate(personUpdateModel.getBirthDate());
        person.setFirstName(personUpdateModel.getFirstName());
        person.setLastName(personUpdateModel.getLastName());
        person.setMiddleName(personUpdateModel.getMiddleName());
        personRepo.save(person);
        Party party = partyRepo.getOne(partyId);
        party.setPartyCode(personUpdateModel.getPartyCode());
        UserLogin u = party.getUserLogin();

        log.info("update Person userlogin = " + u.getUserLoginId());
        //UserRegister ur = userRegisterRepo.findByUserLoginId(u.getUserLoginId());
        UserRegister ur = userRegisterRepo.findById(u.getUserLoginId()).orElse(null);

        if(ur != null){
            ur.setEmail(personUpdateModel.getEmail());
            ur = userRegisterRepo.save(ur);
        }else{
            log.info("update Person, cannot find user register for " + u.getUserLoginId());
        }

        u.setRoles(securityGroupRepo.findAllByGroupIdIn(personUpdateModel.getRoles()));

        if(personUpdateModel.getEnabled().equals("Y")){
            u.setEnabled(true);
        }else if(personUpdateModel.getEnabled().equals("N")){
            u.setEnabled(false);
        }
        u = userLoginRepo.save(u);

        return partyRepo.findById(person.getPartyId()).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public UserLogin findUserLoginByPartyId(UUID partyId) {
        Party party = partyService.findByPartyId(partyId);
        return userLoginRepo.findByParty(party).get(0);
    }

    @Override
    @Transactional
    public SimpleResponse register(RegisterIM im) {
        //log.info("register, affiliations = " + im.getAffiliations().get(0));

        SimpleResponse res;
        String userLoginId = im.getUserLoginId();

        //if (userRegisterRepo.existsByUserLoginIdOrEmail(userLoginId, email) || userLoginRepo.existsById(userLoginId)) {
        if (userLoginRepo.existsById(userLoginId)) {
            res = new SimpleResponse(
                400,
                "existed",
                "Tên người dùng hoặc email đã được sử dụng");
        } else {
            StatusItem userRegistered = statusItemRepo
                .findById("USER_REGISTERED")
                .orElseThrow(NoSuchElementException::new);

            String firstName = StringUtils.normalizeSpace(im.getFirstName());
            String middleName = StringUtils.normalizeSpace(im.getMiddleName());
            String lastName = StringUtils.normalizeSpace(im.getLastName());
            String fullName = String.join(" ", firstName, middleName, lastName);
            String email = im.getEmail();

            UserRegister userRegister = new UserRegister(
                im.getUserLoginId(),
                im.getPassword(),
                email,
                firstName,
                middleName,
                lastName,
                String.join(",", im.getRoles()),
                String.join(",", im.getAffiliations()),
                userRegistered);

            userRegisterRepo.save(userRegister);

            // push notifications
            notificationsService.create(
                im.getUserLoginId(),
                "admin",
                fullName + " đã đăng kí tài khoản. Phê duyệt ngay.",
                "/user-group/user/approve-register");

            // send email.
            EMAIL_EXECUTOR_SERVICE.execute(() -> {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", fullName);
                    model.put("username", im.getUserLoginId());

                    Template template = config.getTemplate("successfully-register-mail-template.html");
                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    MimeMessageHelper helper = mailService.createMimeMessage(
                        new String[]{email},
                        "Open ERP - Đăng ký tài khoản thành công",
                        html,
                        true);
                    File resource = ResourceUtils.getFile("classpath:templates/logo.png");
                    helper.addInline("logo", resource);

                    mailService.sendMultipleMimeMessages(helper.getMimeMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            res = new SimpleResponse(200, null, null);
        }

        return res;
    }

    @Override
    @Transactional(readOnly = true)
    public GetAllRegistsOM getAllRegists() {
        List<RegistsOM> userRegisters = userRegisterRepo.getAllRegists("USER_REGISTERED");

        if (null == userRegisters) {
            return new GetAllRegistsOM();
        }

        return new GetAllRegistsOM(userRegisters, groupService.getRoles());
    }

    @Override
    @Transactional
    public SimpleResponse approve(ApproveRegistrationIM im) {
        UserRegister userRegister = userRegisterRepo.findById(im.getUserLoginId()).orElse(null);

        if (null == userRegister) {
            return new SimpleResponse(404, "not existed", "Đăng ký không tồn tại hoặc đã bị xoá");
        }

        if ("USER_APPROVED".equals(userRegister.getStatusItem().getStatusId())) {
            return new SimpleResponse(400, "approved", "Tài khoản đã được phê duyệt trước đó");
        }

        createAndSaveUserLogin(new PersonModel(
            userRegister.getUserLoginId(),
            userRegister.getPassword(),
            im.getRoles(),
            userRegister.getUserLoginId(),
            userRegister.getFirstName(),
            userRegister.getLastName(),
            userRegister.getMiddleName(),
            null,
            null,userRegister.getAffiliations()));

        StatusItem userApproved = statusItemRepo.findById("USER_APPROVED").orElseThrow(NoSuchElementException::new);
        userRegister.setStatusItem(userApproved);

        userRegisterRepo.save(userRegister);

        // send email
        UserRegister ur = userRegisterRepo.findById(im.getUserLoginId()).orElse(null);
        if(ur != null) {
            String fullName = String.join(" ", ur.getFirstName(), ur.getMiddleName(), ur.getLastName());
            String email = ur.getEmail();
           // log.info("approve, email = " + email + " fullname = " + fullName);
            EMAIL_EXECUTOR_SERVICE.execute(() -> {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", fullName);
                    model.put("username", im.getUserLoginId());

                    Template template = config.getTemplate("successfully-approve-register-mail-template.html");
                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    MimeMessageHelper helper = mailService.createMimeMessage(
                        new String[]{email},
                        "Open ERP - Tài khoản đã được phê duyệt",
                        html,
                        true);
                    File resource = ResourceUtils.getFile("classpath:templates/logo.png");
                    helper.addInline("logo", resource);

                    mailService.sendMultipleMimeMessages(helper.getMimeMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        return new SimpleResponse(200, null, null);
    }

    @Override
    public SimpleResponse disableUserRegistration(DisableUserRegistrationIM im) {
        StatusItem statusItem = statusItemRepo.findByStatusId(UserRegister.STATUS_DISABLED);
        UserRegister u = userRegisterRepo.findById(im.getUserLoginId()).orElse(null);
        if(u != null){
            u.setStatusItem(statusItem);
            u = userRegisterRepo.save(u);
           // log.info("disableUserRegistration OK");

            // send notification email to userLoginId

            String fullName = u.getLastName() + " " + u.getMiddleName() + " " + u.getFirstName();
            String email = u.getEmail();
            // send email.
            EMAIL_EXECUTOR_SERVICE.execute(() -> {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", fullName);
                    model.put("username", im.getUserLoginId());

                    Template template = config.getTemplate("not-approve-register-mail-template.html");
                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    MimeMessageHelper helper = mailService.createMimeMessage(
                        new String[]{email},
                        "Open ERP - Đăng ký tài khoản KHÔNG thành công",
                        html,
                        true);
                    File resource = ResourceUtils.getFile("classpath:templates/logo.png");
                    helper.addInline("logo", resource);

                    mailService.sendMultipleMimeMessages(helper.getMimeMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }
        return new SimpleResponse(200,null,null);
    }

    @Override
    public UserLogin updatePassword2(String userLoginId, String password) {
        //String passWordOut = PASSWORD_ENCODER.encode(password);
       // log.info("::updatePassword2, user_login " + userLoginId + " " + password);
//        UserLogin u = userLoginRepo.getByUserLoginId(userLoginId);
        UserLogin u = userLoginRepo.findByUserLoginId(userLoginId);
        //u.setPassword(passWordOut);
        u.setPassword(password);
        u = userLoginRepo.save(u);
        //log.info("::updatePassword2, user_login " + u.getUserLoginId() + " encrypted password = "+ passWordOut);
        return u;
    }

    @Override
    public List<UserLogin> getALlUserLoginsByGroupId(String groupId) {
        return userLoginRepo.findAllUserLoginsByGroupId(groupId);
    }

    @Override
    public List<String> getGroupPermsByUserLoginId(String userLoginId) {
        return userLoginRepo.findGroupPermsByUserLoginId(userLoginId);
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
            if(userRegister != null) affiliations = userRegister.getAffiliations();
            //log.info("findPersonByUserLoginId, affiliations of " + userLoginId + " = " + affiliations);
            Person person = personRepo.findByPartyId(userLogin.getParty().getPartyId());
            if (person == null) {
                log.info("findPersonByUserLoginId, person of " + userLoginId + " not exists");
                return new PersonModel();
            }
            //log.info("findPersonByUserLoginId, found person {}", person);
            PersonModel personModel = new PersonModel();
            personModel.setUserName(userLoginId);
            if (userRegister != null) {
                personModel.setEmail(userRegister.getEmail());
            }
            personModel.setGender(person.getGender());
            personModel.setBirthDate(person.getBirthDate());
            personModel.setFirstName(person.getFirstName());
            personModel.setMiddleName(person.getMiddleName());
            personModel.setLastName(person.getLastName());
            personModel.setAffiliations(affiliations);
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
        Collection<String> securityGroupIds, String search, Pageable pageable) {
        return userLoginRepo.findUserLoginWithPersonModelBySecurityGroupId(securityGroupIds, search, pageable);
    }

    @Override
    public List<String> findAllUserLoginIdOfGroup(String groupId) {
        return userLoginRepo.findAllUserLoginOfGroup(groupId);
    }

    @Override
    @Transactional
    public SimpleResponse approveCreateAccountActivationSendEmail(ApproveRegistrationIM im) {
       // log.info("approveCreateAccountActivationSendEmail, user_login_id = " + im.getUserLoginId());

        UserRegister userRegister = userRegisterRepo.findById(im.getUserLoginId()).orElse(null);

        AccountActivation accountActivation = new AccountActivation();
        accountActivation.setUserLoginId(im.getUserLoginId());
        accountActivation.setCreatedStamp(new Date());
        accountActivation.setStatusId(AccountActivation.STATUS_CREATED);

        accountActivation = accountActivationRepo.save(accountActivation);
        //String affiliations = "";

        createAndSaveUserLoginNotYetActivated(new PersonModel(
            userRegister.getUserLoginId(),
            userRegister.getPassword(),
            im.getRoles(),
            userRegister.getUserLoginId(),
            userRegister.getFirstName(),
            userRegister.getLastName(),
            userRegister.getMiddleName(),
            null,
            null,userRegister.getAffiliations()));

        StatusItem userApproved = statusItemRepo.findById("USER_APPROVED").orElseThrow(NoSuchElementException::new);
        userRegister.setStatusItem(userApproved);

        userRegisterRepo.save(userRegister);


        UUID activationId = accountActivation.getId();
        // send email activation
        UserRegister ur = userRegisterRepo.findById(im.getUserLoginId()).orElse(null);
        if(ur != null) {
            String fullName = String.join(" ", ur.getFirstName(), ur.getMiddleName(), ur.getLastName());
            String email = ur.getEmail();
          //  log.info("approve, email = " + email + " fullname = " + fullName);
            EMAIL_EXECUTOR_SERVICE.execute(() -> {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", fullName);
                    model.put("username", im.getUserLoginId());
                    model.put("url","https://openerp.dailyopt.ai/activation/activate/" + activationId.toString());

                    Template template = config.getTemplate("approve-register-mail-for-account-activation-template.html");
                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    MimeMessageHelper helper = mailService.createMimeMessage(
                        new String[]{email},
                        "Open ERP - Tài khoản đã được phê duyệt",
                        html,
                        true);
                    File resource = ResourceUtils.getFile("classpath:templates/logo.png");
                    helper.addInline("logo", resource);

                    mailService.sendMultipleMimeMessages(helper.getMimeMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        return new SimpleResponse(200, null, null);
    }

    @Override
    @Transactional
    public SimpleResponse activateAccount(UUID activationId) {
        AccountActivation accountActivation = accountActivationRepo.findById(activationId).orElse(null);
        if(accountActivation == null){
            return new SimpleResponse(404, "not existed", "Đăng ký không tồn tại hoặc đã bị xoá");
        }

        UserLogin u = userLoginRepo.findByUserLoginId(accountActivation.getUserLoginId());
        if(u == null){
            return new SimpleResponse(404, "not existed", "UserLoginId " + accountActivation.getUserLoginId()
                                                          + " không tồn tại hoặc đã bị xoá");

        }
        u.setEnabled(true);
        u = userLoginRepo.save(u);
      //  log.info("activateAccount, userLoginId = " + u.getUserLoginId() + " enabled = " + u.isEnabled());

        accountActivation.setStatusId(AccountActivation.STATUS_ACTIVATED);

        accountActivation = accountActivationRepo.save(accountActivation);

        return new SimpleResponse(200, null, null);
    }
    private String genRandomPassword(){
        String table = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@$!";
        String password = "";
        Random R = new Random();
        for(int i = 0; i <= 10; i++){
            int idx = R.nextInt(table.length());
            password = password + table.charAt(idx);
        }
        return password;
    }
    @Override
    public SimpleResponse resetPassword(String userLoginId) {
        UserRegister ur = userRegisterRepo.findById(userLoginId).orElse(null);
        if(ur != null) {
            String fullName = String.join(" ", ur.getFirstName(), ur.getMiddleName(), ur.getLastName());
            String email = ur.getEmail();
          //  log.info("approve, email = " + email + " fullname = " + fullName);
            String password = genRandomPassword();
            UserLogin u = userLoginRepo.findByUserLoginId(userLoginId);
            if(u == null){
                return new SimpleResponse(404, null, "Tài khoản " + userLoginId + " không tồn tại");
            }
            u.setPassword(password);
            u = userLoginRepo.save(u);

            EMAIL_EXECUTOR_SERVICE.execute(() -> {
                try {
                    Map<String, Object> model = new HashMap<>();
                    model.put("name", fullName);
                    model.put("username", userLoginId);
                    model.put("password",password);

                    Template template = config.getTemplate("send-mail-reset-password-template.html");
                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

                    MimeMessageHelper helper = mailService.createMimeMessage(
                        new String[]{email},
                        "Open ERP - Reset password",
                        html,
                        true);
                    File resource = ResourceUtils.getFile("classpath:templates/logo.png");
                    helper.addInline("logo", resource);

                    mailService.sendMultipleMimeMessages(helper.getMimeMessage());

                    //return new SimpleResponse(200, null, "mật khẩu mới đã được gửi đến email cho tài khoản " + userLoginId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            return new SimpleResponse(200, null, "mật khẩu mới đã được gửi đến email cho tài khoản " + userLoginId);
        }

        return new SimpleResponse(404, null, "Tài khoản " + userLoginId + " không tồn tại");
    }

    @Override
    public SimpleResponse assignGroup2AllUsers(ModelAssignGroupAllUsersInput I) {
        SimpleResponse res = new SimpleResponse(200,"OK","OK");
        List<UserLogin> users = userLoginRepo.findAll();
        SecurityGroup g = securityGroupRepo.findById(I.getGroupId()).orElse(null);
        int cnt = 0;
       // log.info("assignGroup2AllUsers, groupId = " + I.getGroupId() + " g = " + g);
        if(g == null) return res;
        for(UserLogin u: users){
            if(!u.hasRole(I.getGroupId())) {
                u.getRoles().add(g);
                u = userLoginRepo.save(u);
                cnt += 1;
             //   log.info("assignGroup2AllUsers: OK " + cnt + " add group to user " + u.getUserLoginId());
            }

        }
        res.setMessage("numbr of updates = " + cnt);
        return res;
    }

    @Override
    public List<String> getAllEnabledLoginIdsContains(String partOfLoginId, Integer limit) {
        return userLoginRepo.findByEnabledLoginIdContains(partOfLoginId, limit);
    }

    @Override
    public ModelPageUserSearchResponse searchUser(Pageable pageable, String keyword) {

        Page<PersonModel> list = userLoginRepo.searchUser(
            pageable,
            keyword);
        return ModelPageUserSearchResponse.builder()
                                                 .contents(list)
                                                 .build();
    }

//    @Override
//    public UserRegister.OutputModel registerUser(UserRegister.InputModel inputModel) {
//        String userLoginId = inputModel.getUserLoginId();
//        String email = inputModel.getEmail();
//
//        if (userRegisterRepo.existsByUserLoginIdOrEmail(userLoginId, email) || userLoginRepo.existsById(userLoginId)) {
//            return new UserRegister.OutputModel();
//        }
//        StatusItem userRegistered = statusItemRepo
//            .findById("USER_REGISTERED")
//            .orElseThrow(NoSuchElementException::new);
//        UserRegister userRegister = inputModel.createUserRegister(userRegistered);
//        userRegister = userRegisterRepo.save(userRegister);
//
//        EMAIL_EXECUTOR_SERVICE.execute(() -> sendEmail(email, userLoginId));
//
//        return userRegister.toOutputModel();
//    }
//
//    private void sendEmail(String email, String userLoginId) {
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setTo(email);
//
//        simpleMailMessage.setSubject("Đăng ký thành công - SSCM - Quản lý chuỗi cung ứng");
//        simpleMailMessage.setText(String.format(
//            "Bạn đã đăng ký thành công tài khoản tại hệ thống với tên đăng nhập %s, " +
//            "vui lòng chờ cho đến khi được quản trị viên phê duyệt. \nXin cảm ơn!",
//            userLoginId));
//        javaMailSender.send(simpleMailMessage);
//    }

    /*@Override
    public boolean approveRegisterUser(String userLoginId) {
        UserRegister userRegister = userRegisterRepo.findById(userLoginId).orElse(null);
        if (userRegister == null) {
            return false;
        }

        try {
//            createAndSaveUserLogin(userRegister.getUserLoginId(), userRegister.getPassword());

            try {
                createAndSaveUserLogin(new PersonModel(
                    userRegister.getUserLoginId(),
                    userRegister.getPassword(),
                    new ArrayList<>(),
                    userRegister.getUserLoginId(),
                    userRegister.getFirstName(),
                    userRegister.getLastName(),
                    userRegister.getMiddleName(),
                    null,
                    null));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            return false;
        }
        StatusItem userApproved = statusItemRepo.findById("USER_APPROVED").orElseThrow(NoSuchElementException::new);
        userRegister.setStatusItem(userApproved);
        userRegisterRepo.save(userRegister);
        return true;
    }

    @Override
    public List<UserRegister.OutputModel> findAllRegisterUser() {

        StatusItem userRegistered = null;
        try {
            userRegistered = statusItemRepo
                .findById("USER_REGISTERED")
                .orElseThrow(NoSuchElementException::new);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userRegistered != null) {
            List<UserRegister> userRegisters = userRegisterRepo.findAllByStatusItem(userRegistered);
            return userRegisters.stream().map(UserRegister::toOutputModel).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }


        *//*
        StatusItem userRegistered = statusItemRepo
            .findById("USER_REGISTERED")
            .orElseThrow(NoSuchElementException::new);
        List<UserRegister> userRegisters = userRegisterRepo.findAllByStatusItem(userRegistered);
        return userRegisters.stream().map(UserRegister::toOutputModel).collect(Collectors.toList());
        *//*

    }*/
}
