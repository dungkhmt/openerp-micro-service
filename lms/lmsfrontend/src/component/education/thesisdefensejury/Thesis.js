import {
  Button,
  Card,
  CardActions,
  CardContent,
  Checkbox,
  MenuItem,
  TextField,
  Tooltip,
  Typography,
} from "@material-ui/core/";
import {Box, FormControl, InputAdornment, InputLabel, ListSubheader, Select} from "@mui/material";
import React, {useEffect, useMemo, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../../api";
import MaterialTable, {MTableToolbar} from "material-table";
import {Grid} from "@material-ui/core";
import SearchIcon from "@mui/icons-material/Search";
import ModalLoading from "./ModalLoading"
import Delete from '@material-ui/icons/Delete';


function Thesis() {
  const history = useHistory();
  const [thesiss, setThesiss] = useState([]);
  const [selectThesiss, setSelectThesiss] = useState([]);
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);
  const [listPlan, setListPlan] = React.useState([]);
  const [searchText, setSearchText] = useState("");
  const [thesisPlanName, setThesisPlanName] = React.useState("");
  const [defenseJuryName, setDefenseJuryName] = React.useState("");
  const [listJury, setListJury] = React.useState([]);
  const [key, setKey] = React.useState("");
  const [toggle, setToggle] = useState(false)
  const [openLoading, setOpenLoading] = React.useState(false);
  const columns = [
    {title: "Tên luận văn", field: "name"},
    {title: "Mô tả", field: "thesis_abstract"},
    // {title:"Tên chương trình",field:"program_name"},
    {title: "Đợt bảo vệ", field: "thesisPlanName"},
    {title: "Tên HĐ", field: "defense_jury_name"},
    {title: "Người hướng dẫn", field: "supervisor_name"},
    // {title:"Keyword",field:"keyword"},
    {title: "Người tạo", field: "student_name"},
    {title: "Ngày tạo", field: "createdTime"},
  ];
  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;
  const handlerSearch = (event) => {
    event.preventDefault();
    setOpenLoading(true);
    let planRes = listPlan.filter(e => e.name == thesisPlanName)
    console.log(planRes)
    let juryRes = listJury.filter(e => e.name == defenseJuryName)
    console.log(juryRes)
    let planInput = ""
    let juryInput = ""
    if (planRes.length > 0) {
      planInput = planRes[0].id
    }
    if (juryRes.length > 0) {
      juryInput = juryRes[0].id
    }
    let body = {
      "key": key,
      "thesisPlanId": planInput,
      "juryId": juryInput
    }
    request(
      "post",
      "/thesis/_search",
      (res) => {

        console.log(res.data)
        setOpenLoading(false);
        if (res.data.ok) {
          setThesiss(res.data.result)
        } else {
          setThesiss([])
        }

      },
      {
        onError: (e) => {
        }
      },
      body
    ).then();

  }

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      "/thesis_defense_plan",
      (res) => {
        console.log("Plan", res.data)
        let objAll = {
          id: "",
          name: "All"
        }
        res.data.unshift(objAll)
        console.log("Plan", res.data)
        setListPlan(res.data)

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
        let objAll = {
          id: "",
          name: "All"
        }
        res.data.DefenseJurys.unshift(objAll)
        console.log("Plan", res.data.DefenseJurys)
        setListJury(res.data.DefenseJurys)

      }
    );
  }

  const displayedPlanOptions = useMemo(
    () => listPlan.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  const displayedJuryOptions = useMemo(
    () => listJury.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );


  async function getAllThesis() {
    setOpenLoading(true)
    request(
      // token,
      // history,
      "GET",
      "/thesis",
      (res) => {
        console.log(res.data.content)
        setOpenLoading(false)
        setThesiss(res.data.content);
      }
    );
  }


  const handlerCreate = () => {
    history.push({
      pathname: `/thesis/create`,
    });
  }

  async function DeleteThesisById(thesisID, userLoginID) {
    setOpenLoading(true)
    var body = {
      id: thesisID,
      userLogin: userLoginID
    }
    request(
      "post",
      `/thesis/delete`,
      (res) => {
        console.log(res.data)
        setOpenLoading(false)
        setToggle(!toggle)
        // setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
      },
      {
        onError: (e) => {
          // setShowSubmitSuccess(false);
          console.log(e)
        }
      },
      body
    ).then();
  }

  useEffect(() => {

    getAllJury();
    getAllPlan();
  }, [showSubmitSuccess]);
  useEffect(() => {
    getAllThesis();
  }, [toggle]);

  return (
    <Card>
      <Box>
        <Typography variant="h4" mb={4} component={'h4'}>
          Search
        </Typography>
        <Grid container spacing={3}>
          <Grid item={true} xs={3} spacing={2} p={2}>
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
          <Grid item={true} xs={3} spacing={2} p={2}>
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
          <Grid item={true} xs={3} spacing={2} p={2}>
            <Box sx={{minWidth: '100%'}}>
              <TextField
                label="Contains text"
                multiline
                fullWidth={true}
                value={key}
                onChange={(event) => {
                  setKey(event.target.value)
                }}
                name="search"
              />
            </Box>
          </Grid>
          <Button variant="contained" color="success" size="small" style={{
            height: "60%",
            margin: "2.5%",
            display: 'flex', justifyContent: 'end'
          }}
                  onClick={handlerSearch}
          >
            Search
          </Button>

          <Button onClick={handlerCreate} color="primary">
            Thêm mới
          </Button>
        </Grid>
      </Box>
      <MaterialTable
        title={"Danh sách đề tài"}
        columns={columns}
        options={
          {search: false}
        }
        data={thesiss}
        actions={[
          {
            icon: Delete,
            tooltip: "Delete Thesis",
            onClick: (event, rowData) => {
              console.log(rowData)
              console.log(rowData.id)
              DeleteThesisById(rowData.id, rowData.userLoginID)


            }
          }
        ]}
        components={{
          Toolbar: (props) => (
            <div style={{position: "relative"}}>
              <MTableToolbar {...props} />
              <div
                style={{position: "absolute", top: "16px", right: "350px"}}
              >

              </div>
            </div>
          ),
        }}
      />
      <ModalLoading openLoading={openLoading}/>
    </Card>


  );
}

export default Thesis;
      
  