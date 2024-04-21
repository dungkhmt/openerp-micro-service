import { useState, useEffect, useCallback } from "react";
import { request } from "../../api";
import IconButton from "@mui/material/IconButton";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import FormatListBulletedIcon from "@mui/icons-material/FormatListBulleted";
import { useHistory } from "react-router-dom";
import {
  FormControl,
  InputLabel,
  MenuItem,
  Select,
  TextField,
  Paper,
  Tooltip,
} from "@mui/material";
import { styles } from "./index.style";
import { SEMESTER, SEMESTER_LIST } from "config/localize";
import DeleteDialog from "components/dialog/DeleteDialog";
import FileUploadIcon from "@mui/icons-material/FileUpload";
import ApplicatorDialog from "./ApplicatorDialog";
import { DataGrid } from "@mui/x-data-grid";
import { errorNoti, successNoti } from "utils/notification";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 5,
};

const AllClassScreen = () => {
  const [classes, setClasses] = useState([]);
  const [semester, setSemester] = useState(SEMESTER);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [deleteId, setDeleteId] = useState("");
  const [openApplicatorDialog, setOpenApplicatorDialog] = useState(false);
  const [infoClassId, setInfoClassId] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [search, setSearch] = useState("");

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  const history = useHistory();

  const debouncedSearch = useCallback(
    (search) => {
      const timer = setTimeout(() => {
        setPaginationModel({
          ...DEFAULT_PAGINATION_MODEL,
          page: 0,
        });
        handleFetchData();
      }, 1000);

      return () => clearTimeout(timer);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [search]
  );

  useEffect(() => {
    return debouncedSearch(search);
  }, [search, debouncedSearch]);

  useEffect(() => {
    handleFetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [semester, paginationModel]);

  const handleFetchData = () => {
    const searchParam =
      search !== "" ? `&search=${encodeURIComponent(search)}` : "";
    setIsLoading(true);
    request(
      "get",
      `/class-call/get-class-by-semester/${semester}?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}`,
      (res) => {
        setClasses(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const handleNavigateSubjectDetail = (klass) => {
    history.push(`/teacher/class-information/${klass.id}`);
  };

  const handleDeleteClass = () => {
    request("delete", `/class-call/delete-class/${deleteId}`, (res) => {
      handleFetchData();
      setOpenDeleteDialog(false);
    });
  };

  const handleOpenDialog = (klass) => {
    setOpenDeleteDialog(true);
    setDeleteId(klass.id);
  };

  const handleFileChange = (event) => {
    const selectedFile = event.target.files[0];
    const formData = new FormData();
    formData.append("excelFile", selectedFile);
    request(
      "post",
      "/class-call/import-class",
      (res) => {
        successNoti(res.data);
        handleFetchData();
        event.target.value = null;
      },
      {
        onError: (e) => {
          errorNoti(e.response.data);
        },
      },
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    handleFetchData();
  };

  const handleCloseDialog = () => {
    setOpenDeleteDialog(false);
  };

  const handleOpenApplicatorDialog = (klass) => {
    setInfoClassId(klass.id);
    setOpenApplicatorDialog(true);
  };

  const handleSearch = (e) => {
    setSearch(e.target.value);
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
          style={styles.linkedName}
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
    <Paper elevation={3} style={{ paddingTop: "1em" }}>
      <div style={styles.tableToolBar}>
        <h1>Danh sách lớp học</h1>
        <div style={styles.searchArea}>
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

          <Tooltip style={styles.importIcon} title="Import danh sách lớp học">
            <IconButton component="label">
              <input
                type="file"
                accept=".xlsx,.xls"
                onChange={handleFileChange}
                style={{ display: "none" }}
              />
              <FileUploadIcon color="primary" fontSize="large" />
            </IconButton>
          </Tooltip>

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

      <DataGrid
        loading={isLoading}
        rowHeight={60}
        sx={{ fontSize: 16 }}
        rows={dataGridRows}
        columns={dataGridColumns}
        autoHeight
        rowCount={totalElements}
        pagination
        paginationMode="server"
        paginationModel={paginationModel}
        onPaginationModelChange={setPaginationModel}
        pageSizeOptions={[5, 10, 20]}
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </Paper>
  );
};

export default AllClassScreen;
