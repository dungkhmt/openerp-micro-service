import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {Box, Button, Card, CardContent, Input} from "@material-ui/core";
import MaterialTable, {MTableToolbar} from "material-table";
import {tableIcons} from "../../../../utils/iconutil";
import {MuiThemeProvider} from "@material-ui/core/styles";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import UploadButton from "../../UploadButton";
import {request} from "../../../../api";
import {Link, useHistory} from "react-router-dom";
import {themeTable} from "../../../../utils/MaterialTableUtils";
import {FormControl, MenuItem, Select} from "@mui/material";
import useDebounceValue from "../hooks/use-debounce";
import {toast} from "react-toastify";
import TextField from "@material-ui/core/TextField";
import parser from "html-react-parser"
import QuestionBankDelete from "./QuestionBankDelete";
import QuestionBankDetails from "./QuestionBankDetails";

function QuestionBank(props) {

  const columns = [
    {
      field: "id",
      hidden: true
    },
    {
      field: "code",
      sorting: false,
      title: "Mã câu hỏi",
    },
    {
      field: "content",
      title: "Nội dung câu hỏi",
      sorting: false,
      render: (rowData) => {
        return parser(rowData.content)
      }
    },
    {
      field: "answer",
      title: "Đáp án",
      sorting: false,
      render: (rowData) => {
        return parser(rowData.answer)
      }
    },
    {
      field: "explain",
      title: "Giải thích",
      hidden: true
    },
    {
      field: "type",
      title: "Loại câu hỏi",
      sorting: false,
      render: (rowData) => {
        if(rowData.type === 0){
          return 'Trắc nghiệm'
        }else if(rowData.type === 1){
          return 'Tự luận'
        }else{
          return 'Tất cả'
        }
      },
    },
    {
      field: "numberAnswer",
      title: "Số đáp án",
      hidden: true,
    },
    {
      field: "contentAnswer1",
      title: "Phương án số 1",
      hidden: true,
    },
    {
      field: "contentAnswer2",
      title: "Phương án số 2",
      hidden: true,
    },
    {
      field: "contentAnswer2",
      title: "Phương án số 3",
      hidden: true,
    },
    {
      field: "contentAnswer3",
      title: "Phương án số 3",
      hidden: true,
    },
    {
      field: "contentAnswer4",
      title: "Phương án số 4",
      hidden: true,
    },
    {
      field: "contentAnswer5",
      title: "Phương án số 5",
      hidden: true,
    },
    {
      field: "multichoice",
      title: "Nhiều đáp án",
      hidden: true,
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

  const [questionList, setQuestionList] = useState([])
  const [page, setPage] = useState(0)
  const [pageSize, setPageSize] = useState(5)
  const [totalCount, setTotalCount] = useState(0)
  const [keywordFilter, setKeywordFilter] = useState("")
  const [typeFilter, setTypeFilter] = useState('all')
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [idDelete, setIdDelete] = useState("")
  const [openDetailsDialog, setOpenDetailsDialog] = useState(false);
  const [questionDetails, setQuestionDetails] = useState(null)

  const debouncedKeywordFilter = useDebounceValue(keywordFilter, 500)
  const history = useHistory();

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

  const onClickCreateNewButton = () => {
    history.push({
      pathname: "/exam/create-update-question-bank",
      state: {
        question: {
          code: "",
          type: 1,
          content: "",
          numberAnswer: "",
          contentAnswer1: "",
          contentAnswer2: "",
          contentAnswer3: "",
          contentAnswer4: "",
          contentAnswer5: "",
          multichoice: false,
          answer: "",
          explain: ""
        },
        isCreate: true
      },
    });
  };

  const handleUpdateQuestion = (rowData) => {
    history.push({
      pathname: "/exam/create-update-question-bank",
      state: {
        question: {
          code: rowData.code,
          type: rowData.type,
          content: rowData.content,
          numberAnswer: rowData.numberAnswer,
          contentAnswer1: rowData.contentAnswer1,
          contentAnswer2: rowData.contentAnswer2,
          contentAnswer3: rowData.contentAnswer3,
          contentAnswer4: rowData.contentAnswer4,
          contentAnswer5: rowData.contentAnswer5,
          multichoice: rowData.multichoice,
          answer: rowData.answer,
          explain: rowData.explain
        },
        isCreate: false
      },
    });
  };

  const handleDetailsQuestion = (rowData) => {
    detailsQuestion(rowData.id)
  };

  const handleDeleteQuestion = (rowData) => {
    setOpenDeleteDialog(true)
    setIdDelete(rowData.id)
  };

  return (
    <div>
      <MaterialTable
        title={
          <Box display="flex" flexDirection="column" width="100%">
            <h2>Ngân hàng câu hỏi</h2>
            <Box display="flex" justifyContent="flex-end" width="100%">
              <TextField
                autoFocus
                id="questionCode"
                label="Nội dung tìm kiếm"
                placeholder="Tìm kiếm theo code hoặc nội dung"
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
                id="questionType"
                select
                label="Loại câu hỏi"
                style={{ width: "150px"}}
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
        }
        columns={columns}
        key = {questionList.length}
        data={(query) =>
          new Promise(resolve => {
            setPage(query.page);
            setPageSize(query.pageSize);
            resolve({
              data: questionList,
              page: page,
              totalCount: totalCount,
            });
          })
        }
        icons={tableIcons}
        localization={{
          header: {
            actions: "",
          },
          body: {
            emptyDataSourceMessage: "Không có bản ghi nào để hiển thị",
            filterRow: {
              filterTooltip: "Lọc",
            },
          },
        }}
        options={{
          search: false,
          actionsColumnIndex: -1
        }}
        components={{
          Toolbar: (props) => (
            <div>
              <MTableToolbar {...props} />
              <MuiThemeProvider theme={themeTable}>
                <Box display="flex" justifyContent="flex-end" width="98%">
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={onClickCreateNewButton}
                    startIcon={<AddCircleIcon />}
                    style={{ marginRight: 16 }}
                  >
                    Thêm mới
                  </Button>
                  <UploadButton
                    buttonTitle="Tải lên"
                    // onClickSaveButton={onClickSaveButton}
                    filesLimit={1}
                  />
                </Box>
              </MuiThemeProvider>
            </div>
          ),
        }}
        actions={[
          {
            icon: 'info',
            tooltip: 'Xem chi tiết câu hỏi',
            onClick: (event, rowData) => handleDetailsQuestion(rowData)
          },
          {
            icon: 'edit',
            tooltip: 'Cập nhật câu hỏi',
            onClick: (event, rowData) => handleUpdateQuestion(rowData)
          },
          {
            icon: 'delete',
            tooltip: 'Xoá câu hỏi',
            onClick: (event, rowData) => handleDeleteQuestion(rowData)
          }
        ]}
      />
      {
        openDetailsDialog && (
          <QuestionBankDetails
            open={openDetailsDialog}
            setOpen={setOpenDetailsDialog}
            question={questionDetails}
          />
        )
      }
      <QuestionBankDelete
        open={openDeleteDialog}
        setOpen={setOpenDeleteDialog}
        id={idDelete}
        onReloadQuestions={() => {
          filterQuestion()
        }}
      />
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionBank;
