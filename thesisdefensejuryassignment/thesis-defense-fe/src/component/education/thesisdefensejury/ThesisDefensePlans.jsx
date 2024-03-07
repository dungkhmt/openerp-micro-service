import React, { useEffect, useState } from "react";
import { Button, Card } from "@material-ui/core";
import { useNavigate } from "react-router-dom";
import { request } from "../../../api";
import ModalCreateThesisDefensePlan from "./ModalCreateThesisDefensePlan";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "component/button/PrimaryButton";
function ThesisDefensePlans(props) {
  const navigate = useNavigate();
  const [plans, setPlans] = useState([]);
  const [toggle, setToggle] = React.useState(false);
  const [open, setOpen] = React.useState(false);

  const columns = [
    { title: "ID", field: "id" },
    { title: "Tên đợt bảo vệ", field: "name" },
    { title: "Kì học", field: "semester" },
    { title: "Ngày bắt đầu", field: "startDate" },
    { title: "Ngày kết thúc", field: "endDate" },
    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <PrimaryButton
          onClick={() => {
            navigate(`/thesis/thesis_defense_plan/${rowData.id}`);
          }}
          variant="contained"
          color="error"
        >
          Xem hội đồng
        </PrimaryButton>
      ),
    },
  ];
  const handleToggle = () => {
    setToggle(!toggle);
  };

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      `/thesis-defense-plan/get-all`,
      (res) => {
        console.log(res.data);
        setPlans(res.data);
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
  };

  useEffect(() => {
    getAllPlan();
  }, [toggle]);

  return (
    <Card>
      {/* <MaterialTable
        title={"Danh sách đợt bảo vệ"}
        columns={columns}
        data={plans}
        onRowClick={(event, rowData) => {
          console.log(rowData);
          navigate(`/thesis/thesis_defense_plan/${rowData.id}`, {
            state: {},
          });
        }}
        components={{
          Toolbar: (props) => (
            <div style={{ position: "relative" }}>
              <MTableToolbar {...props} />
              <div
                style={{ position: "absolute", top: "16px", right: "350px" }}
              >
                <Button onClick={handleModalOpen} color="primary">
                  Thêm mới
                </Button>
                <ModalCreateThesisDefensePlan
                  open={open}
                  handleClose={handleClose}
                  handleToggle={handleToggle}
                />
              </div>
            </div>
          ),
        }}
      /> */}
      <PrimaryButton onClick={handleModalOpen}>Open Modal</PrimaryButton>
      <StandardTable
        title={"Danh sách đợt bảo vệ"}
        data={plans}
        columns={columns}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
      {open && (
        <div style={{ position: "relative" }}>
          <div style={{ position: "absolute", top: "16px", right: "350px" }}>
            <Button onClick={handleModalOpen} color="primary">
              Thêm mới
            </Button>
            <ModalCreateThesisDefensePlan
              open={open}
              handleClose={handleClose}
              handleToggle={handleToggle}
            />
          </div>
        </div>
      )}
    </Card>
  );
}

export default ThesisDefensePlans;
