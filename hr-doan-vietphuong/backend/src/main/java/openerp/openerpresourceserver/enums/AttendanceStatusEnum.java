package openerp.openerpresourceserver.enums;

import lombok.Getter;
import openerp.openerpresourceserver.util.DateUtil;

import java.time.LocalTime;

@Getter
public enum AttendanceStatusEnum {
    NOT_CLOCK_OUT {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return clockIn.equals(clockOut) && DateUtil.isBeforeOrEqual(clockIn, startTime);
        }
    },
    NOT_CLOCK_IN {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return clockIn.equals(clockOut) && DateUtil.isAfterOrEqual(clockOut, endTime);
        }
    },
    LATE_IN {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return !clockIn.equals(clockOut) &&
                    DateUtil.isAfterOrEqual(clockOut, endTime) &&
                    !DateUtil.isBeforeOrEqual(clockIn, startTime);
        }
    },
    EARLY_OUT {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return !clockIn.equals(clockOut) &&
                    !DateUtil.isAfterOrEqual(clockOut, endTime) &&
                    DateUtil.isBeforeOrEqual(clockIn, startTime);
        }

    },
    NOT_FULL {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return clockIn.isAfter(startTime) &&
                    clockOut.isBefore(endTime);
        }

    },
    FULL {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return !clockIn.equals(clockOut) &&
                    DateUtil.isBeforeOrEqual(clockIn, startTime) &&
                    DateUtil.isAfterOrEqual(clockOut, endTime);
        }

    },
    NO_RECORD {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return false;
        }
    },
    HOLIDAY {
        @Override
        public boolean matches(LocalTime clockIn,
                               LocalTime clockOut,
                               LocalTime startTime,
                               LocalTime endTime) {
            return false;
        }
    },
    WEEKEND {
        @Override
        public boolean matches(final LocalTime clockIn, final LocalTime clockOut, final LocalTime startTime, final LocalTime endTime) {
            return false;
        }
    };

    public static AttendanceStatusEnum determineStatus(LocalTime clockIn,
                                                       LocalTime clockOut,
                                                       LocalTime startTime,
                                                       LocalTime endTime) {
        for (AttendanceStatusEnum status : values()) {
            if (status.matches(clockIn, clockOut, startTime, endTime)) {
                return status;
            }
        }
        return NO_RECORD;
    }

    public abstract boolean matches(LocalTime clockIn,
                                    LocalTime clockOut,
                                    LocalTime startTime,
                                    LocalTime endTime);

}
