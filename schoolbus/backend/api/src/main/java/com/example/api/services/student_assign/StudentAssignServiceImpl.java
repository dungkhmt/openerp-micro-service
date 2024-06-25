package com.example.api.services.student_assign;

import com.example.api.services.student_assign.dto.UpsertStudentAssignInput;
import com.example.shared.db.entities.StudentAssign;
import com.example.shared.db.repo.BusRepository;
import com.example.shared.db.repo.StudentAssignRepository;
import com.example.shared.db.repo.StudentRepository;
import com.example.shared.exception.MyException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentAssignServiceImpl implements StudentAssignService{
    private final StudentAssignRepository studentAssignRepository;
    private final StudentRepository studentRepository;
    private final BusRepository busRepository;

    @Override
    public void upsertStudentAssign(UpsertStudentAssignInput input) {
        List<StudentAssign> studentAssigns = new ArrayList<>();
        // validate all studentIds and numberPlates exist
        if (studentRepository.countByIdIn(input.getItems().stream().map(UpsertStudentAssignInput.Item::getStudentId).toList()) != input.getItems().size()) {
            throw new MyException(
                null,
                "Invalid studentIds",
                "Some studentIds do not exist",
                HttpStatus.BAD_REQUEST
            );
        }
        if (busRepository.countByNumberPlateIn(input.getItems().stream().map(UpsertStudentAssignInput.Item::getNumberPlate).toList()) != input.getItems().size()) {
            throw new MyException(
                null,
                "Invalid numberPlates",
                "Some numberPlates do not exist",
                HttpStatus.BAD_REQUEST
            );
        }

        for (UpsertStudentAssignInput.Item item : input.getItems()) {
            log.info("Upserting student assign for studentId: {} and numberPlate: {}", item.getStudentId(), item.getNumberPlate());
            StudentAssign studentAssign = studentAssignRepository.findByStudentId(item.getStudentId()).orElse(null);

            if (studentAssign == null) {
                studentAssign = StudentAssign.builder()
                    .studentId(item.getStudentId())
                    .numberPlate(item.getNumberPlate())
                    .build();
            } else {
                studentAssign.setNumberPlate(item.getNumberPlate());
            }

            studentAssigns.add(studentAssign);
        }

        studentAssignRepository.saveAll(studentAssigns);
    }
}
