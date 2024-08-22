package com.example.api.services.history;

import com.example.api.services.history.dto.AdminHistoryRideFilterParam;
import com.example.api.services.history.dto.AdminHistoryRideOutput;
import com.example.api.services.history.dto.ClientHistoryRideFilterParam;
import com.example.api.services.history.dto.ClientHistoryRideOutput;
import com.example.api.services.history.dto.EmployeeHistoryRideFilterParam;
import com.example.api.services.history.dto.EmployeeHistoryRideOutput;
import com.example.shared.db.entities.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryService {
    Page<AdminHistoryRideOutput> getAdminHistoryRides(AdminHistoryRideFilterParam filterParam,
                                                      Pageable pageable);

    Page<EmployeeHistoryRideOutput> getEmployeeHistoryRides(
        EmployeeHistoryRideFilterParam filterParam,
        Pageable pageable, Account account);

    Page<ClientHistoryRideOutput> getClientHistoryRides(
        ClientHistoryRideFilterParam filterParam,
        Pageable pageable, Account account);
}
