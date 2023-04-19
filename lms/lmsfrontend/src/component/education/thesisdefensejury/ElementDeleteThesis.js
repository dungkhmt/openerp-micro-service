import React, {useEffect, useState} from "react";
import {request} from "../../../api";
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
import Delete from '@material-ui/icons/Delete';
import ModalLoading from "./ModalLoading"

export default function ElementDeleteThesis({thesis, defenseJuryID, handleToggle, getListThesisOfDefenseJury}) {

  const [openLoading, setOpenLoading] = useState(false);

  async function DeleteThesisById(thesisID, defenseJuryID) {
    setOpenLoading(true)
    var body = {
      thesisId: thesisID
    }
    request(
      "post",
      `/defense_jury/${defenseJuryID}/deleteJury`,
      (res) => {
        console.log(res.data)
        handleToggle()
        setOpenLoading(false)
        // setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
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
    {title: "Tên luận văn", field: "thesisName"},
    {title: "Người tạo", field: "studentName"},
  ];
  useEffect(() => {
    getListThesisOfDefenseJury();

  }, []);
  return (
    <Card>
      <MaterialTable
        title={""}
        columns={columns}
        actions={[
          {
            icon: Delete,
            tooltip: "Delete Thesis",
            onClick: (event, rowData) => {
              console.log(rowData.id)
              DeleteThesisById(rowData.id, defenseJuryID)

              // setToggle(true)
            }
          }
        ]}
        data={thesis}
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
      <ModalLoading openLoading={openLoading}/>
    </Card>
  );
}
