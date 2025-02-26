import React, {useEffect, useState} from 'react';
import withScreenSecurity from "../../../withScreenSecurity";
import {
  Box,
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  Input
} from "@material-ui/core";
import {DialogActions} from "@mui/material";
import {formatDateTime} from "../ultils/DateUltils";
import {request} from "../../../../api";
import {toast} from "react-toastify";
import TestBankDetails from "../testbank/TestBankDetails";
import {DataGrid} from "@material-ui/data-grid";
import ExamMarking from "./ExamMarking";
import {parseHTMLToString} from "../ultils/DataUltils";

const baseColumn = {
  sortable: false,
};

function ExamDetails(props) {

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
      field: "totalScore",
      headerName: "Điểm",
      ...baseColumn,
      minWidth: 100,
    },
    {
      field: "totalTime",
      headerName: "Thời gian làm",
      ...baseColumn,
      minWidth: 130,
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
            {
              rowData?.row?.examResultId ? (
                <Button
                  variant="contained"
                  color="primary"
                  onClick={(data) => handleMarking(rowData?.row)}
                >
                  Chấm điểm
                </Button>
              ) : (
                <Button
                  variant="outlined"
                  color="secondary"
                  style={{pointerEvents: "none"}}
                >
                  Chưa làm
                </Button>
              )
            }
          </Box>
        )
      }
    },
  ];

  const { open, setOpen, dataExam} = props;

  const [data, setData] = useState(dataExam)
  const [openTestDetailsDialog, setOpenTestDetailsDialog] = useState(false);
  const [testDetails, setTestDetails] = useState(null)
  const [openExamDetailsMarkingDialog, setOpenExamDetailsMarkingDialog] = useState(false);
  const [examDetailsMarking, setExamDetailsMarking] = useState(null)

  const handleOpenPopupTestDetails = (test) =>{
    const body = {
      id: test.id
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

  const closeDialog = () => {
    setOpen(false)
  }

  const handleMarking = (rowData) => {
    request(
      "get",
      `/exam/details-marking/${rowData?.id}`,
      (res) => {
        if(res.data.resultCode === 200){
          setExamDetailsMarking(res.data.data)
          setOpenExamDetailsMarkingDialog(true)
        }else{
          toast.error(res.data.resultMsg)
        }
      },
      { onError: (e) => toast.error(e) }
    );
  }

  return (
    <div>
      <Dialog open={open} fullWidth maxWidth="lg">
        <DialogTitle>{data?.name}</DialogTitle>
        <DialogContent>
          <div style={{display: "flex", justifyContent: 'space-between'}}>
            <div style={{display: "flex"}}>
              <h4 style={{margin: '0 5px 0 0', padding: 0}}>Mã kỳ thi:</h4>
              <span>{data?.code}</span>
            </div>
            <div style={{display: "flex"}}>
              <h4 style={{margin: '0 5px 0 0', padding: 0}}>Trạng thái:</h4>
              <span>{data?.status === 0 ? 'Chưa kích hoạt' : 'Kích hoạt'}</span>
            </div>
            <div style={{display: "flex"}}>
              <h4 style={{margin: '0 5px 0 0', padding: 0}}>Thời gian bắt đầu:</h4>
              <span>{formatDateTime(data?.startTime)}</span>
            </div>
            <div style={{display: "flex"}}>
              <h4 style={{margin: '0 5px 0 0', padding: 0}}>Thời gian kết thúc:</h4>
              <span>{formatDateTime(data?.endTime)}</span>
            </div>
          </div>

          <div style={{display: "flex", flexDirection: "column"}}>
            <h4 style={{margin: '15px 5px 0 0', padding: 0}}>Mô tả kỳ thi:</h4>
            <p style={{margin: 0, padding: 0}}>{parseHTMLToString(data?.description)}</p>
          </div>

          <div style={{display: "flex", flexDirection: "column"}}>
            <h4 style={{margin: '15px 5px 0 0', padding: 0}}>Đề thi:</h4>
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
                   width="calc(100% - 80px)"
                   style={{
                     userSelect: "none",
                     WebkitUserSelect: "none",
                     MozUserSelect: "none",
                     msUserSelect: "none"
                   }}>
                <div style={{display: 'flex'}}>
                  <span style={{fontStyle: 'italic', marginRight: '5px'}}>({data.examTests[0]?.code})</span>
                  <span style={{display: "block", fontWeight: 'bold'}}>{data.examTests[0]?.name}</span>
                </div>
                <p>{parseHTMLToString(data.examTests[0]?.description)}</p>
              </Box>

              <Box display="flex" justifyContent='space-between' width="80px">
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
                    handleOpenPopupTestDetails(data?.examTests[0])
                    event.preventDefault()
                    event.stopPropagation()
                  }}>
                  Chi tiết
                </button>
              </Box>
            </div>
          </div>

          <div>
            <h4 style={{margin: '15px 5px 0 0', padding: 0}}>Danh sách học viên:</h4>
            <DataGrid
              rows={data?.examStudents}
              columns={columns}
              getRowId={(row) => row.code}
              disableColumnMenu
              autoHeight
            />
          </div>
        </DialogContent>
        <DialogActions>
          <Button
            variant="contained"
            onClick={closeDialog}
          >
            Hủy
          </Button>
        </DialogActions>
      </Dialog>
      {
        openTestDetailsDialog && (
          <TestBankDetails
            open={openTestDetailsDialog}
            setOpen={setOpenTestDetailsDialog}
            data={testDetails}
          />
        )
      }
      {
        openExamDetailsMarkingDialog && (
          <ExamMarking
            open={openExamDetailsMarkingDialog}
            setOpen={setOpenExamDetailsMarkingDialog}
            data={examDetailsMarking}
            setDataDetails={setData}
          />
        )
      }
    </div>
  );
}

const screenName = "MENU_EXAM_MANAGEMENT";
// export default withScreenSecurity(QuestionBank, screenName, true);
export default ExamDetails;
