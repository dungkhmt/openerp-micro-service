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
import {DataGrid} from "@material-ui/data-grid";
import InfoIcon from "@mui/icons-material/Info";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import ExamSubjectDelete from "./ExamSubjectDelete";

const baseColumn = {
  sortable: false,
};

const rowsPerPage = [5, 10, 20];

function ExamSubjectManagement(props) {

  const columns = [
    {
      field: "code",
      headerName: "Mã môn học",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "name",
      headerName: "Tên môn học",
      minWidth: 250,
      flex: 1,
      ...baseColumn
    },
    {
      field: "status",
      headerName: "Trạng thái",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        if(rowData.value === 'INACTIVE'){
          return (
            <strong style={{color: '#f50000c9'}}>Không hoạt động</strong>
          )
        }else if(rowData.value === 'ACTIVE'){
          return (
            <strong style={{color: '#61bd6d'}}>Hoạt động</strong>
          )
        }
        return ''
      },
    },
    {
      field: "",
      headerName: "",
      sortable: false,
      minWidth: 60,
      maxWidth: 60,
      renderCell: (rowData) => {
        return (
          <Box display="flex" justifyContent="space-between" alignItems='center' width="100%">
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
      value: 'INACTIVE',
      name: 'Không hoạt động'
    },
    {
      value: 'ACTIVE',
      name: 'Hoạt động'
    }
  ]

  const [examSubjectList, setExamSubjectList] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [statusFilter, setStatusFilter] = useState('all')
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [idDelete, setIdDelete] = useState("")

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
      `/exam-subject/filter?page=${page}&size=${pageSize}`,
      (res) => {
        if(res.status === 200){
          setExamSubjectList(res.data.content);
          setTotalCount(res.data.totalElements);
        }else {
          toast.error(res)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const onClickCreateNewButton = () => {
    history.push({
      pathname: "/exam/create-update-subject",
      state: {
        data: {
          code: "",
          name: "",
          status: 'ACTIVE'
        },
        isCreate: true
      },
    });
  };

  const handleUpdate = (rowData) => {
    history.push({
      pathname: "/exam/create-update-subject",
      state: {
        data: rowData,
        isCreate: false
      },
    });
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
                <h4 style={{marginTop: 0, paddingTop: 0}}>Danh sách môn học</h4>
                <Box display="flex" justifyContent="flex-start" width="100%">
                  <TextField
                    autoFocus
                    id="examSubjectCode"
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
                    id="examSubjectStatus"
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
            rows={examSubjectList}
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
      <ExamSubjectDelete
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

const screenName = "MENU_EXAM_SUBJECT";
export default withScreenSecurity(ExamSubjectManagement, screenName, true);
// export default ExamManagement;
