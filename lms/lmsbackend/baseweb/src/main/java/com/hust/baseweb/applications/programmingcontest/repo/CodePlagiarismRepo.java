package com.hust.baseweb.applications.programmingcontest.repo;

import com.hust.baseweb.applications.programmingcontest.entity.CodePlagiarism;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodePlagiarismRepo extends JpaRepository<CodePlagiarism, UUID> {
    List<CodePlagiarism> findAllByContestId(String contestId);

}
