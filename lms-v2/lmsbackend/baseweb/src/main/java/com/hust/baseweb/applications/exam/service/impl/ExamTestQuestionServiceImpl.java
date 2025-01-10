package com.hust.baseweb.applications.exam.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hust.baseweb.applications.exam.repository.ExamTestQuestionRepository;
import com.hust.baseweb.applications.exam.repository.ExamTestRepository;
import com.hust.baseweb.applications.exam.service.ExamTestQuestionService;
import com.hust.baseweb.applications.exam.service.ExamTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ExamTestQuestionServiceImpl implements ExamTestQuestionService {

    private final ExamTestQuestionRepository examTestQuestionRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

}
