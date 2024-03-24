import { useState, useEffect } from "react";
import { request } from "../../api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import DeleteIcon from "@mui/icons-material/Delete";
import { useHistory } from "react-router-dom";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import styles from "./index.style";

const AllClassScreen = () => {
  const [classes, setClasses] = useState([]);
  const [semester, setSemester] = useState("All");
  const history = useHistory();

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = () => {
    request("get", "/class-call/get-all-class", (res) => {
      setClasses(res.data);
    }).then();
  };

  const columns = [
    {
      title: "Mã lớp",
      field: "id",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Mã môn học",
      field: "subjectId",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Tên môn học",
      field: "subjectName",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
      render: (rowData) => (
        <span
          onClick={() => {
            subjectFunction(rowData);
          }}
          style={{ cursor: "pointer" }}
        >
          {rowData.subjectName}
        </span>
      ),
    },
    {
      title: "Lớp học",
      field: "classRoom",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "bold" },
    },
    {
      title: "Delete",
      sorting: false,
      render: (rowData) => (
        <IconButton
          onClick={() => {
            handleDelete(rowData);
          }}
          variant="contained"
          color="error"
        >
          <DeleteIcon />
        </IconButton>
      ),
    },
  ];

  const subjectFunction = (klass) => {
    history.push(`/teacher/class-information/${klass.id}`);
  };

  const handleDelete = (klass) => {
    request(
      "delete",
      `/class-call/delete-class/${klass.id}`,
      (res) => {
        fetchData();
      },
      {},
      {}
    );
  };

  const handleChange = (event) => {
    setSemester(event.target.value);
  };

  return (
    <div>
      <h1>Danh sách lớp học</h1>

      <FormControl variant="standard" style={styles.dropdown}>
        <InputLabel id="ngay-label">Học kì</InputLabel>
        <Select
          labelId="ngay-label"
          id="ngay-select"
          value={semester}
          name="day"
          onChange={handleChange}
        >
          <MenuItem key={0} value="All">
            All
          </MenuItem>
          <MenuItem key={1} value="2023.1">
            2023.1
          </MenuItem>
          <MenuItem key={2} value="2023.2">
            2023.2
          </MenuItem>
        </Select>
      </FormControl>

      <StandardTable
        title=""
        columns={columns}
        data={classes}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </div>
  );
};

export default AllClassScreen;
