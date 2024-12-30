package openerp.openerpresourceserver.constant;

public enum SalaryType {
    MONTHLY,
    WEEKLY,
    HOURLY
    ;
    public static SalaryType getDefaultValue() {
        return MONTHLY;
    }
}
