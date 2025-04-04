import { Dialog, DialogContent, DialogTitle, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { request } from "api";
import { DataGrid } from "@mui/x-data-grid";

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
    request(
      "get",
      `/application/get-application-by-class/${classId}?page=${paginationModel.page}&limit=${paginationModel.pageSize}`,
      (res) => {
        setApplicators(res.data.data);
        setTotalElements(res.data.totalElement);
        setIsLoading(false);
      }
    );
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
        <DialogTitle sx={{ textAlign: "center", marginTop: "1%" }}>
          <Typography variant="h5" fontWeight="bold">
            Danh sách sinh viên đăng ký mã lớp {classId}
          </Typography>
        </DialogTitle>
        <DialogContent>
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
            pageSizeOptions={[5]}
            checkboxSelection={false}
            disableRowSelectionOnClick
          />
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default ApplicatorDialog;
