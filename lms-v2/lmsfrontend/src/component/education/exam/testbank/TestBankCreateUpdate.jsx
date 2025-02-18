import React, {useEffect, useState} from 'react';
import {Box, Button, Card, CardContent, CircularProgress, Input} from "@material-ui/core";
import {request} from "../../../../api";
import DateFnsUtils from "@date-io/date-fns";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import CardActions from "@material-ui/core/CardActions";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {toast} from "react-toastify";
import RichTextEditor from "../../../common/editor/RichTextEditor";
import {makeStyles} from "@material-ui/core/styles";
import {useHistory} from "react-router-dom";
import {useLocation} from "react-router";
import {getFilenameFromString, getFilePathFromString} from "../ultils/FileUltils";
import TestBankQuestionList from "./TestBankQuestionList";
import {arrayMove} from "react-sortable-hoc";
import QuestionFilePreview from "../questionbank/QuestionFilePreview";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import TestBankAddQuestion from "./TestBankAddQuestion";
import withScreenSecurity from "../../../withScreenSecurity";

const useStyles = makeStyles((theme) => ({
  root: {
    "& .MuiTextField-root": {
      margin: theme.spacing(1),
      width: 200,
    },
  },
  formControl: {
    margin: theme.spacing(1),
    minWidth: 120,
    maxWidth: 300,
  },
}));
const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
  PaperProps: {
    style: {
      maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
      width: 250,
    },
  },
};

function TestBankCreateUpdate(props) {

  const classes = useStyles();
  const history = useHistory();
  const location = useLocation();
  const data = location.state?.data
  const isCreate = location.state?.isCreate

  if(isCreate === undefined){
    window.location.href = '/exam/test-bank';
  }

  const [code, setCode] = useState(data?.code);
  const [name, setName] = useState(data?.name);
  const [description, setDescription] = useState(data?.description);
  const [questions, setQuestions] = useState(data?.questions);
  const [isLoading, setIsLoading] = useState(false);
  const [openFilePreviewDialog, setOpenFilePreviewDialog] = useState(false);
  const [filePreview, setFilePreview] = useState(null);
  const [openAddQuestionDialog, setOpenAddQuestionDialog] = useState(false);
  const [questionDelete, setQuestionDelete] = useState(null)
  const [questionDeleteList, setQuestionDeleteList] = useState([])

  useEffect(() => {
    let tmpQuestions = questions.filter(item => item.id !== questionDelete?.id);
    setQuestions(tmpQuestions)
    if(questionDelete?.examTestQuestionId){
      setQuestionDeleteList(questionDeleteList.concat([{
        id: questionDelete?.examTestQuestionId,
      }]))
    }
  }, [questionDelete]);

  const handleSave = () =>{
    let examTestQuestionSaveReqList = []
    for(let i=0;i<questions.length;i++){
      examTestQuestionSaveReqList.push({
        id: questions[i].examTestQuestionId ? questions[i].examTestQuestionId : null,
        examQuestionId: questions[i].id,
        order: i+1
      })
    }
    const body = {
      code: code,
      name:  name,
      description:  description,
      examTestQuestionSaveReqList: examTestQuestionSaveReqList,
      examTestQuestionDeleteReqList: questionDeleteList
    }
    validateBody(body)

    console.log('body',body)

    setIsLoading(true)
    request(
      "post",
      isCreate ? `/exam-test/create` : '/exam-test/update',
      (res) => {
        if(res.status === 200){
          if(res.data.resultCode === 200){
            toast.success(res.data.resultMsg)
            setIsLoading(false)
            history.push("/exam/test-bank")
          }else{
            toast.error(res.data.resultMsg)
            setIsLoading(false)
          }
        }else {
          toast.error(res)
          setIsLoading(false)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const validateBody = (body) => {
    if(body.code == null || body.code === ''){
      toast.error('Mã đề thi không được bỏ trống')
      return
    }
    if(body.name == null || body.name === ''){
      toast.error('Tên đề thi không được bỏ trống')
      return
    }
    if(body.examTestQuestionSaveReqList.length < 1){
      toast.error('Đề thi phải có ít nhất 1 câu hỏi')
      return
    }
  }

  const handleKeyPress = (event) => {
    const regex = /^[a-zA-Z0-9]+$/
    if (!regex.test(event.key)) {
      event.preventDefault();
    }
  }

  const handleOpenFilePreviewDialog = (data) => {
    setOpenFilePreviewDialog(true)
    setFilePreview(getFilePathFromString(data))
  };

  const onSortEnd = ({ oldIndex, newIndex }) => {
    setQuestions(prevItem => (arrayMove(prevItem, oldIndex, newIndex)));
  };

  const handleOpenAddQuestionDialog = (data) => {
    setOpenFilePreviewDialog(true)
    setFilePreview(getFilePathFromString(data))
  };

  const handleAddQuestionSubmit = (data) => {
    setQuestions(questions.concat(data))
  };

  return (
    <div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              {
                isCreate ? (<h4 style={{margin: 0, padding: 0}}>Thêm mới đề thi</h4>) : (<h4 style={{margin: 0, padding: 0}}>Cập nhật đề thi</h4>)
              }
            </Typography>
            <form className={classes.root} noValidate autoComplete="off">
              <div>
                <div>
                  <TextField
                    autoFocus
                    required
                    onKeyPress={handleKeyPress}
                    disabled={!isCreate}
                    id="testCode"
                    label="Mã đề thi"
                    placeholder="Nhập mã đề thi"
                    value={code}
                    onChange={(event) => {
                      setCode(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />

                  <TextField
                    autoFocus
                    required
                    id="testName"
                    label="Tên đề thi"
                    placeholder="Nhập tên đề thi"
                    value={name}
                    onChange={(event) => {
                      setName(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </div>

                <div>
                  <Typography variant="h6">Mô tả</Typography>
                  <RichTextEditor
                    content={description}
                    onContentChange={(value) =>
                      setDescription(value)
                    }
                  />
                </div>

                <Box display="flex" flexDirection="column" width="100%">
                  <Typography variant="h6">Nội dung</Typography>
                  <TestBankQuestionList
                    items={questions}
                    onSortEnd={onSortEnd}
                    setQuestionDelete={setQuestionDelete}/>

                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => setOpenAddQuestionDialog(true)}
                    startIcon={<AddCircleIcon />}
                    style={{ marginRight: 16 , width: '200px'}}
                  >
                    Thêm câu hỏi
                  </Button>
                </Box>
              </div>
            </form>
          </CardContent>
          <CardActions style={{justifyContent: 'flex-end'}}>
            <Button
              variant="contained"
              onClick={() => history.push("/exam/test-bank")}
            >
              Hủy
            </Button>
            <Button
              disabled={isLoading}
              variant="contained"
              color="primary"
              style={{marginLeft: "15px"}}
              onClick={handleSave}
              type="submit"
            >
              {isLoading ? <CircularProgress/> : "Lưu"}
            </Button>
          </CardActions>
          <TestBankAddQuestion
            open={openAddQuestionDialog}
            setOpen={setOpenAddQuestionDialog}
            onSubmit={handleAddQuestionSubmit}
          ></TestBankAddQuestion>
        </Card>
      </MuiPickersUtilsProvider>
    </div>
  );
}

const screenName = "MENU_EXAM_TEST_BANK";
export default withScreenSecurity(TestBankCreateUpdate, screenName, true);
// export default TestBankCreateUpdate;
