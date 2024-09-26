package openerp.openerpresourceserver.labtimetabling.entity.autoscheduling;

import lombok.Data;

@Data
public class AutoSchedulingVar {
    private Long class_id;
    private Long lesson;
    private Long period;
    private Long room_id;
    private Long week;

    private AutoSchedulingVar(Builder builder) {
        this.class_id = builder.class_id;
        this.lesson = builder.lesson;
        this.period = builder.period;
        this.room_id = builder.room_id;
        this.week = builder.week;
    }

    // Getter methods for all fields (class_id, lesson, period, room_id, week)

    public static class Builder {
        private Long class_id;
        private Long lesson;
        private Long period;
        private Long room_id;
        private Long week;

        public Builder() {
            class_id = 0L;
            lesson = 0L;
            period = 0L;
            room_id = 0L;
            week = 0L;

        }

        public Builder setClassId(Long class_id) {
            this.class_id = class_id;
            return this;
        }

        public Builder setLesson(Long lesson) {
            this.lesson = lesson;
            return this;
        }

        public Builder setPeriod(Long period) {
            this.period = period;
            return this;
        }

        public Builder setRoomId(Long room_id) {
            this.room_id = room_id;
            return this;
        }

        public Builder setWeek(Long week) {
            this.week = week;
            return this;
        }

        public AutoSchedulingVar build() {
            return new AutoSchedulingVar(this);
        }
    }
}
