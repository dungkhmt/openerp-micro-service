import { request } from "api";
import { useEffect, useState, useCallback } from "react";
import { SEMESTER } from "../config/localize";
import {
  Chip,
  IconButton,
  MenuItem,
  Select,
  Tooltip,
  Paper,
  TextField,
  Collapse,
  Typography,
} from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import SpeakerNotesIcon from "@mui/icons-material/SpeakerNotes";
import FilterAltOffIcon from "@mui/icons-material/FilterAltOff";
import FilterAltIcon from "@mui/icons-material/FilterAlt";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 5,
};

const RequestApprovalScreen = () => {
  const [applications, setApplications] = useState([]);
  const [originalApplications, setOriginalApplications] = useState([]);

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  const [isFilter, setIsFilter] = useState(false);

  const [statusFilter, setStatusFilter] = useState("ALL");

  const [search, setSearch] = useState("");

  const debouncedSearch = useCallback(
    (search, statusFilter) => {
      const timer = setTimeout(() => {
        console.log({ search, statusFilter });
        console.log(
          "Test stringify " + JSON.stringify({ statusFilter, search })
        );
        setPaginationModel({
          ...DEFAULT_PAGINATION_MODEL,
          page: 0,
        });
        handleFetchData();
      }, 1000);

      return () => clearTimeout(timer);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [search, statusFilter]
  );

  useEffect(() => {
    return debouncedSearch(search, statusFilter);
  }, [search, statusFilter, debouncedSearch]);

  useEffect(() => {
    handleFetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  const handleFetchData = () => {
    const searchParam =
      search !== "" ? `&search=${encodeURIComponent(search)}` : "";
    const applicationStatusParam =
      statusFilter !== "" ? `&appStatus=${statusFilter}` : "";

    console.log({ searchParam, applicationStatusParam });
    setIsLoading(true);
    request(
      "get",
      `/application/get-application-by-semester/${SEMESTER}?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}${applicationStatusParam}`,
      (res) => {
        setApplications(res.data.data);
        setOriginalApplications(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const handleChangeStatusFilter = (e) => {
    setStatusFilter(e.target.value);
  };

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  /**
   * @description Handle change status of application
   * @index Find the index of the application in the applications array
   */
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
        applicationStatus: value,
      };

      setApplications(updatedApplications);
    }
  };

  /**
   * @index Find the index of the application in the applications array
   * @value Get the application status of the application
   */
  const handleSaveData = (rowData) => {
    const { id } = rowData;

    const index = applications.findIndex(
      (application) => application.id === id
    );

    const value = applications[index].applicationStatus;
    const updatedApplication = { ...rowData, applicationStatus: value };

    request(
      "put",
      `/application/update-application-status/${id}`,
      (res) => {
        const updatedOriginalApplications = originalApplications.map((app) =>
          app.id === id ? { ...app, applicationStatus: value } : app
        );
        setOriginalApplications(updatedOriginalApplications);
      },
      {},
      updatedApplication
    );
  };

  const applicationStatusCell = (params) => {
    const rowData = params.row;

    return (
      <Select
        value={rowData.applicationStatus || ""}
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
        <MenuItem value="REJECTED">
          <Chip label="REJECTED" color="error" variant="outlined" />
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
            rowData?.applicationStatus === originalApplication.applicationStatus
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

  const FilterComponent = () => {
    return (
      <div style={{ display: "flex", height: "40px" }}>
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
      flex: 1,
    },
    {
      field: "mssv",
      headerName: "MSSV",
      flex: 1,
    },
    {
      field: "cpa",
      headerName: "CPA",
      flex: 1,
    },
    {
      field: "englishScore",
      headerName: "Điểm tiếng anh",
      flex: 1,
    },
    {
      field: "applicationStatus",
      headerName: "Trạng thái",
      flex: 1.2,
      renderCell: applicationStatusCell,
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
    applicationStatus: application.applicationStatus,
    note: application.note,
  }));

  return (
    <Paper elevation={3} style={{ paddingTop: "1em" }}>
      <div style={styles.tableToolBar}>
        <Typography variant="h4" style={{ fontWeight: "bold" }}>
          Xác nhận tuyển dụng
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

export default RequestApprovalScreen;
