package com.example.api.services.request_registration;

import com.example.api.services.request_registration.dto.CreateRequestInput;
import com.example.api.services.request_registration.dto.GetListRequestRegistrationOutput;
import com.example.api.services.request_registration.dto.HandleRequestRegistrationInput;
import com.example.shared.db.entities.Account;
import com.example.shared.enumeration.RequestRegistrationStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RequestRegistrationService {
    void upsertRegistration(CreateRequestInput input, Account account);

    List<GetListRequestRegistrationOutput> getListRequestRegistration(Account account);

    Page<GetListRequestRegistrationOutput> getPageRequestRegistration(
        String studentName,
        String parentName,
        List<RequestRegistrationStatus> statuses,
        String address,
        Pageable pageable
    );

    void handleRequestRegistration(HandleRequestRegistrationInput input);
}
