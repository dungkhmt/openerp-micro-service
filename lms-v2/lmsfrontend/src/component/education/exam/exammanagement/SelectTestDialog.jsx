import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {Box, Button, Card, CardContent, CardHeader, DialogTitle, Input} from "@material-ui/core";
import {request} from "../../../../api";
import {Link, useHistory} from "react-router-dom";
import {Dialog, DialogActions, DialogContent, FormControl, MenuItem, Select} from "@mui/material";
import useDebounceValue from "../hooks/use-debounce";
import {toast} from "react-toastify";
import TextField from "@material-ui/core/TextField";
import {DataGrid} from "@material-ui/data-grid";
import InfoIcon from "@mui/icons-material/Info";
import QuestionBankDetails from "../questionbank/QuestionBankDetails";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import DeleteIcon from "@material-ui/icons/Delete";
import {formatDateApi, formatDateTime} from "../ultils/DateUltils";
import EditIcon from "@material-ui/icons/Edit";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import {vi} from "date-fns/locale";
import {DatePicker} from "@mui/x-date-pickers";
import TestBankDetails from "../testbank/TestBankDetails";
import {parseHTMLToString} from "../ultils/DataUltils";

const baseColumn = {
  sortable: false,
};

const rowsPerPage = [5, 10, 20];

function SelectTestDialog(props) {

  const columns = [
    {
      field: "code",
      headerName: "Mã đề thi",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "name",
      headerName: "Tên đề thi",
      ...baseColumn,
      minWidth: 250
    },
    {
      field: "description",
      headerName: "Mô tả",
      ...baseColumn,
      flex: 1,
      minWidth: 170,
      renderCell: (rowData) => {
        return parseHTMLToString(rowData.value)
      },
    },
    {
      field: "createdAt",
      headerName: "Thời gian tạo",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        return formatDateTime(rowData.value)
      },
    },
    {
      field: "",
      headerName: "",
      sortable: false,
      minWidth: 50,
      maxWidth: 50,
      renderCell: (rowData) => {
        return (
          <Box display="flex" justifyContent="space-between" alignItems='center' width="100%">
            <InfoIcon style={{cursor: 'pointer'}} onClick={(data) => handleOpenPopupDetails(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const columnsSelected = [
    {
      field: "code",
      headerName: "Mã đề thi",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "name",
      headerName: "Tên đề thi",
      ...baseColumn,
      minWidth: 250
    },
    {
      field: "description",
      headerName: "Mô tả",
      ...baseColumn,
      flex: 1,
      minWidth: 170,
      renderCell: (rowData) => {
        return parseHTMLToString(rowData.value)
      },
    },
    {
      field: "createdAt",
      headerName: "Thời gian tạo",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        return formatDateTime(rowData.value)
      },
    },
    {
      field: "",
      headerName: "",
      sortable: false,
      minWidth: 50,
      maxWidth: 50,
      renderCell: (rowData) => {
        return (
          <Box display="flex" justifyContent="space-between" alignItems='center' width="100%">
            <InfoIcon style={{cursor: 'pointer'}} onClick={(data) => handleOpenPopupDetails(rowData?.row)}/>
            <DeleteIcon style={{cursor: 'pointer', color: 'red'}} onClick={(data) => handleDeleteDataSelected(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const { open, setOpen, onSubmit} = props;

  const [dataList, setDataList] = useState([])
  const [dataSelectionList, setDataSelectionList] = useState([])
  const [dataSelectedList, setDataSelectedList] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [createdFromFilter, setCreatedFromFilter] = useState("")
  const [createdToFilter, setCreatedToFilter] = useState("")
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);
  const [testDetails, setTestDetails] = useState(null)

  const debouncedKeywordFilter = useDebounceValue(keywordFilter, 500)

  useEffect(() => {
    filter()
  }, [page, pageSize, debouncedKeywordFilter, createdFromFilter, createdToFilter]);

  const filter = () =>{
    const body = {
      keyword: keywordFilter,
      createdFrom: formatDateApi(createdFromFilter),
      createdTo: formatDateApi(createdToFilter)
    }
    request(
      "post",
      `/exam-test/filter?page=${page}&size=${pageSize}`,
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

  const handleOpenPopupDetails = (rowData) => {
    detailsTest(rowData.id)
  };

  const closeDialog = () => {
    setDataSelectionList([])
    setDataSelectedList([])
    setOpen(false)
  }

  const onClickAddToSelectedList = () => {
    const selectedRowsData = dataSelectionList.map((id) => dataList.find((row) => row.id === id));
    setDataSelectedList(dataSelectedList.concat(selectedRowsData))
    setDataSelectionList([])
  }

  const handleDeleteDataSelected = (data) => {
    let tmpDataSelectedList = dataSelectedList.filter(item => item.id !== data.id);
    setDataSelectedList(tmpDataSelectedList)
  }

  const handleAdd = () => {
    onSubmit(dataSelectedList)
    closeDialog()
  }

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="lg">
        <DialogTitle>Thêm đề thi cho kỳ thi</DialogTitle>
        <DialogContent>
          <Card elevation={5}>
            <CardHeader
              title={
                <Box display="flex" justifyContent="space-between" alignItems="end" width="100%">
                  <Box display="flex" flexDirection="column" width="80%">
                    <h5 style={{margin: '0'}}>Ngân hàng đề thi</h5>
                    <Box display="flex" justifyContent="flex-start" width="100%">
                      <TextField
                        autoFocus
                        id="testCode"
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

                      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
                        <DatePicker
                          label="Thời gian tạo từ"
                          value={createdFromFilter}
                          onChange={event => setCreatedFromFilter(event)}
                          renderInput={(params) => <TextField {...params} error={false} style={{ marginRight: '16px' }}/>}
                        />
                      </LocalizationProvider>

                      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
                        <DatePicker
                          label="Thời gian tạo đến"
                          value={createdToFilter}
                          onChange={event => setCreatedToFilter(event)}
                          renderInput={(params) => <TextField {...params} error={false}/>}
                        />
                      </LocalizationProvider>
                    </Box>
                  </Box>

                  <Box display="flex" justifyContent="flex-end" width="20%">
                    <Button
                      variant="contained"
                      disabled={dataSelectionList.length < 1}
                      color="primary"
                      onClick={onClickAddToSelectedList}
                      startIcon={<AddCircleIcon />}
                    >
                      Thêm vào danh sách
                    </Button>
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
                checkboxSelection
                isRowSelectable={(params) => !dataSelectedList.includes(params.row)}
                onSelectionModelChange = {(ids) => setDataSelectionList(ids)}
                selectionModel={dataSelectionList}
              />
            </CardContent>
          </Card>

          <Card elevation={5} >
            <CardHeader title={
              <h5 style={{margin: '0'}}>Danh sách đề thi đã chọn</h5>
            }/>
            <CardContent>
              <DataGrid
                rows={dataSelectedList}
                columns={columnsSelected}
                disableColumnMenu
                autoHeight
              />
            </CardContent>
          </Card>
        </DialogContent>
        <DialogActions>
          <div>
            <Button
              variant="contained"
              onClick={closeDialog}
            >
              Hủy
            </Button>
            <Button
              variant="contained"
              color="primary"
              disabled={dataSelectedList.length < 1}
              style={{marginLeft: "15px"}}
              onClick={handleAdd}
            >
              Lưu
            </Button>
          </div>
        </DialogActions>
      </Dialog>
      {
        openDetailsDialog && (
          <TestBankDetails
            open={openDetailsDialog}
            setOpen={setOpenDetailsDialog}
            data={testDetails}
          />
        )
      }
    </div>
  );
}

const screenName = "MENU_EXAM_MANAGEMENT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default SelectTestDialog;
