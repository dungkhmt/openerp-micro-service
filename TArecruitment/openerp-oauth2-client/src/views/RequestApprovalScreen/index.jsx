import { request } from "api";
import { useEffect, useState } from "react";
import { SEMESTER } from "config/localize";
import { Chip, IconButton, MenuItem, Select, Tooltip } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import SpeakerNotesIcon from "@mui/icons-material/SpeakerNotes";
import { DataGrid } from "@mui/x-data-grid";

const RequestApprovalScreen = () => {
  const [applications, setApplications] = useState([]);
  const [originalApplications, setOriginalApplications] = useState([]);

  useEffect(() => {
    request(
      "get",
      `/application/get-application-by-semester/${SEMESTER}`,
      (res) => {
        setApplications(res.data);
        setOriginalApplications(res.data);
      }
    );
  }, []);

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
        <MenuItem value="REJECTED">
          <Chip label="REJECTED" color="error" variant="outlined" />
        </MenuItem>
      </Select>
    );
  };

  const actionCell = (params) => {
    const rowData = params.row;
    console.log(rowData);
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
            rowData.applicationStatus ===
            originalApplications.find(
              (application) => application.id === rowData.id
            ).applicationStatus
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
      field: "applicationStatus",
      headerName: "Trạng thái",
      flex: 1,
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
    <div>
      <h1>Xác nhận tuyển dụng</h1>
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

export default RequestApprovalScreen;
