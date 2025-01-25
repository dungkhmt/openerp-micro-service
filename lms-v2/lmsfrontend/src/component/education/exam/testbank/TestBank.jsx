import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {Box, Button, Card, CardContent, CardHeader, Input} from "@material-ui/core";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import {request} from "../../../../api";
import {Link, useHistory} from "react-router-dom";
import useDebounceValue from "../hooks/use-debounce";
import {toast} from "react-toastify";
import TextField from "@material-ui/core/TextField";
import {DataGrid} from "@material-ui/data-grid";
import InfoIcon from "@mui/icons-material/Info";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";
import {formatDate, formatDateApi, formatDateTime} from "../ultils/DateUltils";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import {DatePicker} from "@mui/x-date-pickers";
import {vi} from "date-fns/locale";
import TestBankDelete from "./TestBankDelete";
import TestBankDetails from "./TestBankDetails";
import {parseHTMLToString} from "../ultils/DataUltils";

const baseColumn = {
  sortable: false,
};

const rowsPerPage = [5, 10, 20];

function TestBank(props) {

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
      minWidth: 120,
      maxWidth: 120,
      renderCell: (rowData) => {
        return (
          <Box display="flex" justifyContent="space-between" alignItems='center' width="100%">
            <InfoIcon style={{cursor: 'pointer'}} onClick={(data) => handleOpenPopupDetails(rowData?.row)}/>
            <EditIcon style={{cursor: 'pointer'}} onClick={(data) => handleUpdate(rowData?.row)}/>
            <DeleteIcon style={{cursor: 'pointer', color: 'red'}} onClick={(data) => handleOpenPopupDelete(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const [data, setData] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [createdFromFilter, setCreatedFromFilter] = useState("")
  const [createdToFilter, setCreatedToFilter] = useState("")
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [idDelete, setIdDelete] = useState("")
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);
  const [testDetails, setTestDetails] = useState(null)

  const debouncedKeywordFilter = useDebounceValue(keywordFilter, 500)
  const history = useHistory();

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
          setData(res.data.content);
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
      pathname: "/exam/create-update-test-bank",
      state: {
        data: {
          code: "",
          name: "",
          description: "",
          questions: []
        },
        isCreate: true
      },
    });
  };

  const handleUpdate = (rowData) => {
    const body = {
      id: rowData.id
    }
    request(
      "post",
      `/exam-test/details`,
      (res) => {
        if(res.data.resultCode === 200){
          setTestDetails(res.data.data)
          let tmpQuestions = []
          for(let question of res.data.data.examTestQuestionDetails){
            tmpQuestions.push({
              examTestQuestionId: question.examTestQuestionId,
              id: question.questionId,
              code: question.questionCode,
              type: question.questionType,
              content: question.questionContent,
              filePath: question.questionFile,
              numberAnswer: question.questionNumberAnswer,
              contentAnswer1: question.questionContentAnswer1,
              contentAnswer2: question.questionContentAnswer2,
              contentAnswer3: question.questionContentAnswer3,
              contentAnswer4: question.questionContentAnswer4,
              contentAnswer5: question.questionContentAnswer5,
              multichoice: question.questionMultichoice,
              answer: question.questionAnswer,
              explain: question.questionExplain,
              order: question.questionOrder
            })
          }
          history.push({
            pathname: "/exam/create-update-test-bank",
            state: {
              data: {
                code: res.data.data.code,
                name: res.data.data.name,
                description: res.data.data.description,
                questions: tmpQuestions
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

  const handleOpenPopupDetails = (rowData) => {
    detailsTest(rowData.id)
  };

  const handleOpenPopupDelete = (rowData) => {
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
                <h4 style={{marginTop: 0, paddingTop: 0}}>Ngân hàng đề thi</h4>
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
            rows={data}
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
      <TestBankDelete
        open={openDeleteDialog}
        setOpen={setOpenDeleteDialog}
        id={idDelete}
        onReloadData={() => {
          filter()
        }}
      />
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
export default withScreenSecurity(TestBank, screenName, true);
// export default TestBank;
