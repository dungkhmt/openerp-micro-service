import {Card} from "@material-ui/core/";
import Delete from "@material-ui/icons/Delete";
import {Alert} from "@material-ui/lab";
import MaterialTable, {MTableToolbar} from "material-table";
import {useEffect, useState} from "react";
import {request} from "../../../api";
import ModalLoading from "./ModalLoading";

export default function ElementDeleteTeacher({
                                               teacher,
                                               defenseJuryID,
                                               handleToggleTeacher,
                                               handlerIsLoad,
                                               handlerNotLoad
                                             }) {
  const [err, setErr] = useState("");
  const [openLoading, setOpenLoading] = useState(false)

  async function DeleteTeacherById(teacherID, defenseJuryID) {
    setOpenLoading(true)
    handlerIsLoad()
    var body = {
      teacherId: teacherID
    }
    request(
      "post",
      `/defense_jury/${defenseJuryID}/deleteTeacher`,
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
  useEffect(() => {
    //   getListTeacherOfDefenseJury();

  }, []);
  return (
    <Card>
      <MaterialTable
        title={""}
        columns={columns}
        actions={[
          {
            icon: Delete,
            tooltip: "Delete Teacher",
            onClick: (event, rowData) => {
              console.log(rowData)
              DeleteTeacherById(rowData.teacherId, defenseJuryID)

              // setToggle(true)
            }
          }
        ]}
        data={teacher}
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
