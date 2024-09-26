package com.example.api.services.account;

import com.example.api.services.account.dto.ParentAddInput;
import com.example.api.services.account.dto.ParentDetailOutput;
import com.example.api.services.account.dto.ParentSearchInput;
import com.example.api.services.account.dto.ParentSearchOutput;
import com.example.api.services.account.dto.ParentUpdateInput;
import com.example.api.services.account.dto.StudentAddInput;
import com.example.api.services.account.dto.StudentDetailOutput;
import com.example.api.services.account.dto.StudentSearchInput;
import com.example.api.services.account.dto.StudentSearchOutput;
import com.example.api.services.account.dto.StudentUpdateInput;
import com.example.shared.db.entities.Account;
import org.springframework.data.domain.Page;

public interface AccountService {
    Page<ParentSearchOutput> searchParents(ParentSearchInput input);

    Page<StudentSearchOutput> searchStudents(StudentSearchInput input);

    Page<StudentSearchOutput> searchStudents(StudentSearchInput input, Account account);
    ParentDetailOutput getParentDetail(Long id);

    ParentDetailOutput getParentDetail(Account account);

    ParentDetailOutput getParentDetail(Long id, Account account);

    StudentDetailOutput getStudentDetail(Long id);

    StudentDetailOutput getStudentDetail(Long id, Account account);

    void addStudent(StudentAddInput input);
    void addStudent(StudentAddInput input, Account account);
    void updateStudent(StudentUpdateInput input);

    void updateStudent(StudentUpdateInput input, Account account);

    void deleteStudent(Long id);

    void deleteStudent(Long id, Account account);

    void addParent(ParentAddInput input);

    void updateParent(ParentUpdateInput input);

    void updateParent(ParentUpdateInput input, Account account);

    void deleteParent(Long id);

    void deleteParent(Long id, Account account);
}
