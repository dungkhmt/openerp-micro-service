import { Chip } from "@mui/material";
import { request } from "api";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useState } from "react";

const ApplicationResultScreen = () => {
  const [applications, setApplications] = useState([]);

  useEffect(() => {
    request("get", `/application/my-applications`, (res) => {
      setApplications(res.data);
      console.log(res.data);
    });
  }, []);

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
      title: "Trạng thái đăng ký",
      field: "applicationStatus",
      headerStyle: { fontWeight: "bold" },
      render: (rowData) => (
        <div>
          {rowData.applicationStatus === "PENDING" ? (
            <Chip label="PENDING" color="warning" variant="outlined" />
          ) : rowData.applicationStatus === "APPROVED" ? (
            <Chip label="APPROVED" color="success" variant="outlined" />
          ) : (
            <Chip label="REJECTED" color="error" variant="outlined" />
          )}
        </div>
      ),
    },
    {
      title: "Trạng thái phân công",
      field: "assignStatus",
      headerStyle: { fontWeight: "bold" },
      render: (rowData) => (
        <div>
          {rowData.assignStatus === "NONE" ? (
            <Chip label="PENDING" color="warning" variant="outlined" />
          ) : (
            <Chip label="APPROVED" color="success" variant="outlined" />
          )}
        </div>
      ),
    },
  ];

  return (
    <div>
      <h1>Kết quả tuyển dụng</h1>
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

export default ApplicationResultScreen;
