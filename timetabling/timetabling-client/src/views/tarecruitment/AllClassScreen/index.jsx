import { useState, useEffect, useMemo } from "react";
import useDebounce from "../config/debounce";
import { request } from "api";
import { useHistory } from "react-router-dom";
import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Paper,
  Typography,
  Button,
} from "@mui/material";
import { styles } from "./index.style";
import DeleteDialog from "../components/DeleteDialog";
import ApplicatorDialog from "./ApplicatorDialog";
import { DataGrid } from "@mui/x-data-grid";
import ImportDialog from "./ImportDialog";
import { classCallUrl, semesterUrl } from "../apiURL";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const AllClassScreen = () => {
  const [classes, setClasses] = useState([]);
  const [semester, setSemester] = useState("");
  const [allSemester, setAllSemester] = useState([]);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [deleteId, setDeleteId] = useState("");
  const [openApplicatorDialog, setOpenApplicatorDialog] = useState(false);
  const [infoClassId, setInfoClassId] = useState("");
  const [openImportDialog, setOpenImportDialog] = useState(false);

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [search, setSearch] = useState("");

  const debouncedSearch = useDebounce(search, 1000);

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  const [rowSelect, setRowSelect] = useState([]);

  const history = useHistory();

  useEffect(() => {
    request("get", semesterUrl.getCurrentSemester, (res) => {
      setSemester(res.data);
    });
    request("get", semesterUrl.getAllSemester, (res) => {
      setAllSemester(res.data);
    });
  }, []);

  useEffect(() => {
    if (semester !== "") {
      handleFetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel, semester, debouncedSearch]);

  const handleFetchData = () => {
    const searchParam =
      debouncedSearch !== ""
        ? `&search=${encodeURIComponent(debouncedSearch)}`
        : "";
    setIsLoading(true);
    request(
      "get",
      `${classCallUrl.getClassBySemesterL}/${semester}?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}`,
      (res) => {
        setClasses(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const handleNavigateSubjectDetail = (klass) => {
    history.push(`/ta-recruitment/teacher/class-information/${klass.id}`);
  };

  const handleNavigateCreateClass = () => {
    history.push(`/ta-recruitment/teacher/create-class/${semester}`);
  };

  const handleDeleteClass = () => {
    if (rowSelect.length === 0) {
      request("delete", `${classCallUrl.deleteClass}/${deleteId}`, (res) => {
        handleFetchData();
        setOpenDeleteDialog(false);
      });
    } else if (rowSelect.length === 1) {
      request(
        "delete",
        `${classCallUrl.deleteClass}/${rowSelect[0]}`,
        (res) => {
          handleFetchData();
          setOpenDeleteDialog(false);
        }
      );
    } else {
      let idList = rowSelect;
      request(
        "delete",
        `${classCallUrl.deleteMultipleClass}`,
        (res) => {
          handleFetchData();
          setOpenDeleteDialog(false);
        },
        {},
        idList
      );
    }
  };

  const handleOpenDialog = (klass) => {
    setOpenDeleteDialog(true);
    setDeleteId(klass.id);
  };

  const handleFileChange = (event) => {
    setOpenImportDialog(true);
  };

  const handleCloseImportDialog = () => {
    setOpenImportDialog(false);
  };

  const handleCloseDialog = () => {
    setOpenDeleteDialog(false);
  };

  const handleOpenApplicatorDialog = (klass) => {
    setInfoClassId(klass.id);
    setOpenApplicatorDialog(true);
  };

  const handleOpenEditClassScreen = (classData) => {
    history.push(`/ta-recruitment/teacher/class-information/${classData.id}`, {
      isEdited: true,
    });
  }

  const handleSearch = useMemo(
    () => (e) => {
      setSearch(e.target.value);
    },
    []
  );

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
        <Button
          variant="outlined"
          onClick={() => handleOpenEditClassScreen(rowData)}
          style={styles.leftActionButton}
        >
          Sửa
        </Button>
        <Button
          variant="outlined"
          onClick={() => handleOpenApplicatorDialog(rowData)}
        >
          DS Đăng ký
        </Button>
        <Button
          variant="outlined"
          color="error"
          onClick={() => handleOpenDialog(rowData)}
          style={styles.rightActionButton}
        >
          Xóa
        </Button>
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
          style={styles.linkedName}
          onClick={() => handleNavigateSubjectDetail(params.row)}
        >
          {params.value}
        </div>
      ),
    },
    { field: "classRoom", headerName: "Lớp học", flex: 1 },
    {
      field: "day",
      headerName: "Thời gian",
      flex: 1,
    },
    {
      headerName: "Hành động",
      renderCell: actionCell,
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
  ];

  const dataGridRows = classes?.map((klass) => ({
    id: klass.id,
    subjectId: klass.subjectId,
    subjectName: klass.subjectName,
    day: `Thứ ${klass.day}, tiết ${klass.startPeriod} - ${klass.endPeriod}`,
    classRoom: klass.classRoom,
    actions: { rowData: klass },
  }));

  return (
    <Paper elevation={3}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Danh sách lớp học
        </Typography>
        <div style={styles.searchArea}>
          <FormControl style={styles.dropdown} fullWidth size="small">
            <InputLabel id="semester-label">Học kì</InputLabel>
            <Select
              labelId="semester-label"
              id="semester-select"
              value={semester}
              name="day"
              label="Học kì"
              onChange={handleChangeSemester}
              MenuProps={{ PaperProps: { sx: styles.selection } }}
            >
              {allSemester.map((semester, index) => (
                <MenuItem key={index} value={semester}>
                  {semester}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <Button
            style={styles.firstButton}
            variant="outlined"
            onClick={handleNavigateCreateClass}
          >
            Thêm lớp học
          </Button>

          <Button
            style={styles.actionButton}
            variant="outlined"
            onClick={handleFileChange}
          >
            Import danh sách lớp học
          </Button>

          <Button
            style={styles.actionButton}
            variant="outlined"
            color="error"
            disabled={rowSelect.length === 0}
            onClick={() => setOpenDeleteDialog(true)}
          >
            Xóa
          </Button>

          <TextField
            style={styles.searchBox}
            variant="outlined"
            name="search"
            value={search}
            onChange={handleSearch}
            placeholder="Tìm kiếm"
          />
        </div>
      </div>

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

      <ImportDialog
        open={openImportDialog}
        handleClose={handleCloseImportDialog}
        fetchData={handleFetchData}
        semester={semester}
      />

      <DataGrid
        loading={isLoading}
        rowHeight={60}
        sx={styles.table}
        rows={dataGridRows}
        columns={dataGridColumns}
        rowCount={totalElements}
        pagination
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[10, 20, 50]}
        checkboxSelection
        onRowSelectionModelChange={(newRowSelectionModel) => {
          setRowSelect(newRowSelectionModel);
        }}
        rowSelectionModel={rowSelect}
        disableRowSelectionOnClick
      />
    </Paper>
  );
};

export default AllClassScreen;
