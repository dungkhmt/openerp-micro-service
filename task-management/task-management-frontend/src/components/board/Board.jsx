import ArrowDropDownCircleRoundedIcon from "@mui/icons-material/ArrowDropDownCircleRounded";
import {
  Box,
  Grid,
  IconButton,
  MenuItem,
  Skeleton,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { request } from "../../api";
import DateTimePickerBasic from "../datetimepicker/DateTimePickerBasic";
import BoardContent from "./BoardContent";

const Board = () => {
  const { projectId } = useParams();

  const [categories, setCategories] = useState([]);
  const [priorities, setPriorities] = useState([]);
  const [assignees, setAssignees] = useState([]);

  const [categoryId, setCategoryId] = useState("");
  const [partyId, setPartyId] = useState("");
  const [priorityId, setPriorityId] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [keyName, setKeyName] = useState("");

  const [data, setData] = useState(null);

  const [isExtraOptions, setIsExtraOptions] = useState(false);

  useEffect(() => {
    request(
      "get",
      "/task-categories",
      (res) => {
        setCategories(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
    request(
      "get",
      `/projects/${projectId}/members`,
      (res) => {
        setAssignees(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
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
  }, [projectId]);

  useEffect(() => {
    const dataForm = {
      projectId: projectId,
      categoryId: categoryId,
      partyId: partyId,
      priorityId: priorityId,
      startDate: startDate,
      endDate: endDate,
      keyName: keyName,
    };
    request(
      "post",
      "/board",
      (res) => {
        setData(res.data);
        console.log(res.data);
      },
      (err) => {
        console.log(err);
      },
      dataForm
    );
  }, [partyId, categoryId, priorityId, keyName, startDate, endDate, projectId]);

  return (
    <>
      <Box>
        <Typography variant="h4" mb={4} component={"h4"}>
          Board
        </Typography>
      </Box>
      <Box mb={2}>
        <Grid container columnSpacing={2}>
          <Grid item={true} xs={6}>
            <Grid container mb={3} spacing={2}>
              <Grid item={true} xs={6}>
                <TextField
                  select
                  fullWidth
                  label={"Danh mục"}
                  defaultValue=""
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                  sx={{ backgroundColor: "#fff" }}
                >
                  <MenuItem value={""}>Không chọn</MenuItem>
                  {categories.map((item) => (
                    <MenuItem key={item.categoryId} value={item.categoryId}>
                      {item.categoryName}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
              <Grid item={true} xs={6}>
                <TextField
                  select
                  fullWidth
                  label={"Gán cho"}
                  defaultValue=""
                  value={partyId}
                  onChange={(e) => setPartyId(e.target.value)}
                  sx={{ backgroundColor: "#fff" }}
                >
                  <MenuItem value={""}>Không chọn</MenuItem>
                  {assignees.map((item) => (
                    <MenuItem key={item.partyId} value={item.partyId}>
                      {item.id} ({item.firstName} {item.lastName})
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
            </Grid>
          </Grid>
          <Grid item={true} xs={6}>
            <Grid container mb={3}>
              <Grid item={true} xs={6}>
                <TextField
                  select
                  fullWidth
                  label={"Độ ưu tiên"}
                  defaultValue=""
                  value={priorityId}
                  onChange={(e) => setPriorityId(e.target.value)}
                  sx={{ backgroundColor: "#fff" }}
                >
                  <MenuItem value={""}>Không chọn</MenuItem>
                  {priorities.map((item) => (
                    <MenuItem key={item.priorityId} value={item.priorityId}>
                      {item.priorityName}
                    </MenuItem>
                  ))}
                </TextField>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
        <Box display={"flex"} alignItems={"center"} mb={2}>
          <Typography>Bộ lọc mở rộng</Typography>
          <IconButton
            color="primary"
            onClick={() => setIsExtraOptions(!isExtraOptions)}
          >
            <ArrowDropDownCircleRoundedIcon />
          </IconButton>
        </Box>
        {isExtraOptions && (
          <Grid container>
            <Grid item={true} xs={6}>
              <Grid container mb={3} spacing={2}>
                <Grid item={true} xs={6}>
                  <DateTimePickerBasic
                    label={"Thời điểm bắt đầu"}
                    onChange={(date) => {
                      setStartDate(date);
                    }}
                    value={startDate}
                  />
                </Grid>
                <Grid item={true} xs={6}>
                  <DateTimePickerBasic
                    label={"Thời điểm kết thúc"}
                    onChange={(date) => {
                      setEndDate(date);
                    }}
                    value={endDate}
                  />
                </Grid>
              </Grid>
            </Grid>
            <Grid item={true} xs={12}>
              <Grid container>
                <Grid item={true} xs={6}>
                  <TextField
                    variant="outlined"
                    label="Tên dự án"
                    onChange={(e) => setKeyName(e.target.value)}
                    fullWidth
                    sx={{ backgroundColor: "#fff" }}
                  />
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        )}
      </Box>
      {data ? (
        <BoardContent data={data} />
      ) : (
        <Stack spacing={1}>
          <Skeleton variant="text" />
          <Skeleton variant="circular" width={40} height={40} />
          <Skeleton variant="rectangular" height={200} />
        </Stack>
      )}
    </>
  );
};

export default Board;
