import { Box, Button, Typography } from "@mui/material";


export const ClassroomToolbar = ({ onCreateNew, onImportExcel }) => {
  return (
    <div>
      <Box style={{ width: "100%", display: "flex", justifyContent: "center", alignItems: "center" }}>
        <Typography variant="h5">Danh sách phòng học</Typography>
      </Box>
      
      <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
        <Button
          variant="outlined"
          color="primary"
          style={{ marginRight: "8px" }}
          onClick={onCreateNew}
        >
          Thêm mới
        </Button>
      </div>

      <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
        <Button
          variant="outlined"
          color="primary"
          style={{ marginRight: "8px", marginTop: "8px" }}
          onClick={onImportExcel}
        >
          Tải lên danh sách phòng
        </Button>
      </div>
    </div>
  );
};