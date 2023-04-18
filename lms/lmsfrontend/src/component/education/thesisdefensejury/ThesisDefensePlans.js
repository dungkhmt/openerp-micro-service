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
import ModalCreateThesisDefensePlan from "./ModalCreateThesisDefensePlan";


function ThesisDefensePlans(props) {
  const history = useHistory();
  const [plans, setPlans] = useState([]);
  const [toggle, setToggle] = React.useState(false);
  const [open, setOpen] = React.useState(false);

  const columns = [
    {title: "ID", field: "id"},
    {title: "Tên đợt bảo vệ", field: "name"},
    {title: "Ngày tạo", field: "createdTime"},
  ];
  const handleToggle = () => {
    setToggle(!toggle);
  }

  async function getAllPlan() {

    request(
      // token,
      // history,
      "GET",
      `/thesis_defense_plan`,
      (res) => {
        console.log(res.data)
        setPlans(res.data)
      }
    );
  }

  const handleModalOpen = () => {
    setOpen(true);
  };

  const handleModalClose = () => {
    setOpen(false);
  };
  const handleClose = () => {
    setOpen(false);
  }


  useEffect(() => {
    getAllPlan();
  }, [toggle]);

  return (
    <Card>
      <MaterialTable
        title={"Danh sách đợt bảo vệ"}
        columns={columns}
        data={plans}
        onRowClick={(event, rowData) => {
          console.log(rowData)
          history.push({
            pathname: `/thesis/thesis_defense_plan/${rowData.id}`,
            state: {},
          });
        }}
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
                <ModalCreateThesisDefensePlan open={open} handleClose={handleClose} handleToggle={handleToggle}/>
              </div>
            </div>
          ),
        }}
      />

    </Card>
  );
}

export default ThesisDefensePlans;
  