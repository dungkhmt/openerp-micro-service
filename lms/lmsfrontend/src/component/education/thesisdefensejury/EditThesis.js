import {Button, Grid} from "@material-ui/core";
import React, {useEffect, useMemo, useState} from "react";
import {useHistory, useLocation, useParams} from "react-router-dom";
import {SubmitSuccess} from "../programmingcontestFE/SubmitSuccess";
import {request} from "../../../api";
import {Alert} from "@material-ui/lab";
import {Box, FormControl, InputAdornment, InputLabel, ListSubheader, MenuItem, Select, TextField} from "@mui/material";
import SearchIcon from "@mui/icons-material/Search";


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

function EditThesis(props) {
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
  const [keyword, setKeyword] = React.useState([]);
  const [openAlert, setOpenAlert] = React.useState(false);
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);
  const [listProgram, setListProgram] = React.useState([]);
  const [listJury, setListJury] = React.useState([]);
  const [listTeacher, setListTeacher] = React.useState([]);
  const [listPlan, setListPlan] = React.useState([]);
  const [searchText, setSearchText] = useState("");
  const params = useParams();
  const location = useLocation();

  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;


  async function getThesisDetail() {
    request(
      // token,
      // history,
      "GET",
      `/thesis/${params.id}`,
      (res) => {
        console.log(res.data)
        var thesis = res.data;
        setName(thesis.name);
        setThesisAbstract(thesis.thesis_abstract);
        setProgramName(thesis.program_name);
        setThesisPlanName(thesis.thesisPlanName);
        setStudentName(thesis.student_name);
        setSupervisorName(thesis.supervisor_name);
        setReviewerName(thesis.reviewer_name);
        setDefenseJuryName(thesis.defense_jury_name);
        setUserLoginID(thesis.userLoginID);
        setKeyword([]);
        //   setListProgram(res.data)

      }
    );
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

  const handleBack = (e) => {
    e.preventDefault();
    history.push({
      pathname: `/thesis/${params.id}`,
    });
  }

  const handleFormSubmit = (event) => {
    event.preventDefault();
    let body = {
      id: params.id,
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
    setTimeout(
      () => setOpenAlert(true),
      3000
    );
    request(
      "put",
      "/thesis/edit",
      (res) => {
        console.log(res.data)
        setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
      },
      {
        onError: (e) => {
          setShowSubmitSuccess(false);
        }
      },
      body
    ).then();
    history.push({
      pathname: `/thesis`,
    });
  }

  useEffect(() => {
    getAllProgram();
    getAllJury();
    getAllTeachers();
    getAllPlan();
    getThesisDetail();
  }, [])

  useEffect(() => {
    console.log(location.state.thesisID);
  }, [location]);

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
  return (

    <div style={modalStyle.paper}>
      <h2 id="simple-modal-title">Cập nhật luận văn</h2>
      <div width="100%">
        <form>
          <Grid container spacing={1}>

            <Grid container item xs={12} spacing={2}>
              <TextField
                style={{margin: "2% 0px"}}
                value={name}
                onChange={(event) => {
                  setName(event.target.value)
                }} fullWidth={true} id="input-with-icon-grid" label="Tên luận văn"/>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <TextField
                style={{margin: "2% 0px"}}
                value={thesisAbstract}
                onChange={(event) => {
                  setThesisAbstract(event.target.value)
                }} fullWidth={true} id="input-with-icon-grid" label="Mô tả luận văn"/>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <Box sx={{minWidth: '100%'}}>
                <FormControl fullWidth style={{margin: "2% 0px"}}>
                  <InputLabel id="search-select-label">Tên hội đồng</InputLabel>
                  <Select
                    // Disables auto focus on MenuItems and allows TextField to be in focus
                    MenuProps={{autoFocus: false}}
                    labelId="search-select-label"
                    id="search-select"
                    value={defenseJuryName}
                    label="Options"
                    onChange={(e) => setDefenseJuryName(e.target.value)}
                    onClose={() => setSearchText("")}
                    // This prevents rendering empty string in Select's value
                    // if search text would exclude currently selected option.
                    renderValue={() => defenseJuryName}
                  >
                    {/* TextField is put into ListSubheader so that it doesn't
                                            act as a selectable item in the menu
                                            i.e. we can click the TextField without triggering any selection.*/}
                    <ListSubheader>
                      <TextField
                        size="small"
                        // Autofocus on textfield
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
                            // Prevents autoselecting item while typing (default Select behaviour)
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
            <Grid container item xs={12} spacing={2}>
              <TextField
                style={{margin: "2% 0px"}}
                value={studentName}
                onChange={(event) => {
                  setStudentName(event.target.value)
                }} fullWidth={true} id="input-with-icon-grid" label="Tên sinh viên"/>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <Box sx={{minWidth: '100%'}}>
                <FormControl fullWidth style={{margin: "2% 0px"}}>
                  <InputLabel id="search-select-label">Tên giảng viên hướng dẫn</InputLabel>
                  <Select
                    // Disables auto focus on MenuItems and allows TextField to be in focus
                    MenuProps={{autoFocus: false}}
                    labelId="search-select-label"
                    id="search-select"
                    value={supervisorName}
                    label="Options"
                    onChange={(e) => setSupervisorName(e.target.value)}
                    onClose={() => setSearchText("")}
                    // This prevents rendering empty string in Select's value
                    // if search text would exclude currently selected option.
                    renderValue={() => supervisorName}
                  >
                    {/* TextField is put into ListSubheader so that it doesn't
                                            act as a selectable item in the menu
                                            i.e. we can click the TextField without triggering any selection.*/}
                    <ListSubheader>
                      <TextField
                        size="small"
                        // Autofocus on textfield
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
                            // Prevents autoselecting item while typing (default Select behaviour)
                            e.stopPropagation();
                          }
                        }}
                      />
                    </ListSubheader>
                    {displayedTeacherOptions.map((option, i) => (
                      <MenuItem key={i} value={option.teacherName}>
                        {option.teacherName}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <Box sx={{minWidth: '100%'}}>
                <FormControl fullWidth style={{margin: "2% 0px"}}>
                  <InputLabel id="search-select-label">Tên giảng viên Reviewer</InputLabel>
                  <Select
                    // Disables auto focus on MenuItems and allows TextField to be in focus
                    MenuProps={{autoFocus: false}}
                    labelId="search-select-label"
                    id="search-select"
                    value={reviewerName}
                    label="Options"
                    onChange={(e) => setReviewerName(e.target.value)}
                    onClose={() => setSearchText("")}
                    // This prevents rendering empty string in Select's value
                    // if search text would exclude currently selected option.
                    renderValue={() => reviewerName}
                  >
                    {/* TextField is put into ListSubheader so that it doesn't
                                            act as a selectable item in the menu
                                            i.e. we can click the TextField without triggering any selection.*/}
                    <ListSubheader>
                      <TextField
                        size="small"
                        // Autofocus on textfield
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
                            // Prevents autoselecting item while typing (default Select behaviour)
                            e.stopPropagation();
                          }
                        }}
                      />
                    </ListSubheader>
                    {displayedTeacherOptions.map((option, i) => (
                      <MenuItem key={i} value={option.teacherName}>
                        {option.teacherName}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Box>
            </Grid>
            <Grid container item xs={12} spacing={2}>
              <Box sx={{minWidth: '100%'}}>
                <FormControl fullWidth style={{margin: "2% 0px"}}>
                  <InputLabel id="search-select-label">Đợt bảo vệ</InputLabel>
                  <Select
                    // Disables auto focus on MenuItems and allows TextField to be in focus
                    MenuProps={{autoFocus: false}}
                    labelId="search-select-label"
                    id="search-select"
                    value={thesisPlanName}
                    label="Options"
                    onChange={(e) => setThesisPlanName(e.target.value)}
                    onClose={() => setSearchText("")}
                    // This prevents rendering empty string in Select's value
                    // if search text would exclude currently selected option.
                    renderValue={() => thesisPlanName}
                  >
                    {/* TextField is put into ListSubheader so that it doesn't
                                            act as a selectable item in the menu
                                            i.e. we can click the TextField without triggering any selection.*/}
                    <ListSubheader>
                      <TextField
                        size="small"
                        // Autofocus on textfield
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
                            // Prevents autoselecting item while typing (default Select behaviour)
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
            <Grid container item xs={12} spacing={2}>
              <TextField
                style={{margin: "2% 0px"}}
                value={userLoginID}
                onChange={(event) => {
                  setUserLoginID(event.target.value)
                }} fullWidth={true} id="input-with-icon-grid" label="Tên người tạo"/>
            </Grid>
            {/* <Grid container item xs={12} spacing={2}>
                                <TextField 
                                    style={{margin:"2% 0px"}}
                                    value={keyword}
                                    onChange={(event) => {
                                    setKeyword(event.target.value)
                                }} fullWidth={true} id="input-with-icon-grid" label="Tên hướng đề tài lựa chọn" />
                            </Grid> */}
            <Grid container item xs={12} spacing={2}>
              <Box sx={{minWidth: '100%'}}>
                <FormControl fullWidth style={{margin: "2% 0px"}}>
                  <InputLabel id="search-select-label">Program Name</InputLabel>
                  <Select
                    // Disables auto focus on MenuItems and allows TextField to be in focus
                    MenuProps={{autoFocus: false}}
                    labelId="search-select-label"
                    id="search-select"
                    value={programName}
                    label="Options"
                    onChange={(e) => setProgramName(e.target.value)}
                    onClose={() => setSearchText("")}
                    // This prevents rendering empty string in Select's value
                    // if search text would exclude currently selected option.
                    renderValue={() => programName}
                  >
                    {/* TextField is put into ListSubheader so that it doesn't
                                            act as a selectable item in the menu
                                            i.e. we can click the TextField without triggering any selection.*/}
                    <ListSubheader>
                      <TextField
                        size="small"
                        // Autofocus on textfield
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
                            // Prevents autoselecting item while typing (default Select behaviour)
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


            <Grid container spacing={2}>
              <Grid item xs={6} alignItems="center">
                <Button color="primary" type="submit" onClick={handleFormSubmit} width="100%">Cập nhật</Button>
              </Grid>
              <Grid item xs={6} alignItems="center">
                <Button color="primary" type="submit" onClick={handleBack} width="100%">Back</Button>
              </Grid>
            </Grid>

            {(openAlert === true) ? (<div>
              {showSubmitSuccess === true ? (<SubmitSuccess
                showSubmitSuccess={showSubmitSuccess}
                content={"You have saved defense jury"}
              />) : (<Alert severity="error">Failed</Alert>)}

            </div>) : (<></>)}
          </Grid>
        </form>
      </div>
    </div>

  );
}

export default EditThesis;