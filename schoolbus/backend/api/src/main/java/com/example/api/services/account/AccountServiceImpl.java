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
import com.example.api.services.auth.AuthService;
import com.example.api.services.auth.dto.SignUpInput;
import com.example.api.utils.UserContextUtil;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Parent;
import com.example.shared.db.entities.Student;
import com.example.shared.db.repo.AccountRepository;
import com.example.shared.db.repo.ParentRepository;
import com.example.shared.db.repo.StudentRepository;
import com.example.shared.enumeration.UserRole;
import com.example.shared.exception.MyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
    private final AuthService authService;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;


    @Override
    public Page<ParentSearchOutput> searchParents(ParentSearchInput input) {
            var res = parentRepository.searchPageParent(
            input.getId(),
            input.getName(),
            input.getRole(),
            input.getSearchBy() == null ? null : input.getSearchBy().getValue(),
            input.getPhoneNumber(),
            input.getStudentId(),
            input.getPageable()
        );
        return res.map(ParentSearchOutput::from);
    }

    @Override
    public Page<StudentSearchOutput> searchStudents(StudentSearchInput input) {
        var res = studentRepository.searchPageStudent(
            input.getId(),
            input.getName(),
            input.getPhoneNumber(),
            input.getParentId(),
            input.getStudentClass(),
            input.getPageable()
        );
        return res.map(StudentSearchOutput::from);
    }

    @Override
    public Page<StudentSearchOutput> searchStudents(StudentSearchInput input, Account account) {
        Parent parent = parentRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));

        StudentSearchInput newInput = StudentSearchInput.builder()
            .id(input.getId())
            .name(input.getName())
            .phoneNumber(input.getPhoneNumber())
            .parentId(parent.getId())
            .studentClass(input.getStudentClass())
            .pageable(input.getPageable())
            .build();
        return searchStudents(newInput);

    }

    @Override
    public ParentDetailOutput getParentDetail(Long id) {
        Parent parent = parentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        List<Student> students = studentRepository.findByParent_Id(id);
        return ParentDetailOutput.builder()
            .id(parent.getId())
            .name(parent.getName())
            .dob(parent.getDob())
            .avatar(parent.getAvatar())
            .username(parent.getAccount().getUsername())
            .phoneNumber(parent.getPhoneNumber())
            .students(students.stream().map(StudentSearchOutput::from).toList())
            .createdAt(parent.getCreatedAt())
            .updatedAt(parent.getUpdatedAt())
            .build();
    }

    @Override
    public ParentDetailOutput getParentDetail(Account account) {
        Parent parent = parentRepository.findByAccountId(account.getId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        return getParentDetail(parent.getId());
    }

    @Override
    public ParentDetailOutput getParentDetail(Long id, Account account) {
        Parent parent = parentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        if (UserContextUtil.isCurrentUser(parent.getAccount().getId())){
            return getParentDetail(id);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public StudentDetailOutput getStudentDetail(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "STUDENT_NOT_FOUND", "Student not found",
                HttpStatus.BAD_REQUEST));
        return StudentDetailOutput.from(student);
    }

    @Override
    public StudentDetailOutput getStudentDetail(Long id, Account account) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "STUDENT_NOT_FOUND", "Student not found",
                HttpStatus.BAD_REQUEST));
        if (UserContextUtil.isCurrentUser(student.getParent().getAccount().getId())){
            return getStudentDetail(id);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void addStudent(StudentAddInput input) {
        Parent parent = parentRepository.findById(input.getParentId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        Student student = Student.builder()
            .name(input.getName())
            .studentClass(input.getStudentClass())
            .parent(parent)
            .phoneNumber(input.getPhoneNumber())
            .dob(input.getDob())
            .avatar(input.getAvatar())
            .build();
        studentRepository.save(student);
    }

    @Override
    public void addStudent(StudentAddInput input, Account account) {
        Parent parent = parentRepository.findById(input.getParentId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        if (UserContextUtil.isCurrentUser(parent.getAccount().getId())){
            input.setParentId(parent.getId());
            addStudent(input);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }


    @Override
    public void updateStudent(StudentUpdateInput input) {
        Student student = studentRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(null, "STUDENT_NOT_FOUND", "Student not found",
                HttpStatus.BAD_REQUEST));
        Parent parent = parentRepository.findById(input.getParentId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));

        student.setName(input.getName());
        student.setStudentClass(input.getStudentClass());
        student.setParent(parent);
        student.setDob(input.getDob());
        student.setAvatar(input.getAvatar());
        student.setPhoneNumber(input.getPhoneNumber());
        studentRepository.save(student);
    }

    @Override
    public void updateStudent(StudentUpdateInput input, Account account) {
        Student student = studentRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(null, "STUDENT_NOT_FOUND", "Student not found",
                HttpStatus.BAD_REQUEST));
        if (UserContextUtil.isCurrentUser(student.getParent().getAccount().getId())){
            input.setParentId(student.getParent().getId());
            updateStudent(input);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "STUDENT_NOT_FOUND", "Student not found",
                HttpStatus.BAD_REQUEST));
        studentRepository.delete(student);
    }

    @Override
    public void deleteStudent(Long id, Account account) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "STUDENT_NOT_FOUND", "Student not found",
                HttpStatus.BAD_REQUEST));
        Parent parent = student.getParent();
        if (UserContextUtil.isCurrentUser(parent.getAccount().getId())){
            deleteStudent(id);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void addParent(ParentAddInput input) {
        Parent.ParentBuilder parentBuilder = Parent.builder()
            .name(input.getName())
            .dob(input.getDob())
            .avatar(input.getAvatar())
            .phoneNumber(input.getPhoneNumber());

        if (input.getStudentIds() != null) {
            parentBuilder.students(studentRepository.findAllById(input.getStudentIds()));
        }
        Account account = authService.signUp(SignUpInput.builder()
            .username(input.getUsername())
            .password(input.getPassword())
            .role(UserRole.CLIENT)
            .build());
        parentBuilder.account(account);

        Parent parent = parentBuilder.build();
        parentRepository.save(parent);
    }

    @Override
    public void updateParent(ParentUpdateInput input) {
        Parent parent = parentRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        parent.setName(input.getName());
        parent.setDob(input.getDob());
        parent.setAvatar(input.getAvatar());
        parent.setPhoneNumber(input.getPhoneNumber());
        Account account = parent.getAccount();
        if(input.getUsername() != null && !input.getUsername().isEmpty()) {
            account.setUsername(input.getUsername());
        }
        if(input.getPassword() != null && !input.getPassword().isEmpty()) {
            account.setPassword(authService.encodePassword(input.getPassword()));
        }
        parent.setAccount(account);
        if (input.getStudentIds() != null) {
            parent.setStudents(studentRepository.findAllById(input.getStudentIds()));
        }
        parentRepository.save(parent);
    }

    @Override
    public void updateParent(ParentUpdateInput input, Account account) {
        Parent parent = parentRepository.findById(input.getId())
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        if (UserContextUtil.isCurrentUser(parent.getAccount().getId())){
            updateParent(input);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public void deleteParent(Long id) {
        Parent parent = parentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        parentRepository.delete(parent);
    }

    @Override
    public void deleteParent(Long id, Account account) {
        Parent parent = parentRepository.findById(id)
            .orElseThrow(() -> new MyException(null, "PARENT_NOT_FOUND", "Parent not found",
                HttpStatus.BAD_REQUEST));
        if (UserContextUtil.isCurrentUser(parent.getAccount().getId())){
            deleteParent(id);
        } else {
            throw new MyException(null, "FORBIDDEN", "Forbidden", HttpStatus.FORBIDDEN);
        }
    }
}
