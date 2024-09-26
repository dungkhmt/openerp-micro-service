import ControlPointIcon from "@mui/icons-material/ControlPoint";
import {
  Box,
  Button,
  Grid,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { successNoti } from "../../../utils/notification";
import { request } from "../../../api";

const PriorityManage = () => {
  const [priorityId, setPriorityId] = useState("");
  const [priorityName, setPriorityName] = useState("");
  const [isAddNewPriority, setIsAddNewPriority] = useState(false);
  const [isLoadTablePriority, setIsLoadTablePriority] = useState(false);
  const [priorities, setPriorities] = useState([]);
  useEffect(() => {
    request(
      "get",
      "/task-priorities",
      (res) => {
        setPriorities(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
  }, [isLoadTablePriority]);

  const handleSubmitPriority = () => {
    const dataForm = {
      priorityId: priorityId,
      priorityName: priorityName,
    };
    request(
      "post",
      "/task-priorities",
      () => {
        successNoti("Đã thêm mới độ ưu tiên thành công!", true);
        setIsLoadTablePriority(!isLoadTablePriority);
        setPriorityId("");
        setPriorityName("");
      },
      (err) => {
        console.log(err);
      },
      dataForm
    );
  };

  const handleDeletePriority = (priorityId) => {
    request(
      "delete",
      `task-priority/${priorityId}`,
      () => {
        successNoti("Đã xóa thành công độ ưu tiên", true);
        setIsLoadTablePriority(!isLoadTablePriority);
      },
      (err) => {
        console.log(err);
      }
    );
  };

  return (
    <>
      <Box mb={5}>
        <Typography>QUẢN LÝ ĐỘ ƯU TIÊN</Typography>
      </Box>
      <Box mb={3}>
        <Button
          variant="contained"
          onClick={() => setIsAddNewPriority(!isAddNewPriority)}
        >
          <ControlPointIcon /> Thêm độ ưu tiên
        </Button>
      </Box>
      {isAddNewPriority && (
        <Box mb={3}>
          <Grid container sx={{ mb: 3 }}>
            <Grid item={true} xs={6}>
              <TextField
                fullWidth={true}
                label="Id độ ưu tiên"
                variant="outlined"
                value={priorityId}
                onChange={(e) => setPriorityId(e.target.value)}
              />
            </Grid>
          </Grid>
          <Grid container sx={{ mb: 3 }}>
            <Grid item={true} xs={6}>
              <TextField
                fullWidth={true}
                label="Tên độ ưu tiên"
                variant="outlined"
                value={priorityName}
                onChange={(e) => setPriorityName(e.target.value)}
              />
            </Grid>
          </Grid>
          <Box>
            <Button
              color="success"
              variant="contained"
              onClick={handleSubmitPriority}
            >
              Submit
            </Button>
          </Box>
        </Box>
      )}
      <Box mb={3}>
        <Typography>DANH SÁCH ĐỘ ƯU TIÊN</Typography>
      </Box>
      <Box>
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} size="small" aria-label="a dense table">
            <TableHead>
              <TableRow>
                <TableCell>Id độ ưu tiên</TableCell>
                <TableCell align="right">Tên độ ưu tiên</TableCell>
                <TableCell align="right">Xóa</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {priorities.map((row) => (
                <TableRow
                  key={row.priorityId}
                  sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                >
                  <TableCell component="th" scope="row">
                    {row.priorityId}
                  </TableCell>
                  <TableCell align="right">{row.priorityName}</TableCell>
                  <TableCell align="right">
                    <Button
                      onClick={() => handleDeletePriority(row.priorityId)}
                    >
                      Delete
                    </Button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </Box>
    </>
  );
};

export default PriorityManage;
