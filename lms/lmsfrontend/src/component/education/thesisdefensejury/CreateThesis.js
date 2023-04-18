import {Button, Grid} from "@material-ui/core";
import React, {useEffect, useMemo, useState} from "react";
import {useHistory} from "react-router-dom";
import {SubmitSuccess} from "../programmingcontestFE/SubmitSuccess";
import {request} from "../../../api";
import {Alert} from "@material-ui/lab";
import {
  Box,
  FormControl,
  InputAdornment,
  InputLabel,
  ListSubheader,
  MenuItem,
  Select,
  TextField,
  Typography
} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";
import ModalLoading from "./ModalLoading"
import {boxChildComponent, boxComponentStyle} from "../../taskmanagement/ultis/constant";
import {useForm} from "react-hook-form";
import FormError from "./FormError";
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import OutlinedInput from '@mui/material/OutlinedInput';

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
    boxSizing: 'border-box',
    position: 'absolute',
    width: '55%',
    maxHeight: 1000,
    // border: '2px solid #000',
    borderRadius: '5px',
    boxShadow: '0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)',
    backgroundColor: 'white',
    zIndex: 999,
    left: '60%',
    top: '90%',
    transform: 'translate(-50% , -50%)',
    padding: '20px 40px'

  }
}


function CreateThesis(props) {
  const history = useHistory();
  const [name, setName] = React.useState("");
  const [thesisAbstract, setThesisAbstract] = React.useState("");
  const [programName, setProgramName] = React.useState("");
  const [thesisPlanName, setThesisPlanName] = React.useState("");
  const [studentName, setStudentName] = React.useState("");
  const [supervisorName, setSupervisorName] = React.useState("");
  const [reviewerName, setReviewerName] = React.useState("");
  const [defenseJuryName, setDefenseJuryName] = React.useState("");
  const [userLoginID, setUserLoginID] = React.useState("");
  const [keywords, setKeywords] = React.useState([]);
  const [openAlert, setOpenAlert] = React.useState(false);
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);
  const [listProgram, setListProgram] = React.useState([]);
  const [listJury, setListJury] = React.useState([]);
  const [listTeacher, setListTeacher] = React.useState([]);
  const [listPlan, setListPlan] = React.useState([]);
  const [searchText, setSearchText] = useState("");
  const [listUserLoginID, setListUserLoginID] = React.useState([]);
  const [openLoading, setOpenLoading] = React.useState(false);
  const {register, errors} = useForm();
  const [isInputValidThesis, setIsInputValidThesis] = React.useState(true);
  const [isInputValidDescript, setIsInputValidDescrip] = React.useState(true);
  const [isInputValidStudentName, setIsInputValidStudentName] = React.useState(true);
  const [isInputValidKeyWord, setIsInputValidKeyWord] = React.useState(true);
  const [isInputValidStudentID, setIsInputValidStudentID] = React.useState(true);

  const [errorMessage, setErrorMessage] = React.useState('');
  const [keyword, setKeyword] = React.useState([]);


  const handleChangeKeyword = (event) => {
    const {
      target: {value},
    } = event;
    setKeyword(
      // On autofill we get a stringified value.
      typeof value === 'string' ? value.split(',') : value,
    );
  };
  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;

  const validateInput = (checkingText) => {
    /* reg exp để kiểm tra xem chuỗi có chỉ bao gồm 10 - 11 chữ số hay không */
    if (checkingText == "" || checkingText == null) {
      return {
        isInputValid: false,
        errorMessage: 'Trường này không được bỏ trống và không có ký tự đặc biệt'
      };
    }
    return {
      isInputValid: true,
      errorMessage: ''
    };
  }
  const handleInputValidationThesis = e => {
    console.log(e)
    const {isInputValid, errorMessage} = validateInput(e.target.value);
    setIsInputValidThesis(isInputValid)
    setErrorMessage(errorMessage)
  }
  const handleInputValidationDescript = e => {
    console.log(e)
    const {isInputValid, errorMessage} = validateInput(e.target.value);
    setIsInputValidDescrip(isInputValid)
    setErrorMessage(errorMessage)
  }
  const handleInputValidationStudentName = e => {
    console.log(e)
    const {isInputValid, errorMessage} = validateInput(e.target.value);
    setIsInputValidStudentName(isInputValid)
    setErrorMessage(errorMessage)
  }
  const handleInputValidationStudentID = e => {
    console.log(e)
    const {isInputValid, errorMessage} = validateInput(e.target.value);
    setIsInputValidStudentID(isInputValid)
    setErrorMessage(errorMessage)
  }

  async function getAllProgram() {
    request(
      // token,
      // history,
      "GET",
      "/program_tranings",
      (res) => {
        console.log(res.data)
        setListProgram(res.data)

      }
    );
  }

  async function getAllUserLoginID() {
    request(
      // token,
      // history,
      "GET",
      "/thesis/userlogins",
      (res) => {
        console.log(res.data)
        setListUserLoginID(res.data)

      }
    );
  }

  async function getAllJury() {
    request(
      // token,
      // history,
      "GET",
      "/defense_jurys",
      (res) => {
        console.log("Jury", res.data)
        setListJury(res.data.DefenseJurys)

      }
    );
  }

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      "/thesis_defense_plan",
      (res) => {
        console.log("Plan", res.data)
        setListPlan(res.data)

      }
    );
  }

  async function getAllTeachers() {
    request(
      // token,
      // history,
      "GET",
      "/teachers",
      (res) => {
        console.log("Teachers", res.data)
        setListTeacher(res.data)

      }
    );
  }

  async function getAllKeywords() {
    request(
      // token,
      // history,
      "GET",
      "/academic_keywords",
      (res) => {
        console.log(res.data)
        var keywordsArray = [];
        for (let i = 0; i < res.data.length; i++) {
          keywordsArray.push(res.data[i].keyword)
        }
        console.log(keywordsArray);
        setKeywords(keywordsArray);

      }
    );
  }

  const handleFormSubmit = (event) => {
    event.preventDefault();
    // setOpenLoading(true);
    let body = {
      name: name,
      thesis_abstract: thesisAbstract,
      program_name: programName,
      student_name: studentName,
      supervisor_name: supervisorName,
      reviewer_name: reviewerName,
      defense_jury_name: defenseJuryName,
      userLoginID: userLoginID,
      thesisPlanName: thesisPlanName,
      keyword: keyword,
    };
    console.log("Body Thesis:", body)
    setTimeout(
      () => setOpenAlert(true),
      3000
    );
    request(
      "post",
      "/thesis",
      (res) => {
        console.log(res.data)
        setShowSubmitSuccess(true);
        setOpenLoading(false);
        history.push({
          pathname: `/thesis`,
        });
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
      },
      {
        onError: (e) => {
          setShowSubmitSuccess(false);
          setOpenLoading(false);
        }
      },
      body
    ).then();

  }

  useEffect(() => {
    getAllProgram();
    getAllJury();
    getAllTeachers();
    getAllPlan();
    getAllKeywords();
    // getAllUserLoginID();
  }, [])

  const displayedProgramOptions = useMemo(
    () => listProgram.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  const displayedTeacherOptions = useMemo(
    () => listTeacher.filter((option) => containsText(option.teacherName, searchText)),
    [searchText]
  );
  const displayedJuryOptions = useMemo(
    () => listJury.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  const displayedPlanOptions = useMemo(
    () => listPlan.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  const displayedUserLoginIDOptions = useMemo(
    () => listUserLoginID.filter((option) => containsText(option.userLoginId, searchText)),
    [searchText]
  );
  return (
    <>
      <Box sx={boxComponentStyle}>
        <Typography variant="h4" mb={4} component={'h4'}>
          Thêm mới luận văn
        </Typography>
        <div width="100%">
          <form>
            <Box mb={3}>
              <TextField
                fullWidth={true}
                label="Tên luận văn"
                variant="outlined"
                name="name"
                value={name}
                onChange={(event) => {
                  setName(event.target.value)
                }}
                onBlur={(e) => handleInputValidationThesis(e)}
                inputRef={register({required: "Thiếu tên luận văn!"})}
                error={!!errors.name}
                helperText={errors.name?.message}
              />
              <FormError
                isHidden={isInputValidThesis}
                errorMessage={errorMessage}
              />
            </Box>
            <Box sx={boxChildComponent} mb={3}>
              <TextField
                label="Mô tả luận văn"
                multiline
                fullWidth={true}
                rows={5}
                value={thesisAbstract}
                onChange={(event) => {
                  setThesisAbstract(event.target.value)
                }}
                onBlur={(e) => handleInputValidationDescript(e)}

                name="description"
                inputRef={register}
                sx={{mb: 3}}
              />
              <FormError
                isHidden={isInputValidDescript}
                errorMessage={errorMessage}
              />
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{minWidth: '100%'}}>
                    <FormControl fullWidth style={{margin: "2% 0px"}}>
                      <InputLabel id="search-select-label">Tên hội đồng</InputLabel>
                      <Select
                        MenuProps={{autoFocus: false}}
                        labelId="search-select-label"
                        id="search-select"
                        value={defenseJuryName}
                        label="Options"
                        onChange={(e) => setDefenseJuryName(e.target.value)}
                        onClose={() => setSearchText("")}
                        renderValue={() => defenseJuryName}
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
                                  <SearchIcon/>
                                </InputAdornment>
                              )
                            }}
                            onChange={(e) => setSearchText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {
                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedJuryOptions.map((option, i) => (
                          <MenuItem key={i} value={option.name}>
                            {option.name}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <TextField
                    style={{margin: "2% 0px"}}
                    value={studentName}
                    onChange={(event) => {
                      setStudentName(event.target.value)
                    }} fullWidth={true}
                    onBlur={(e) => handleInputValidationStudentName(e)} id="input-with-icon-grid"
                    label="Tên sinh viên"/>
                  <FormError
                    isHidden={isInputValidStudentName}
                    errorMessage={errorMessage}
                  />
                </Grid>
              </Grid>
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{minWidth: '100%'}}>
                    <FormControl fullWidth style={{margin: "2% 0px"}}>
                      <InputLabel id="search-select-label">Tên giảng viên hướng dẫn</InputLabel>
                      <Select

                        MenuProps={{autoFocus: false}}
                        labelId="search-select-label"
                        id="search-select"
                        value={supervisorName}
                        label="Options"
                        onChange={(e) => setSupervisorName(e.target.value)}
                        onClose={() => setSearchText("")}

                        renderValue={() => supervisorName}
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
                                  <SearchIcon/>
                                </InputAdornment>
                              )
                            }}
                            onChange={(e) => setSearchText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {

                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedTeacherOptions.map((option, i) => (
                          <MenuItem key={i} value={option.id}>
                            {option.id}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{minWidth: '100%'}}>
                    <FormControl fullWidth style={{margin: "2% 0px"}}>
                      <InputLabel id="search-select-label">Tên giảng viên Reviewer</InputLabel>
                      <Select

                        MenuProps={{autoFocus: false}}
                        labelId="search-select-label"
                        id="search-select"
                        value={reviewerName}
                        label="Options"
                        onChange={(e) => setReviewerName(e.target.value)}
                        onClose={() => setSearchText("")}

                        renderValue={() => reviewerName}
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
                                  <SearchIcon/>
                                </InputAdornment>
                              )
                            }}
                            onChange={(e) => setSearchText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {

                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedTeacherOptions.map((option, i) => (
                          <MenuItem key={i} value={option.id}>
                            {option.id}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
              </Grid>
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{minWidth: '100%'}}>
                    <FormControl fullWidth style={{margin: "2% 0px"}}>
                      <InputLabel id="search-select-label">Đợt bảo vệ</InputLabel>
                      <Select

                        MenuProps={{autoFocus: false}}
                        labelId="search-select-label"
                        id="search-select"
                        value={thesisPlanName}
                        label="Options"
                        onChange={(e) => setThesisPlanName(e.target.value)}
                        onClose={() => setSearchText("")}

                        renderValue={() => thesisPlanName}
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
                                  <SearchIcon/>
                                </InputAdornment>
                              )
                            }}
                            onChange={(e) => setSearchText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {

                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedPlanOptions.map((option, i) => (
                          <MenuItem key={i} value={option.name}>
                            {option.name}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <TextField
                    style={{margin: "2% 0px"}}
                    value={userLoginID}
                    onBlur={(e) => handleInputValidationStudentID(e)}
                    onChange={(event) => {
                      setUserLoginID(event.target.value)
                    }} fullWidth={true} id="input-with-icon-grid" label="StudentID"/>
                  <FormError
                    isHidden={isInputValidStudentID}
                    errorMessage={errorMessage}
                  />
                </Grid>
              </Grid>
              <Grid container spacing={2}>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <FormControl sx={{m: 1, width: 300}}>
                    <InputLabel id="demo-multiple-checkbox-label">Hướng đề tài</InputLabel>
                    <Select
                      labelId="demo-multiple-checkbox-label"
                      id="demo-multiple-checkbox"
                      multiple
                      value={keyword}
                      onChange={handleChangeKeyword}
                      input={<OutlinedInput label="Tag"/>}
                      renderValue={(selected) => selected.join(', ')}
                      MenuProps={MenuProps}
                    >
                      {keywords.map((ele) => (
                        <MenuItem key={ele} value={ele}>
                          <Checkbox checked={keyword.indexOf(ele) > -1}/>
                          <ListItemText primary={ele}/>
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>
                <Grid item={true} xs={6} spacing={2} p={2}>
                  <Box sx={{minWidth: '100%'}}>
                    <FormControl fullWidth style={{margin: "2% 0px"}}>
                      <InputLabel id="search-select-label">Tên chương trình đào tạo</InputLabel>
                      <Select

                        MenuProps={{autoFocus: false}}
                        labelId="search-select-label"
                        id="search-select"
                        value={programName}
                        label="Options"
                        onChange={(e) => setProgramName(e.target.value)}
                        onClose={() => setSearchText("")}

                        renderValue={() => programName}
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
                                  <SearchIcon/>
                                </InputAdornment>
                              )
                            }}
                            onChange={(e) => setSearchText(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key !== "Escape") {

                                e.stopPropagation();
                              }
                            }}
                          />
                        </ListSubheader>
                        {displayedProgramOptions.map((option, i) => (
                          <MenuItem key={i} value={option.name}>
                            {option.name}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Box>
                </Grid>
              </Grid>
            </Box>
            <Box sx={{display: 'flex', justifyContent: 'end'}}>
              <Button variant="contained" color="success" onClick={handleFormSubmit}>
                Thêm luận văn
              </Button>
            </Box>

            {(openAlert === true) ? (<div>
              {showSubmitSuccess === true ? (<SubmitSuccess
                showSubmitSuccess={showSubmitSuccess}
                content={"Tạo mới thành công"}
              />) : (<Alert severity="error">Thêm mới thất bại</Alert>)}

            </div>) : (<></>)}
            {/* </Grid> */}
          </form>
          <ModalLoading openLoading={openLoading}/>
        </div>

      </Box>
    </>
  );
}

export default CreateThesis;