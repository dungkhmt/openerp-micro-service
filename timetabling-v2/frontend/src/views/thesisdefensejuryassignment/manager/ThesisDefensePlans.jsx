import React, { useState } from "react";
import { Button, Card, Box, IconButton } from "@mui/material";
import { useHistory } from "react-router-dom";
import ModalCreateThesisDefensePlan from "components/thesisdefensejury/modal/ModalCreateThesisDefensePlan";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import ModalLoading from "components/common/ModalLoading";
import EditIcon from '@mui/icons-material/Edit';
import { useThesisDefensePlans } from "services/useThesisDefensePlans";

function ThesisDefensePlans() {
  const history = useHistory();
  const [open, setOpen] = useState(false);
  const { plans, isLoading, refetch } = useThesisDefensePlans();

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

  const handleModalOpen = () => {
    setOpen(true);
  };

  const handleModalClose = () => {
    setOpen(false);
  };

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
      {isLoading && <ModalLoading loading={isLoading} />}
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
              handleClose={handleModalClose}
              refetchPlans={refetch}
            />
          </div>
        </div>
      )}
    </Card>
  );
}

export default ThesisDefensePlans;
