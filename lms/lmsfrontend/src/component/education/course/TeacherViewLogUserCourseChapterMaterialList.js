import React, {useEffect, useMemo, useReducer, useRef, useState} from "react";
import {request} from "../../../api";
import SearchIcon from "@material-ui/icons/Search";
import {DataGrid} from "@material-ui/data-grid";
import {Card, CardContent, CardHeader, InputAdornment, makeStyles, TextField,} from "@material-ui/core/";

const useStyles = makeStyles((theme) => ({
  root: {
    padding: theme.spacing(2),
  },
  search: {
    width: 200,
  },
}));

const baseColumn = {
  headerAlign: "center",
  minWidth: 170,
  sortable: false,
};

const columns = [
  { headerName: "Tài khoản", field: "userLoginId", ...baseColumn },
  { headerName: "Họ tên", field: "fullName", ...baseColumn },
  {
    headerName: "Hoạt động",
    field: "eduCourseMaterialName",
    flex: 1,
    ...baseColumn,
  },
  {
    headerName: "Thời gian",
    field: "createdStamp",
    ...baseColumn,
    sortable: true,
  },
];

const rowsPerPage = [5, 10, 20];

const initState = {
  data: [],
  loading: false,
  page: 0,
  pageSize: 5,
  totalCount: 0,
  keyword: "",
  sort: "desc",
};

function reducer(state, action) {
  const { type, payload } = action;
  switch (type) {
    case "fetchData":
      return {
        ...state,
        loading: true,
        error: "",
      };
    case "success":
      return {
        ...state,
        data: payload.data,
        totalCount: payload.totalCount,
        loading: false,
        error: "",
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
    case "pageSizeChange":
      return {
        ...state,
        page: 0,
        pageSize: action.pageSize,
      };
    case "sortChange":
      return {
        ...state,
        sort: payload,
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

function TeacherViewLogUserCourseChapterMaterialList(props) {
  const classId = props.classId;
  const classes = useStyles();
  // const [logs, setLogs] = useState([]);
  const [state, localDispatch] = useReducer(reducer, initState);
  const { data, page, pageSize, totalCount, loading, keyword, sort } = state;

  const sortModel = useMemo(() => {
    if (sort === "") return [];
    return [{ field: "createdStamp", sort: sort }];
  }, [sort]);

  const getLogs = () => {
    let url = "/edu/class/get-log-user-course-chapter-material-by-page";
    let urlParam = `classId=${classId}&page=${page}&pageSize=${pageSize}`;
    if (sort) urlParam = `${urlParam}&sortType=${sort}`;
    if (keyword) urlParam = `${urlParam}&keyword=${keyword}`;
    url = `${url}?${urlParam}`;

    localDispatch({ type: "fetchData" });

    request(
      "get",
      url,
      ({ data }) => {
        const dataWidthId = data.content.map((item) => ({
          ...item,
          id: `${item.userLoginId}${item.createdStamp}`,
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
  };

  const handlePageSizeChange = (newPageSize) => {
    // console.log(newPageSize);
    localDispatch({ type: "pageSizeChange", pageSize: newPageSize });
  };

  const handlePageChange = (newPage) => {
    localDispatch({ type: "pageChange", payload: newPage });
  };

  const handleKeywordChange = (txt) => {
    localDispatch({
      type: "keywordChange",
      payload: { keyword: txt },
    });
  };

  const handleSortModelChange = (model) => {
    const sortType = model.length !== 0 ? model[0].sort : "";
    localDispatch({
      type: "sortChange",
      payload: sortType,
    });
  };

  useEffect(() => {
    getLogs();
  }, [page, pageSize, keyword]);

  return (
    <Card elevation={5} className={classes.root}>
      <CardHeader
        action={<TextSearch onSubmid={handleKeywordChange} />}
        title="Lịch sử học"
      />
      <CardContent>
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
          rowsPerPageOptions={rowsPerPage}
          sortModel={sortModel}
          onSortModelChange={handleSortModelChange}
          loading={loading}
          disableColumnMenu
          autoHeight
        />
      </CardContent>
    </Card>
  );
}

export default TeacherViewLogUserCourseChapterMaterialList;

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
        id="search"
        placeholder="Find by user id"
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
