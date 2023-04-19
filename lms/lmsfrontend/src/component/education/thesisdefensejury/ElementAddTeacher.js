import React, {useState} from "react";
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
import Add from '@mui/icons-material/Add';
import ModalLoading from "./ModalLoading"

export default function ElementAddTeacher({
                                            listTeacher,
                                            defenseJuryID,
                                            handleToggleTeacher,
                                            handlerIsLoad,
                                            handlerNotLoad
                                          }) {
  const [err, setErr] = useState("");
  const [openLoading, setOpenLoading] = useState(false)

  async function AddTeacherById(teacherID, defenseJuryID) {
    handlerIsLoad()
    setOpenLoading(true)
    var body = {
      teacherId: teacherID
    }
    request(
      "post",
      `/defense_jury/${defenseJuryID}/addTeacher`,
      (res) => {
        console.log(res.data)
        if (res.data.ok) {
          handleToggleTeacher()

        } else if (res.data.err !== "") {
          setErr(res.data.err)
          setTimeout(
            () => setErr(""),
            5000
          );

        }


        // setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
        handlerNotLoad()
        setOpenLoading(false)
      },
      {
        onError: (e) => {
          // setShowSubmitSuccess(false);
          console.log(e)
        }
      },
      body
    ).then();
  }

  const columns = [
    {title: "STT", field: "stt"},
    {title: "Tên giảng viên", field: "teacherName"},
  ];
  // useEffect(() => {
  //   getAllThesisNotBelongToDefenseJury();

  //   }, []);
  return (
    <Card>
      <MaterialTable
        title={""}
        columns={columns}
        actions={[
          {
            icon: Add,
            tooltip: "Add Teacher",
            onClick: (event, rowData) => {
              console.log("AddTeacher RowData:", rowData)
              AddTeacherById(rowData.teacherId, defenseJuryID)

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

              </div>
            </div>
          ),
        }}
      />
      {(err !== "") ?
        <Alert severity={(err !== "") ? 'error' : 'success'}>{(err !== "") ? err : "Successed"}</Alert> : <></>}
      <ModalLoading openLoading={openLoading}/>
    </Card>
  );
}
