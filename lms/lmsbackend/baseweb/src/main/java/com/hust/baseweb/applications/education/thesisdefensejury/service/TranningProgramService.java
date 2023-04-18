package com.hust.baseweb.applications.education.thesisdefensejury.service;

import com.hust.baseweb.applications.education.thesisdefensejury.entity.TraningProgram;
import com.hust.baseweb.applications.education.thesisdefensejury.models.Response;
import com.hust.baseweb.applications.education.thesisdefensejury.models.TranningProgramIM;

import java.util.List;

public interface TranningProgramService {
    List<TraningProgram> getAllTranningProgram();
    Response createTranningProgram(TranningProgramIM request);
}
