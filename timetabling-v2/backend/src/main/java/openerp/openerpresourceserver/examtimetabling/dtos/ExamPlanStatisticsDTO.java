package openerp.openerpresourceserver.examtimetabling.dtos;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ExamPlanStatisticsDTO {
    private UUID examPlanId;
    private String examPlanName;
    private long totalExamClasses;
    private List<SchoolStatistic> topSchools;
    private SchoolStatistic otherSchools;
    
    @Data
    public static class SchoolStatistic {
        private String schoolName;
        private long count;
        private double percentage;
    }
}
