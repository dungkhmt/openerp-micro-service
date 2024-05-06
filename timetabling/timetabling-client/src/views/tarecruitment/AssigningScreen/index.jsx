import { request } from "api";
import { useState, useEffect, useCallback } from "react";
import { SEMESTER } from "../config/localize";
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
import { errorNoti } from "utils/notification";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 5,
};

const AssigningScreen = () => {
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
    const assignStatusParam =
      statusFilter !== "" ? `&assignStatus=${statusFilter}` : "";

    console.log({ searchParam, assignStatusParam });

    setIsLoading(true);
    request(
      "get",
      `/application/get-application-by-status-and-semester/${SEMESTER}/APPROVED?page=${paginationModel.page}&limit=${paginationModel.pageSize}${searchParam}${assignStatusParam}`,
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
      `/application/update-assign-status/${id}`,
      (res) => {
        const updatedOriginalApplications = originalApplications.map((app) =>
          app.id === id ? { ...app, assignStatus: value } : app
        );
        setOriginalApplications(updatedOriginalApplications);
      },
      {
        onError: (e) => {
          errorNoti(e.response.data);
        },
      },
      updatedApplication
    );
  };

  const handleAutoAssign = () => {
    setIsLoading(true);
    request("get", `/application/auto-assign-class/${SEMESTER}`, (res) => {
      handleFetchData();
      setIsLoading(false);
    });
  };

  const handleChangeStatusFilter = (e) => {
    setStatusFilter(e.target.value);
  };

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };

  const handleDownloadFile = () => {
    request(
      "get",
      `/application/get-assign-list-file/${SEMESTER}`,
      (res) => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", "assign_list.xlsx");
        document.body.appendChild(link);
        link.click();
      },
      {},
      {},
      { responseType: "arraybuffer" }
    );
  };

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

  return (
    <Paper elevation={3}>
      <div style={styles.tableToolBar}>
        <Typography
          variant="h4"
          style={{
            fontWeight: "bold",
            marginBottom: "0.5em",
            paddingTop: "1em",
          }}
        >
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

            <Button variant="outlined" onClick={handleDownloadFile}>
              Xuất file
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
        sx={{ fontSize: 16, height: "65vh" }}
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
