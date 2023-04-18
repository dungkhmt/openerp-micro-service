import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import Alert from '@mui/material/Alert';
import {
  Button,
  Card,
  CardActions,
  CardContent,
  Checkbox,
  MenuItem,
  TextField,
  Tooltip,
  Typography,
} from "@material-ui/core/";
import MaterialTable, {MTableToolbar} from "material-table";
import Edit from '@mui/icons-material/Edit';
import ModalLoading from "./ModalLoading"
import {useHistory} from "react-router-dom";
import ModalAssignKeywordToTeacher from "./ModalAssignKeywordToTeacher"

export default function TeacherBelongToPlan(props) {
  const [toggle, setToggle] = React.useState(false);
  const [err, setErr] = useState("");
  const [openLoading, setOpenLoading] = useState(false)
  const defensePlanId = props.defensePlanId;
  const [listTeacher, setListTeacher] = useState([]);
  const history = useHistory();
  const [teacherID, setTeacherID] = useState("");
  const [teacherName, setTeacherName] = useState("");
  const [open, setOpen] = React.useState(false);


  const columns = [
    {title: "STT", field: "stt"},
    {title: "Tên giảng viên", field: "teacherName"},
    {title: "ID giảng viên", field: "teacherId"},
    {title: "Keyword", field: "keywords"},
  ];

  const handleToggle = () => {
    setToggle(!toggle);
  }

  async function getAllTeacherBelongToPlan() {
    console.log(defensePlanId)
    setOpenLoading(true);
    request(
      // token,
      // history,
      "GET",
      `/thesis_defense_plan/${defensePlanId}/teachers`,
      (res) => {
        console.log(res.data)
        let teachers = []
        if (res.data != null) {
          for (let i = 0; i < res.data.length; i++) {
            let keys = ""
            console.log(res.data[i].keywords)
            if (res.data[i].keywords != null) {
              for (let j = 0; j < res.data[i].keywords.length; j++) {
                if (j == 0) {
                  keys = keys.concat("", res.data[i].keywords[j])
                  continue
                }
                keys = keys.concat(",", res.data[i].keywords[j])
              }
            }
            console.log(keys)
            teachers.push({
              stt: i + 1,
              teacherId: res.data[i].teacherId,
              teacherName: res.data[i].teacherName,
              keywords: keys
            })
          }
          console.log("Teachers", teachers)
          setListTeacher(teachers)
          setOpenLoading(false);
          // setOpenLoading(false);
        }
      }
    );
  }

  const handleAssign = () => {
    history.push({
      pathname: `/thesis/defensePlan/${defensePlanId}/assignTeacher`
    });
  }
  const handleClose = () => {
    setOpen(false);
  };


  useEffect(() => {
    getAllTeacherBelongToPlan();

  }, [toggle]);
  return (
    <Card>
      <MaterialTable
        title={""}
        columns={columns}
        actions={[
          {
            icon: Edit,
            tooltip: "Edit Teacher",
            onClick: (event, rowData) => {
              console.log("EditTeacher RowData:", rowData)

              setTeacherID(rowData.teacherId)
              setTeacherName(rowData.teacherName)
              setOpen(true)
            }
          }
        ]}
        data={listTeacher}
        components={{
          Toolbar: (props) => (
            <div style={{position: "relative"}}>
              <MTableToolbar {...props} />
              <div
                style={{position: "absolute", top: "16px", right: "350px"}}
              >
                <Button onClick={handleAssign} color="primary">
                  Phân công giáo viên vào đợt bảo vệ
                </Button>
              </div>
            </div>
          ),
        }}
      />
      {(err !== "") ?
        <Alert severity={(err !== "") ? 'error' : 'success'}>{(err !== "") ? err : "Successed"}</Alert> : <></>}
      <ModalLoading openLoading={openLoading}/>
      <ModalAssignKeywordToTeacher open={open} handleClose={handleClose} teacherId={teacherID} teacherName={teacherName}
                                   planId={defensePlanId} handleToggle={handleToggle}/>
    </Card>
  );
}
