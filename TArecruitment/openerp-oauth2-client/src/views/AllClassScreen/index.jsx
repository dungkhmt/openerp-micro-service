import { useState, useEffect } from "react";
import { request } from "../../api";
import IconButton from "@mui/material/IconButton";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import { useHistory } from "react-router-dom";
import { FormControl, InputLabel, MenuItem, Select } from "@mui/material";
import { styles } from "./index.style";
import { SEMESTER, SEMESTER_LIST } from "config/localize";
import DeleteDialog from "components/dialog/DeleteDialog";
import ApplicatorDialog from "./ApplicatorDialog";
import { DataGrid } from "@mui/x-data-grid";

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
      console.log(res.data);
    });
  };

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

  const actionCell = (params) => {
    const rowData = params.row;

    return (
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
    );
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã lớp",
      align: "center",
      headerAlign: "center",
    },
    { field: "subjectId", headerName: "Mã môn học", flex: 1 },
    {
      field: "subjectName",
      headerName: "Tên môn học",
      flex: 1,
      renderCell: (params) => (
        <div
          style={{
            cursor: "pointer",
            textDecoration: "underline",
            fontWeight: "bold",
          }}
          onClick={() => handleNavigateSubjectDetail(params.row)}
        >
          {params.value}
        </div>
      ),
    },
    { field: "classRoom", headerName: "Lớp học", flex: 1 },
    {
      headerName: "Hành động",
      renderCell: actionCell,
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
  ];

  const dataGridRows = classes.map((klass) => ({
    id: klass.id,
    subjectId: klass.subjectId,
    subjectName: klass.subjectName,
    classRoom: klass.classRoom,
    actions: { rowData: klass },
  }));

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

      <DataGrid
        rowHeight={60}
        sx={{ fontSize: 16 }}
        rows={dataGridRows}
        columns={dataGridColumns}
        autoHeight
        initialState={{
          pagination: {
            paginationModel: {
              pageSize: 5,
            },
          },
        }}
        pageSizeOptions={[5, 10, 20]}
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </div>
  );
};

export default AllClassScreen;
