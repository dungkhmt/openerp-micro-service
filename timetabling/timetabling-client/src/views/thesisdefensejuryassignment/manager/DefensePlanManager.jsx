import { useEffect, useState } from "react";
import { useParams, useHistory } from "react-router-dom";
import { request } from "api";
import PrimaryButton from "components/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import CreateDefenseJury from "components/thesisdefensejury/modal/ModalCreateDefenseJury";
import { Box, IconButton } from "@mui/material";
import EditIcon from '@mui/icons-material/Edit';
import Chip from "@mui/material/Chip";
import ModalLoading from "components/common/ModalLoading";
export default function DefensePlanManager() {
  const { id } = useParams();
  const [loading, setLoading] = useState(true);
  const history = useHistory();
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
        <Box display={'flex'}>
          <IconButton aria-label="Chỉnh sửa" sx={{ marginRight: 2 }} onClick={() => {
            history.push(`/thesis/thesis_defense_plan/${id}/defense_jury/${rowData.id}/edit`);
          }}>
            <EditIcon />
          </IconButton>
          <PrimaryButton
            onClick={() => {
              history.push(`/thesis/thesis_defense_plan/${id}/defense_jury/${rowData.id}`);
            }}
            variant="contained"
            color="error"
            sx={{ float: "right" }}
          >
            Xem hội đồng
          </PrimaryButton>
        </Box>
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

  async function getPlanById() {
    setLoading(true);
    request(
      // token,
      // history,
      "GET",
      `/thesis-defense-plan/${id}`,
      (res) => {
        const data = res.data.defenseJuries;
        setDefenseJuries(
          data.map((item) => ({
            ...item,
            keywords: item?.academicKeywordList.map((item) => item.keyword),
          }))
        );
        setLoading(false)
      }
    );
  }

  useEffect(() => {
    getPlanById();
  }, [toggle]);
  return (
    <div>
      {loading && <ModalLoading />}
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
        <PrimaryButton onClick={() => { history.push('assign-automatically') }} sx={{ float: "right", marginRight: "16px" }}>
          Phân chia hội đồng tự động
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
              thesisPlanName={id}
            />
          </div>
        </div>
      )}
    </div>
  );
}
