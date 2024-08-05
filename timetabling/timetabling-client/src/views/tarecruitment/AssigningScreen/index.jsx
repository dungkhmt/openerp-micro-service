import { request } from "api";
import { useState, useEffect, useMemo } from "react";
import useDebounce from "../config/debounce";
import {
  Button,
  Chip,
  IconButton,
  MenuItem,
  Select,
  Tooltip,
  TextField,
  Paper,
  Collapse,
  Typography,
} from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import SpeakerNotesIcon from "@mui/icons-material/SpeakerNotes";
import FilterAltOffIcon from "@mui/icons-material/FilterAltOff";
import FilterAltIcon from "@mui/icons-material/FilterAlt";
import { errorNoti, successNoti, warningNoti } from "utils/notification";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";
import { applicationUrl, semesterUrl } from "../apiURL";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const AssigningScreen = () => {
  const [applications, setApplications] = useState([]);
  const [originalApplications, setOriginalApplications] = useState([]);
  const [semester, setSemester] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  const [isFilter, setIsFilter] = useState(false);

  const [statusFilter, setStatusFilter] = useState("ALL");

  const [search, setSearch] = useState("");

  const debouncedSearch = useDebounce(search, 1000);

  useEffect(() => {
    request("get", semesterUrl.getCurrentSemester, (res) => {
      setSemester(res.data);
    });
  }, []);

  useEffect(() => {
    if (semester !== "") {
      handleFetchData();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel, semester, debouncedSearch, statusFilter]);

  const handleFetchData = () => {
    const searchParam = debouncedSearch
      ? `&search=${encodeURIComponent(debouncedSearch)}`
      : "";
    const assignStatusParam =
      statusFilter !== "" ? `&assignStatus=${statusFilter}` : "";

    console.log({ searchParam, assignStatusParam });

    setIsLoading(true);
    request(
      "get",
      `${applicationUrl.getApplicationByStatusAndSemester}/${semester}/APPROVED?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}${assignStatusParam}`,
      (res) => {
        setApplications(res.data.data);
        setOriginalApplications(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const handleChangeStatus = (event, rowData) => {
    const { value } = event.target;
    const { id } = rowData;

    const index = applications.findIndex(
      (application) => application.id === id
    );

    if (index !== -1) {
      const updatedApplications = [...applications];
      updatedApplications[index] = {
        ...updatedApplications[index],
        assignStatus: value,
      };

      setApplications(updatedApplications);
    }
  };

  const handleSaveData = (rowData) => {
    const { id } = rowData;

    const index = applications.findIndex(
      (application) => application.id === id
    );

    const value = applications[index].assignStatus;
    const updatedApplication = { ...rowData, assignStatus: value };

    request(
      "put",
      `${applicationUrl.updateAssignStatus}/${id}`,
      (res) => {
        const updatedOriginalApplications = originalApplications.map((app) =>
          app.id === id ? { ...app, assignStatus: value } : app
        );
        setOriginalApplications(updatedOriginalApplications);
        handleFetchData();
      },
      (res) => {
        console.log(res);
        errorNoti(res.response.data, 5000);
      },
      updatedApplication
    );
  };

  const handleAutoAssign = () => {
    setIsLoading(true);
    warningNoti("Đang sắp xếp, vui lòng đợi", 5000);
    request("get", `${applicationUrl.autoAssignClass}/${semester}`, (res) => {
      handleFetchData();
      successNoti("Sắp xếp tự động thành công", 5000);
      setIsLoading(false);
    });
  };

  const handleOldAutoAssign = () => {
    setIsLoading(true);
    warningNoti("Đang sắp xếp, vui lòng đợi", 5000);
    request(
      "get",
      `${applicationUrl.oldAutoAssignClass}/${semester}`,
      (res) => {
        handleFetchData();
        successNoti("Sắp xếp tự động thành công", 5000);
      },
      (e) => {
        errorNoti("Có lỗi xảy ra trong quá trình sắp xếp tự động", 5000);
      }
    );
    setIsLoading(false);
  };

  const handleChangeStatusFilter = (e) => {
    setStatusFilter(e.target.value);
  };

  const handleSearch = useMemo(
    () => (e) => {
      setSearch(e.target.value);
    },
    []
  );

  const assignStatusCell = (params) => {
    const rowData = params.row;

    return (
      <Select
        value={rowData?.assignStatus || ""}
        onChange={(e) => {
          handleChangeStatus(e, rowData);
        }}
        id="application-status"
        name="application-status"
        sx={styles.selection}
      >
        <MenuItem value="APPROVED">
          <Chip label="APPROVED" color="success" variant="outlined" />
        </MenuItem>
        <MenuItem value="PENDING">
          <Chip label="PENDING" color="warning" variant="outlined" />
        </MenuItem>
        <MenuItem value="CANCELED">
          <Chip label="CANCELED" color="error" variant="outlined" />
        </MenuItem>
      </Select>
    );
  };

  const actionCell = (params) => {
    const rowData = params.row;

    const originalApplication = originalApplications.find(
      (application) => application.id === rowData.id
    );

    return (
      <span>
        <IconButton
          variant="contained"
          color="primary"
          onClick={() => {
            handleSaveData(rowData);
          }}
          /**
           * @disabled If the application status is the same as the original application status, the button is disabled
           */
          disabled={
            originalApplication &&
            rowData?.assignStatus === originalApplication.assignStatus
          }
        >
          <SaveIcon />
        </IconButton>
        <IconButton disableRipple>
          <Tooltip
            color="info"
            title={
              <span style={{ whiteSpace: "pre-line" }}>
                {rowData?.note || "Empty"}
              </span>
            }
          >
            <SpeakerNotesIcon />
          </Tooltip>
        </IconButton>
      </span>
    );
  };

  const dataGridColumns = [
    {
      field: "classId",
      headerName: "Mã lớp",
      align: "center",
      headerAlign: "center",
    },
    {
      field: "subjectName",
      headerName: "Tên môn học",
      flex: 1,
    },
    {
      field: "day",
      headerName: "Thời gian",
      flex: 1,
    },
    {
      field: "name",
      headerName: "Tên sinh viên",
      flex: 1.5,
    },
    {
      field: "mssv",
      headerName: "MSSV",
      flex: 1,
    },
    {
      field: "cpa",
      headerName: "CPA",
      flex: 0.5,
    },
    {
      field: "englishScore",
      headerName: "Điểm tiếng anh",
      flex: 1,
    },
    {
      field: "assignStatus",
      headerName: "Trạng thái",
      flex: 1.2,
      renderCell: assignStatusCell,
      align: "center",
      headerAlign: "center",
    },
    {
      headerName: "Hành động",
      flex: 1,
      align: "center",
      headerAlign: "center",
      renderCell: actionCell,
    },
  ];

  const dataGridRows = applications.map((application) => ({
    id: application.id,
    classId: application.classCall.id,
    subjectName: application.classCall.subjectName,
    day: `Thứ ${application.classCall.day}, tiết ${application.classCall.startPeriod} - ${application.classCall.endPeriod}`,
    name: application.name,
    mssv: application.mssv,
    cpa: application.cpa,
    englishScore: application.englishScore,
    assignStatus: application.assignStatus,
    note: application.note,
  }));

  const FilterComponent = () => {
    return (
      <div style={styles.filterContent}>
        <Typography variant="h6">Trạng thái: </Typography>
        <Select
          value={statusFilter}
          id="application-status"
          name="application-status"
          sx={styles.selection}
          onChange={(e) => {
            handleChangeStatusFilter(e);
          }}
        >
          <MenuItem value="ALL">
            <Chip label="ALL" color="primary" variant="outlined" />
          </MenuItem>
          <MenuItem value="APPROVED">
            <Chip label="APPROVED" color="success" variant="outlined" />
          </MenuItem>
          <MenuItem value="PENDING">
            <Chip label="PENDING" color="warning" variant="outlined" />
          </MenuItem>
          <MenuItem value="CANCELED">
            <Chip label="CANCELED" color="error" variant="outlined" />
          </MenuItem>
        </Select>
      </div>
    );
  };

  return (
    <Paper elevation={3}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={styles.title}>
          Phân công trợ giảng
        </Typography>
        <div style={styles.toolLine}>
          <div style={styles.leftTool}>
            <Tooltip title="Phân loại">
              <IconButton>
                {isFilter ? (
                  <FilterAltIcon
                    color="primary"
                    fontSize="large"
                    onClick={() => setIsFilter(false)}
                  />
                ) : (
                  <FilterAltOffIcon
                    fontSize="large"
                    onClick={() => setIsFilter(true)}
                  />
                )}
              </IconButton>
            </Tooltip>

            <Button
              style={styles.autoButton}
              variant="outlined"
              onClick={handleAutoAssign}
            >
              Sắp xếp tự động
            </Button>

            <Button
              style={styles.autoButton}
              variant="outlined"
              onClick={handleOldAutoAssign}
            >
              Sắp xếp tự động (cũ)
            </Button>
          </div>

          <TextField
            style={styles.searchBox}
            variant="outlined"
            name="search"
            value={search}
            onChange={handleSearch}
            placeholder="Tìm kiếm"
          />
        </div>
        <Collapse in={isFilter}>
          <FilterComponent />
        </Collapse>
      </div>

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
        checkboxSelection={false}
        disableRowSelectionOnClick
      />
    </Paper>
  );
};

export default AssigningScreen;
