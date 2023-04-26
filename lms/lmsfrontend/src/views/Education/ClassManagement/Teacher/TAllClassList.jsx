import React, {useEffect, useState} from "react";
import {request} from "../../../../api";
//import { Card, CardContent } from "@material-ui/core";
import StandardTable from "../../../../component/table/StandardTable";
//import { makeStyles, MuiThemeProvider } from "@material-ui/core/styles";
//import { defaultDatetimeFormat } from "../../utils/dateutils";
//import { TextField, Button } from "@mui/material";
import {useHistory} from "react-router";

export default function TAllClassList() {
  const [eduClassList, setEduClassList] = useState([]);
  const history = useHistory();
  const columns = [
    { title: "ClassCode", field: "classCode" },
    { title: "Course Code", field: "courseId" },
    { title: "Course Name", field: "courseName" },
    { title: "Created By", field: "createdByUserId" },
    { title: "Semester", field: "semesterId" },
  ];
  function navigateToClassDetailPage(event, rowData) {
    history.push(`/edu/teacher/class/detail/${rowData.id}`);
  }

  function getAllClass() {
    request("get", "/edu/class/get-all-class", (res) => {
      setEduClassList(res.data);
    }).then();
  }
  useEffect(() => {
    getAllClass();
  }, []);
  return (
    <div>
      <StandardTable
        title={"Class List"}
        columns={columns}
        data={eduClassList}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        onRowClick={navigateToClassDetailPage}
      />
    </div>
  );
}
