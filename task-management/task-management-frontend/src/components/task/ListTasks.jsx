import LocalFireDepartmentIcon from "@mui/icons-material/LocalFireDepartment";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import TabContext from "@mui/lab/TabContext";
import TabList from "@mui/lab/TabList";
import TabPanel from "@mui/lab/TabPanel";
import {
  Avatar,
  Box,
  Button,
  Grid,
  IconButton,
  Menu,
  MenuItem,
  Paper,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TablePagination,
  TableRow,
  Typography,
} from "@mui/material";
import * as React from "react";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { request } from "../../api";
import PieChart from "../chart/PieChart";
import CategoryElement from "../common/CategoryElement";
import HistoryStatus from "../common/HistoryStatus";
import { useScroll } from "../customhook/useScroll";
import ExportExcelButton from "../exportexcel/ExportExcelButton";
import AddNewMemberModal from "../projects/AddNewMemberModal";
import { boxChildComponent, boxComponentStyle } from "../utils/constant";
import { LimitString } from "../utils/helpers";
import MemberTable from "./MemberTable";

const ListTasks = () => {
  const [openAddNewMemberModal, setOpenAddNewMemberModal] = useState(false);

  const [members, setMembers] = useState([]);
  const [isLoadMember, setIsLoadMember] = useState(false);
  const { projectId } = useParams();
  const [projectName, setProjectName] = useState("");

  const [executeScroll, elRef] = useScroll();

  const [anchorEl, setAnchorEl] = useState(null);
  const openMenu = Boolean(anchorEl);
  const handleClickMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };
  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  const columns = [
    {
      id: "category",
      label: "Loại nhiệm vụ",
      minWidth: 170,
      align: "center",
    },
    {
      id: "taskName",
      label: "Tên nhiệm vụ",
      minWidth: 170,
      align: "center",
    },
    {
      id: "description",
      label: "Mô tả",
      minWidth: 170,
      align: "center",
    },
    {
      id: "status",
      label: "Trạng thái",
      minWidth: 170,
      align: "center",
    },
    {
      id: "priority",
      label: "Mức độ ưu tiên",
      minWidth: 170,
      align: "center",
    },
    {
      id: "assignee",
      label: "Gán cho",
      minWidth: 170,
      align: "center",
    },
    {
      id: "dueDate",
      label: "Hạn kết thúc",
      minWidth: 170,
      align: "center",
    },
  ];

  const [history, setHistory] = useState([]);
  const [tasks, setTasks] = useState([]);

  const [labelsCategory, setLabelsCategory] = useState([]);
  const [dataChartCategory, setDataChartCategory] = useState([]);

  const [labelsStatus, setLabelsStatus] = useState([]);
  const [dataChartStatus, setDataChartStatus] = useState([]);

  const [rows, setRows] = useState([]);

  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const [value, setValue] = React.useState("4");

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  useEffect(() => {
    request(
      "get",
      `/projects/${projectId}/members`,
      (res) => {
        setMembers(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
  }, [isLoadMember]);

  useEffect(() => {
    executeScroll;

    request(
      "get",
      `/projects/${projectId}/tasks`,
      (res) => {
        setTasks(res.data);
        let rowsTask = res.data.map((task) => {
          return {
            id: task.id,
            category: task.taskCategory?.categoryName,
            taskName: LimitString(50, task.name),
            description: LimitString(50, task.description),
            status:
              task.statusItem === null
                ? "Không xác định !"
                : task.statusItem?.statusCode,
            priority: task.taskPriority?.priorityName,
            assignee: task.assignee,
            dueDate: task.dueDate,
            categoryId: task.taskCategory?.categoryId,
            outOfDate: task.outOfDate,
          };
        });
        console.log(rowsTask);
        setRows(rowsTask);
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      `/projects/${projectId}/statics/category`,
      (res) => {
        setLabelsCategory(res.data.map((item) => item.name));
        setDataChartCategory(res.data.map((item) => item.value));
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      `/projects/${projectId}/statics/status`,
      (res) => {
        setLabelsStatus(res.data.map((item) => item.name));
        setDataChartStatus(res.data.map((item) => item.value));
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      `/projects/${projectId}`,
      (res) => {
        setProjectName(res.data.name);
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      `/projects/${projectId}/history`,
      (res) => {
        setHistory(res.data);
      },
      (err) => {
        console.log(err);
      }
    );
  }, []);

  return (
    <>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h3" sx={{ textAlign: "center" }}>
          {projectName}
        </Typography>
      </Box>
      <Box
        sx={{
          backgroundColor: "#fff",
          boxShadow:
            "rgba(60, 64, 67, 0.3) 0px 1px 2px 0px, rgba(60, 64, 67, 0.15) 0px 1px 3px 1px",
          borderRadius: "5px",
        }}
      >
        <TabContext value={value}>
          <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
            <TabList
              onChange={handleChange}
              aria-label="lab API tabs example"
              variant="fullWidth"
            >
              <Tab label="Danh sách nhiệm vụ" value="4" />
              <Tab label="Lịch sử" value="2" />
              <Tab label="Thống kê" value="3" />
              <Tab label="Thành viên dự án" value="1" />
            </TabList>
          </Box>
          <TabPanel value="1">
            <Box sx={boxComponentStyle}>
              <Box sx={{ display: "flex", flexDirection: "column", mb: 3 }}>
                <Box sx={{ mr: 3, mb: 3 }}>
                  <Typography variant="h5">Quản lý thành viên dự án</Typography>
                </Box>
                <Box
                  display={"flex"}
                  flexDirection={"row"}
                  alignItems={"center"}
                >
                  {/* <Box mr={3}>
                                        <AvatarGroup total={members.length} onClick={handleOpen}>
                                            {members.slice().splice(0, 3).map(item => (
                                                <Avatar key={item.id}>{item.fullName.charAt(0).toUpperCase()}</Avatar>
                                            ))}
                                        </AvatarGroup>
                                        <MemberModal open={open} handleClose={handleClose} members={members} />
                                    </Box> */}
                  <Box>
                    <Button
                      variant="contained"
                      color="success"
                      onClick={() => setOpenAddNewMemberModal(true)}
                    >
                      Thêm thành viên mới
                    </Button>
                    <AddNewMemberModal
                      open={openAddNewMemberModal}
                      handleClose={() => setOpenAddNewMemberModal(false)}
                      projectName={projectName}
                      projectId={projectId}
                      callBackIsLoadMember={() =>
                        setIsLoadMember(!isLoadMember)
                      }
                    />
                  </Box>
                </Box>
              </Box>
              <Box sx={boxChildComponent}>
                <MemberTable
                  members={members.map((member) => ({
                    ...member,
                    fullName: `${member.firstName} ${member.lastName}`,
                  }))}
                />
              </Box>
            </Box>
          </TabPanel>
          <TabPanel value="2">
            <Box sx={boxComponentStyle} overflow={"auto"} maxHeight={"100vh"}>
              <Box sx={{ mb: 3 }}>
                <Typography variant="h5">Lịch sử</Typography>
              </Box>
              {history.length > 0 ? (
                history.map((item) => (
                  <Box sx={boxChildComponent} key={item.date} px={0} mb={3}>
                    <Box pb={2}>
                      <Typography variant="body1">{item.date}</Typography>
                    </Box>
                    <Box>
                      {item.taskExecutionList.map((taskExecution) => (
                        <Box
                          display={"flex"}
                          key={taskExecution.id}
                          py={2}
                          borderTop={1}
                          borderColor={"#cdb8b8"}
                        >
                          <Box mr={2}>
                            <Avatar>
                              {taskExecution.createdByUserLoginId != null
                                ? taskExecution.createdByUserLoginId
                                    .charAt(0)
                                    .toUpperCase()
                                : "N"}
                            </Avatar>
                          </Box>
                          <Box display={"flex"} flexDirection={"column"}>
                            <Box display={"flex"} flexDirection={"row"} mb={2}>
                              <Typography variant="body1" sx={{ mr: 1 }}>
                                {taskExecution.createdByUserLoginId}
                              </Typography>
                              <HistoryStatus
                                tag={taskExecution.executionTags}
                              />
                            </Box>
                            <Box mb={1}>
                              <Typography variant="body2" color={"primary"}>
                                Nhiệm vụ: {taskExecution.task.name}
                              </Typography>
                            </Box>
                            {taskExecution.comment && (
                              <Box>
                                <Typography variant="body2">
                                  Nội dung: {taskExecution.comment}
                                </Typography>
                              </Box>
                            )}
                            {taskExecution.comment == "" && (
                              <Box>
                                <Typography
                                  variant="body2"
                                  sx={{ color: "red" }}
                                >
                                  Bình luận này đã bị xóa !
                                </Typography>
                              </Box>
                            )}
                            <Box display={"flex"} flexDirection={"column"}>
                              {taskExecution.changes.length == 0 &&
                                taskExecution.executionTags == "updated" && (
                                  <Typography variant="caption">
                                    Không có thay đổi gì xảy ra!
                                  </Typography>
                                )}
                              {taskExecution.changes.map((item, index) => (
                                <Typography variant="caption" key={index}>
                                  [{item.field}: {item.value}]
                                </Typography>
                              ))}
                            </Box>
                          </Box>
                        </Box>
                      ))}
                    </Box>
                  </Box>
                ))
              ) : (
                <Typography variant="body1">
                  Danh sách lịch sử trống ...
                </Typography>
              )}
            </Box>
          </TabPanel>
          <TabPanel value="3">
            <Box sx={boxComponentStyle} id="static">
              <Box sx={{ mb: 3 }}>
                <Typography variant="h5">Thống kê dự án</Typography>
              </Box>
              <Box sx={boxChildComponent} mb={3}>
                <Typography variant="body1">Danh mục</Typography>
                <Grid container justifyContent="center">
                  <Grid item={true} xs={12}>
                    <PieChart
                      labels={labelsCategory}
                      datasets={[
                        {
                          data: dataChartCategory,
                          backgroundColor: [
                            "#003f5c",
                            "#bc5090",
                            "#ff6361",
                            "#ffa600",
                            "#58508d",
                          ],
                        },
                      ]}
                    />
                  </Grid>
                </Grid>
              </Box>
              <Box sx={boxChildComponent}>
                <Typography variant="body1">Trạng thái</Typography>
                <Grid container justifyContent="center">
                  <Grid item={true} xs={12}>
                    <PieChart
                      labels={labelsStatus}
                      datasets={[
                        {
                          data: dataChartStatus,
                          backgroundColor: [
                            "#003f5c",
                            "#bc5090",
                            "#ff6361",
                            "#ffa600",
                            "#58508d",
                          ],
                        },
                      ]}
                    />
                  </Grid>
                </Grid>
              </Box>
            </Box>
          </TabPanel>
          <TabPanel value="4">
            <Box sx={boxComponentStyle} ref={elRef}>
              <Box
                sx={{ mb: 3, display: "flex", justifyContent: "space-between" }}
              >
                <Typography variant="h5">Danh sách các nhiệm vụ</Typography>
                <Box display={"flex"} alignItems={"center"}>
                  <Link
                    to={`/project/tasks/create/${projectId}`}
                    style={{ textDecoration: "none", marginRight: "15px" }}
                  >
                    <Button variant="outlined" color="primary">
                      Thêm nhiệm vụ
                    </Button>
                  </Link>
                  <Box>
                    <IconButton
                      aria-label="delete"
                      size="large"
                      onClick={handleClickMenu}
                    >
                      <SettingsOutlinedIcon />
                    </IconButton>
                  </Box>
                </Box>
              </Box>
              <Paper sx={{ width: "100%", overflow: "hidden" }}>
                <TableContainer sx={{ maxHeight: 440 }}>
                  <Table stickyHeader aria-label="sticky table">
                    <TableHead>
                      <TableRow>
                        {columns.map((column) => (
                          <TableCell
                            key={column.id}
                            align={column.align}
                            style={{ minWidth: column.minWidth }}
                          >
                            {column.label}
                          </TableCell>
                        ))}
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {rows
                        .slice(
                          page * rowsPerPage,
                          page * rowsPerPage + rowsPerPage
                        )
                        .map((row) => {
                          return (
                            <TableRow
                              hover
                              role="checkbox"
                              tabIndex={-1}
                              key={row.id}
                            >
                              {columns.map((column) => {
                                const value = row[column.id];
                                if (column.id === "category") {
                                  return (
                                    <TableCell
                                      key={column.id}
                                      align={column.align}
                                    >
                                      <CategoryElement
                                        categoryId={row.categoryId}
                                        value={value}
                                      />
                                    </TableCell>
                                  );
                                }

                                if (column.id === "dueDate") {
                                  return (
                                    <TableCell
                                      key={column.id}
                                      align={column.align}
                                    >
                                      <Box
                                        sx={{
                                          display: "flex",
                                          alignItems: "center",
                                        }}
                                      >
                                        {row.outOfDate && (
                                          <>
                                            <LocalFireDepartmentIcon color="error" />
                                          </>
                                        )}{" "}
                                        {value}
                                      </Box>
                                    </TableCell>
                                  );
                                }

                                if (column.id === "taskName") {
                                  return (
                                    <TableCell
                                      key={column.id}
                                      align={column.align}
                                    >
                                      <Link
                                        to={`/tasks/${row.id}`}
                                        style={{ textDecoration: "none" }}
                                      >
                                        {value}
                                      </Link>
                                    </TableCell>
                                  );
                                }

                                return (
                                  <TableCell
                                    key={column.id}
                                    align={column.align}
                                  >
                                    {value}
                                  </TableCell>
                                );
                              })}
                            </TableRow>
                          );
                        })}
                    </TableBody>
                  </Table>
                </TableContainer>
                <TablePagination
                  rowsPerPageOptions={[10, 25, 100]}
                  component="div"
                  count={rows.length}
                  rowsPerPage={rowsPerPage}
                  page={page}
                  onPageChange={handleChangePage}
                  onRowsPerPageChange={handleChangeRowsPerPage}
                />
              </Paper>
            </Box>
          </TabPanel>
        </TabContext>
      </Box>
      <Menu
        anchorEl={anchorEl}
        open={openMenu}
        onClose={handleCloseMenu}
        anchorOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
        transformOrigin={{
          vertical: "top",
          horizontal: "left",
        }}
      >
        <MenuItem onClick={handleCloseMenu}>
          <ExportExcelButton projectName={projectName} tasks={tasks} />
        </MenuItem>
      </Menu>
    </>
  );
};

export default ListTasks;
