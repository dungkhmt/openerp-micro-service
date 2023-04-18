CREATE TABLE whiteboard
(
    id    VARCHAR(60) NOT NULL,
    name     VARCHAR(60),
    data                  text,
    total_page         integer,
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    class_session_id uuid,
    last_modified_date TIMESTAMP,
    created_by      VARCHAR(60),
    last_modified_by VARCHAR(60),

    CONSTRAINT whiteboard_id PRIMARY KEY (id),
    constraint fk_whiteboard_session foreign key(class_session_id) references edu_class_session(session_id)
);

CREATE TABLE whiteboard_data
(
    id                  uuid,
    type               VARCHAR(60),
    data                  VARCHAR(1000),
    whiteboard_id      VARCHAR(60),
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP,
    created_by      VARCHAR(60),
    last_modified_by VARCHAR(60),
    CONSTRAINT whiteboard_data_id PRIMARY KEY (id),
    CONSTRAINT fk_whiteboard FOREIGN KEY (whiteboard_id) REFERENCES whiteboard(id)
);


CREATE TABLE user_whiteboard
(
    id          uuid,
    user_id     varchar(60),
    role_id varchar(100),
    status_id varchar(100),
    whiteboard_id    varchar(60),
    CONSTRAINT user_whiteboard_id PRIMARY KEY (id),
    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id) REFERENCES user_login(user_login_id),
    CONSTRAINT fk_whiteboard_id
        FOREIGN KEY (whiteboard_id) REFERENCES whiteboard(id)
);
