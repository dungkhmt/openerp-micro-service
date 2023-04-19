import React, {useEffect, useState} from "react";
import {useHistory} from "react-router";
import {Link, useParams} from 'react-router-dom';
import {Box, Button, Typography} from '@mui/material';

import {TextField} from '@material-ui/core';
import {boxChildComponent, boxComponentStyle} from '../ultis/constant';
import {request} from "../../../api";
import BasicAlert from "../alert/BasicAlert";
import {useForm} from "react-hook-form";

export default function CreateProject() {

  const { projectId, type } = useParams();

  const [openModal, setOpenModal] = useState(false);
  const [typeAlert, setTypeAlert] = useState("success");
  const [message, setMessage] = useState("Đã thêm mới thành công");

  const {
    register,
    handleSubmit,
    errors,
    setValue
  } = useForm();

  const history = useHistory();

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }

    setOpenModal(false);
  };

  useEffect(() => {
    request(
      "get",
      `/projects/${projectId}`,
      (res) => {
        console.log(res.data);
        setValue('name', res.data.name);
        setValue('code', res.data.code);
      },
      {}
    );
  }, []);

  const onSubmit = (data) => {
    console.log(data);
    request(
      "post",
      "/projects",
      (res) => {
        setOpenModal(true);
        setTypeAlert("success");
        setMessage("Đã thêm mới thành công");
        setTimeout(() => {
          history.push('/taskmanagement/project/list');
        }, 1000);
      },
      {},
      data
    );
  }

  const onUpdate = (data) => {
    console.log(data);
    request(
      "put",
      `/projects/${projectId}`,
      (res) => {
        setOpenModal(true);
        setTypeAlert("success");
        setMessage("Đã cập nhật thành công");
        setTimeout(() => {
          history.push('/taskmanagement/project/list');
        }, 1000);
      },
      {},
      data
    );
  }

  return (
    <>
      <Box sx={boxComponentStyle}>
        <Box sx={{ mb: 3 }}>
          <Typography variant="h4" mb={4} component={'h4'}>
            {type === 'create' ? ("Thêm dự án mới") : ("Chỉnh sửa dự án")}
          </Typography>
        </Box>
        <Box sx={boxChildComponent}>
          <Box>
            <Box mb={3}>
              <Typography variant="body1">
                Tên dự án *
              </Typography>
              <TextField
                fullWidth={true}
                autoFocus
                placeholder="Điền tên dự án ..."
                variant="standard"
                name="name"
                inputRef={register({ required: "Thiếu tên dự án!" })}
                helperText={errors.name?.message}
              />
            </Box>
            <Box mb={3}>
              <Typography variant="body1">
                Mã dự án *
              </Typography>
              <TextField
                fullWidth={true}
                placeholder="Điền mã dự án ..."
                variant="standard"
                name="code"
                inputRef={register({
                  required: "Thiếu mã dự án!"
                })}
                helperText={errors.code?.message}
              />
            </Box>
            <Box mb={3} backgroundColor={"#EEE"}>
              <Typography paragraph={true} px={2}>
                Mã dự án là một chuỗi kí tự được chỉ định cho dự án đó, vì vậy nó nên là duy nhất!
              </Typography>
            </Box>
            <Box>
              {type === 'create' ?
                <Button variant="contained" color="primary" sx={{ mr: 2 }} onClick={handleSubmit(onSubmit)}>Thêm mới</Button>
                :
                <Button variant="contained" color="primary" sx={{ mr: 2 }} onClick={handleSubmit(onUpdate)}>Cập nhật</Button>
              }
              <Button variant="contained" color="success"><Link to={"/taskmanagement/project/list"} style={{textDecoration: 'none', color: '#fff'}}>Hủy</Link></Button>
            </Box>
          </Box>
        </Box>
      </Box>
      <BasicAlert
        openModal={openModal}
        handleClose={handleClose}
        typeAlert={typeAlert}
        message={message}
      />
    </>
  );
}
