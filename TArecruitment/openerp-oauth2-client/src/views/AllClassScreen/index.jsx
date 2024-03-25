import { useState, useEffect } from "react";
import { request } from "../../api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import { useHistory } from "react-router-dom";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import { styles } from "./index.style";
import { SEMESTER } from "config/localize";
import DeleteDialog from "components/dialog/DeleteDialog";
import ApplicatorDialog from "./ApplicatorDialog";

const AllClassScreen = () => {
  const [classes, setClasses] = useState([]);
  const [semester, setSemester] = useState(SEMESTER);
  const [openDialog, setOpenDialog] = useState(false);
  const [deleteId, setDeleteId] = useState("");
  const [openApplicatorDialog, setOpenApplicatorDialog] = useState(false);
  const [infoClassId, setInfoClassId] = useState("");

  const history = useHistory();

  useEffect(() => {
    fetchData();
  }, [semester]);

  const fetchData = () => {
    request("get", `/class-call/get-class-by-semester/${semester}`, (res) => {
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
      title: "Hành động",
      sorting: false,
      render: (rowData) => (
        <div>
          <IconButton variant="contained" color="primary">
            <FormatListBulletedIcon
              onClick={() => handleOpenApplicatorDialog(rowData)}
            />
          </IconButton>
          <IconButton
            onClick={() => {
              handleOpenDialog(rowData);
            }}
            variant="contained"
            color="error"
          >
            <DeleteOutlineIcon />
          </IconButton>
        </div>
      ),
      headerStyle: { width: "10%", textAlign: "center" },
      cellStyle: { width: "10%", textAlign: "center" },
    },
  ];

  const subjectFunction = (klass) => {
    history.push(`/teacher/class-information/${klass.id}`);
  };

  const handleDelete = () => {
    request(
      "delete",
      `/class-call/delete-class/${deleteId}`,
      (res) => {
        fetchData();
        setOpenDialog(false);
      },
      {},
      {}
    );
  };

  const handleOpenDialog = (klass) => {
    console.log(klass, "klass");
    setOpenDialog(true);
    setDeleteId(klass.id);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleOpenApplicatorDialog = (klass) => {
    setInfoClassId(klass.id);
    setOpenApplicatorDialog(true);
  };

  const handleCloseApplicatorDialog = () => {
    setOpenApplicatorDialog(false);
  };

  const handleChange = (event) => {
    setSemester(event.target.value);
  };

  return (
    <div>
      <h1>Danh sách lớp học</h1>
      <DeleteDialog
        open={openDialog}
        handleDelete={handleDelete}
        handleClose={handleCloseDialog}
      />

      <ApplicatorDialog
        open={openApplicatorDialog}
        handleClose={handleCloseApplicatorDialog}
        classId={infoClassId}
      />

      <FormControl variant="standard" style={styles.dropdown}>
        <InputLabel id="semester-label">Học kì</InputLabel>
        <Select
          labelId="semester-label"
          id="semester-select"
          value={semester}
          name="day"
          onChange={handleChange}
          MenuProps={{ PaperProps: { sx: styles.selection } }}
        >
          {/**
           * @TODO Change this to dynamic
           */}
          <MenuItem key={1} value="2023-2">
            2023-2
          </MenuItem>
          <MenuItem key={2} value="2023-1">
            2023-1
          </MenuItem>
          <MenuItem key={3} value="2022-2">
            2022-2
          </MenuItem>
          <MenuItem key={4} value="2022-1">
            2022-1
          </MenuItem>
          <MenuItem key={5} value="2021-2">
            2021-2
          </MenuItem>
          <MenuItem key={6} value="2021-1">
            2021-1
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
