CREATE TABLE IF NOT EXISTS room
(
    id uuid NOT NULL,
    room_name text COLLATE pg_catalog."default" NOT NULL,
    host_id varchar (100) not null,
    open_in date,
    close_in date,
    constraint room_pkey PRIMARY KEY (id),
    constraint fk_room_host_id FOREIGN KEY (host_id)
    	REFERENCES user_login(user_login_id)
);

CREATE TABLE IF NOT EXISTS room_participant
(
    id uuid NOT NULL,
    room_id uuid NOT NULL,
    participant_id  varchar (100) not null,
    peer_id text NOT NULL,
    is_active text NOT NULL DEFAULT 1,
    CONSTRAINT room_participant_pkey PRIMARY KEY (id),
    CONSTRAINT room_participant_room_id FOREIGN KEY (room_id)
    	REFERENCES public.room (id),
    CONSTRAINT room_participant_participant_id FOREIGN KEY (participant_id)
    	REFERENCES public.user_login (user_login_id)
);
