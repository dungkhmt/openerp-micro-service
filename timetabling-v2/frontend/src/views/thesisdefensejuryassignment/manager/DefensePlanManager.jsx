import { useEffect, useState, useRef } from "react";
import { useParams, useHistory } from "react-router-dom";
import { request } from "api";
import PrimaryButton from "components/button/PrimaryButton";
import { StandardTable } from "erp-hust/lib/StandardTable";
import CreateDefenseJury from "components/thesisdefensejury/modal/ModalCreateDefenseJury";
import { Box, IconButton, FormControl, Select, MenuItem, InputLabel } from "@mui/material";
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import ModalLoading from "components/common/ModalLoading";
import ModalDeleteItem from "components/thesisdefensejury/modal/ModalDeleteItem";
import { successNoti } from "utils/notification";
import useQuery from "hooks/useQuery";
// Màn quản lý các hội đồng bảo vệ
export default function DefensePlanManager() {
  const deletedJuryId = useRef("");
  const { id } = useParams();
  const [loading, setLoading] = useState(true);
  const history = useHistory();
  const [defenseJuries, setDefenseJuries] = useState([]);
  const [juryTopicList, setJuryTopicList] = useState([]);
  const [currentJuryTopic, setCurrentJuryTopic] = useState({});
  const [open, setOpen] = useState(false);
  const [deleteModalOpen, setDeleteModalOpen] = useState(false);
  const [toggle, setToggle] = useState(false);
  let query = useQuery();
  const juryTopic = query?.get('juryTopic');
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
      render: (rowData) => rowData?.defenseJurySessionList?.map(({ defenseSession }) => defenseSession?.name)?.join(" & "),
    },

    {
      title: "Phòng", field: "defenseRoom", render: (rowData) => rowData?.defenseRoom?.name,
    },
    {
      title: "Số đồ án tối da", field: "maxThesis",
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
            console.log(rowData?.id)
            deletedJuryId.current = rowData.id;
            setDeleteModalOpen((prevDeleteModalOpen) => !prevDeleteModalOpen)
          }}>
            <DeleteIcon />
          </IconButton>
        </Box>
      ),
    },
    {
      title: "",
      sorting: false,
      render: (rowData) => (
        <PrimaryButton
          onClick={() => {
            history.push(`/thesis/thesis_defense_plan/${id}/defense_jury/${rowData?.id}?isassigned=${rowData?.assigned ? "True" : "False"}`);
          }}
          variant="contained"
          color="error"
          sx={{ float: "right" }}
        >
          Xem hội đồng
        </PrimaryButton>
      )
    }
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
  const handleChange = (e) => {
    const currentTopic = e?.target?.value;
    history?.push(`?juryTopic=${currentTopic}`)
  }
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
  useEffect(() => {
    setLoading(true);
    request(
      "GET",
      `/jury-topic/${id}`,
      (res) => {
        const data = res.data;
        console.log(data);
        setJuryTopicList(data);
        if (juryTopic) {
          const topic = parseInt(juryTopic);
          setCurrentJuryTopic(data?.find((item) => item?.id === topic))
          setDefenseJuries(data?.find((item) => item?.id === topic)?.defenseJuryList)
        }
        setLoading(false)
      }
    );

  }, [toggle, juryTopic]);

  return (
    <div>
      {loading && <ModalLoading />}
      <Box sx={{ width: "100%" }}>
        <FormControl sx={{ width: '30%' }}>
          <InputLabel id="thesisDefensePlan">Chọn phân ban</InputLabel>
          <Select
            labelId="thesisDefensePlan"
            id="thesisDefensePlanSelect"
            value={juryTopic}
            label="thesisDefensePlan"
            onChange={handleChange}
          >
            {juryTopicList?.map(({ id, name }) =>
              <MenuItem key={id} value={id} selected={id === juryTopic}>
                {name}
              </MenuItem>
            )}
          </Select>
        </FormControl>
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
              thesisPlanName={id}
              juryTopic={currentJuryTopic}
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
