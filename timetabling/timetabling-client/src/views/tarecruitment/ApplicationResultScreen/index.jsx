import {
  Button,
  Chip,
  Paper,
  Typography,
  Box,
  IconButton,
} from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { useEffect, useState } from "react";
import { styles } from "./index.style";
import DeleteDialog from "../components/DeleteDialog";
import UpdateApplicationDialog from "./UpdateApplicationDialog";
import { applicationUrl } from "../apiURL";
import { useHistory } from "react-router-dom"; // Updated import
import InfoOutlinedIcon from "@mui/icons-material/InfoOutlined";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 10,
};

const ApplicationResultScreen = () => {
  const [applications, setApplications] = useState([]);

  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [deleteId, setDeleteId] = useState("");
  const [openUpdateDialog, setOpenUpdateDialog] = useState(false);
  const [updateId, setUpdateId] = useState("");

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );
  const history = useHistory();

  const [rowSelect, setRowSelect] = useState([]);

  useEffect(() => {
    handleFetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [paginationModel]);

  const handleFetchData = () => {
    setIsLoading(true);
    request(
      "get",
      `${applicationUrl.getMyApplication}?page=${paginationModel.page}&limit=${paginationModel.pageSize}`,
      (res) => {
        setApplications(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
  };

  const handleCloseUpdateDialog = () => {
    setOpenUpdateDialog(false);
  };

  const handleOpenUpdateDialog = (application) => {
    setUpdateId(application.id);
    setOpenUpdateDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDeleteDialog(false);
  };

  const handleDeleteApplication = () => {
    if (rowSelect.length === 0) {
      request(
        "delete",
        `${applicationUrl.deleteApplication}/${deleteId}`,
        (res) => {
          handleFetchData();
          setOpenDeleteDialog(false);
        }
      );
    } else if (rowSelect.length === 1) {
      request(
        "delete",
        `${applicationUrl.deleteApplication}/${rowSelect[0]}`,
        (res) => {
          handleFetchData();
          setOpenDeleteDialog(false);
        }
      );
    } else {
      let idList = rowSelect;
      request(
        "delete",
        `${applicationUrl.deleteMultipleApplication}`,
        (res) => {
          handleFetchData();
          setOpenDeleteDialog(false);
        },
        {},
        idList
      );
    }
  };

  const handleOpenDialog = (application) => {
    setOpenDeleteDialog(true);
    setDeleteId(application.id);
  };

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

  const actionCell = (params) => {
    const rowData = params.row;

    return (
      <div>
        <Box display={"flex"}>
          <Button
            variant="outlined"
            onClick={() => {
              history.push(`/ta-recruitment/student/result/${rowData.id}`);
            }}
          >
            Xem chi tiết
          </Button>
        </Box>
      </div>
    );
  };

  const dataGridColumns = [
    {
      field: "id",
      headerName: "Mã đơn xin",
      align: "center",
      headerAlign: "center",
    },
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
      field: "applicationStatus",
      align: "center",
      headerAlign: "center",
    },
    {
      headerName: "Trạng thái phân công",
      flex: 1,
      renderCell: assignStatusCell,
      field: "assignStatus",
      align: "center",
      headerAlign: "center",
    },
    {
      headerName: "Hành động",
      field: "actions",
      flex: 1,
      renderCell: actionCell,
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
    actions: { rowData: application },
  }));

  return (
    <Paper elevation={3}>
      <div>
        <div style={styles.tableToolBar}>
          <Typography variant="h4" style={styles.title}>
            Kết quả tuyển dụng
          </Typography>

          <Button
            color="error"
            variant="outlined"
            disabled={rowSelect.length === 0}
            onClick={() => setOpenDeleteDialog(true)}
          >
            Xóa
          </Button>
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
          checkboxSelection={true}
          onRowSelectionModelChange={(newRowSelectionModel) => {
            setRowSelect(newRowSelectionModel);
            console.log(newRowSelectionModel);
          }}
          disableRowSelectionOnClick
        />
      </div>

      <DeleteDialog
        open={openDeleteDialog}
        handleDelete={handleDeleteApplication}
        handleClose={handleCloseDialog}
      />

      <UpdateApplicationDialog
        open={openUpdateDialog}
        handleClose={handleCloseUpdateDialog}
        applicationId={updateId}
        fetchData={handleFetchData}
      />
    </Paper>
  );
};

export default ApplicationResultScreen;
