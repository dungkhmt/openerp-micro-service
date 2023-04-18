

create table lake(
    lake_id varchar(100),
    lake_name varchar(500),
    latitude varchar(30) not null,
    longitude varchar(30) not null,

    cap_cong_trinh varchar(20),
    dien_tich_luu_vuc varchar(20),
    muc_dam_bao_tuoi varchar(20),
    dien_tich_tuoi varchar(20),
    muc_nuoc_chet varchar(20),
    muc_nuoc_dang_binh_thuong varchar(20),
    muc_nuoc_lu_thiet_ke varchar(20),
    muc_nuoc_lu_kiem_tra varchar(20),
    dung_tich_toan_bo varchar(20),
    dung_tich_huu_ich varchar(20),
    dung_tich_chet varchar(20),
    luu_luong_xa_lu_thiet_ke varchar(20),
    luu_luong_xa_lu_kiem_tra varchar(20),

    muc_nuoc_ho varchar(20),
    dung_tich_ho varchar(20),
    dong_chay_den varchar(20),
    luong_mua_trung_binh varchar(20),
    do_mo_tran_so_1 varchar(20),
    do_mo_tran_so_2 varchar(20),
    do_mo_tran_so_3 varchar(20),
    do_mo_tran_so_4 varchar(20),
    do_mo_tran_so_5 varchar(20),

    luu_luong_tran_so_1 varchar(20),
    luu_luong_tran_so_2 varchar(20),
    luu_luong_tran_so_3 varchar(20),
    luu_luong_tran_so_4 varchar(20),
    luu_luong_tran_so_5 varchar(20),

    do_mo_cong varchar(20),
    muc_nuoc_kenh varchar(20),
    luu_luong_cong varchar(20),
    tong_luu_luong_xa varchar(20),

    luong_mua varchar(20),
    nhiet_do varchar(20),
    do_am varchar(20),
    toc_do_gio varchar(20),
    huong_gio varchar(20),
    buc_xa_mat_troi varchar(20),


    last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

	constraint pk_lake primary key (lake_id)
);

create table lake_role(
    lake_role_id uuid not null default uuid_generate_v1(),
    lake_id varchar(100),
    user_login_id varchar(60),
    from_date timestamp,
    thru_date timestamp,
    role_type_id varchar(60),
    last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,

    constraint pk_lake_role primary key (lake_role_id),
    constraint fk_lake_role_lake_id foreign key(lake_id) references lake(lake_id),
    constraint fk_lake_role_user_login_id foreign key(user_login_id) references user_login(user_login_id),
    constraint fk_lake_role_role_type_id foreign key(role_type_id) references role_type(role_type_id)
);

create table lake_sensing_information(
    lake_id varchar(100),
    time_point timestamp,
    
    last_updated_stamp timestamp NULL,
	created_stamp timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    constraint pk_
);
