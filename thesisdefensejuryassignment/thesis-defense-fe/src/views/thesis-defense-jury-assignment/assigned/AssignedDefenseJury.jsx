import React, { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import PrimaryButton from "component/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { Box } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import KeywordChip from "component/common/KeywordChip";
import { useFetchData } from "hooks/useFetchData";
export default function AssignedDefenseJury() {
  const { id } = useParams();
  const navigate = useNavigate();
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
            navigate(`defense_jury/${rowData.id}`);
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

  const data = useFetchData(
    `/thesis-defense-plan/get-assigned-for-teacher/${keycloak?.tokenParsed?.email}/${id}`
  );
  console.log(data);
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
