package com.example.api.services.bus;

import com.example.api.controllers.admin.dto.BusManipulateParam;
import com.example.api.services.bus.dto.GetBusDetailOutput;
import com.example.api.services.bus.dto.GetListBusOutput;
import com.example.api.services.bus.dto.GetListManipulateBusOutPut;
import com.example.api.services.bus.dto.ListBusFilterParam;
import com.example.api.services.bus.dto.AddBusInput;
import com.example.api.services.bus.dto.UpdateBusEmployeeInput;
import com.example.api.services.bus.dto.UpdateBusInput;
import com.example.shared.db.entities.Account;
import com.example.shared.db.entities.Bus;
import com.example.shared.enumeration.EmployeeRole;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BusService {
    Page<GetListBusOutput> getListBus(ListBusFilterParam filterParam, Pageable pageable);

    GetBusDetailOutput getBus(Long busId);

    void addBus(AddBusInput input);

    void updateBus(UpdateBusInput input);

    void deleteBus(Long id);

    List<Bus> getAvailableBuses(EmployeeRole role, String numberPlate);

    Page<GetListManipulateBusOutPut> getListManipulateBusPage(BusManipulateParam param,
                                                          Pageable pageable);

    void updateBusEmployee(UpdateBusEmployeeInput input, Account account);
}
