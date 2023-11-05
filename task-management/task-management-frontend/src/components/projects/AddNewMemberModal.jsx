import {
  Autocomplete,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
  Typography,
} from "@mui/material";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { errorNoti, successNoti } from "../../utils/notification";
import { request } from "../../api";

const AddNewMemberModal = ({
  open,
  handleClose,
  projectName,
  projectId,
  callBackIsLoadMember,
}) => {
  const [persons, setPersons] = useState([]);
  const [person, setPerson] = useState(null);

  useEffect(() => {
    request(
      "get",
      "/task-persons",
      (res) => {
        setPersons(res.data);
        setPerson(res.data[0]);
      },
      (err) => {
        console.log(err);
      }
    );
  }, []);

  const handleAddNew = () => {
    const dataForm = {
      projectId: projectId,
      memberId: person.id,
    };
    request(
      "post",
      `/projects/${projectId}/members`,
      (res) => {
        if (res.data.error) {
          errorNoti(res.data.error, true);
        } else {
          successNoti("Đã thêm mới thành công!", true);
          callBackIsLoadMember();
          handleClose();
        }
      },
      (err) => {
        console.log(err);
      },
      dataForm
    );
  };

  return (
    <Box minWidth={"500px"}>
      <Dialog open={open} onClose={handleClose} fullWidth={true}>
        <DialogTitle>Thêm thành viên mới</DialogTitle>
        <DialogContent>
          <Box>
            <Box mb={2}>
              <Typography variant="body1" color="primary">
                Chọn thành viên
              </Typography>
            </Box>
            <Box mb={3}>
              <Autocomplete
                disablePortal
                options={persons}
                value={person}
                onChange={(e, value) => setPerson(value)}
                getOptionLabel={(option) => {
                  return `${option.id} (${option.firstName} ${option.lastName})`;
                }}
                fullWidth
                renderInput={(params) => (
                  <TextField {...params} label="Danh mục" placeholder="" />
                )}
              />
            </Box>
            <Box mb={2}>
              <Typography variant="body1" color="primary">
                Tới dự án:
              </Typography>
              <Typography variant="body1">{projectName}</Typography>
            </Box>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} variant="contained" color="success">
            Hủy
          </Button>
          <Button onClick={handleAddNew} variant="contained" color="primary">
            Thêm mới
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

AddNewMemberModal.propTypes = {
  open: PropTypes.bool,
  handleClose: PropTypes.func,
  projectName: PropTypes.string,
  projectId: PropTypes.string,
  callBackIsLoadMember: PropTypes.func,
};

export default AddNewMemberModal;
