import { useState, useEffect } from "react";
import { request } from "../../api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import { useHistory } from "react-router-dom";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import { styles } from "./index.style";
import { SEMESTER, SEMESTER_LIST } from "config/localize";
import DeleteDialog from "components/dialog/DeleteDialog";
import ApplicatorDialog from "./ApplicatorDialog";

const AllClassScreen = () => {
  const [classes, setClasses] = useState([]);
  const [semester, setSemester] = useState(SEMESTER);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [deleteId, setDeleteId] = useState("");
  const [openApplicatorDialog, setOpenApplicatorDialog] = useState(false);
  const [infoClassId, setInfoClassId] = useState("");

  const history = useHistory();

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [semester]);

  const fetchData = () => {
    request("get", `/class-call/get-class-by-semester/${semester}`, (res) => {
      setClasses(res.data);
    });
  };

  const columns = [
    {
      title: "Mã lớp",
      field: "id",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "Mã môn học",
      field: "subjectId",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "Tên môn học",
      field: "subjectName",
      headerStyle: { fontWeight: "bold" },
      cellStyle: {
        fontWeight: "bold",
        textDecoration: "underline",
      },
      render: (rowData) => (
        <span
          onClick={() => {
            handleNavigateSubjectDetail(rowData);
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
      cellStyle: { fontWeight: "470" },
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

  const handleNavigateSubjectDetail = (klass) => {
    history.push(`/teacher/class-information/${klass.id}`);
  };

  const handleDeleteClass = () => {
    request("delete", `/class-call/delete-class/${deleteId}`, (res) => {
      fetchData();
      setOpenDeleteDialog(false);
    });
  };

  const handleOpenDialog = (klass) => {
    setOpenDeleteDialog(true);
    setDeleteId(klass.id);
  };

  const handleCloseDialog = () => {
    setOpenDeleteDialog(false);
  };

  const handleOpenApplicatorDialog = (klass) => {
    setInfoClassId(klass.id);
    setOpenApplicatorDialog(true);
  };

  const handleCloseApplicatorDialog = () => {
    setOpenApplicatorDialog(false);
  };

  const handleChangeSemester = (event) => {
    setSemester(event.target.value);
  };

  return (
    <div>
      <h1>Danh sách lớp học</h1>
      <DeleteDialog
        open={openDeleteDialog}
        handleDelete={handleDeleteClass}
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
          onChange={handleChangeSemester}
          MenuProps={{ PaperProps: { sx: styles.selection } }}
        >
          {SEMESTER_LIST.map((semester, index) => (
            <MenuItem key={index} value={semester}>
              {semester}
            </MenuItem>
          ))}
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
