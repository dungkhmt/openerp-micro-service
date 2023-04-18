import {Box, Container, IconButton, InputAdornment, Paper, TextField, Tooltip, Typography,} from "@material-ui/core";
import {makeStyles} from "@material-ui/core/styles";
import {DataGrid} from "@material-ui/data-grid";
import PersonAddIcon from "@material-ui/icons/PersonAdd";
import SearchIcon from "@material-ui/icons/Search";
import {axiosPost, request} from "api";
import React, {useEffect, useReducer, useRef, useState} from "react";
import {useSelector} from "react-redux";
import {Link} from "react-router-dom";
import {toast} from "react-toastify";
import {
  errorNoti,
  processingNoti,
  successNoti,
  updateErrorNoti,
  updateSuccessNoti,
  warningNoti,
} from "utils/notification";
import XLSX from "xlsx";
import UploadButton from "../UploadButton";
import AddTeacherModal from "./AddTeacherModal";

const useStyles = makeStyles((theme) => ({
  wrapper: {
    paddingTop: theme.spacing(2),
    paddingBottom: theme.spacing(3),
  },
  header: {
    flexGrow: 1,
    textAlign: "left",
    paddingLeft: theme.spacing(1),
  },
  search: {
    width: 200,
  },
}));

const columns = [
  {
    headerName: "Tên giảng viên",
    field: "teacherName",
    headerAlign: "center",
    flex: 1,
    minWidth: 200,
    renderCell: (row) => (
      <Link to={`/edu/teacher/detail/${row.row.teacherId}`}>{row.value}</Link>
    ),
  },
  {
    headerName: "Email",
    field: "teacherId",
    headerAlign: "center",
    flex: 1,
    minWidth: 200,
  },
  {
    headerName: "Tài khoản",
    field: "userLoginId",
    headerAlign: "center",
    minWidth: 200,
  },
];

const initState = {
  data: [],
  loading: false,
  page: 0,
  pageSize: 5,
  totalCount: 0,
  keyword: "",
  sortBy: "",
  sortType: "",
};

function reducer(state, action) {
  const { type, payload } = action;
  switch (type) {
    case "fetchData":
      return {
        ...state,
        loading: true,
      };
    case "success":
      return {
        ...state,
        data: payload.data,
        totalCount: payload.totalCount,
        loading: false,
      };
    case "error":
      return {
        ...state,
        loading: false,
        page: 0,
        totalCount: 1,
        data: [],
      };
    case "pageChange":
      return {
        ...state,
        page: action.payload,
      };
    case "sortChange":
      return {
        ...state,
        sortBy: action.sortBy,
        sortType: action.sortType,
      };
    case "pageSizeChange":
      return {
        ...state,
        page: 0,
        pageSize: action.pageSize,
      };
    case "keywordChange":
      return {
        ...state,
        keyword: payload.keyword,
        page: 0,
      };
    default:
      return state;
  }
}

export default function TeacherList() {
  const [state, localDispatch] = useReducer(reducer, initState);

  const [openTeacherAdd, setOpenTeacherAdd] = useState(false);

  const token = useSelector((state) => state.auth.token);

  // Snackbar
  const toastId = React.useRef(null);

  const {
    data,
    page,
    pageSize,
    totalCount,
    loading,
    keyword,
    sortBy,
    sortType,
  } = state;

  const classes = useStyles();

  useEffect(() => {
    let url = "/get-all-teachers-by-page";
    url += `?page=${page}&pageSize=${pageSize}`;
    if (sortBy) url += `&sortBy=${sortBy}&sortType=${sortType}`;
    if (keyword !== "") url += `&keyword=${keyword}`;
    console.log(url);
    localDispatch({ type: "fetchData" });

    request(
      "get",
      url,
      ({ data }) => {
        const dataWidthId = data.content.map((item, index) => ({
          ...item,
          id: index,
        }));
        localDispatch({
          type: "success",
          payload: {
            data: dataWidthId,
            totalCount: data.totalElements,
          },
        });
      },
      {
        onError: (res) => {
          console.log({ res });
          localDispatch({ type: "error" });
        },
      }
    );
  }, [page, pageSize, sortBy, sortType, keyword]);

  const handlePageSizeChange = (newPageSize) => {
    localDispatch({ type: "pageSizeChange", pageSize: newPageSize });
  };

  const handlePageChange = (newPage) => {
    localDispatch({ type: "pageChange", payload: newPage });
  };

  const handleSortModelChange = ([params]) => {
    const sortBy = params?.field || "";
    const sortType = params?.sort || "";
    localDispatch({
      type: "sortChange",
      sortBy,
      sortType,
    });
  };

  const handleKeywordChange = (txt) => {
    localDispatch({
      type: "keywordChange",
      payload: { keyword: txt },
    });
  };

  const handleOpen = () => {
    setOpenTeacherAdd(true);
  };
  const handleClose = () => {
    setOpenTeacherAdd(false);
  };

  const onClickSaveButton = (files) => {
    processingNoti(toastId, false);

    let file = files[0];
    let sheetName = "teachers";
    let noCols = 3;
    let dataTypeOfCol = ["s", "s", "n"];
    let dataColName = ["teacherName", "email", "maxCredit"];
    let excelColName = [];

    const reader = new FileReader();

    reader.onload = (event) => {
      try {
        const { result } = event.target;
        const workbook = XLSX.read(result, { type: "binary" });

        if (workbook.Sheets.hasOwnProperty(sheetName)) {
          let sheet = workbook.Sheets[sheetName];
          // console.log(sheet);

          if (!sheet.hasOwnProperty("!ref")) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Sheet "${sheetName}" không chứa dữ liệu`
              );
            } else {
              errorNoti(`Sheet "${sheetName}" không chứa dữ liệu`);
            }

            return;
          }

          let range = XLSX.utils.decode_range(sheet["!ref"]);
          let startRow = range.s.r + 1;
          let endRow = range.e.r + 1;

          if (endRow === startRow) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Sheet "${sheetName}" không chứa dữ liệu`
              );
            } else {
              errorNoti(`Sheet "${sheetName}" không chứa dữ liệu`);
            }

            return;
          }

          // Check number of column.
          let startCol = range.s.c; // number
          let endCol = range.e.c; // number

          if (endCol - startCol + 1 > noCols) {
            warningNoti(
              `Số cột dữ liệu nhiều hơn so với yêu cầu. Các cột không được yêu cầu sẽ bị bỏ qua. Tải xuống tệp định dạng nội dung chuẩn`
            );
          } else if (endCol - startCol + 1 < noCols) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Số cột dữ liệu ít hơn so với yêu cầu. Tải xuống tệp định dạng nội dung chuẩn`
              );
            } else {
              errorNoti(
                `Số cột dữ liệu ít hơn so với yêu cầu. Tải xuống tệp định dạng nội dung chuẩn`
              );
            }

            return;
          }

          // Xem bat loi nay o day da hop ly chua
          if (sheet.hasOwnProperty("!merges")) {
            if (toast.isActive(toastId.current)) {
              updateErrorNoti(
                toastId,
                `Bảng có chứa ${sheet["!merges"].length} ô hợp nhất (merge cell). Tải xuống tệp định dạng nội dung chuẩn`
              );
            } else {
              errorNoti(
                `Bảng có chứa ${sheet["!merges"].length} ô hợp nhất (merge cell). Tải xuống tệp định dạng nội dung chuẩn`
              );
            }

            return;
          }

          // Round 1: validate cell.
          let processingCell;

          for (let i = 0; i < noCols; i++) {
            excelColName.push(XLSX.utils.encode_col(startCol + i));

            sheet[excelColName[i] + startRow] = {
              t: dataTypeOfCol[i],
              v: dataColName[i],
            };

            for (let j = startRow + 1; j <= endRow; j++) {
              processingCell = excelColName[i] + j;

              if (sheet.hasOwnProperty(processingCell)) {
                if (!(sheet[processingCell].t === dataTypeOfCol[i])) {
                  if (toast.isActive(toastId.current)) {
                    updateErrorNoti(
                      toastId,
                      `Kiểu dữ liệu của ô "${processingCell}" không đúng định dạng. Tải xuống tệp định dạng nội dung chuẩn`
                    );
                  } else {
                    errorNoti(
                      `Kiểu dữ liệu của ô "${processingCell}" không đúng định dạng. Tải xuống tệp định dạng nội dung chuẩn`
                    );
                  }

                  return;
                }
              } else {
                if (toast.isActive(toastId.current)) {
                  updateErrorNoti(
                    toastId,
                    `Ô "${processingCell}" không chứa dữ liệu. Tải xuống tệp định dạng nội dung chuẩn`
                  );
                } else {
                  errorNoti(
                    `Ô "${processingCell}" không chứa dữ liệu. Tải xuống tệp định dạng nội dung chuẩn`
                  );
                }

                return;
              }
            }
          }

          // Round 2: format data.
          let validEmailRegex =
            /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;

          for (let i = startRow + 1; i <= endRow; i++) {
            processingCell = excelColName[1] + i;

            // Remove all white space.
            sheet[processingCell].v = sheet[processingCell].v
              .trim()
              .replace(/\s/g, "");

            // Validate email.
            if (!validEmailRegex.test(sheet[processingCell].v)) {
              if (toast.isActive(toastId.current)) {
                updateErrorNoti(
                  toastId,
                  `Dữ liệu của ô "${processingCell}" không phải là địa chỉ email hợp lệ. Tải xuống tệp định dạng nội dung chuẩn`
                );
              } else {
                errorNoti(
                  `Dữ liệu của ô "${processingCell}" không phải là địa chỉ email hợp lệ. Tải xuống tệp định dạng nội dung chuẩn`
                );
              }

              return;
            }

            // Format teacherName.
            processingCell = excelColName[0] + i;
            sheet[processingCell].v = sheet[processingCell].v
              .trim()
              .replace(/\s+/g, " ");
          }

          // Round 3: check duplicate record.
          let duplicateTeachers = new Set();

          for (let i = startRow + 1; i < endRow; i++) {
            processingCell = excelColName[1] + i;

            for (let j = i + 1; j <= endRow; j++) {
              if (
                sheet[processingCell].v.localeCompare(
                  sheet[excelColName[1] + j].v,
                  undefined,
                  { sensitivity: "accent" }
                ) === 0
              ) {
                duplicateTeachers.add(sheet[processingCell].v);
              }
            }
          }

          if (duplicateTeachers.size > 0) {
            let warningMess;
            let duplTeachersArr = Array.from(duplicateTeachers);
            let len = duplTeachersArr.length - 1;

            if (duplTeachersArr.length === 1) {
              warningMess = `Phát hiện các bản ghi trùng nhau của giảng viên có email: `;
            } else {
              warningMess = `Phát hiện các bản ghi trùng nhau của các giảng viên có email: `;

              for (let i = 0; i < len; i++) {
                warningMess += duplTeachersArr[i] + ", ";
              }
            }

            warningMess += `${duplTeachersArr[len]}. Một bản ghi trong mỗi cặp trùng nhau sẽ bị loại bỏ`;

            warningNoti(warningMess);
          }

          // Everything is OK!.
          axiosPost(
            token,
            "/edu/teacher/add-list-of-teachers",
            XLSX.utils.sheet_to_json(sheet)
          )
            .then((res) => {
              if (toast.isActive(toastId.current)) {
                updateSuccessNoti(toastId, res.data);
              } else {
                successNoti(res.data);
              }

              // getAllTeachers();
            })
            .catch((error) => {
              if (toast.isActive(toastId.current)) {
                updateErrorNoti(toastId, "Rất tiếc! Đã xảy ra lỗi :((");
              } else {
                errorNoti("Rất tiếc! Đã xảy ra lỗi :((");
              }

              console.log("onClickSaveButton, error ", error);
            });
        } else {
          updateErrorNoti(toastId, `Không tìm thấy sheet "${sheetName}"`);
          return;
        }
      } catch (e) {
        console.log(e);

        if (toast.isActive(toastId.current)) {
          updateErrorNoti(toastId, "Rất tiếc! Đã xảy ra lỗi :((");
        } else {
          errorNoti("Rất tiếc! Đã xảy ra lỗi :((");
        }

        return;
      }
    };

    reader.readAsBinaryString(file);
  };

  return (
    <Container maxWidth="md">
      <Paper elevation={5} className={classes.wrapper}>
        <Container>
          <Box
            display="flex"
            aria-orientation="horizontal"
            alignItems="center"
            p={1}
          >
            <Typography variant="h6" className={classes.header}>
              Danh sách giảng viên
            </Typography>
            <TextSearch onSubmid={handleKeywordChange} />
          </Box>
          <Box
            display="flex"
            aria-orientation="horizontal"
            alignItems="center"
            justifyContent="center"
            p={1}
          >
            <MyIconButton
              onClick={handleOpen}
              title="Add new teacher"
              icon={PersonAddIcon}
              color="#0030e0"
            />
            {/* <MyIconButton
              color="#0fbf00"
              title="Import from a excel file"
              icon={CloudUploadIcon}
            /> */}
            <UploadButton
              buttonTitle="Tải lên"
              onClickSaveButton={onClickSaveButton}
              filesLimit={1}
            />
          </Box>
          <DataGrid
            rowCount={totalCount}
            rows={data}
            columns={columns}
            page={page}
            pageSize={pageSize}
            pagination
            paginationMode="server"
            onPageChange={handlePageChange}
            onPageSizeChange={handlePageSizeChange}
            rowsPerPageOptions={[5, 10, 20]}
            sortingMode="server"
            onSortModelChange={handleSortModelChange}
            loading={loading}
            disableColumnMenu
            autoHeight
          />
        </Container>
      </Paper>
      <AddTeacherModal open={openTeacherAdd} handleClose={handleClose} />
    </Container>
  );
}

const TextSearch = ({ onSubmid }) => {
  const classes = useStyles();

  const [text, setText] = useState("");
  const timeoutRef = useRef(null);

  const handleTextChange = (event) => {
    const value = event.target.value;
    console.log(event);
    setText(value);
    if (timeoutRef.current) clearTimeout(timeoutRef.current);
    timeoutRef.current = setTimeout(() => {
      onSubmid(value);
    }, 600);
  };

  const handleSubmid = (e) => {
    e.preventDefault();
    onSubmid(text);
  };

  return (
    <form onSubmit={handleSubmid}>
      <TextField
        className={classes.search}
        size="small"
        // variant="outlined"
        id="search"
        placeholder="Search"
        name="search"
        value={text}
        onChange={handleTextChange}
        InputProps={{
          startAdornment: (
            <InputAdornment position="start">
              <SearchIcon />
            </InputAdornment>
          ),
        }}
      />
    </form>
  );
};

function MyIconButton(props) {
  const { title, icon: Icon, onClick, color } = props;
  const handleClick = (e) => {
    if (onClick) {
      onClick();
    }
  };
  return (
    <Tooltip title={title}>
      <IconButton aria-label={title} onClick={handleClick}>
        <Icon style={{ color: color }} />
      </IconButton>
    </Tooltip>
  );
}
