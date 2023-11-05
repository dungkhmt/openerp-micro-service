import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  MenuItem,
  TextField,
} from "@mui/material";
import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import { successNoti } from "../../utils/notification";
import { request } from "../../api";
import DateTimePickerBasic from "../datetimepicker/DateTimePickerBasic";

const ChangeStatusModal = ({
  open,
  handleClose,
  taskId,
  projectId,
  partyIdDf,
  statusIdDf,
  dueDateDf,
  onLoadTask,
}) => {
  const [partyId, setPartyId] = useState(partyIdDf);
  const [persons, setPersons] = useState([]);
  const [dueDate, setDueDate] = useState(new Date(dueDateDf));
  const [statusId, setStatusId] = useState(statusIdDf);
  const [listStatus, setListStatus] = useState([]);

  useEffect(() => {
    request(
      "get",
      `/projects/${projectId}/members`,
      (res) => {
        setPersons(res.data);
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      `/task-status-list`,
      (res) => {
        setListStatus(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
  }, []);

  const onUpdated = () => {
    let dataForm = {
      statusId: statusId,
      assignee: partyId,
      dueDate: dueDate,
    };

    request(
      "put",
      `/tasks/${taskId}/status`,
      () => {
        successNoti("Cập nhật trạng thái thành công!", true);
        handleClose();
        onLoadTask();
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
        <DialogTitle>Thay đổi trạng thái</DialogTitle>
        <DialogContent>
          <Grid container spacing={3}>
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label={"Trạng thái"}
                defaultValue="Đang xử lý"
                value={statusId}
                onChange={(e) => setStatusId(e.target.value)}
                required
                sx={{ mt: 1 }}
              >
                {listStatus.map((item) => (
                  <MenuItem key={item.statusId} value={item.statusId}>
                    {item.description}
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label={"Gán cho"}
                value={partyId}
                onChange={(e) => setPartyId(e.target.value)}
                required
              >
                {persons.map((item) => (
                  <MenuItem key={item.id} value={item.id}>
                    {item.id} ({item.firstName} {item.lastName})
                  </MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <DateTimePickerBasic
                label={"Chọn thời hạn"}
                onChange={(date) => {
                  setDueDate(date);
                }}
                value={dueDate}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} variant="contained" color="success">
            Hủy
          </Button>
          <Button onClick={onUpdated} variant="contained" color="primary">
            Cập nhật
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

ChangeStatusModal.propTypes = {
  open: PropTypes.bool.isRequired,
  handleClose: PropTypes.func.isRequired,
  taskId: PropTypes.string.isRequired,
  projectId: PropTypes.string.isRequired,
  partyIdDf: PropTypes.string.isRequired,
  statusIdDf: PropTypes.string.isRequired,
  dueDateDf: PropTypes.string.isRequired,
  onLoadTask: PropTypes.func.isRequired,
};

export default ChangeStatusModal;
