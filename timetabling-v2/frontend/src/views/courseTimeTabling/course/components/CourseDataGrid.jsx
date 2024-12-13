import { Box, Button, Typography } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";


export default function CourseDataGrid({ courses, isLoading, onEdit, onDelete, onCreate }) {
  const columns = [
    { headerName: "Mã môn học", field: "id", width: 170 },
    { headerName: "Tên môn học", field: "courseName", width: 400 },
    { headerName: "Số tín chỉ", field: "credit", width: 170 },
    {
      headerName: "Hành động",
      field: "actions",
      width: 200,
      renderCell: (params) => (
        <div>
          <Button
            variant="outlined"
            color="primary"
            onClick={() => onEdit(params.row)}
            style={{ marginRight: "8px" }}
          >
            Sửa
          </Button>
          <Button
            variant="outlined"
            color="secondary"
            onClick={() => onDelete(params.row.id)}
          >
            Xóa
          </Button>
        </div>
      ),
    },
  ];

  const CustomToolbar = () => (
    <>
      <Box sx={{ width: "100%", display: "flex", justifyContent: "center" }}>
        <Typography variant="h5">Danh sách môn học</Typography>
      </Box>
      <Box
        sx={{ display: "flex", justifyContent: "flex-end", marginRight: "8px" }}
      >
        <Button variant="outlined" color="primary" onClick={onCreate}>
          Thêm mới
        </Button>
      </Box>
    </>
  );

  return (
    <DataGrid
      loading={isLoading}
      className="h-full"
      components={{ Toolbar: CustomToolbar }}
      rows={courses}
      columns={columns}
      pageSize={10}
    />
  );
}