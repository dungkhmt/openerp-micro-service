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
import {parseHTMLToString} from "../ultils/DataUltils";

const baseColumn = {
  sortable: false,
};

const rowsPerPage = [5, 10, 20];

function TestBankAddQuestion(props) {

  const columns = [
    {
      field: "code",
      headerName: "Mã câu hỏi",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "content",
      headerName: "Nội dung câu hỏi",
      ...baseColumn,
      flex: 1,
      renderCell: (rowData) => {
        return parseHTMLToString(rowData.value)
      }
    },
    {
      field: "type",
      headerName: "Loại câu hỏi",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        if(rowData.value === 0){
          return (
            <strong style={{color: '#716DF2'}}>Trắc nghiệm</strong>
          )
        }else if(rowData.value === 1){
          return (
            <strong style={{color: '#61bd6d'}}>Tự luận</strong>
          )
        }else{
          return 'Tất cả'
        }
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
            <InfoIcon style={{cursor: 'pointer'}} onClick={(data) => handleDetailsQuestion(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const columnsSelected = [
    {
      field: "code",
      headerName: "Mã câu hỏi",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "content",
      headerName: "Nội dung câu hỏi",
      ...baseColumn,
      flex: 1,
      renderCell: (rowData) => {
        return parseHTMLToString(rowData.value)
      }
    },
    {
      field: "type",
      headerName: "Loại câu hỏi",
      ...baseColumn,
      minWidth: 170,
      renderCell: (rowData) => {
        if(rowData.value === 0){
          return (
            <strong style={{color: '#716DF2'}}>Trắc nghiệm</strong>
          )
        }else if(rowData.value === 1){
          return (
            <strong style={{color: '#61bd6d'}}>Tự luận</strong>
          )
        }else{
          return 'Tất cả'
        }
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
            <InfoIcon style={{cursor: 'pointer'}} onClick={(data) => handleDetailsQuestion(rowData?.row)}/>
            <DeleteIcon style={{cursor: 'pointer', color: 'red'}} onClick={(data) => handleDeleteQuestionSelected(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const questionTypes = [
    {
      value: 'all',
      name: 'Tất cả'
    },
    {
      value: 0,
      name: 'Trắc nghiệm'
    },
    {
      value: 1,
      name: 'Tự luận'
    }
  ]

  const { open, setOpen, onSubmit} = props;

  const [questionList, setQuestionList] = useState([])
  const [questionSelectionList, setQuestionSelectionList] = useState([])
  const [questionSelectedList, setQuestionSelectedList] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [typeFilter, setTypeFilter] = useState('all')
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);
  const [questionDetails, setQuestionDetails] = useState(null)

  const debouncedKeywordFilter = useDebounceValue(keywordFilter, 500)

  useEffect(() => {
    filterQuestion()
  }, [page, pageSize, debouncedKeywordFilter, typeFilter]);

  const filterQuestion = () =>{
    const body = {
      keyword: keywordFilter,
      type: typeFilter === 'all' ? null : typeFilter
    }
    request(
      "post",
      `/exam-question/filter?page=${page}&size=${pageSize}`,
      (res) => {
        if(res.status === 200){
          setQuestionList(res.data.content);
          setTotalCount(res.data.totalElements);
        }else {
          toast.error(res)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const detailsQuestion = (id) =>{
    const body = {
      id: id
    }
    request(
      "post",
      `/exam-question/details`,
      (res) => {
        if(res.data.resultCode === 200){
          setQuestionDetails(res.data.data)
          setOpenDetailsDialog(true)
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const handleDetailsQuestion = (rowData) => {
    detailsQuestion(rowData.id)
  };

  const closeDialog = () => {
    setQuestionSelectionList([])
    setQuestionSelectedList([])
    setOpen(false)
  }

  const onClickAddToSelectedList = () => {
    const selectedRowsData = questionSelectionList.map((id) => questionList.find((row) => row.id === id));
    setQuestionSelectedList(questionSelectedList.concat(selectedRowsData))
    setQuestionSelectionList([])
  }

  const handleDeleteQuestionSelected = (data) => {
    let tmpQuestionSelectedList = questionSelectedList.filter(item => item.id !== data.id);
    setQuestionSelectedList(tmpQuestionSelectedList)
  }

  const handleAdd = () => {
    onSubmit(questionSelectedList)
    closeDialog()
  }

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="lg">
        <DialogTitle>Thêm câu hỏi vào đề thi</DialogTitle>
        <DialogContent>
          <Card elevation={5}>
            <CardHeader
              title={
                <Box display="flex" justifyContent="space-between" alignItems="end" width="100%">
                  <Box display="flex" flexDirection="column" width="80%">
                    <h5 style={{marginTop: '0', paddingTop: '0'}}>Tìm kiếm trong Ngân hàng câu hỏi</h5>
                    <Box display="flex" justifyContent="flex-start" width="100%">
                      <TextField
                        autoFocus
                        id="questionCode"
                        label="Nội dung tìm kiếm"
                        placeholder="Tìm kiếm theo code hoặc nội dung"
                        value={keywordFilter}
                        style={{width: "300px", marginRight: "16px"}}
                        onChange={(event) => {
                          setKeywordFilter(event.target.value);
                        }}
                        InputLabelProps={{
                          shrink: true,
                        }}
                      />

                      <TextField
                        id="questionType"
                        select
                        label="Loại câu hỏi"
                        style={{width: "150px"}}
                        value={typeFilter}
                        onChange={(event) => {
                          setTypeFilter(event.target.value);
                        }}
                      >
                        {
                          questionTypes.map(item => {
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
                      disabled={questionSelectionList.length < 1}
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
                rows={questionList}
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
                isRowSelectable={(params) => !questionSelectedList.includes(params.row)}
                onSelectionModelChange = {(ids) => setQuestionSelectionList(ids)}
                selectionModel={questionSelectionList}
              />
            </CardContent>
          </Card>

          <Card elevation={5} >
            <CardHeader title={
              <h5 style={{marginTop: '0', paddingTop: '0'}}>Danh sách câu hỏi đã chọn</h5>
            }/>
            <CardContent>
              <DataGrid
                rows={questionSelectedList}
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
              disabled={questionSelectedList.length < 1}
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
          <QuestionBankDetails
            open={openDetailsDialog}
            setOpen={setOpenDetailsDialog}
            question={questionDetails}
          />
        )
      }
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default TestBankAddQuestion;
