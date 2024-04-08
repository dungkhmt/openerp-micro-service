import React, {useState} from "react";
import {request} from "../../../../api";
import {Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from "@mui/material";
import {makeStyles} from "@material-ui/core/styles";
import {errorNoti, successNoti} from "../../../../utils/notification";
import PropTypes from "prop-types";

const useStyles = makeStyles(theme => ({
  formInput: {
    width: '100%',
    marginTop: '10px !important'
  }
}))

export default function CreateLearningSessionDlg(props) {
  const classes = useStyles();
  const { classId, open, setOpen, onCreateSuccess } = props;
  const [newLearningSession, setNewLearningSession] = useState({ sessionName: "", description: ""  })

  function setSessionName(event) {
    setNewLearningSession({
      ...newLearningSession,
      sessionName: event.target.value
    });
  }
  function setDescription(event) {
    setNewLearningSession({
      ...newLearningSession,
      description: event.target.value
    });
  }

  function createLearningSession() {
    let newSession = { classId, ...newLearningSession };
    let successHandler = res => {
      successNoti("Tạo mới buổi học thành công. Xem kết quả trên giao diện!", 3000);
      onCreateSuccess(res)
      setOpen(false);
    }
    let errorHandlers = {
      onError: (error) => errorNoti("Đã xảy ra lỗi. Vui lòng kiểm tra lại!", 3000)
    }
    request("POST", "edu/class/add-a-session-of-class", successHandler, errorHandlers, newSession);
  }

  return (
    <Dialog {...props} open={open} onClose={() => setOpen(false)} >
      <DialogTitle>Thêm buổi học</DialogTitle>

      <DialogContent>
        <TextField
          label="Tên buổi học"
          className={classes.formInput}
          onChange={setSessionName}/>
        <TextField
          label="Mô tả"
          multiline
          rows={4}
          maxRows={8}
          className={classes.formInput}
          onChange={setDescription} />
      </DialogContent>

      <DialogActions>
        <Button variant="outlined" onClick={createLearningSession}>Lưu</Button>
        <Button variant="outlined" onClick={() => setOpen(false)}>Đóng</Button>
      </DialogActions>
    </Dialog>
  );
}

const noOp = (...args) => {};

CreateLearningSessionDlg.propTypes = {
  classId: PropTypes.string.isRequired,
  open: PropTypes.bool.isRequired,
  setOpen: PropTypes.func.isRequired,
  onCreateSuccess: PropTypes.func
}

CreateLearningSessionDlg.defaultProps = {
  onCreateSuccess: noOp
}


