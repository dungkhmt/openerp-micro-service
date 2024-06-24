import { useEffect, useState, useRef } from "react";
import { useParams, useHistory } from "react-router-dom";
import { request } from "api";
import PrimaryButton from "components/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import CreateDefenseJury from "components/thesisdefensejury/modal/ModalCreateDefenseJury";
import { Box, IconButton, Typography } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import Chip from "@mui/material/Chip";
import ModalLoading from "components/common/ModalLoading";
import ModalDeleteItem from "components/thesisdefensejury/modal/ModalDeleteItem";
import { successNoti } from "utils/notification";
export default function DefensePlanManager() {
  const deletedJuryId = useRef("");
  const { id } = useParams();
  const [loading, setLoading] = useState(true);
  const history = useHistory();
  const [defenseJuries, setDefenseJuries] = useState([]);
  const [thesisDefensePlan, setThesisDefensePlan] = useState({});
  const [open, setOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [toggle, setToggle] = useState(false);
  const columns = [
    { title: "Tên hội đồng", field: "name" },
    {
      title: "Ngày",
      field: "defenseDate",
      render: (rowData) => rowData?.defenseDate,
    },
    {
      title: "Ca bảo vệ",
      field: "defenseSession",
      render: (rowData) => rowData?.defenseSession?.name,
    },

    {
      title: "Phòng", field: "defenseRoom", render: (rowData) => rowData?.defenseRoom?.name,
    },
    {
      title: "Phân ban", field: "juryTopic",
    },
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
          <IconButton aria-label="Chỉnh sửa" sx={{ marginRight: 2 }} onClick={() => {
            deletedJuryId.current = rowData.id;
            setDeleteModalOpen((prevDeleteModalOpen) => !prevDeleteModalOpen)
          }}>
            <DeleteIcon />
          </IconButton>
          <PrimaryButton
            onClick={() => {
              history.push(`/thesis/thesis_defense_plan/${id}/defense_jury/${rowData.id}?isassigned=${rowData?.defenseJuryTeacherRoles?.length > 0 ? "True" : "False"}`);
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
  const handleDelete = () => {
    request("DELETE",
      `/defense-jury/delete/${deletedJuryId.current}`, (res) => {
        if (res?.data) {
          successNoti(res?.data, true);
          setDeleteModalOpen((prevDeleteModalOpen) => !prevDeleteModalOpen)
          setToggle((prevToggle) => !prevToggle)
          deletedJuryId.current = ""
        }
      }, (e) => {
        console.log(e)
      })
  }
  async function getPlanById() {
    setLoading(true);
    request(
      // token,
      // history,
      "GET",
      `/thesis-defense-plan/${id}`,
      (res) => {
        const data = res.data?.defenseJuries;
        setThesisDefensePlan(res.data);
        setDefenseJuries(
          data?.reverse()?.map((item) => ({
            ...item,
            juryTopic: item?.juryTopic?.name,
            keywords: item?.juryTopic?.academicKeywordList.map((item) => item.keyword),
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
      {deleteModalOpen &&
        <ModalDeleteItem
          handleClose={() => setDeleteModalOpen((prevDeleteModalOpen) => !prevDeleteModalOpen)}
          open={deleteModalOpen}
          title={"Xóa hội đồng"}
          content={`Bạn có muốn xóa hội đồng này không ?`}
          handleDelete={handleDelete}
        />}
    </div>
  );
}
