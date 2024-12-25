import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {Box, Button, Card, CardContent, Input} from "@material-ui/core";
import MaterialTable, {MTableToolbar} from "material-table";
import {tableIcons} from "../../../../utils/iconutil";
import {MuiThemeProvider} from "@material-ui/core/styles";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import UploadButton from "../../UploadButton";
import {request} from "../../../../api";
import {Link} from "react-router-dom";
import {themeTable} from "../../../../utils/MaterialTableUtils";
import {FormControl, MenuItem, Select} from "@mui/material";
import useDebounceValue from "../hooks/use-debounce";
import InputLabel from "@material-ui/core/InputLabel";
import displayTime from "../../../../utils/DateTimeUtils";

function QuestionBank(props) {

  const columns = [
    {
      field: "code",
      title: "Mã câu hỏi",
    },
    {
      field: "content",
      title: "Nội dung câu hỏi",
    },
    {
      field: "explain",
      title: "Giải thích",
    },
    {
      field: "type",
      title: "Loại câu hỏi",
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
          console.log("filter question bank, error ", res)
        }
      },
      { onError: (e) => console.log("filter question bank, error ", e) },
      body
    );
  }

  return (
    <div>
      <MaterialTable
        title={
          <Box display="flex" flexDirection="column" width="100%">
            <h2>Ngân hàng câu hỏi</h2>
            <Box display="flex" justifyContent="flex-end" width="100%">
              <Input
                value={keywordFilter}
                placeholder="Tìm kiếm theo code hoặc nội dung"
                style={{ width: "300px", marginRight: "16px"}}
                onChange={(event) => setKeywordFilter(event.target.value)}
              />

              <FormControl>
                <InputLabel id="questionType">Loại câu hỏi</InputLabel>
                <Select
                  labelId="questionType"
                  value={typeFilter}
                  style={{ width: "150px"}}
                  onChange={event => setTypeFilter(event.target.value)}
                >
                  {
                    questionTypes.map(item => {
                      return (
                        <MenuItem value={item.value}>{item.name}</MenuItem>
                      )
                    })
                  }
                </Select>
              </FormControl>
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
          search: false
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
                    // onClick={onClickCreateNewButton}
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
      />
    </div>
  );
}

const screenName = "MENU_EXAM_QUESTION_BANK";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default QuestionBank;
