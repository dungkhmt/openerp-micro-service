import {
  Backdrop,
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  CardHeader,
  Fade,
  InputLabel,
  MenuItem,
  Modal,
  Select,
  TextField,
} from "@material-ui/core";
import {request} from "../../../api";

import {makeStyles} from "@material-ui/core/styles";
import Alert from "@mui/material/Alert";
import React, {useEffect, useState} from "react";
import Checkbox from '@mui/material/Checkbox';
import OutlinedInput from '@mui/material/OutlinedInput';
import ListItemText from '@mui/material/ListItemText';
import {SubmitSuccess} from "../programmingcontestFE/SubmitSuccess";

const useStyles = makeStyles((theme) => ({
  modal: {
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
  },
  card: {
    minWidth: 400,
  },
  action: {
    display: "flex",
    justifyContent: "center",
  },
  error: {
    textAlign: "center",
    color: "red",
    marginTop: theme.spacing(2),
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


export default function ModalAssignKeywordToTeacher({open, handleClose, teacherId, planId, teacherName, handleToggle}) {
  const classes = useStyles();

  const [name, setName] = useState(teacherName);
  const [keywords, setKeywords] = React.useState([]);
  const [keyword, setKeyword] = React.useState([]);
  const [teacherID, setTeacherID] = useState(teacherId);
  const [planID, setPlanID] = useState(planId);
  const [openAlert, setOpenAlert] = React.useState(false);
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);

  // const toastId = React.useRef(null);
  async function getAllKeywords() {
    request(
      // token,
      // history,
      "GET",
      "/academic_keywords",
      (res) => {
        console.log(res.data)
        var keywordsArray = [];
        for (let i = 0; i < res.data.length; i++) {
          keywordsArray.push(res.data[i].keyword)
        }
        console.log(keywordsArray);
        setKeywords(keywordsArray);

      }
    );
  }

  const handleChangeKeyword = (event) => {
    const {
      target: {value},
    } = event;
    setKeyword(
      // On autofill we get a stringified value.
      typeof value === 'string' ? value.split(',') : value,
    );
  };
  useEffect(() => {
    console.log(teacherId)
    getAllKeywords()

  }, []);


  async function editKeywordTeacher(e) {
    e.preventDefault()
    console.log(teacherID)
    var body = {
      teacherId: teacherId,
      teacherName: teacherName,
      keywords: keyword
    }
    request(
      "post",
      `/thesis_defense_plan/${planID}/teacher/edit`,
      (res) => {
        console.log(res.data)
        if (res.data.ok) {
          handleToggle()
          setShowSubmitSuccess(true);
          setOpenAlert(true)
          setTimeout(() => {
              handleClose()
            }, 5000
          );
        } else if (res.data.err !== "") {
          setOpenAlert(true)
          setShowSubmitSuccess(false);
        }

      },
      {
        onError: (e) => {
          setShowSubmitSuccess(false);
          console.log(e)
        }
      },
      body
    ).then();
  };


  return (
    <Modal
      className={classes.modal}
      open={open}
      onClose={handleClose}
      closeAfterTransition
      BackdropComponent={Backdrop}
      BackdropProps={{
        timeout: 500,
      }}
    >
      <Fade in={open}>
        <form onSubmit={editKeywordTeacher}>
          <Card className={classes.card}>
            <CardHeader title="Gán chuyên môn cho giáo viên"/>
            <CardContent>
              <Box
                display="flex"
                flexDirection="column"
                justifyContent="center"
              >
                <TextField
                  id="name"
                  value={teacherName}
                  onChange={(e) => setName(e.target.value)}
                  label="Tên giảng viên"
                />
                <TextField
                  id="id"
                  value={teacherId}
                  onChange={(e) => setTeacherID(e.target.value)}
                  label="ID giảng viên"
                />

                <InputLabel id="demo-multiple-checkbox-label">Keywords</InputLabel>
                <Select
                  labelId="demo-multiple-checkbox-label"
                  id="demo-multiple-checkbox"
                  multiple
                  value={keyword}
                  onChange={handleChangeKeyword}
                  input={<OutlinedInput label="Tag"/>}
                  renderValue={(selected) => selected.join(', ')}
                  MenuProps={MenuProps}
                >
                  {keywords.map((ele) => (
                    <MenuItem key={ele} value={ele}>
                      <Checkbox checked={keyword.indexOf(ele) > -1}/>
                      <ListItemText primary={ele}/>
                    </MenuItem>
                  ))}
                </Select>
              </Box>
            </CardContent>
            <CardActions className={classes.action}>
              <Button type="submit" variant="contained" color="primary">
                Edit
              </Button>
              {(openAlert === true) ? (<div>
                {showSubmitSuccess === true ? (<SubmitSuccess
                  showSubmitSuccess={showSubmitSuccess}
                  content={"Bạn vừa tạo đợt bảo vệ mới thành công"}
                />) : (<Alert severity="error">Thêm mới thất bại</Alert>)}

              </div>) : (<></>)}
            </CardActions>
          </Card>
        </form>
      </Fade>
    </Modal>
  );
}
  