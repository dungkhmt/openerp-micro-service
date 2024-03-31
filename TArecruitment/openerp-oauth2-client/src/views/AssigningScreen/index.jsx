import { request } from "api";
import { useState, useEffect } from "react";
import { SEMESTER } from "config/localize";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { Chip, IconButton, MenuItem, Select, Tooltip } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";
import SpeakerNotesIcon from "@mui/icons-material/SpeakerNotes";

const AssigningScreen = () => {
  const [applications, setApplications] = useState([]);
  const [originalApplications, setOriginalApplications] = useState([]);

  useEffect(() => {
    request(
      "get",
      `/application/get-application-by-status-and-semester/${SEMESTER}/APPROVED`,
      (res) => {
        setApplications(res.data);
        setOriginalApplications(res.data);
        console.log(res.data);
      }
    );
  }, []);

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
          alert(e.response.data);
        },
      },
      updatedApplication
    );
  };

  const columns = [
    {
      title: "Mã lớp",
      field: "classCall.id",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "Mã môn học",
      field: "classCall.subjectId",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "Tên môn học",
      field: "classCall.subjectName",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "Thời gian",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
      render: (rowData) => (
        <span>
          Thứ {rowData.classCall.day}, tiết {rowData.classCall.startPeriod} -{" "}
          {rowData.classCall.endPeriod}
        </span>
      ),
    },
    {
      title: "Trạng thái",
      headerStyle: { fontWeight: "bold", textAlign: "center" },
      render: (rowData) => (
        <span>
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
        </span>
      ),
    },
    {
      title: "Hành động",
      headerStyle: { fontWeight: "bold", textAlign: "center" },
      cellStyle: { textAlign: "center" },
      render: (rowData) => (
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
      ),
    },
  ];

  return (
    <div>
      <h1>Phân công trợ giảng</h1>
      <StandardTable
        title=""
        columns={columns}
        data={applications}
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

export default AssigningScreen;
