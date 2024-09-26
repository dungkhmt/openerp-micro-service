import React from "react";
import { Button, Card, Box } from "@material-ui/core";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "component/button/PrimaryButton";
import { useKeycloak } from "@react-keycloak/web";
import { useNavigate } from "react-router-dom";
import { useFetchData } from "hooks/useFetchData";
export default function AssignedThesisDefensePlan() {
  const { keycloak } = useKeycloak();
  const navigate = useNavigate();
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
        <PrimaryButton
          onClick={() => {
            navigate(`/thesis/thesis_defense_plan/assigned/${rowData.id}`);
          }}
          variant="contained"
          color="error"
        >
          Xem hội đồng
        </PrimaryButton>
      ),
    },
  ];
  const assignedThesisDefensePlanList = useFetchData(
    `/thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}`
  );

  console.log(assignedThesisDefensePlanList, keycloak?.tokenParsed?.email);
  return (
    <Card>
      <StandardTable
        title={"Danh sách đợt bảo vệ được phân công"}
        data={assignedThesisDefensePlanList}
        columns={columns}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </Card>
  );
}
