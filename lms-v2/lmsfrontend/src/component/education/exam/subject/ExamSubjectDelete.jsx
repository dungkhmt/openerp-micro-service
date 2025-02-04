import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Input
} from "@material-ui/core";
import {request} from "../../../../api";
import {toast} from "react-toastify";
import {DialogActions} from "@mui/material";

function ExamSubjectDelete(props) {

  const { open, setOpen, id , onReload} = props;

  const handleDelete = () =>{
    const body = {
      id: id
    }
    request(
      "post",
      `/exam-subject/delete`,
      (res) => {
        if(res.data.resultCode === 200){
          onReload()
          toast.success(res.data.resultMsg)
          setOpen(false)
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const closeDialog = () => {
    setOpen(false)
  }

  return (
    <div>
      <Dialog open={open}>
        <DialogTitle>Xoá môn học</DialogTitle>
        <DialogContent>
          <p style={{marginBottom: "30px"}}>Bạn có chắc chắn muốn xoá môn học?</p>
        </DialogContent>
        <DialogActions>
          <Button
            variant="contained"
            onClick={closeDialog}
          >
            Hủy
          </Button>
          <Button
            variant="contained"
            color="primary"
            style={{marginLeft: "15px"}}
            onClick={handleDelete}
          >
            Lưu
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

const screenName = "MENU_EXAM_SUBJECT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default ExamSubjectDelete;
