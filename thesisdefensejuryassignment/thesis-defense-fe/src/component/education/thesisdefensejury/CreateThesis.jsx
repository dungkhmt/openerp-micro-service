import { Button, Grid } from "@material-ui/core";
import { Alert } from "@material-ui/lab";
import SearchIcon from "@mui/icons-material/Search";
import {
  Box,
  FormControl,
  InputAdornment,
  InputLabel,
  ListSubheader,
  MenuItem,
  Select,
  TextField,
  Typography,
} from "@mui/material";
import Checkbox from "@mui/material/Checkbox";
import ListItemText from "@mui/material/ListItemText";
import OutlinedInput from "@mui/material/OutlinedInput";
import React, { useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { request } from "../../../api";
import { boxChildComponent, boxComponentStyle } from "./constant";
import { useFetchData } from "hooks/useFetchData";
import { successNoti } from "utils/notification";
import { useKeycloak } from "@react-keycloak/web";
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

const modalStyle = {
  paper: {
    boxSizing: "border-box",
    position: "absolute",
    width: "55%",
    maxHeight: 1000,
    // border: '2px solid #000',
    borderRadius: "5px",
    boxShadow:
      "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)",
    backgroundColor: "white",
    zIndex: 999,
    left: "60%",
    top: "90%",
    transform: "translate(-50% , -50%)",
    padding: "20px 40px",
  },
};

function CreateThesis(props) {
  const navigate = useNavigate();
  const { keycloak } = useKeycloak();
  const [openAlert, setOpenAlert] = React.useState(false);
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);
  const [searchProgramText, setSearchProgramText] = useState("");
  const [searchTeacherText, setSearchTeacherText] = useState("");
  const [searchThesisPlanText, setSearchThesisPlanText] = useState("");
  //
  const [openLoading, setOpenLoading] = React.useState(false);
  const { register, handleSubmit, formState: { errors } } = useForm({
    defaultValues: {
      thesisName: '',
      thesisAbstract: '',
      programId: '',
      thesisDefensePlanId: '',
      studentName: '',
      studentId: '',
      supervisorId: '',
      studentEmail: keycloak?.tokenParsed?.email,
    }
  });
  const [isInputValidThesis, setIsInputValidThesis] = React.useState(true);
  const [isInputValidDescript, setIsInputValidDescrip] = React.useState(true);
  const [isInputValidStudentName, setIsInputValidStudentName] =
    React.useState(true);
  const [isInputValidKeyWord, setIsInputValidKeyWord] = React.useState(true);
  const [isInputValidStudentID, setIsInputValidStudentID] =
    React.useState(true);

  const [errorMessage, setErrorMessage] = React.useState("");
  const [keyword, setKeyword] = React.useState([]);

  const handleChangeKeyword = (event) => {
    const {
      target: { value },
    } = event;
    setKeyword(
      // On autofill we get a stringified value.
      typeof value === "string" ? value.split(",") : value
    );
  };
  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;

  const validateInput = (checkingText) => {
    /* reg exp để kiểm tra xem chuỗi có chỉ bao gồm 10 - 11 chữ số hay không */
    if (checkingText == "" || checkingText == null) {
      return {
        isInputValid: false,
        errorMessage:
          "Trường này không được bỏ trống và không có ký tự đặc biệt",
      };
    }
    return {
      isInputValid: true,
      errorMessage: "",
    };
  };
  const handleInputValidationThesis = (e) => {
    console.log(e);
    const { isInputValid, errorMessage } = validateInput(e.target.value);
    setIsInputValidThesis(isInputValid);
    setErrorMessage(errorMessage);
  };
  const handleInputValidationDescript = (e) => {
    console.log(e);
    const { isInputValid, errorMessage } = validateInput(e.target.value);
    setIsInputValidDescrip(isInputValid);
    setErrorMessage(errorMessage);
  };
  const handleInputValidationStudentName = (e) => {
    console.log(e);
    const { isInputValid, errorMessage } = validateInput(e.target.value);
    setIsInputValidStudentName(isInputValid);
    setErrorMessage(errorMessage);
  };
  const handleInputValidationStudentID = (e) => {
    console.log(e);
    const { isInputValid, errorMessage } = validateInput(e.target.value);
    setIsInputValidStudentID(isInputValid);
    setErrorMessage(errorMessage);
  };


  const handleFormSubmit = (data) => {
    // event.preventDefault();
    // setOpenLoading(true);
    let body = {
      thesisKeyword: keyword,
      ...data
    };
    // setTimeout(() => setOpenAlert(true), 3000);
    request(
      "post",
      "/thesis/save",
      (res) => {
        successNoti('Thêm đồ án mới thành công', true)
      },
      {
        onError: (e) => {
          setShowSubmitSuccess(false);
          setOpenLoading(false);
        },
      },
      body
    ).then();
  };
  const listProgram = useFetchData('/thesis/training-program');
  const listTeacher = useFetchData('/defense-jury/teachers');
  const listPlan = useFetchData('/thesis-defense-plan/get-all');
  const keywords = useFetchData('/academic_keywords/get-all')
  const displayedProgramOptions = useMemo(
    () => listProgram?.filter((option) => containsText(option.name, searchProgramText)),
    [searchProgramText]
  );

  const displayedTeacherOptions = useMemo(
    () =>
      listTeacher?.filter((option) =>
        containsText(option.teacherName, searchTeacherText)
      ),
    [searchTeacherText]
  );
  const displayedPlanOptions = useMemo(
    () => listPlan?.filter((option) => containsText(option.name, searchThesisPlanText)),
    [searchThesisPlanText]
  );

  return (
    <>
      <Box sx={boxComponentStyle}>
        <Typography variant="h4" mb={4} component={"h4"}>
          Thêm mới luận văn
        </Typography>
        <div width="100%">
          <form onSubmit={handleSubmit(handleFormSubmit)}>
            <Box mb={3}>
              <TextField
                fullWidth={true}
                label="Tên luận văn"
                variant="outlined"
                name="name"
                {...register("thesisName", { required: true })}
              />
            </Box>
            <Box sx={boxChildComponent} mb={3}>
              <TextField
                label="Mô tả luận văn"
                multiline
                fullWidth={true}
                rows={5}
                {...register("thesisAbstract")}
                sx={{ mb: 3 }}
              />
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <TextField
                    style={{ margin: "2% 0px" }}
                    fullWidth={true}
                    id="input-with-icon-grid"
                    label="Tên sinh viên"
                    {...register('studentName', { required: true })}
                  />
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <TextField
                    style={{ margin: "2% 0px" }}
                    fullWidth={true}
                    id="input-with-icon-grid"
                    label="Mã số sinh viên"
                    {...register('studentId', { required: true })}
                  />
                </Grid>
              </Grid>
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{ minWidth: "100%" }}>
                    <FormControl fullWidth style={{ margin: "2% 0px" }}>
                      <InputLabel id="search-select-label">
                        Đợt bảo vệ
                      </InputLabel>
                      <Select
                        MenuProps={{ autoFocus: false }}
                        labelId="search-plan-label"
                        id="search-select"
                        label="Options"
                        {...register('thesisDefensePlanId', { required: true })}
                      >
                        <ListSubheader>
                          <TextField
                            size="small"
                            autoFocus
                            placeholder="Type to search..."
                            fullWidth
                            InputProps={{
                              startAdornment: (
                                <InputAdornment position="start">
                                  <SearchIcon />
                                </InputAdornment>
                              ),
                            }}
                            onChange={(e) => setSearchThesisPlanText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {
                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedPlanOptions?.map((option) => (
                          <MenuItem key={option?.id} value={option?.id}>
                            {option?.name}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{ minWidth: "100%" }}>
                    <FormControl fullWidth style={{ margin: "2% 0px" }}>
                      <InputLabel id="search-select-label">
                        Tên giảng viên hướng dẫn
                      </InputLabel>
                      <Select
                        MenuProps={{ autoFocus: false }}
                        labelId="search-select-label"
                        id="search-select"
                        label="Options"
                        {...register('supervisorId', { required: true })}
                      >
                        <ListSubheader>
                          <TextField
                            size="small"
                            autoFocus
                            placeholder="Type to search..."
                            fullWidth
                            InputProps={{
                              startAdornment: (
                                <InputAdornment position="start">
                                  <SearchIcon />
                                </InputAdornment>
                              ),
                            }}
                            onChange={(e) => setSearchTeacherText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {
                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedTeacherOptions?.map((option) => (
                          <MenuItem key={option?.id} value={option?.id}>
                            {option?.teacherName}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
              </Grid>
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{ minWidth: "100%" }}>
                    <FormControl fullWidth style={{ margin: "2% 0px" }}>
                      <InputLabel id="search-select-label">
                        Tên chương trình đào tạo
                      </InputLabel>
                      <Select
                        MenuProps={{ autoFocus: false }}
                        id="search-select"
                        label="Options"
                        {...register('programId', { required: true })}
                      >
                        <ListSubheader>
                          <TextField
                            size="small"
                            autoFocus
                            placeholder="Type to search..."
                            fullWidth
                            InputProps={{
                              startAdornment: (
                                <InputAdornment position="start">
                                  <SearchIcon />
                                </InputAdornment>
                              ),
                            }}
                            onChange={(e) => setSearchProgramText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {
                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedProgramOptions?.map((option) => (
                          <MenuItem key={option?.id} value={option?.id}>
                            {option?.name}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <FormControl fullWidth sx={{ m: 1 }}>
                    <InputLabel id="demo-multiple-checkbox-label">Hướng đề tài</InputLabel>
                    <Select
                      labelId="demo-multiple-checkbox-label"
                      id="demo-multiple-checkbox"
                      multiple
                      value={keyword}
                      onChange={handleChangeKeyword}
                      input={<OutlinedInput label="Tag" />}
                      renderValue={(selected) => selected.join(', ')}
                      MenuProps={MenuProps}
                    >
                      {keywords?.map((ele) => (
                        <MenuItem key={ele?.keyword} value={ele?.keyword}>
                          <Checkbox checked={keyword.indexOf(ele?.keyword) !== -1} />
                          <ListItemText primary={ele?.keyword} />
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
              </Grid>
            </Box>
            <Box sx={{ display: "flex", justifyContent: "end" }}>
              <Button
                type="submit"
                variant="contained"
                color="success"
              >
                Thêm luận văn
              </Button>
            </Box>

            {/* </Grid> */}
          </form>
        </div>
      </Box>
    </>
  );
}

export default CreateThesis;
