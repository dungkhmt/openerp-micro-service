import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Paper,
  Step,
  StepLabel,
  Stepper,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  Slide,
  CircularProgress,
} from "@mui/material";
import { useState, useRef, useEffect } from "react";
import xlsxIcon from "../../../assets/img/xlsx_icon.svg";
import { request } from "api";
import ReportProblemIcon from "@mui/icons-material/ReportProblem";

const ImportDialog = ({ open, handleClose, fetchData, semester }) => {
  const [activeStep, setActiveStep] = useState(0);
  const [file, setFile] = useState(null);
  const [isImportSuccess, setIsImportSuccess] = useState(true);
  const [response, setResponse] = useState("");
  const inputRef = useRef();

  useEffect(() => {
    setIsImportSuccess(true);
    setFile(null);
    setActiveStep(0);
  }, [open, semester]);

  const handleDragOver = (event) => {
    event.preventDefault();
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setFile(event.dataTransfer.files[0]);
  };

  const handleDemoDataStep = () => {
    setActiveStep(1);
  };

  const handleBackStep = () => {
    setActiveStep(activeStep - 1);
  };

  const handleImport = () => {
    setActiveStep(2);
    const formData = new FormData();
    formData.append("excelFile", file);
    request(
      "post",
      `/class-call/import-class/${semester}`,
      (res) => {
        setActiveStep(3);
        setResponse(res.data);
        fetchData();
      },
      {
        onError: (e) => {
          setIsImportSuccess(false);
          setResponse("File không đúng định dạng");
        },
      },
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
  };

  const TableDemoData = () => {
    return (
      <TableContainer component={Paper}>
        <TableHead>
          <TableRow>
            <TableCell width={100}>Mã lớp</TableCell>
            <TableCell width={150}>Mã môn học</TableCell>
            <TableCell width={200}>Tên môn học</TableCell>
            <TableCell width={150}>Phòng học</TableCell>
            <TableCell width={50}>Ngày</TableCell>
            <TableCell width={150}>Tiết bắt đầu</TableCell>
            <TableCell width={150}>Tiết kết thúc</TableCell>
            <TableCell width={200}>Ghi chú</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow key="1">
            <TableCell>100</TableCell>
            <TableCell>IT1001</TableCell>
            <TableCell>Tin học đại cương</TableCell>
            <TableCell>B1-301</TableCell>
            <TableCell style={{ textAlign: "center" }}>4</TableCell>
            <TableCell style={{ textAlign: "center" }}>1</TableCell>
            <TableCell style={{ textAlign: "center" }}>6</TableCell>
            <TableCell>Ghi chú của lớp học 1</TableCell>
          </TableRow>
          <TableRow key="2">
            <TableCell>101</TableCell>
            <TableCell>IT1002</TableCell>
            <TableCell>Cơ sở dữ liệu</TableCell>
            <TableCell>B1-302</TableCell>
            <TableCell style={{ textAlign: "center" }}>5</TableCell>
            <TableCell style={{ textAlign: "center" }}>1</TableCell>
            <TableCell style={{ textAlign: "center" }}>6</TableCell>
            <TableCell>Ghi chú của lớp học 2</TableCell>
          </TableRow>
        </TableBody>
      </TableContainer>
    );
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      fullWidth
      maxWidth="lg"
      sx={{
        "& .MuiDialog-paper": {
          height: 800,
        },
      }}
    >
      <DialogTitle sx={{ textAlign: "center", marginTop: "1%" }}>
        <Typography variant="h5" fontWeight="bold">
          Import danh sách lớp học cho học kì {semester}
        </Typography>
      </DialogTitle>
      <DialogContent sx={{ padding: "2em" }}>
        <Stepper nonLinear activeStep={activeStep}>
          <Step key={0} completed={activeStep > 0}>
            <StepLabel>Yêu cầu dữ liệu</StepLabel>
          </Step>
          <Step key={1} completed={activeStep > 1}>
            <StepLabel>Lấy dữ liệu</StepLabel>
          </Step>
          <Step key={2} completed={activeStep > 2}>
            <StepLabel error={!isImportSuccess}>Xử lý</StepLabel>
          </Step>
          <Step key={3}>
            <StepLabel>Hoàn thành</StepLabel>
          </Step>
        </Stepper>

        <div
          style={{
            display: "flex",
            justifyContent: "space-around",
            alignItems: "center",
            height: "90%",
            overflowX: "hidden",
          }}
        >
          <Slide
            direction={activeStep === 0 ? "left" : "right"}
            in={activeStep === 0}
            unmountOnExit
            mountOnEnter
            timeout={{ enter: 300, exit: 200 }}
          >
            <div style={{ width: "90%" }}>
              <Typography
                variant="h4"
                style={{ textAlign: "center", fontWeight: "bold" }}
              >
                Yêu cầu dữ liệu Excel
              </Typography>
              <TableDemoData />
            </div>
          </Slide>

          <Slide
            direction={activeStep === 1 ? "left" : "right"}
            in={activeStep === 1}
            unmountOnExit
            mountOnEnter
            timeout={{ enter: 300, exit: 200 }}
          >
            <div>
              <div
                onDragOver={handleDragOver}
                onDrop={handleDrop}
                style={{
                  display: "flex",
                  flexDirection: "column",
                  alignItems: "center",
                  justifyContent: "center",
                  gap: "1rem",
                  border: "2px dashed #aaaaaa",
                  padding: "8rem",
                  borderRadius: "8px",
                  marginTop: "2em",
                }}
              >
                <Typography variant="h5">Kéo thả file vào đây</Typography>
                <Typography style={{ textAlign: "center" }} variant="body1">
                  Hoặc
                </Typography>
                <input
                  type="file"
                  accept=".xlsx,.xls"
                  onChange={(event) => setFile(event.target.files[0])}
                  style={{ display: "none" }}
                  ref={inputRef}
                />
                <Button
                  variant="contained"
                  onClick={() => inputRef.current.click()}
                  style={{ alignSelf: "center" }}
                >
                  Chọn file
                </Button>
                {file && (
                  <div>
                    <img
                      src={xlsxIcon}
                      alt="xlsx icon"
                      style={{ width: "50px", height: "50px" }}
                    />
                  </div>
                )}
                <Typography variant="body1">{file ? file.name : ""}</Typography>
              </div>
            </div>
          </Slide>

          <Slide
            direction={activeStep === 2 ? "left" : "right"}
            in={activeStep === 2}
            unmountOnExit
            mountOnEnter
            timeout={{ enter: 300, exit: 200 }}
          >
            <div
              style={{
                width: "90%",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              {isImportSuccess ? (
                <div>
                  <Typography
                    variant="h4"
                    style={{ textAlign: "center", fontWeight: "bold" }}
                  >
                    Đang xử lý dữ liệu
                  </Typography>
                  <div style={{ textAlign: "center", marginTop: "2em" }}>
                    <CircularProgress size={80} thickness={3} />
                  </div>
                </div>
              ) : (
                <div
                  style={{
                    width: "90%",
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                    justifyContent: "center",
                  }}
                >
                  <ReportProblemIcon style={{ fontSize: 200 }} color="error" />
                  <Typography
                    variant="h5"
                    style={{
                      textAlign: "center",
                      fontWeight: "bold",
                      marginBottom: "2em",
                    }}
                  >
                    {response ? response : "Lỗi không xác định"}
                  </Typography>
                </div>
              )}
            </div>
          </Slide>

          <Slide
            direction={activeStep === 3 ? "left" : "right"}
            in={activeStep === 3}
            unmountOnExit
            mountOnEnter
            timeout={{ enter: 300, exit: 200 }}
          >
            <div
              style={{
                width: "90%",
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <Typography
                variant="h4"
                style={{ textAlign: "center", fontWeight: "bold" }}
              >
                Nhập dữ liệu thành công
              </Typography>
              <Typography style={{ textAlign: "center" }} variant="body1">
                {response}
              </Typography>
            </div>
          </Slide>
        </div>
      </DialogContent>
      <DialogActions>
        {activeStep === 0 && (
          <Button variant="contained" onClick={handleDemoDataStep}>
            Tiếp tục
          </Button>
        )}
        {activeStep === 1 && (
          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              width: "100%",
            }}
          >
            <Button onClick={handleBackStep} variant="contained">
              Quay lại
            </Button>
            <Button
              disabled={file === null}
              onClick={handleImport}
              variant="contained"
            >
              Nhập dữ liệu
            </Button>
          </div>
        )}
        {activeStep === 2 && (
          <Button onClick={handleClose} variant="contained">
            Thoát
          </Button>
        )}
        {activeStep === 3 && (
          <Button onClick={handleClose} variant="contained">
            Hoàn thành
          </Button>
        )}
      </DialogActions>
    </Dialog>
  );
};

export default ImportDialog;
