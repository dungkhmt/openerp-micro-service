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
import { importDialogStyles } from "./index.style";
import { courseUrl } from "../apiURL";

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
      `${courseUrl.importCourse}`,
      (res) => {
        setActiveStep(3);
        setResponse(res.data);
        fetchData();
      },
      () => {
        setIsImportSuccess(false);
        setResponse("File không đúng định dạng");
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
            <TableCell style={{ width: "200px" }}>Mã học phần</TableCell>
            <TableCell style={{ width: "300px" }}>Tên học phần</TableCell>
            <TableCell style={{ width: "200px" }}>Số tín chỉ</TableCell>
            <TableCell style={{ width: "400px" }}>
              Học phần tiên quyết
            </TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          <TableRow key="1">
            <TableCell>IT1001</TableCell>
            <TableCell>Tin học đại cương</TableCell>
            <TableCell>3</TableCell>
            <TableCell> </TableCell>
          </TableRow>
          <TableRow key="2">
            <TableCell>IT1002</TableCell>
            <TableCell>Cơ sở dữ liệu</TableCell>
            <TableCell>4</TableCell>
            <TableCell>IT1001</TableCell>
          </TableRow>
          <TableRow key="3">
            <TableCell>IT1003</TableCell>
            <TableCell>Lập trình Java</TableCell>
            <TableCell>4</TableCell>
            <TableCell>IT1001, IT1002</TableCell>
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
      <DialogTitle style={importDialogStyles.dialogTitle}>
        <Typography variant="h5" fontWeight="bold">
          Import danh sách học phần
        </Typography>
      </DialogTitle>

      <DialogContent style={importDialogStyles.dialogContent}>
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

        <div style={importDialogStyles.content}>
          <Slide
            direction={activeStep === 0 ? "left" : "right"}
            in={activeStep === 0}
            unmountOnExit
            mountOnEnter
            timeout={{ enter: 300, exit: 200 }}
          >
            <div style={importDialogStyles.slide1.content}>
              <Typography variant="h4" style={importDialogStyles.slide1.title}>
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
                style={importDialogStyles.slide2.content}
              >
                <Typography variant="h5">Kéo thả file vào đây</Typography>
                <Typography
                  style={importDialogStyles.slide2.title}
                  variant="body1"
                >
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
                  style={importDialogStyles.slide2.button}
                >
                  Chọn file
                </Button>
                {file && (
                  <div>
                    <img
                      src={xlsxIcon}
                      alt="xlsx icon"
                      style={importDialogStyles.slide2.image}
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
            <div style={importDialogStyles.slide3.content}>
              {isImportSuccess ? (
                <div>
                  <Typography
                    variant="h4"
                    style={importDialogStyles.slide3.title}
                  >
                    Đang xử lý dữ liệu
                  </Typography>
                  <div style={importDialogStyles.slide3.loadingIcon}>
                    <CircularProgress size={80} thickness={3} />
                  </div>
                </div>
              ) : (
                <div style={importDialogStyles.slide3.content}>
                  <ReportProblemIcon
                    style={importDialogStyles.slide3.errorIcon}
                    color="error"
                  />
                  <Typography
                    variant="h5"
                    style={importDialogStyles.slide3.errorText}
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
            <div style={importDialogStyles.slide4.content}>
              <Typography variant="h4" style={importDialogStyles.slide4.title}>
                Nhập dữ liệu thành công
              </Typography>
              <Typography
                style={importDialogStyles.slide4.response}
                variant="body1"
              >
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
          <div style={importDialogStyles.slide2.actionButton}>
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
