import { request } from "api";
import { useEffect, useState } from "react";
import { SEMESTER } from "config/localize";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { Chip, IconButton, MenuItem, Select } from "@mui/material";
import SaveIcon from "@mui/icons-material/Save";

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
        setOriginalApplications(applications);
      },
      {},
      updatedApplication
    );
  };

  const columns = [
    {
      title: "Mã lớp",
      field: "classCall.id",
      headerStyle: { fontWeight: "bold", width: "8%" },
      cellStyle: { fontWeight: "470", width: "8%" },
    },
    {
      title: "Môn học",
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
      title: "Tên sinh viên",
      field: "name",
      headerStyle: { fontWeight: "bold", width: "15%" },
      cellStyle: { fontWeight: "470", width: "15%" },
    },
    {
      title: "MSSV",
      field: "mssv",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "CPA",
      field: "cpa",
      headerStyle: { fontWeight: "bold", width: "8%" },
      cellStyle: { fontWeight: "470", width: "8%" },
    },
    {
      title: "Điểm tiếng anh",
      field: "englishScore",
      headerStyle: { fontWeight: "bold" },
      cellStyle: { fontWeight: "470" },
    },
    {
      title: "Trạng thái",
      headerStyle: { fontWeight: "bold", textAlign: "center" },
      render: (rowData) => (
        <span>
          <Select
            value={rowData?.applicationStatus || ""}
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
              <Chip label="APPROVED" color="success" />
            </MenuItem>
            <MenuItem value="PENDING">
              <Chip label="PENDING" color="warning" />
            </MenuItem>
            <MenuItem value="REJECTED">
              <Chip label="REJECTED" color="error" />
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
              rowData.applicationStatus ===
              originalApplications.find(
                (application) => application.id === rowData.id
              ).applicationStatus
            }
          >
            <SaveIcon />
          </IconButton>
        </span>
      ),
    },
  ];

  return (
    <div>
      <h1>Xác nhận tuyển dụng</h1>
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

export default RequestApprovalScreen;
