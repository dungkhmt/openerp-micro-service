package com.hust.baseweb.applications.examclassandaccount.service;

import com.hust.baseweb.applications.examclassandaccount.entity.ExamClassUserloginMap;
import com.hust.baseweb.applications.examclassandaccount.entity.RandomGeneratedUserLogin;
import com.hust.baseweb.applications.examclassandaccount.model.UserLoginModel;
import com.hust.baseweb.applications.examclassandaccount.repo.ExamClassUserloginMapRepo;
import com.hust.baseweb.applications.examclassandaccount.repo.RandomGeneratedUserLoginRepo;
import com.hust.baseweb.entity.UserLogin;
import com.hust.baseweb.repo.UserLoginRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@AllArgsConstructor(onConstructor_ = @Autowired)
@Service
@Transactional
@javax.transaction.Transactional
public class ExamClassUserloginMapServiceImpl implements ExamClassUserloginMapService{
    @Autowired
    private ExamClassUserloginMapRepo examClassUserloginMapRepo;
    @Autowired
    private RandomGeneratedUserLoginRepo randomGeneratedUserLoginRepo;

    @Autowired
    private UserLoginRepo userLoginRepo;


    @Override
    public List<ExamClassUserloginMap> getExamClassUserloginMap(UUID examClassId) {
        List<ExamClassUserloginMap> res = examClassUserloginMapRepo.findByExamClassId(examClassId);

        return res;

    }

    public static String genRandomPassword(int L){
        String T = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random R = new Random();
        String p = "";
        for(int i = 1; i <= L; i++) p = p + T.charAt(R.nextInt(T.length()));
        return p;
    }
    @Override
    public List<ExamClassUserloginMap> createExamClassAccount(UUID examClassId, List<UserLoginModel> users) {
        List<RandomGeneratedUserLogin> generatedUsers = randomGeneratedUserLoginRepo.findAll();
        List<ExamClassUserloginMap> lst = examClassUserloginMapRepo.findAllByStatus(ExamClassUserloginMap.STATUS_ACTIVE);
        Set<String> activeUsers = new HashSet<String>();
        for(ExamClassUserloginMap e: lst){
            activeUsers.add(e.getRandomUserLoginId());
        }
        Map<String, RandomGeneratedUserLogin> mUserID2User = new HashMap<String, RandomGeneratedUserLogin>();
        for(RandomGeneratedUserLogin ru: generatedUsers){
            mUserID2User.put(ru.getUserLoginId(),ru);
        }
        String[] availableUsers = new String[generatedUsers.size() - activeUsers.size()];
        int sz = 0;
        for(RandomGeneratedUserLogin ru: generatedUsers){
            if(!activeUsers.contains(ru.getUserLoginId())){
                sz++; availableUsers[sz-1] = ru.getUserLoginId();
            }
        }

        List<ExamClassUserloginMap> res = new ArrayList<ExamClassUserloginMap>();
        Random R = new Random();
        for(UserLoginModel um: users){
            if (sz <= 0) {
                break;
            }
            int i = R.nextInt(sz);
            String selectedUserLogin = availableUsers[i];
            availableUsers[i] = availableUsers[sz-1];
            sz--;
            ExamClassUserloginMap ecum = new ExamClassUserloginMap();
            ecum.setRealUserLoginId(um.getUserLoginId());
            ecum.setRandomUserLoginId(selectedUserLogin);
            ecum.setStudentCode(um.getStudentCode());
            ecum.setFullname(um.getFullName());
            ecum.setStatus(ExamClassUserloginMap.STATUS_ACTIVE);
            RandomGeneratedUserLogin ru = mUserID2User.get(selectedUserLogin);
            //String password = "";
            //if(ru != null) password = ru.getPassword();
            String password = genRandomPassword(10);
            ecum.setPassword(password);
            ecum.setExamClassId(examClassId);

            ecum = examClassUserloginMapRepo.save(ecum);

            UserLogin ul = userLoginRepo.findByUserLoginId(selectedUserLogin);
            if(ul == null){
                ul = new UserLogin();
                ul.setUserLoginId(selectedUserLogin);
                //ul.setEnabled(true);
            }else{

            }
            ul.setEnabled(true);
            ul.setFirstName(um.getFullName());
            ul = userLoginRepo.save(ul);

            res.add(ecum);
        }
        return res;
    }
    public static void main(String[] args){
        //ExamClassUserloginMapServiceImpl app = new ExamClassUserloginMapServiceImpl();
        System.out.println(ExamClassUserloginMapServiceImpl.genRandomPassword(10));
    }
}
