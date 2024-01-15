package openerp.openerpresourceserver;

import openerp.openerpresourceserver.model.entity.ClassPeriod;
import openerp.openerpresourceserver.model.entity.WeekDay;
import openerp.openerpresourceserver.repo.ClassPeriodRepo;
import openerp.openerpresourceserver.repo.WeekDayRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitialization implements ApplicationListener<ContextRefreshedEvent> {

    private final ClassPeriodRepo classPeriodRepository;
    private final WeekDayRepo weekdayRepository;

    @Autowired
    public DataInitialization(ClassPeriodRepo classPeriodRepository, WeekDayRepo weekdayRepository) {
        this.classPeriodRepository = classPeriodRepository;
        this.weekdayRepository = weekdayRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        initializeClassPeriod();
        initializeWeekday();
    }

    private void initializeClassPeriod() {
        if (classPeriodRepository.count() == 0) {
            for (int i = 1; i <= 6; i++) {
                ClassPeriod classPeriod = new ClassPeriod();
                classPeriod.setClassPeriod(i+"");
                classPeriodRepository.save(classPeriod);
            }
        }
    }

    private void initializeWeekday() {
        if (weekdayRepository.count() == 0) {
            for (int i = 2; i <= 6; i++) {
                WeekDay weekday = new WeekDay();
                weekday.setWeekDay(i+"");
                weekdayRepository.save(weekday);
            }
        }
    }
}

