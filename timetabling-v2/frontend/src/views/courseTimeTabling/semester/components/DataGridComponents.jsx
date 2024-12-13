import { Box, Button, Typography } from "@mui/material";
import CreateNewSemester from "../CreateNewSemesterScreen";


export const DataGridToolbar = ({ isDialogOpen, handleCreate, handleCloseDialog, selectedSemester }) => (
  <div>
    <div style={{ display: "flex", gap: 16, justifyContent: "flex-end" }}>
      <Button
        variant="outlined"
        color="primary"
        style={{ marginRight: "8px" }}
        onClick={handleCreate}
      >
        Thêm mới
      </Button>
    </div>
    <CreateNewSemester
      open={isDialogOpen}
      handleClose={handleCloseDialog}
      selectedSemester={selectedSemester}
    />
  </div>
);

export const DataGridTitle = () => (
  <Box style={{
    width: "100%",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
  }}>
    <Typography variant="h5">Danh sách kỳ học</Typography>
  </Box>
);