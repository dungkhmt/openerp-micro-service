package openerp.openerpresourceserver;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import com.google.ortools.util.Domain;
import lombok.extern.log4j.Log4j2;
import openerp.openerpresourceserver.generaltimetabling.algorithms.V2ClassScheduler;
import openerp.openerpresourceserver.generaltimetabling.helper.ClassTimeComparator;
import openerp.openerpresourceserver.generaltimetabling.helper.ExcelHelper;
import openerp.openerpresourceserver.generaltimetabling.helper.GeneralExcelHelper;
import openerp.openerpresourceserver.generaltimetabling.helper.MassExtractor;
import openerp.openerpresourceserver.generaltimetabling.model.dto.request.general.V2UpdateClassScheduleRequest;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Classroom;
import openerp.openerpresourceserver.generaltimetabling.model.entity.Group;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.GeneralClass;
import openerp.openerpresourceserver.generaltimetabling.model.entity.general.RoomReservation;
import openerp.openerpresourceserver.generaltimetabling.model.entity.occupation.RoomOccupation;
import openerp.openerpresourceserver.generaltimetabling.repo.ClassroomRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.GeneralClassRepository;
import openerp.openerpresourceserver.generaltimetabling.repo.GroupRepo;
import openerp.openerpresourceserver.generaltimetabling.repo.RoomOccupationRepo;
import openerp.openerpresourceserver.generaltimetabling.service.GeneralClassService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@SpringBootTest
class OpenerpResourceServerApplicationTests {

    private final GeneralClassRepository gcoRepo;
    private final ClassroomRepo classroomRepo;
    private final RoomOccupationRepo roomOccupationRepo;
    private final GroupRepo groupRepo;

    private final GeneralClassService gService;
    private final int minPeriod = 2;
    private final int maxPeriod = 4;

    private GeneralExcelHelper excelHelper;

    @Autowired
    OpenerpResourceServerApplicationTests(GeneralExcelHelper excelHelper, GeneralClassRepository gcoRepo, ClassroomRepo classroomRepo, RoomOccupationRepo roomOccupationRepo, GroupRepo groupRepo, GeneralClassService gService) {
        this.gcoRepo = gcoRepo;
        this.classroomRepo = classroomRepo;
        this.roomOccupationRepo = roomOccupationRepo;
        this.groupRepo = groupRepo;
        this.gService = gService;
        this.excelHelper = excelHelper;
    }

    @Test
    void printROExportRo() {
        excelHelper.convertRoomOccupationToExcel(roomOccupationRepo.findAllBySemesterAndWeekIndex("20232", 2));
    }

}
