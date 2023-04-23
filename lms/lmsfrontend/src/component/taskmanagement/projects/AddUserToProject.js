import React, {useEffect, useRef, useState} from "react";
import {request} from "../../../api";
import {Box, Button, Grid, MenuItem, TextField, Typography} from "@mui/material";
import {boxChildComponent, boxComponentStyle} from "../ultis/constant";
import {useHistory} from "react-router";
import {processingNoti, successNoti} from "utils/notification";

const AddUserToProject = () => {

  const history = useHistory();

  const toastId = useRef();

  const [members, setMembers] = useState([]);
  const [projects, setProjects] = useState([]);

  const [typeAlert, setTypeAlert] = useState("success");
  const [message, setMessage] = useState("Đã thêm mới thành công");

  const [partyId, setPartyId] = useState("");
  const [projectId, setProjectId] = useState("");


  useEffect(() => {
    request('get', '/task-persons', res => {
      setMembers(res.data);
    }, err => {
      console.log(err);
    });

    request('get', '/projects', res => {
      setProjects(res.data);
    }, err => {
      console.log(err);
    });
  }, []);

  const onSubmit = () => {
    processingNoti(toastId, true);
    const data = {
      projectId: projectId,
      partyId: partyId
    }
    request(
      "post",
      `/projects/${data.projectId}/members`,
      (res) => {
        console.log(res.data);
        // setOpen(true);
        // setTypeAlert("success");
        // setMessage("Đã thêm mới thành công");
        successNoti("Đã thêm mới thành công!", true);
        setTimeout(() => {
          history.push(`/taskmanagement/project/${data.projectId}/tasks`);
        }, 1000);
      },
      (err) => {
        console.log(err);
        setOpen(true);
        setTypeAlert("error");
        setMessage(err);
      },
      data
    );
  }

  const [open, setOpen] = useState(false);

  const handleClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }

    setOpen(false);
  };

  return (
    <>
      <Box sx={boxComponentStyle}>
        <Typography variant="h4" mb={4}>
          Thêm thành viên cho dự án
        </Typography>
        <Box sx={boxChildComponent}>
          <Grid container spacing={2} mb={3}>
            <Grid item={true} xs={4}>
              <TextField
                select
                fullWidth
                label={"Danh sách thành viên"}
                defaultValue=""
                value={partyId}
                onChange={(e) => setPartyId(e.target.value)}
                required
              >
                {members.map((item) => (
                  <MenuItem key={item.partyId} value={item.partyId}>{item.fullName}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item={true} xs={4}>
              <TextField
                select
                fullWidth
                label={"Danh sách dự án"}
                defaultValue=""
                value={projectId}
                onChange={e => setProjectId(e.target.value)}
                required
              >
                {projects.map(item => (
                  <MenuItem key={item.id} value={item.id}>{item.name}</MenuItem>
                ))}
              </TextField>
            </Grid>
          </Grid>
          <Box mb={2}>
            <Button variant="contained" color="primary" onClick={onSubmit}>Submit</Button>
            <Typography variant="caption" color="success" px={3}>
              Invited users will be added to these teams
              nghiatitan All Members
            </Typography>
          </Box>
        </Box>
      </Box>
      {/* <BasicAlert
                openModal={open}
                handleClose={handleClose}
                typeAlert={typeAlert}
                message={message}
            /> */}
    </>
  );
}

export default AddUserToProject;