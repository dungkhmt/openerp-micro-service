import * as React from "react";
import { useEffect, useState } from "react";
import { Link, useParams, useNavigate } from "react-router-dom";
import { request } from "../../../api";
import PrimaryButton from "component/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import CreateDefenseJury from "./CreateDefenseJury";
import { Box } from "@mui/material";
import Chip from "@mui/material/Chip";
export default function DefensePlanManager() {
  const params = useParams();
  const navigate = useNavigate();
  const [defenseJuries, setDefenseJuries] = useState([]);
  const [open, setOpen] = useState(false);
  const [toggle, setToggle] = useState(false);
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
        rowData.keywords.map((item) => <Chip key={item} label={item} />),
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

  const handleClose = () => {
    setOpen(false);
  };
  const handleModalOpen = () => {
    setOpen(true);
  };
  const handleToggle = () => {
    setToggle(!toggle);
  };

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      `/thesis-defense-plan/${params.id}`,
      (res) => {
        const data = res.data.defenseJuries;
        setDefenseJuries(
          data.map((item) => ({
            ...item,
            keywords: item?.academicKeywordList.map((item) => item.keyword),
          }))
        );
      }
    );
  }

  useEffect(() => {
    getAllPlan();
  }, [toggle]);
  return (
    <div>
      <Box sx={{ width: "100%" }}>
        <PrimaryButton
          onClick={() => {
            handleModalOpen();
          }}
          variant="contained"
          color="error"
          sx={{ float: "right", marginRight: "16px" }}
        >
          Tạo hội đồng mới
        </PrimaryButton>
      </Box>
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
      {open && (
        <div style={{ position: "relative" }}>
          <div style={{ position: "absolute", top: "16px", right: "350px" }}>
            <CreateDefenseJury
              open={open}
              handleClose={handleClose}
              handleToggle={handleToggle}
              thesisPlanName={params.id}
            />
          </div>
        </div>
      )}
    </div>
  );
}
