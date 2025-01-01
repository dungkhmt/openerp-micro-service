import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {Box, Button, Card, CardContent, CardHeader, Input} from "@material-ui/core";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import {request} from "../../../../api";
import {Link, useHistory} from "react-router-dom";
import {FormControl, MenuItem, Select} from "@mui/material";
import useDebounceValue from "../hooks/use-debounce";
import {toast} from "react-toastify";
import TextField from "@material-ui/core/TextField";
import parser from "html-react-parser"
import {DataGrid} from "@material-ui/data-grid";
import InfoIcon from "@mui/icons-material/Info";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import {formatDateTime} from "../ultils/DateUltils";
import TestBankDetails from "../testbank/TestBankDetails";
import {Assignment} from "@material-ui/icons";
import ExamDelete from "./ExamDelete";

const baseColumn = {
  sortable: false,
};

const rowsPerPage = [5, 10, 20];

function ExamManagement(props) {

  const columns = [
    {
      field: "code",
      headerName: "Mã kỳ thi",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "name",
      headerName: "Tên kỳ thi",
      minWidth: 200,
      ...baseColumn
    },
    {
      field: "description",
      headerName: "Mô tả kỳ thi",
      ...baseColumn,
      flex: 1,
      minWidth: 200,
      renderCell: (rowData) => {
        return parser(rowData.value)
      }
    },
    {
      field: "startTime",
      headerName: "Thời gian bắt đầu",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        return formatDateTime(rowData.value)
      },
    },
    {
      field: "endTime",
      headerName: "Thời gian kết thúc",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        return formatDateTime(rowData.value)
      },
    },
    {
      field: "status",
      headerName: "Trạng thái",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        if(rowData.value === 0){
          return 'Chưa kích hoạt'
        }else{
          return 'Kích hoạt'
        }
      },
    },
    {
      field: "",
      headerName: "",
      sortable: false,
      minWidth: 120,
      maxWidth: 120,
      renderCell: (rowData) => {
        return (
          <Box display="flex" justifyContent="space-between" alignItems='center' width="100%">
            <Assignment style={{cursor: 'pointer'}} onClick={(data) => handleDetailsTest(rowData?.row)}/>
            <EditIcon style={{cursor: 'pointer'}} onClick={(data) => handleUpdate(rowData?.row)}/>
            <DeleteIcon style={{cursor: 'pointer', color: 'red'}} onClick={(data) => handleDelete(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const statusList = [
    {
      value: 'all',
      name: 'Tất cả'
    },
    {
      value: 0,
      name: 'Chưa kích hoạt'
    },
    {
      value: 1,
      name: 'Kích hoạt'
    }
  ]

  const [examList, setExamList] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [statusFilter, setStatusFilter] = useState('all')
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [idDelete, setIdDelete] = useState("")
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);
  const [testDetails, setTestDetails] = useState(null)

  const debouncedKeywordFilter = useDebounceValue(keywordFilter, 500)
  const history = useHistory();

  useEffect(() => {
    handleFilter()
  }, [page, pageSize, debouncedKeywordFilter, statusFilter]);

  const handleFilter = () =>{
    const body = {
      keyword: keywordFilter,
      status: statusFilter === 'all' ? null : statusFilter
    }
    request(
      "post",
      `/exam/filter?page=${page}&size=${pageSize}`,
      (res) => {
        if(res.status === 200){
          setExamList(res.data.content);
          setTotalCount(res.data.totalElements);
        }else {
          toast.error(res)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const detailsTest = (id) =>{
    const body = {
      id: id
    }
    request(
      "post",
      `/exam-test/details`,
      (res) => {
        if(res.data.resultCode === 200){
          setTestDetails(res.data.data)
          setOpenDetailsDialog(true)
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const onClickCreateNewButton = () => {
    history.push({
      pathname: "/exam/create-update",
      state: {
        data: {
          examTestId: "",
          examTests: "",
          code: "",
          name: "",
          description: "",
          status: 1,
          startTime: "",
          endTime: ""
        },
        isCreate: true
      },
    });
  };

  const handleUpdate = (rowData) => {
    const body = {
      id: rowData.examTestId
    }
    request(
      "post",
      `/exam-test/details`,
      (res) => {
        if(res.data.resultCode === 200){
          let examTests = []
          examTests.push(res.data.data)
          history.push({
            pathname: "/exam/create-update",
            state: {
              data: {
                examTestId: rowData.examTestId,
                examTests: examTests,
                code: rowData.code,
                name: rowData.name,
                description: rowData.description,
                status: rowData.status,
                startTime: rowData.startTime,
                endTime: rowData.endTime
              },
              isCreate: false
            },
          });
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  };

  const handleDetailsTest = (rowData) => {
    detailsTest(rowData.examTestId)
  };

  const handleDelete = (rowData) => {
    setOpenDeleteDialog(true)
    setIdDelete(rowData.id)
  };

  return (
    <div>
      <Card elevation={5} >
        <CardHeader
          title={
            <Box display="flex" justifyContent="space-between" alignItems="end" width="100%">
              <Box display="flex" flexDirection="column" width="80%">
                <h4 style={{marginTop: 0, paddingTop: 0}}>Danh sách kỳ thi</h4>
                <Box display="flex" justifyContent="flex-start" width="100%">
                  <TextField
                    autoFocus
                    id="examCode"
                    label="Nội dung tìm kiếm"
                    placeholder="Tìm kiếm theo code hoặc tên"
                    value={keywordFilter}
                    style={{ width: "300px", marginRight: "16px"}}
                    onChange={(event) => {
                      setKeywordFilter(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />

                  <TextField
                    id="examStatus"
                    select
                    label="Trạng thái"
                    style={{ width: "150px"}}
                    value={statusFilter}
                    onChange={(event) => {
                      setStatusFilter(event.target.value);
                    }}
                  >
                    {
                      statusList.map(item => {
                        return (
                          <MenuItem value={item.value}>{item.name}</MenuItem>
                        )
                      })
                    }
                  </TextField>
                </Box>
              </Box>

              <Box display="flex" justifyContent="flex-end" width="20%">
                <Button
                  variant="contained"
                  color="primary"
                  onClick={onClickCreateNewButton}
                  startIcon={<AddCircleIcon />}
                  style={{ marginRight: 16 }}
                >
                  Thêm mới
                </Button>
              </Box>
            </Box>
          }/>
        <CardContent>
          <DataGrid
            rowCount={totalCount}
            rows={examList}
            columns={columns}
            page={page}
            pageSize={pageSize}
            pagination
            paginationMode="server"
            onPageChange={(page) => setPage(page)}
            onPageSizeChange={(pageSize) => setPageSize(pageSize)}
            rowsPerPageOptions={rowsPerPage}
            disableColumnMenu
            autoHeight
          />
        </CardContent>
      </Card>
      {
        openDetailsDialog && (
          <TestBankDetails
            open={openDetailsDialog}
            setOpen={setOpenDetailsDialog}
            data={testDetails}
          />
        )
      }
      <ExamDelete
        open={openDeleteDialog}
        setOpen={setOpenDeleteDialog}
        id={idDelete}
        onReload={() => {
          handleFilter()
        }}
      />
    </div>
  );
}

const screenName = "MENU_EXAM_MANAGEMENT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default ExamManagement;
