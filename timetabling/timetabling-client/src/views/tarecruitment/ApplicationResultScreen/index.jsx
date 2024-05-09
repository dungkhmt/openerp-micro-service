import { Chip, Paper, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { useEffect, useState } from "react";
import styles from "./index.style";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 5,
};

const ApplicationResultScreen = () => {
  const [applications, setApplications] = useState([]);

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  useEffect(() => {
    setIsLoading(true);
    request(
      "get",
      `/application/my-applications?page=${paginationModel.page}&limit=${paginationModel.pageSize}`,
      (res) => {
        setApplications(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  }, [paginationModel]);

  const applicationStatusCell = (params) => {
    const rowData = params.row;

    return (
      <div>
        {rowData.applicationStatus === "PENDING" ? (
          <Chip label="PENDING" color="warning" variant="outlined" />
        ) : rowData.applicationStatus === "APPROVED" ? (
          <Chip label="APPROVED" color="success" variant="outlined" />
        ) : (
          <Chip label="REJECTED" color="error" variant="outlined" />
        )}
      </div>
    );
  };

  const assignStatusCell = (params) => {
    const rowData = params.row;

    return (
      <div>
        {rowData.assignStatus === "PENDING" ? (
          <Chip label="PENDING" color="warning" variant="outlined" />
        ) : rowData.assignStatus === "APPROVED" ? (
          <Chip label="APPROVED" color="success" variant="outlined" />
        ) : (
          <Chip label="CANCELED" color="error" variant="outlined" />
        )}
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
      field: "subjectId",
      headerName: "Mã môn học",
      flex: 1,
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
      headerName: "Trạng thái đăng ký",
      flex: 1,
      renderCell: applicationStatusCell,
      align: "center",
      headerAlign: "center",
    },
    {
      headerName: "Trạng thái phân công",
      flex: 1,
      renderCell: assignStatusCell,
      align: "center",
      headerAlign: "center",
    },
  ];

  const dataGridRows = applications.map((application) => ({
    id: application.id,
    classId: application.classCall.id,
    subjectId: application.classCall.subjectId,
    subjectName: application.classCall.subjectName,
    day: `Thứ ${application.classCall.day}, tiết ${application.classCall.startPeriod} - ${application.classCall.endPeriod}`,
    applicationStatus: application.applicationStatus,
    assignStatus: application.assignStatus,
  }));

  return (
    <Paper elevation={3}>
      <div>
        <div style={styles.tableToolBar}>
          <Typography
            variant="h4"
            style={{
              fontWeight: "bold",
              marginBottom: "0.5em",
              paddingTop: "1em",
            }}
          >
            Kết quả tuyển dụng
          </Typography>
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
      </div>
    </Paper>
  );
};

export default ApplicationResultScreen;
