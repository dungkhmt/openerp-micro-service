import React, { useState } from "react";
import { request } from "../../../api";
import Alert from "@mui/material/Alert";
import { Card } from "@material-ui/core";
import MaterialTable, { MTableToolbar } from "material-table";
import Add from "@mui/icons-material/Add";
import ModalLoading from "./ModalLoading";
import { StandardTable } from "erp-hust/lib/StandardTable";
export default function ElementAddTeacherPlan({
  listTeacher,
  defensePlanID,
  toggleTeacher,
  handleToggleTeacher,
  handlerIsLoad,
  handlerNotLoad,
}) {
  const [err, setErr] = useState("");
  const [showSubmitSuccess, setShowSubmitSuccess] = useState(false);
  const [openLoading, setOpenLoading] = useState(false);

  async function AddTeacherById(teacherID, defensePlanID) {
    handlerIsLoad();
    setOpenLoading(true);
    var body = {
      teacherId: teacherID,
    };
    request(
      "post",
      `/thesis_defense_plan/${defensePlanID}/addTeacher`,
      (res) => {
        console.log(res.data);
        if (res.data.ok) {
          handleToggleTeacher();
        } else if (res.data.err !== "") {
          setShowSubmitSuccess(true);
          setErr(res.data.err);
          setTimeout(() => setErr(""), 5000);
        }

        // setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
        handlerNotLoad();
        setOpenLoading(false);
      },
      {
        onError: (e) => {
          // setShowSubmitSuccess(false);
          console.log(e);
        },
      },
      body
    ).then();
  }

  const columns = [
    { title: "STT", field: "stt" },
    { title: "Tên giảng viên", field: "teacherName" },
  ];
  // useEffect(() => {
  //   getAllThesisNotBelongToDefenseJury();

  //   }, []);
  return (
    <Card>
      <StandardTable
        title={""}
        data={listTeacher}
        columns={columns}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
      {err !== "" ? (
        <Alert severity={err !== "" ? "error" : "success"}>
          {err !== "" ? err : "Successed"}
        </Alert>
      ) : (
        <></>
      )}
      <ModalLoading openLoading={openLoading} />
    </Card>
  );
}
