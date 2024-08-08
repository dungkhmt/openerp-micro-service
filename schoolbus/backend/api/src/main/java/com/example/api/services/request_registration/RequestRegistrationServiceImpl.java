package com.example.api.services.request_registration;

import com.example.api.services.common_dto.ParentOutput;
import com.example.api.services.common_dto.RequestRegistrationOutput;
import com.example.api.services.common_dto.StudentOutput;
import com.example.api.services.request_registration.dto.CreateRequestInput;
import com.example.api.services.request_registration.dto.GetListRequestRegistrationOutput;
import com.example.api.services.request_registration.dto.HandleRequestRegistrationInput;
import com.example.shared.db.dto.GetListRequestRegistrationDTO;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Parent;
import com.example.shared.db.entities.PickupPoint;
import com.example.shared.db.entities.RequestRegistration;
import com.example.shared.db.entities.Student;
import com.example.shared.db.entities.StudentPickupPoint;
import com.example.shared.db.entities.StudentPickupPointHistory;
import com.example.shared.db.repo.ParentRepository;
import com.example.shared.db.repo.PickupPointRepository;
import com.example.shared.db.repo.RequestRegistrationRepository;
import com.example.shared.db.repo.StudentPickupPointHistoryRepository;
import com.example.shared.db.repo.StudentPickupPointRepository;
import com.example.shared.db.repo.StudentRepository;
import com.example.shared.enumeration.RequestRegistrationStatus;
import com.example.shared.enumeration.StudentPickupPointStatus;
import com.example.shared.exception.MyException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestRegistrationServiceImpl implements RequestRegistrationService{
    private final RequestRegistrationRepository requestRegistrationRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final PickupPointRepository pickupPointRepository;
    private final StudentPickupPointRepository studentPickupPointRepository;
    private final StudentPickupPointHistoryRepository studentPickupPointHistoryRepository;

    @Override
    @Transactional
    public void upsertRegistration(CreateRequestInput input, Account account) {
        if (input.getStudentIds() == null || input.getStudentIds().isEmpty()
            || input.getAddress() == null || input.getAddress().isBlank()
            || input.getLongitude() == null || input.getLatitude() == null) {
            throw new MyException(
                null,
                "INVALID_INPUT",
                "Invalid input",
                HttpStatus.BAD_REQUEST
            );
        }

        Parent parent = parentRepository.findByAccountId(account.getId()).orElseThrow(
            () -> new MyException(
                null,
                "PARENT_NOT_FOUND",
                "Parent not found",
                HttpStatus.NOT_FOUND
            )
        );

        // validate student is children of parent
        List<Long> studentIds = studentRepository.findStudentIdsByParentId(parent.getId());
        for (Long studentId : input.getStudentIds()) {
            if (!studentIds.contains(studentId)) {
                throw new MyException(
                    null,
                    "STUDENT_NOT_CHILDREN_OF_PARENT",
                    "Student not children of parent",
                    HttpStatus.NOT_FOUND
                );
            }
        }

        // validate if exist request registration for those students => delete them to add new one
        List<RequestRegistration> requestRegistrations = requestRegistrationRepository
            .findByParentIdAndStudentIdInAndStatus(parent.getId(), input.getStudentIds(), RequestRegistrationStatus.PENDING);
        if (!requestRegistrations.isEmpty()) {
            requestRegistrationRepository.deleteAll(requestRegistrations);
        }

        for (Long studentId : input.getStudentIds()) {
            Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new MyException(
                    null,
                    "STUDENT_NOT_FOUND",
                    "Student not found",
                    HttpStatus.NOT_FOUND
                )
            );
            RequestRegistration requestRegistration = RequestRegistration.builder()
                .parent(parent)
                .student(student)
                .status(RequestRegistrationStatus.PENDING)
                .address(input.getAddress())
                .longitude(input.getLongitude())
                .latitude(input.getLatitude())
                .build();
            requestRegistrationRepository.save(requestRegistration);
        }

    }

    @Override
    public List<GetListRequestRegistrationOutput> getListRequestRegistration(Account account) {
        Parent parent = parentRepository.findByAccountId(account.getId()).orElseThrow(
            () -> new MyException(
                null,
                "PARENT_NOT_FOUND",
                "Parent not found",
                HttpStatus.NOT_FOUND
            )
        );

        List<RequestRegistration> requestRegistrations = requestRegistrationRepository
            .findByParentIdOrderByCreatedAtDesc(parent.getId());

        return requestRegistrations.stream().map(requestRegistration -> {
            Student student = requestRegistration.getStudent();
            return GetListRequestRegistrationOutput.builder()
                .parent(ParentOutput.fromEntity(parent))
                .student(StudentOutput.fromEntity(student))
                .requestRegistration(RequestRegistrationOutput.fromEntity(requestRegistration))
                .build();
        }).toList();
    }

    @Override
    public Page<GetListRequestRegistrationOutput> getPageRequestRegistration(
        String studentName,
        String parentName,
        List<RequestRegistrationStatus> statuses,
        String address,
        Pageable pageable
    ) {
        if (statuses != null && statuses.isEmpty()) {
            statuses = null;
        }
       Page<GetListRequestRegistrationDTO> pageRequestRegistration = requestRegistrationRepository
           .getPageRequestRegistration(studentName, parentName, statuses, address, pageable);

         return pageRequestRegistration.map(GetListRequestRegistrationOutput::fromDTO);
    }

    @Override
    @Transactional
    public void handleRequestRegistration(HandleRequestRegistrationInput input) {
        if (input.getRequestIds() == null || input.getRequestIds().isEmpty()) {
            throw new MyException(
                null,
                "INVALID_INPUT",
                "Invalid input",
                HttpStatus.BAD_REQUEST
            );
        }

        for (Long requestId : input.getRequestIds()) {
            handleSingleRequestRegistration(input, requestId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSingleRequestRegistration(HandleRequestRegistrationInput input,
                                                Long requestId) {
        RequestRegistration requestRegistration = requestRegistrationRepository
            .findById(requestId).orElseThrow(
                () -> new MyException(
                    null,
                    "REQUEST_REGISTRATION_NOT_FOUND",
                    "Request registration not found",
                    HttpStatus.NOT_FOUND
                )
            );

        if (requestRegistration.getStatus() != RequestRegistrationStatus.PENDING) {
            throw new MyException(
                null,
                "REQUEST_REGISTRATION_NOT_PENDING",
                "Request registration not pending",
                HttpStatus.BAD_REQUEST
            );
        }

        requestRegistration.setStatus(input.getStatus());
        requestRegistration.setNote(input.getNote());
        requestRegistrationRepository.save(requestRegistration);

        if (input.getStatus() == RequestRegistrationStatus.ACCEPTED) {
            // delete old pickup point of student
            studentPickupPointRepository.deleteByStudentId(requestRegistration.getStudent().getId());
            // if a pickup point with no student, delete it
//            pickupPointRepository.deletePickupPointWithNoStudent(); //todo: manage pickup point for admin role later
            // upsert pickup point
            PickupPoint pickupPoint = null;
            if (input.getPickupPointId() == null) {
                if (input.getAddress() == null || input.getAddress().isBlank()
                    || input.getLongitude() == null || input.getLatitude() == null) {
                    throw new MyException(
                        null,
                        "INVALID_INPUT",
                        "Invalid input",
                        HttpStatus.BAD_REQUEST
                    );
                }
                pickupPoint = PickupPoint.builder()
                    .address(input.getAddress())
                    .longitude(input.getLongitude())
                    .latitude(input.getLatitude())
                    .build();
            } else {
                pickupPoint = pickupPointRepository.findById(input.getPickupPointId())
                    .orElseThrow(
                        () -> new MyException(
                            null,
                            "PICKUP_POINT_NOT_FOUND",
                            "Pickup point not found",
                            HttpStatus.NOT_FOUND
                        )
                    );
            }

            pickupPoint = pickupPointRepository.save(pickupPoint);
            // save student pickup point
            StudentPickupPoint studentPickupPoint = studentPickupPointRepository.save(
                StudentPickupPoint.builder()
                    .student(requestRegistration.getStudent())
                    .pickupPoint(pickupPoint)
                    .status(StudentPickupPointStatus.PICKING)
                    .build()
            );
            // save note for request registration
            requestRegistration.setNote( "Địa chỉ tiếp nhận: " + pickupPoint.getAddress());
            // save history
            studentPickupPointHistoryRepository.save(
                StudentPickupPointHistory.builder()
                    .studentPickupPointId(studentPickupPoint.getId())
                    .studentId(studentPickupPoint.getStudent().getId())
                    .pickupPointId(studentPickupPoint.getPickupPoint().getId())
                    .status(studentPickupPoint.getStatus())
                    .address(studentPickupPoint.getPickupPoint().getAddress())
                    .latitude(studentPickupPoint.getPickupPoint().getLatitude())
                    .longitude(studentPickupPoint.getPickupPoint().getLongitude())
                    .rideId(null)
                    .build()
            );
        }
    }
}
