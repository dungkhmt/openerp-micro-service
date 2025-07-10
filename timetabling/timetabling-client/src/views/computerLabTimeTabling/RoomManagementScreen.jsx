import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import { Button, IconButton, TextField } from "@mui/material";
import { pink } from "@mui/material/colors";
import { request } from "api";
import { HustModal } from "erp-hust/lib/HustModal/HustModal";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import BasicSelect from "./components/SelectBox";
function RoomManagementScreen() {
  const [rooms, setRooms] = useState([]);
  const [departments, setDepartments] = useState([]);

  const [creationModalVisible, setCreationModalVisible] = useState(false);
  const [updateModalVisible, setUpdateModalVisible] = useState(false);
  const [deletionModalVisible, setDeletionModalVisible] = useState(false);

  const [loading, setLoading] = useState(false);
  const [selectedItem, setSelectedItem] = useState({});

  const [nameInput, setNameInput] = useState("");
  const [capInput, setCapInput] = useState("");
  const [departmentInput, setDepartmentInput] = useState("");

  useEffect(() => {
    request("get", "/lab-timetabling/department/get-all", (res) => {
      setDepartments(res.data);
      setDepartmentInput(res.data[0]?.id);
    }).then();
    request("get", "/lab-timetabling/room/get-all", (res) => {
      setRooms(res.data);
      console.log(res.data);
    }).then();
  }, []);

  const columns = [
    {
      title: "Tên phòng",
      field: "name",
    },
    {
      title: "Số chỗ",
      field: "capacity",
    },
    {
      title: "Chỉnh sửa",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            setNameInput(rowData.name);
            setCapInput(rowData.capacity);
            edit_btn_onclick(rowData);
          }}
          variant="contained"
          color="success"
        >
          <EditIcon />
        </IconButton>
      ),
    },
    {
      title: "Xóa",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            delete_btn_onclick(rowData);
          }}
          variant="contained"
          sx={{ color: pink[500] }}
        >
          <DeleteIcon />
        </IconButton>
      ),
    },
  ];

  const update_callback = (type, msg) => {
    toast(msg, {
      position: toast.POSITION.TOP_RIGHT,
      hideProgressBar: false,
      type: type,
    });
  };

  const edit_btn_onclick = (item) => {
    setSelectedItem(item);
    setUpdateModalVisible(true);
  };

  const delete_btn_onclick = (item) => {
    setSelectedItem(item);
    setDeletionModalVisible(true);
  };

  const closeModal = () => {
    setUpdateModalVisible(false);
    setCreationModalVisible(false);
    setDeletionModalVisible(false);
  };

  const create_btn_onclick = () => {
    setNameInput("");
    setCapInput("");
    setCreationModalVisible(true);
  };
  const submit_handler = (http_method, url, data) => {
    setLoading(true);
    request(
      http_method,
      url,
      () => {
        update_callback("success", `${http_method} thành công`);
      },
      {
        onError: () => {
          update_callback("error", `${http_method} thất bại`);
        },
      },
      data
    ).then(() => {
      closeModal();
      setLoading(false);
    });
  };

  const department_on_change = (value) => {
    setDepartmentInput(value.target.value);
  };
  return (
    <div>
      <div
        style={{
          display: "flex",
          flexDirection: "row-reverse",
          paddingBottom: "12px",
        }}
      >
        <Button variant="outlined" onClick={create_btn_onclick}>
            <AddIcon/>
            Tạo mới
          </Button>
      </div>
      <StandardTable
        title="Danh sách phòng học"
        columns={columns}
        data={rooms}
        hideCommandBar={true}
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
      <HustModal
        open={updateModalVisible}
        onClose={closeModal}
        // textClose="Update"
        title="Chỉnh sửa thông tin phòng"
        isLoading={loading}
        textOk="Lưu"
        onOk={() => {
          var data = {
            name: nameInput,
            capacity: capInput,
            department_id: departmentInput,
          };
          console.log(data);
          submit_handler("patch", `/lab-timetabling/room/${selectedItem.id}`, data);
        }}
      >
        <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          <div>
            <BasicSelect
              items={departments}
              label={"Department"}
              value={departmentInput}
              onChange={department_on_change}
            />
          </div>
          <TextField
            label="Tên phòng"
            defaultValue={nameInput}
            onChange={(e) => setNameInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
          <TextField
            label="Số chỗ"
            defaultValue={capInput}
            onChange={(e) => setCapInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
        </div>
      </HustModal>
      <HustModal
        open={creationModalVisible}
        onClose={closeModal}
        // textClose="Update"
        title="Tạo mới"
        isLoading={loading}
        textOk="Tạo"
        onOk={() => {
          var data = {
            name: nameInput,
            capacity: capInput,
            department_id: departmentInput,
          };
          console.log(data);
          submit_handler("post", `/lab-timetabling/room`, data);
        }}
      >
        <div style={{ display: "flex", flexDirection: "column", gap: "1rem" }}>
          <div>
            <BasicSelect
              items={departments}
              label={"Department"}
              value={departmentInput}
              onChange={department_on_change}
            />
          </div>
          <TextField
            label="Tên phòng"
            defaultValue={nameInput}
            onChange={(e) => setNameInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
          <TextField
            label="Số chỗ"
            defaultValue={capInput}
            onChange={(e) => setCapInput(e.target.value)}
            size="small"
            style={inputTextStyle}
          />
        </div>
      </HustModal>
      {/*  */}
      <HustModal
        open={deletionModalVisible}
        onClose={closeModal}
        // textClose="Update"
        title="Xóa phòng"
        isLoading={loading}
        textOk="Xóa"
        onOk={() => {
          submit_handler("delete", `/lab-timetabling/room/${selectedItem.id}`, null);
        }}
      ></HustModal>
    </div>
  );
}
const inputTextStyle = {
  marginBottom: "1rem",
};
export default RoomManagementScreen;
