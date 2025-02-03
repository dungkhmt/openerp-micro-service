import React, {useEffect, useState} from 'react';
import {Box, Button, Card, CardContent, CircularProgress, Input} from "@material-ui/core";
import {request} from "../../../../api";
import {FormControl, MenuItem, Select} from "@mui/material";
import DateFnsUtils from "@date-io/date-fns";
import Typography from "@material-ui/core/Typography";
import TextField from "@material-ui/core/TextField";
import CardActions from "@material-ui/core/CardActions";
import {MuiPickersUtilsProvider} from "@material-ui/pickers";
import {toast} from "react-toastify";
import RichTextEditor from "../../../common/editor/RichTextEditor";
import FileUploader from "../../../common/uploader/FileUploader";
import {DropzoneArea} from "material-ui-dropzone";
import {makeStyles} from "@material-ui/core/styles";
import {useHistory} from "react-router-dom";
import {useLocation} from "react-router";
import {getFilenameFromString, getFilePathFromString} from "../ultils/FileUltils";
import {Assignment, AttachFileOutlined} from "@material-ui/icons";
import DeleteIcon from "@material-ui/icons/Delete";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import {vi} from "date-fns/locale";
import {DatePicker, DateTimePicker} from "@mui/x-date-pickers";
import {LocalizationProvider} from "@mui/x-date-pickers/LocalizationProvider";
import AddCircleIcon from "@material-ui/icons/AddCircle";
import SelectTestDialog from "./SelectTestDialog";
import TestBankDetails from "../testbank/TestBankDetails";
import {formatDateTime, formatDateTimeApi} from "../ultils/DateUltils";
import XLSX from "xlsx";
import {DataGrid} from "@material-ui/data-grid";
import withScreenSecurity from "../../../withScreenSecurity";
import {parseHTMLToString} from "../ultils/DataUltils";

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
const baseColumn = {
  sortable: false,
};

function ExamCreateUpdate(props) {

  const columns = [
    {
      field: "code",
      headerName: "Mã học viên",
      minWidth: 170,
      ...baseColumn
    },
    {
      field: "name",
      headerName: "Họ và tên",
      minWidth: 200,
      flex: 1,
      ...baseColumn
    },
    {
      field: "email",
      headerName: "Email",
      ...baseColumn,
      flex: 1,
      minWidth: 200,
    },
    {
      field: "phone",
      headerName: "Số điện thoại",
      ...baseColumn,
      minWidth: 200,
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
            <DeleteIcon style={{cursor: 'pointer', color: 'red'}} onClick={(data) => handleDeleteStudent(rowData?.row)}/>
          </Box>
        )
      }
    },
  ];

  const statusList = [
    {
      value: 0,
      name: 'Chưa kích hoạt'
    },
    {
      value: 1,
      name: 'Kích hoạt'
    }
  ]

  const answerStatusList = [
    {
      value: 'NO_OPEN',
      name: 'Không được xem'
    },
    {
      value: 'OPEN',
      name: 'Được xem'
    }
  ]

  const classes = useStyles();
  const history = useHistory();
  const location = useLocation();
  const data = location.state?.data
  const isCreate = location.state?.isCreate

  if(isCreate === undefined){
    window.location.href = '/exam/management';
  }

  const [code, setCode] = useState(data?.code);
  const [name, setName] = useState(data?.name);
  const [status, setStatus] = useState(data?.status);
  const [answerStatus, setAnswerStatus] = useState(data?.answerStatus);
  const [description, setDescription] = useState(data?.description ? data?.description : '');
  const [examTestId, setExamTestId] = useState(data?.examTestId);
  const [startTime, setStartTime] = useState(data?.startTime);
  const [endTime, setEndTime] = useState(data?.endTime);
  const [isLoading, setIsLoading] = useState(false);
  const [openSelectTestDialog, setOpenSelectTestDialog] = useState(false);
  const [testList, setTestList] = useState(data?.examTests)
  const [openTestDetailsDialog, setOpenTestDetailsDialog] = useState(false);
  const [testDetails, setTestDetails] = useState(null)
  const [examStudents, setExamStudents] = useState(data?.examStudents)
  const [examStudentDeletes, setExamStudentsDeletes] = useState([])

  const handleSave = () =>{
    const body = {
      code: code,
      name: name,
      description: description,
      status: status,
      answerStatus: answerStatus,
      examTestId: examTestId,
      startTime: formatDateTimeApi(startTime),
      endTime: formatDateTimeApi(endTime),
      examStudents: examStudents,
      examStudentDeletes: examStudentDeletes
    }
    validateBody(body)

    setIsLoading(true)
    request(
      "post",
      isCreate ? `/exam/create` : '/exam/update',
      (res) => {
        if(res.status === 200){
          if(res.data.resultCode === 200){
            toast.success(res.data.resultMsg)
            setIsLoading(false)
            history.push("/exam/management")
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
      toast.error('Mã kỳ thi không được bỏ trống')
      return
    }
    if(body.name == null || body.name === ''){
      toast.error('Tên kỳ thi không được bỏ trống')
      return
    }
    if(body.examTestId == null || body.examTestId === ''){
      toast.error('Chọn đề thi cho kỳ thi')
      return
    }
    if(body.startTime == null || body.startTime === ''){
      toast.error('Thời gian bắt đầu không được bỏ trống')
      return
    }
    if(body.endTime == null || body.endTime === ''){
      toast.error('Thời gian kết thúc không được bỏ trống')
      return
    }
  }

  const handleKeyPress = (event) => {
    const regex = /^[a-zA-Z0-9]+$/
    if (!regex.test(event.key)) {
      event.preventDefault();
    }
  }

  const handleAddTestSubmit = (data) => {
    // setTestList(testList.concat(data))
    setTestList(data)
    setExamTestId(data[0]?.id)
  };

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
          setOpenTestDetailsDialog(true)
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) },
      body
    );
  }

  const handleOpenPopupDetails = (data) => {
    detailsTest(data.id)
  };

  const handleImportFile = (file) => {
    let tmpExamStudents = []
    if(file){
      let fileReader = new FileReader();
      fileReader.onload = (event) => {
        const data = event.target.result;

        const workbook = XLSX.read(data, {
          type: 'binary'
        });
        workbook.SheetNames.forEach(sheet => {
          const rowObject = XLSX.utils.sheet_to_json(workbook.Sheets[sheet], {
            header: 1,
          });
          const filteredRows = rowObject.slice(1);
          filteredRows.map((row) => {
            tmpExamStudents.push({
              code: row[1],
              name: row[2],
              email: row[3],
              phone: row[4]
            })
          });
          setExamStudents((prevExamStudents) => {
            const newStudents = tmpExamStudents.filter(
              (tmpStudent) =>
                !prevExamStudents.some((student) => student.code === tmpStudent.code)
            );
            return [...prevExamStudents, ...newStudents];
          });
        });
      };
      fileReader.readAsBinaryString(file);
    }
  }

  const handleDeleteStudent = (data) => {
    setExamStudents(examStudents.filter(value => value.code !== data.code))
    if(data.id){
      setExamStudentsDeletes(examStudentDeletes.concat([data]))
    }
  }

  return (
    <div>
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <Card>
          <CardContent>
            <Typography variant="h5" component="h2">
              {
                isCreate ? (<h4 style={{margin: 0, padding: 0}}>Thêm mới kỳ thi</h4>) : (<h4 style={{margin: 0, padding: 0}}>Cập nhật kỳ thi</h4>)
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
                    id="examCode"
                    label="Mã kỳ thi"
                    placeholder="Nhập mã kỳ thi"
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
                    id="examName"
                    label="Tên kỳ thi"
                    placeholder="Nhập tên kỳ thi"
                    value={name}
                    style={{width: '50%'}}
                    onChange={(event) => {
                      setName(event.target.value);
                    }}
                    InputLabelProps={{
                      shrink: true,
                    }}
                  />
                </div>

                <div>
                  <TextField
                    required
                    autoFocus
                    id="Examstatus"
                    select
                    label="Trạng thái"
                    value={status}
                    onChange={(event) => {
                      setStatus(event.target.value);
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

                  <TextField
                    required
                    autoFocus
                    id="ExamAnswerstatus"
                    select
                    label="Trạng thái đáp án"
                    value={answerStatus}
                    onChange={(event) => {
                      setAnswerStatus(event.target.value);
                    }}
                  >
                    {
                      answerStatusList.map(item => {
                        return (
                          <MenuItem value={item.value}>{item.name}</MenuItem>
                        )
                      })
                    }
                  </TextField>

                  <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
                    <DateTimePicker
                      label="Thời gian bắt đầu"
                      value={startTime}
                      onChange={event => setStartTime(event)}
                      renderInput={(params) => <TextField {...params} error={false} style={{marginRight: '16px'}}/>}
                    />
                  </LocalizationProvider>

                  <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={vi}>
                    <DateTimePicker
                      label="Thời gian kết thúc"
                      value={endTime}
                      onChange={event => setEndTime(event)}
                      renderInput={(params) => <TextField {...params} error={false} style={{marginRight: '16px'}}/>}
                    />
                  </LocalizationProvider>
                </div>

                <div>
                  {
                    testList.length < 1 ?
                      (
                        <Button
                          variant="contained"
                          color="primary"
                          onClick={() => setOpenSelectTestDialog(true)}
                          startIcon={<AddCircleIcon/>}
                          style={{marginRight: 16, width: '200px'}}
                        >
                          Chọn đề thi
                        </Button>
                      )
                      :
                      (
                        <div style={{
                          border: '2px solid #f5f5f5',
                          display: 'flex',
                          justifyContent: 'space-between',
                          borderRadius: '10px',
                          padding: '10px',
                          marginBottom: '10px',
                          marginTop: '10px'
                        }}>
                          <Box display="flex"
                               flexDirection='column'
                               width="calc(100% - 110px)"
                               style={{
                                 userSelect: "none",
                                 WebkitUserSelect: "none",
                                 MozUserSelect: "none",
                                 msUserSelect: "none"
                               }}>
                            <div style={{display: 'flex'}}>
                              <span style={{fontStyle: 'italic', marginRight: '5px'}}>({testList[0]?.code})</span>
                              <span style={{display: "block", fontWeight: 'bold'}}>{testList[0]?.name}</span>
                            </div>
                            <p>{parseHTMLToString(testList[0]?.description)}</p>
                          </Box>

                          <Box display="flex" justifyContent='space-between' width="110px">
                            <button
                              style={{
                                height: 'max-content',
                                padding: '8px',
                                border: 'none',
                                borderRadius: '8px',
                                cursor: 'pointer',
                                fontWeight: 'bold'
                              }}
                              onClick={(event) => {
                                handleOpenPopupDetails(testList[0])
                                event.preventDefault()
                                event.stopPropagation()
                              }}>
                              Chi tiết
                            </button>
                            <button
                              style={{
                                height: 'max-content',
                                padding: '8px',
                                border: 'none',
                                borderRadius: '8px',
                                color: 'red',
                                cursor: 'pointer',
                                fontWeight: 'bold'
                              }}
                              onClick={(event) => {
                                setTestList([])
                                event.preventDefault()
                                event.stopPropagation()
                              }}>
                              Xoá
                            </button>
                          </Box>
                        </div>
                      )
                  }

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

                <div>
                  <Typography
                    variant="subtitle1"
                    display="block"
                    style={{margin: "5px 0 0 7px", width: "100%"}}
                  >
                    Import danh sách học viên
                  </Typography>
                  <DropzoneArea
                    dropzoneClass={classes.dropZone}
                    filesLimit={1}
                    showPreviews={true}
                    showPreviewsInDropzone={false}
                    useChipsForPreview
                    dropzoneText="Kéo và thả tệp vào đây hoặc nhấn để chọn tệp"
                    previewText="Xem trước:"
                    previewChipProps={{
                      variant: "outlined",
                      color: "primary",
                      size: "medium",
                    }}
                    getFileAddedMessage={(fileName) =>
                      `Tệp ${fileName} tải lên thành công`
                    }
                    getFileRemovedMessage={(fileName) => `Tệp ${fileName} đã loại bỏ`}
                    getFileLimitExceedMessage={(filesLimit) =>
                      `Vượt quá số lượng tệp tối đa được cho phép. Chỉ được phép tải lên tối đa ${filesLimit} tệp.`
                    }
                    alertSnackbarProps={{
                      anchorOrigin: {vertical: "bottom", horizontal: "right"},
                      autoHideDuration: 1800,
                    }}
                    acceptedFiles={[".xls", ".xlsx"]}
                    onChange={(files) => handleImportFile(files[0])}
                  ></DropzoneArea>
                </div>

                <div>
                  <Typography
                    variant="subtitle1"
                    display="block"
                    style={{margin: "5px 0 0 7px", width: "100%"}}
                  >
                    Danh sách học viên
                  </Typography>
                  <DataGrid
                    rows={examStudents}
                    columns={columns}
                    getRowId={(row) => row.code}
                    disableColumnMenu
                    autoHeight
                  />
                </div>
              </div>
            </form>
          </CardContent>
          <CardActions style={{justifyContent: 'flex-end'}}>
            <Button
              variant="contained"
              onClick={() => history.push("/exam/management")}
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
          {
            openTestDetailsDialog && (
              <TestBankDetails
                open={openTestDetailsDialog}
                setOpen={setOpenTestDetailsDialog}
                data={testDetails}
              />
            )
          }
          <SelectTestDialog
            open={openSelectTestDialog}
            setOpen={setOpenSelectTestDialog}
            onSubmit={handleAddTestSubmit}
          ></SelectTestDialog>
        </Card>
      </MuiPickersUtilsProvider>
    </div>
  );
}

const screenName = "MENU_EXAM_MANAGEMENT";
export default withScreenSecurity(ExamCreateUpdate, screenName, true);
// export default ExamCreateUpdate;
