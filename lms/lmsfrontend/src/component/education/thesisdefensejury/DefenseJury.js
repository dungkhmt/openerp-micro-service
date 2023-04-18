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
import React, {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import MaterialTable, {MTableToolbar} from "material-table";


function DefenseJury() {
  const history = useHistory();
  const [jurys, setJurys] = useState([]);

  const columns = [
    {title: "ID", field: "id"},
    {title: "Tên HD", field: "name"},
    {title: "Người tạo", field: "userLoginId"},
    {title: "Ngày tạo", field: "createdTime"},
  ];

  async function getAllDefenseJury() {
    request(
      // token,
      // history,
      "GET",
      "/defense_jurys",
      (res) => {
        console.log(res.data)
        setJurys(res.data.DefenseJurys);
      }
    );
  }

  const handleModalOpen = () => {
    history.push({
      pathname: `/thesis/defense_jury/create`,
      state: {},
    });
  };
  useEffect(() => {
    getAllDefenseJury();
  }, []);

  return (
    <Card>
      <MaterialTable
        title={"Danh sách HD Bao ve"}
        columns={columns}
        data={jurys}
        // onRowClick = {(event,rowData) => {
        //       console.log(rowData)
        //       history.push({
        //       pathname: `/thesis/defense_jury/${rowData.id}`,
        //       state: {
        //           defenseJuryId: rowData.id,
        //       },
        //     });
        //     }}
        components={{
          Toolbar: (props) => (
            <div style={{position: "relative"}}>
              <MTableToolbar {...props} />
              <div
                style={{position: "absolute", top: "16px", right: "350px"}}
              >
                <Button onClick={handleModalOpen} color="primary">
                  Thêm mới
                </Button>
              </div>
            </div>
          ),
        }}
      />
    </Card>
  );
}

export default DefenseJury;
  