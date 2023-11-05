import {
  Box,
  Button,
  Grid,
  MenuItem,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router";
import { processingNoti, successNoti } from "../../utils/notification";
import { request } from "../../api";
import { boxChildComponent, boxComponentStyle } from "../utils/constant";

const AddUserToProject = () => {
  const navigate = useNavigate();

  const toastId = useRef();

  const [members, setMembers] = useState([]);
  const [projects, setProjects] = useState([]);

  const [partyId, setPartyId] = useState("");
  const [projectId, setProjectId] = useState("");

  useEffect(() => {
    request(
      "get",
      "/task-persons",
      (res) => {
        setMembers(res.data);
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      "/projects",
      (res) => {
        setProjects(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
  }, []);

  const onSubmit = () => {
    processingNoti(toastId, true);
    const data = {
      projectId: projectId,
      partyId: partyId,
    };
    request(
      "post",
      `/projects/${data.projectId}/members`,
      () => {
        successNoti("Đã thêm mới thành công!", true);
        setTimeout(() => {
          navigate(`/project/${data.projectId}/tasks`);
        }, 1000);
      },
      (err) => {
        console.log(err);
      },
      data
    );
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
                  <MenuItem key={item.partyId} value={item.partyId}>
                    {item.fullName}
                  </MenuItem>
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
                onChange={(e) => setProjectId(e.target.value)}
                required
              >
                {projects.map((item) => (
                  <MenuItem key={item.id} value={item.id}>
                    {item.name}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
          </Grid>
          <Box mb={2}>
            <Button variant="contained" color="primary" onClick={onSubmit}>
              Submit
            </Button>
            <Typography variant="caption" color="success" px={3}>
              Invited users will be added to these teams nghiatitan All Members
            </Typography>
          </Box>
        </Box>
      </Box>
    </>
  );
};

export default AddUserToProject;
