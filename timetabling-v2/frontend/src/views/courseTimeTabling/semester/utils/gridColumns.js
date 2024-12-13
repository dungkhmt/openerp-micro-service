import { Button } from "@mui/material";

export const getColumns = ({ handleUpdate, handleDelete }) => [
  {
    headerName: "Semester ID",
    field: "id",
    width: 170,
  },
  {
    headerName: "Kỳ học",
    field: "semester",
    width: 170,
  },
  {
    headerName: "Mô tả",
    field: "description",
    width: 300,
  },
  {
    headerName: "Hành động",
    field: "actions",
    width: 200,
    renderCell: (params) => (
      <div>
        <Button
          variant="outlined"
          color="primary"
          onClick={() => handleUpdate(params.row)}
          style={{ marginRight: "8px" }}
        >
          Sửa
        </Button>
        <Button
          variant="outlined"
          color="secondary"
          onClick={() => handleDelete(params.row.id)}
        >
          Xóa
        </Button>
      </div>
    ),
  },
];