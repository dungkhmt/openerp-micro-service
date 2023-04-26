package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.TraningProgram;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.TranningProgramIM;
import com.hust.baseweb.applications.education.thesisdefensejury.repo.TranningProgramRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Service
@Transactional
@Slf4j
public class TranningProgramImpl implements TranningProgramService{
    private final TranningProgramRepo tranningProgramRepo;

    @Override
    public List<TraningProgram> getAllTranningProgram() {
        return tranningProgramRepo.findAll();
    }

    @Override
    public Response createTranningProgram(TranningProgramIM request) {
        Response res = new Response();
        // check request
        if (request.getName() == null  || request.getName() == "" ){
            res.setErr("Invalid tranning program name");
            res.setOk(false);
            return res;
        }
        TraningProgram tp = new TraningProgram();
        tp.setName(request.getName());
        tranningProgramRepo.save(tp);
        res.setOk(true);
        return res;
    }
}
