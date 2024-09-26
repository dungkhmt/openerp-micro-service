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

const SkillManage = () => {
  const [skillId, setSkillId] = useState("");
  const [skillName, setSkillName] = useState("");
  const [skillDescription, setSkillDescription] = useState("");
  const [isAddNewSkill, setIsAddNewSkill] = useState(false);
  const [isLoadTableSkill, setIsLoadTableSkill] = useState(false);
  const [skills, setSkills] = useState([]);
  useEffect(() => {
    request(
      "get",
      "/skills",
      (res) => {
        setSkills(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
  }, [isLoadTableSkill]);

  const handleSubmitSkill = () => {
    const dataForm = {
      skillId: skillId,
      skillName: skillName,
      skillDescription: skillDescription,
    };
    request(
      "post",
      "/skills",
      () => {
        successNoti("Đã thêm mới kỹ năng thành công!", true);
        setIsLoadTableSkill(!isLoadTableSkill);
        setSkillId("");
        setSkillName("");
        setSkillDescription("");
      },
      (err) => {
        console.log(err);
      },
      dataForm
    );
  };

  const handleDeleteSkill = (skillId) => {
    request(
      "delete",
      `skill/${skillId}`,
      () => {
        successNoti("Đã xóa thành công kỹ năng", true);
        setIsLoadTableSkill(!isLoadTableSkill);
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
          onClick={() => setIsAddNewSkill(!isAddNewSkill)}
        >
          <ControlPointIcon /> Thêm kỹ năng
        </Button>
      </Box>
      {isAddNewSkill && (
        <Box mb={3}>
          <Grid container sx={{ mb: 3 }}>
            <Grid item={true} xs={6}>
              <TextField
                fullWidth={true}
                label="Id kỹ năng"
                variant="outlined"
                value={skillId}
                onChange={(e) => setSkillId(e.target.value)}
              />
            </Grid>
          </Grid>
          <Grid container sx={{ mb: 3 }}>
            <Grid item={true} xs={6}>
              <TextField
                fullWidth={true}
                label="Tên kỹ năng"
                variant="outlined"
                value={skillName}
                onChange={(e) => setSkillName(e.target.value)}
              />
            </Grid>
          </Grid>
          <Grid container sx={{ mb: 3 }}>
            <Grid item={true} xs={6}>
              <TextField
                label="Mô tả kỹ năng"
                multiline
                fullWidth={true}
                rows={5}
                sx={{ mb: 3 }}
                onChange={(e) => setSkillDescription(e.target.value)}
              />
            </Grid>
          </Grid>
          <Box>
            <Button
              color="success"
              variant="contained"
              onClick={handleSubmitSkill}
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
                <TableCell>Id kỹ năng</TableCell>
                <TableCell align="right">Tên kỹ năng</TableCell>
                <TableCell align="right">Mô tả</TableCell>
                <TableCell align="right">Xóa</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {skills.map((row) => (
                <TableRow
                  key={row.skillId}
                  sx={{ "&:last-child td, &:last-child th": { border: 0 } }}
                >
                  <TableCell component="th" scope="row">
                    {row.skillId}
                  </TableCell>
                  <TableCell align="right">{row.name}</TableCell>
                  <TableCell align="right">{row.description}</TableCell>
                  <TableCell align="right">
                    <Button onClick={() => handleDeleteSkill(row.skillId)}>
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

export default SkillManage;
