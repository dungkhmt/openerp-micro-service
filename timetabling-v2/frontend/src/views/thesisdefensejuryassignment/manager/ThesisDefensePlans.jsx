import React, { useEffect, useState } from "react";
import { Button, Card, Box, IconButton } from "@mui/material";
import { useHistory } from "react-router-dom";
import ModalCreateThesisDefensePlan from "components/thesisdefensejury/modal/ModalCreateThesisDefensePlan";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import { request } from "api";
import ModalLoading from "components/common/ModalLoading";
import EditIcon from '@mui/icons-material/Edit';
// Màn quản lý đợt bảo vệ đồ án
function ThesisDefensePlans() {
  const history = useHistory();
  const [loading, setLoading] = useState(true);
  const [plans, setPlans] = useState([]);
  const [toggle, setToggle] = React.useState(false);
  const [open, setOpen] = React.useState(false);

  const columns = [
    { title: "ID", field: "id" },
    { title: "Tên đợt bảo vệ", field: "name" },
    { title: "Kì học", field: "semester" },
    {
      title: "Ngày bắt đầu",
      field: "startDate",
      render: (rowData) => rowData?.startDate?.split("T")[0],
    },
    {
      title: "Ngày kết thúc",
      field: "endDate",
      render: (rowData) => rowData?.endDate?.split("T")[0],
    },

    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <Box display={'flex'}>
          <IconButton aria-label="Chỉnh sửa" sx={{ marginRight: 2 }} onClick={() => {
            history.push(`/thesis/thesis_defense_plan/${rowData.id}/edit`);
          }}>
            <EditIcon />
          </IconButton>
          <PrimaryButton
            onClick={() => {
              history.push(`/thesis/thesis_defense_plan/${rowData.id}`);
            }}
            variant="contained"
            color="error"
          >
            Xem hội đồng
          </PrimaryButton>
        </Box>
      ),
    },
  ];
  const handleToggle = () => {
    setToggle(!toggle);
  };

  async function getAllPlan() {
    request(
      "GET",
      `/thesis-defense-plan/get-all`,
      (res) => {
        console.log(res.data);
        setPlans(res.data);
        setLoading(false)
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
    setLoading(true);
    getAllPlan();
  }, [toggle]);
  return (
    <Card>
      <Box
        display={"flex"}
        flexDirection={"row-reverse"}
        marginTop={3}
        paddingRight={6}
      >
        <PrimaryButton onClick={handleModalOpen}>
          Tạo đợt bảo vệ đồ án mới
        </PrimaryButton>
      </Box>
      {loading && <ModalLoading loading={loading} />}
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
