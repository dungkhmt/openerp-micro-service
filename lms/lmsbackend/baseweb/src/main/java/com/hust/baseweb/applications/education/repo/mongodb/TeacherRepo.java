package com.hust.baseweb.applications.education.repo.mongodb;

import com.hust.baseweb.applications.education.entity.mongodb.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeacherRepo extends MongoRepository<Teacher, String> {

}
