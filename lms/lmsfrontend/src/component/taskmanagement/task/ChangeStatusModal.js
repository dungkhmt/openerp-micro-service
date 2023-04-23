import * as React from 'react';
import {useEffect, useState} from 'react';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, MenuItem, TextField} from '@mui/material';
import {request} from "../../../api";
import DateTimePickerBasic from '../datetimepicker/DateTimePickerBasic';
import {successNoti} from "utils/notification";

const ChangeStatusModal = ({
                             open,
                             handleClose,
                             taskId,
                             projectId,
                             partyIdDf,
                             statusIdDf,
                             dueDateDf,
                             onLoadTask
                           }) => {

  const [partyId, setPartyId] = useState(partyIdDf);
  const [persons, setPersons] = useState([]);
  const [dueDate, setDueDate] = useState(dueDateDf);
  const [statusId, setStatusId] = useState(statusIdDf);
  const [listStatus, setListStatus] = useState([]);

  useEffect(() => {
    request('get', `/projects/${projectId}/members`, res => {
      setPersons(res.data);
    }, err => {
      console.log(err);
    });

    request('get', `/task-status-list`, res => {
      setListStatus(res.data);
    }, err => {
      console.log(err);
    });


  }, []);

  const onUpdated = () => {
    let dataForm = {
      statusId: statusId,
      partyId: partyId,
      dueDate: dueDate
    }

    request(
      'put',
      `/tasks/${taskId}/status`,
      (res) => {
        successNoti("Cập nhật trạng thái thành công!", true);
        handleClose();
        onLoadTask();
      },
      (err) => {
        console.log(err);
      },
      dataForm
    )
  }

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
              >
                {listStatus.map((item) => (
                  <MenuItem key={item.statusId} value={item.statusId}>{item.description}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <TextField
                select
                fullWidth
                label={"Gán cho"}
                defaultValue=""
                value={partyId}
                onChange={(e) => setPartyId(e.target.value)}
                required
              >
                {persons.map((item) => (
                  <MenuItem key={item.partyId} value={item.partyId}>{item.userLoginId} ({item.fullName})</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={12}>
              <DateTimePickerBasic
                label={"Chọn thời hạn"}
                onChange={(date) => {
                  setDueDate(date)
                }}
                value={dueDate}
              />
            </Grid>
          </Grid>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} variant="contained" color="success">Hủy</Button>
          <Button onClick={onUpdated} variant="contained" color="primary">Cập nhật</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}

export default ChangeStatusModal;