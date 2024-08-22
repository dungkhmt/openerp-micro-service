import React from "react";
import { Button, Card, Box } from "@mui/material";
import { StandardTable } from "erp-hust/lib/StandardTable";
import PrimaryButton from "components/button/PrimaryButton";
import { useKeycloak } from "@react-keycloak/web";
import { useHistory, useRouteMatch } from "react-router-dom";
import { useFetch } from "hooks/useFetch";
//- Theo dõi đợt bảo vệ được phân công
export default function AssignedThesisDefensePlan() {
  const { keycloak } = useKeycloak();
  const history = useHistory();
  const { path } = useRouteMatch();
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
            history.push(`${path}/${rowData.id}`);
          }}
          variant="contained"
          color="error"
        >
          Xem hội đồng
        </PrimaryButton>
      ),
    },
  ];
  const { loading, data: assignedThesisDefensePlanList } = useFetch(
    `/thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}`
  );
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
