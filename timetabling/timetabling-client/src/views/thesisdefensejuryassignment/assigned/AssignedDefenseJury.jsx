import React from "react";
import { useParams, useHistory, useRouteMatch } from "react-router-dom";
import PrimaryButton from "components/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useKeycloak } from "@react-keycloak/web";
import KeywordChip from "components/common/KeywordChip";
import { useFetch } from "hooks/useFetch";
export default function AssignedDefenseJury() {
  const { id } = useParams();
  const history = useHistory();
  const { path } = useRouteMatch();
  const { keycloak } = useKeycloak();

  const columns = [
    { title: "Tên hội đồng", field: "name" },
    {
      title: "Ngày",
      field: "defenseDate",
      render: (rowData) => rowData.defenseDate.split("T")[0],
    },
    { title: "Số luận án tối đa", field: "maxThesis" },
    {
      title: "Keywords",
      field: "keywords",
      render: (rowData) =>
        rowData.keywords.map((item) => <KeywordChip keyword={item} />),
    },
    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <PrimaryButton
          onClick={() => {
            history.push(`/thesis/teacher/assigned/${id}/defense_jury/${rowData.id}`);
          }}
          variant="contained"
          color="error"
          sx={{ float: "right" }}
        >
          Xem hội đồng
        </PrimaryButton>
      ),
    },
  ];

  const { data: data } = useFetch(
    `/thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}/${id}`
  );
  const defenseJuries =
    data &&
    data?.defenseJuries?.map((item) => ({
      ...item,
      keywords: item?.academicKeywordList.map((item) => item.keyword),
    }));
  return (
    <StandardTable
      title={"Danh sách hội đồng bảo vệ"}
      data={defenseJuries}
      columns={columns}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
    />
  );
}