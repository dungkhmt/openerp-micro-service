import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {Box, Button, Card, CardContent, CardHeader, Input} from "@material-ui/core";
import {request} from "../../../../api";
import {useHistory} from "react-router-dom";
import {MenuItem} from "@mui/material";
import useDebounceValue from "../hooks/use-debounce";
import {toast} from "react-toastify";
import TextField from "@material-ui/core/TextField";
import {DataGrid} from "@material-ui/data-grid";
import {formatDateTime} from "../ultils/DateUltils";
import {BorderColor} from "@material-ui/icons";

const baseColumn = {
  sortable: false,
};

const rowsPerPage = [5, 10, 20];

function MyExam(props) {

  const columns = [
    {
      field: "examName",
      headerName: "Tên kỳ thi",
      minWidth: 200,
      flex: 1,
      ...baseColumn
    },
    {
      field: "examTestName",
      headerName: "Tên đề thi",
      minWidth: 200,
      flex: 1,
      ...baseColumn
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
      field: "examResultId",
      headerName: "Trạng thái",
      ...baseColumn,
      minWidth: 170,
      height: 20,
      renderCell: (rowData) => {
        if(rowData.row?.examResultId != null && rowData.row?.totalScore == null ){
          return (
            <strong style={{color: '#716DF2'}}>Chưa chấm</strong>
          )
        }else if(rowData.row?.examResultId != null && rowData.row?.totalScore != null ){
          return (
            <strong style={{color: '#61bd6d'}}>Đã chấm</strong>
          )
        }else{
          return (
            <strong style={{color: '#f50000c9'}}>Chưa làm</strong>
          )
        }
      },
    },
    {
      field: "",
      headerName: "",
      sortable: false,
      minWidth: 30,
      maxWidth: 30,
      renderCell: (rowData) => {
        return (
          <Box display="flex" justifyContent="space-between" alignItems='center' width="100%">
            <BorderColor style={{cursor: 'pointer'}} onClick={(data) => handleDoingExam(rowData?.row)}/>
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
      name: 'Chưa làm'
    },
    {
      value: 1,
      name: 'Chưa chấm'
    },
    {
      value: 2,
      name: 'Đã chấm'
    }
  ]

  const [dataList, setDataList] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [statusFilter, setStatusFilter] = useState('all')

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
      `/exam/filter-my-exam?page=${page}&size=${pageSize}`,
      (res) => {
        if(res.status === 200){
          setDataList(res.data.content);
          setTotalCount(res.data.totalElements);
        }else {
          toast.error(res)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const handleDoingExam = (rowData) => {
    const body = {
      examId: rowData?.examId,
      examStudentId: rowData?.examStudentId
    }
    request(
      "post",
      `/exam/details-my-exam`,
      (res) => {
        if(res.status === 200){
          if(res.data.resultCode === 200){
            history.push({
              pathname: `/exam/doing`,
              state: {
                data: res.data.data
              },
            });
          }else{
            toast.error(res.data.resultMsg)
          }
        }else {
          toast.error(res)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  };

  return (
    <div>
      <Card elevation={5} >
        <CardHeader
          title={
            <Box display="flex" justifyContent="space-between" alignItems="end" width="100%">
              <Box display="flex" flexDirection="column" width="100%">
                <h4 style={{marginTop: 0, paddingTop: 0}}>Danh sách kỳ thi của tôi</h4>
                <Box display="flex" justifyContent="flex-start" width="100%">
                  <TextField
                    autoFocus
                    id="keywordMyExam"
                    label="Nội dung tìm kiếm"
                    placeholder="Tìm kiếm theo tên kỳ thi hoặc tên đề thi"
                    value={keywordFilter}
                    style={{ width: "400px", marginRight: "16px"}}
                    onChange={(event) => {
                      setKeywordFilter(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />

                  <TextField
                    id="statusMyExam"
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
            </Box>
          }/>
        <CardContent>
          <DataGrid
            rowCount={totalCount}
            rows={dataList}
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
            getRowId={(row) => row.examId}
          />
        </CardContent>
      </Card>
    </div>
  );
}

const screenName = "MENU_EXAMINEE_PARTICIPANT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default MyExam;
