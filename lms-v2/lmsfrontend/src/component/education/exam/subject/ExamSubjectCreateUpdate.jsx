import React, {useEffect, useState} from 'react';
import {Box, Button, Card, CardContent, CircularProgress, Input} from "@material-ui/core";
import {request} from "../../../../api";
import {FormControl, MenuItem, Select} from "@mui/material";
import DateFnsUtils from "@date-io/date-fns";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import CardActions from "@material-ui/core/CardActions";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {toast} from "react-toastify";
import {makeStyles} from "@material-ui/core/styles";
import {useHistory} from "react-router-dom";
import {useLocation} from "react-router";
import withScreenSecurity from "../../../withScreenSecurity";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};
const baseColumn = {
  sortable: false,
};

function ExamSubjectCreateUpdate(props) {

  const statusList = [
    {
      value: 'INACTIVE',
      name: 'Không hoạt động'
    },
    {
      value: 'ACTIVE',
      name: 'Hoạt động'
    }
  ]

  const classes = useStyles();
  const history = useHistory();
  const location = useLocation();
  const data = location.state?.data
  const isCreate = location.state?.isCreate

  if(isCreate === undefined){
    window.location.href = '/exam/subject';
  }

  const [code, setCode] = useState(data?.code);
  const [name, setName] = useState(data?.name);
  const [status, setStatus] = useState(data?.status);
  const [isLoading, setIsLoading] = useState(false);

  const handleSave = () =>{
    const body = {
      code: code,
      name: name,
      status: status
    }
    validateBody(body)

    setIsLoading(true)
    request(
      "post",
      isCreate ? `/exam-subject/create` : '/exam-subject/update',
      (res) => {
        if(res.status === 200){
          if(res.data.resultCode === 200){
            toast.success(res.data.resultMsg)
            setIsLoading(false)
            history.push("/exam/subject")
          }else{
            toast.error(res.data.resultMsg)
            setIsLoading(false)
          }
        }else {
          toast.error(res)
          setIsLoading(false)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const validateBody = (body) => {
    if(body.code == null || body.code === ''){
      toast.error('Mã môn học không được bỏ trống')
      return
    }
    if(body.name == null || body.name === ''){
      toast.error('Tên môn học không được bỏ trống')
      return
    }
  }

  const handleKeyPress = (event) => {
    const regex = /^[a-zA-Z0-9]+$/
    if (!regex.test(event.key)) {
      event.preventDefault();
    }
  }

  return (
    <div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              {
                isCreate ? (<h4 style={{margin: 0, padding: 0}}>Thêm mới môn học</h4>) : (<h4 style={{margin: 0, padding: 0}}>Cập nhật môn học</h4>)
              }
            </Typography>
            <form className={classes.root} noValidate autoComplete="off">
              <div>
                <div>
                  <TextField
                    autoFocus
                    required
                    onKeyPress={handleKeyPress}
                    disabled={!isCreate}
                    id="examSubjectCode"
                    label="Mã môn học"
                    placeholder="Nhập mã môn học"
                    value={code}
                    onChange={(event) => {
                      setCode(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />

                  <TextField
                    autoFocus
                    required
                    id="examSubjectName"
                    label="Tên môn học"
                    placeholder="Nhập tên môn học"
                    value={name}
                    style={{width: '50%'}}
                    onChange={(event) => {
                      setName(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </div>

                <div>
                  <TextField
                    required
                    autoFocus
                    id="ExamSubjectStatus"
                    select
                    label="Trạng thái"
                    value={status}
                    onChange={(event) => {
                      setStatus(event.target.value);
                    }}
                  >
                    {
                      statusList.map(item => {
                        return (
                          <MenuItem value={item.value}>{item.name}</MenuItem>
                        )
                      })
                    }
                  </TextField>
                </div>
              </div>
            </form>
          </CardContent>
          <CardActions style={{justifyContent: 'flex-end'}}>
            <Button
              variant="contained"
              onClick={() => history.push("/exam/subject")}
            >
              Hủy
            </Button>
            <Button
              disabled={isLoading}
              variant="contained"
              color="primary"
              style={{marginLeft: "15px"}}
              onClick={handleSave}
              type="submit"
            >
              {isLoading ? <CircularProgress/> : "Lưu"}
            </Button>
          </CardActions>
        </Card>
      </MuiPickersUtilsProvider>
    </div>
  );
}

const screenName = "MENU_EXAM_SUBJECT";
export default withScreenSecurity(ExamSubjectCreateUpdate, screenName, true);
// export default ExamCreateUpdate;
