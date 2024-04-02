import { request } from "api";
import { useState, useEffect } from "react";
import { SEMESTER } from "config/localize";
import {
  Button,
  Chip,
  IconButton,
  MenuItem,
  Select,
  Tooltip,
} from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import SpeakerNotesIcon from "@mui/icons-material/SpeakerNotes";
import { errorNoti } from "utils/notification";
import { DataGrid } from "@mui/x-data-grid";
import styles from "./index.style";

const AssigningScreen = () => {
  const [applications, setApplications] = useState([]);
  const [originalApplications, setOriginalApplications] = useState([]);

  useEffect(() => {
    handleFetchData();
  }, []);

  const handleFetchData = () => {
    request(
      "get",
      `/application/get-application-by-status-and-semester/${SEMESTER}/APPROVED`,
      (res) => {
        setApplications(res.data);
        setOriginalApplications(res.data);
        console.log(res.data);
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
    request("get", `/application/auto-assign-class/${SEMESTER}`, (res) => {
      handleFetchData();
    });
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
        sx={{
          boxShadow: "none",
          ".MuiOutlinedInput-notchedOutline": { border: 0 },
          "&.MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline": {
            border: 0,
          },
          "&.MuiOutlinedInput-root.Mui-focused .MuiOutlinedInput-notchedOutline":
            {
              border: 0,
            },
          width: 145,
        }}
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
            rowData.assignStatus ===
            originalApplications.find(
              (application) => application.id === rowData.id
            ).assignStatus
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
      flex: 1,
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

  return (
    <div>
      <h1>Phân công trợ giảng</h1>
      <Button
        style={styles.autoButton}
        variant="contained"
        onClick={handleAutoAssign}
      >
        Sắp xếp tự động
      </Button>
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

export default AssigningScreen;
