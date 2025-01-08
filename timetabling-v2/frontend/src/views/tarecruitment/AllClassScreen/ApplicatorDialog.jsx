import { Dialog, DialogContent, DialogTitle, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { request } from "api";
import { DataGrid } from "@mui/x-data-grid";
import { applicatorDialogStyles } from "./index.style";
import { applicationUrl } from "../apiURL";

const DEFAULT_PAGINATION_MODEL = {
  page: 0,
  pageSize: 5,
};

const ApplicatorDialog = ({ open, handleClose, classId }) => {
  const [applicators, setApplicators] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [totalElements, setTotalElements] = useState(0);

  const [paginationModel, setPaginationModel] = useState(
    DEFAULT_PAGINATION_MODEL
  );

  useEffect(() => {
    setIsLoading(true);
    if (classId) {
      request(
        "get",
        `${applicationUrl.getApplicationByClass}/${classId}?page=${paginationModel.page}&limit=${paginationModel.pageSize}`,
        (res) => {
          setApplicators(res.data.data);
          setTotalElements(res.data.totalElement);
          setIsLoading(false);
        }
      );
    }
  }, [classId, paginationModel]);

  const dataGridColumns = [
    {
      field: "mssv",
      headerName: "MSSV",
      align: "center",
      headerAlign: "center",
    },
    {
      field: "name",
      headerName: "Tên sinh viên",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
    {
      field: "phoneNumber",
      headerName: "Số điện thoại",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
    {
      field: "email",
      headerName: "Email",
      align: "center",
      headerAlign: "center",
      flex: 1,
    },
  ];

  const dataGridRows = applicators.map((applicator, index) => ({
    id: index,
    mssv: applicator.mssv,
    name: applicator.name,
    phoneNumber: applicator.phoneNumber,
    email: applicator.email,
    applicationStatus: applicator.applicationStatus,
    assignStatus: applicator.assignStatus,
  }));

  return (
    <div>
      <Dialog
        open={open}
        onClose={handleClose}
        fullWidth
        maxWidth="lg"
        sx={{
          "& .MuiDialog-paper": {
            height: 550,
          },
        }}
      >
        <DialogTitle style={applicatorDialogStyles.dialogTitle}>
          <Typography variant="h5" fontWeight="bold">
            Danh sách sinh viên đăng ký mã lớp {classId}
          </Typography>
        </DialogTitle>
        <DialogContent>
          <DataGrid
            loading={isLoading}
            rowHeight={60}
            rows={dataGridRows}
            columns={dataGridColumns}
            rowCount={totalElements}
            pagination
            paginationMode="server"
            paginationModel={paginationModel}
            onPaginationModelChange={setPaginationModel}
            pageSizeOptions={[5]}
            checkboxSelection={false}
            disableRowSelectionOnClick
            getRowClassName={(params) =>
              params.row.applicationStatus === "APPROVED" &&
              params.row.assignStatus === "APPROVED"
                ? "bold-row"
                : ""
            }
            sx={{
              "& .bold-row": {
                fontWeight: "bold",
              },
              fontSize: 16,
              height: "45vh",
            }}
          />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ApplicatorDialog;
