CREATE TABLE "schedule"(
    "id" BIGINT NOT NULL,
    "to_school_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "out_school_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "student_id" BIGINT NOT NULL,
    "day_of_week" VARCHAR(255) CHECK
        ("day_of_week" IN('')) NOT NULL,
        "description" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "schedule" ADD PRIMARY KEY("id");
CREATE TABLE "pickup_point"(
    "id" BIGINT NOT NULL,
    "address" VARCHAR(255) NOT NULL,
    "longlitude" BIGINT NOT NULL,
    "lattitude" BIGINT NOT NULL
);
ALTER TABLE
    "pickup_point" ADD PRIMARY KEY("id");
CREATE TABLE "ride_pickup_point"(
    "id" BIGINT NOT NULL,
    "pickup_point_id" BIGINT NOT NULL,
    "ride_id" BIGINT NOT NULL
);
ALTER TABLE
    "ride_pickup_point" ADD PRIMARY KEY("id");
CREATE TABLE "student_pickup_point"(
    "id" BIGINT NOT NULL,
    "student_id" BIGINT NOT NULL,
    "pickup_point_id" BIGINT NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('')) NOT NULL
);
ALTER TABLE
    "student_pickup_point" ADD PRIMARY KEY("id");
CREATE TABLE "student"(
    "id" BIGINT NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "avatar" VARCHAR(255) NOT NULL,
    "parent_id" BIGINT NOT NULL,
    "dob" DATE NOT NULL
);
ALTER TABLE
    "student" ADD PRIMARY KEY("id");
CREATE TABLE "parent"(
    "id" BIGINT NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "avatar" VARCHAR(255) NOT NULL
);
ALTER TABLE
    "parent" ADD PRIMARY KEY("id");
CREATE TABLE "account"(
    "id" BIGINT NOT NULL,
    "username" VARCHAR(255) NOT NULL,
    "password" BIGINT NOT NULL,
    "role" BIGINT NOT NULL,
    "created_at" DATE NOT NULL,
    "updated_at" DATE NOT NULL
);
ALTER TABLE
    "account" ADD PRIMARY KEY("id");
CREATE TABLE "ride_history"(
    "id" BIGINT NOT NULL,
    "ride_id" BIGINT NOT NULL,
    "bus_id" BIGINT NULL,
    "start_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "end_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "start_from" VARCHAR(255) NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('')) NOT NULL
);
ALTER TABLE
    "ride_history" ADD PRIMARY KEY("id");
CREATE TABLE "ride"(
    "id" BIGINT NOT NULL,
    "bus_id" BIGINT NOT NULL,
    "start_at" TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,
    "end_at" BIGINT NOT NULL,
    "start_from" VARCHAR(255) NOT NULL,
    "current_longtitude" BIGINT NOT NULL,
    "current_lattitude" BIGINT NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('')) NOT NULL
);
ALTER TABLE
    "ride" ADD PRIMARY KEY("id");
CREATE TABLE "employee"(
    "id" BIGINT NOT NULL,
    "name" VARCHAR(255) NOT NULL,
    "avatar" VARCHAR(255) NOT NULL,
    "age" BIGINT NOT NULL
);
ALTER TABLE
    "employee" ADD PRIMARY KEY("id");
CREATE TABLE "request_registration"(
    "id" BIGINT NOT NULL,
    "student_id" BIGINT NOT NULL,
    "parent_id" BIGINT NOT NULL,
    "pickup_point_id" BIGINT NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('')) NOT NULL,
        "created_at" DATE NOT NULL,
        "updated_at" DATE NOT NULL
);
ALTER TABLE
    "request_registration" ADD PRIMARY KEY("id");
CREATE TABLE "review"(
    "id" BIGINT NOT NULL,
    "aggregate_code" VARCHAR(255) NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    "score" BIGINT NOT NULL,
    "user_id" BIGINT NOT NULL
);
ALTER TABLE
    "review" ADD PRIMARY KEY("id");
CREATE TABLE "bus"(
    "id" BIGINT NOT NULL,
    "number_plate" VARCHAR(255) NOT NULL,
    "seat_number" BIGINT NOT NULL,
    "driver_id" BIGINT NOT NULL,
    "driver_mate_id" BIGINT NOT NULL,
    "status" VARCHAR(255) CHECK
        ("status" IN('')) NOT NULL
);
ALTER TABLE
    "bus" ADD PRIMARY KEY("id");
ALTER TABLE
    "student_pickup_point" ADD CONSTRAINT "student_pickup_point_student_id_foreign" FOREIGN KEY("student_id") REFERENCES "student"("id");
ALTER TABLE
    "ride_pickup_point" ADD CONSTRAINT "ride_pickup_point_pickup_point_id_foreign" FOREIGN KEY("pickup_point_id") REFERENCES "pickup_point"("id");
ALTER TABLE
    "student_pickup_point" ADD CONSTRAINT "student_pickup_point_pickup_point_id_foreign" FOREIGN KEY("pickup_point_id") REFERENCES "pickup_point"("id");
ALTER TABLE
    "request_registration" ADD CONSTRAINT "request_registration_pickup_point_id_foreign" FOREIGN KEY("pickup_point_id") REFERENCES "pickup_point"("id");
ALTER TABLE
    "student" ADD CONSTRAINT "student_parent_id_foreign" FOREIGN KEY("parent_id") REFERENCES "parent"("id");
ALTER TABLE
    "bus" ADD CONSTRAINT "bus_driver_id_foreign" FOREIGN KEY("driver_id") REFERENCES "employee"("id");
ALTER TABLE
    "ride" ADD CONSTRAINT "ride_bus_id_foreign" FOREIGN KEY("bus_id") REFERENCES "bus"("id");
ALTER TABLE
    "request_registration" ADD CONSTRAINT "request_registration_student_id_foreign" FOREIGN KEY("student_id") REFERENCES "student"("id");
ALTER TABLE
    "schedule" ADD CONSTRAINT "schedule_student_id_foreign" FOREIGN KEY("student_id") REFERENCES "student"("id");
ALTER TABLE
    "request_registration" ADD CONSTRAINT "request_registration_parent_id_foreign" FOREIGN KEY("parent_id") REFERENCES "parent"("id");
ALTER TABLE
    "ride_pickup_point" ADD CONSTRAINT "ride_pickup_point_ride_id_foreign" FOREIGN KEY("ride_id") REFERENCES "ride"("id");
ALTER TABLE
    "ride_history" ADD CONSTRAINT "ride_history_ride_id_foreign" FOREIGN KEY("ride_id") REFERENCES "ride"("id");
ALTER TABLE
    "account" ADD CONSTRAINT "account_id_foreign" FOREIGN KEY("id") REFERENCES "parent"("id");
ALTER TABLE
    "bus" ADD CONSTRAINT "bus_driver_mate_id_foreign" FOREIGN KEY("driver_mate_id") REFERENCES "employee"("id");
ALTER TABLE
    "account" ADD CONSTRAINT "account_id_foreign" FOREIGN KEY("id") REFERENCES "employee"("id");