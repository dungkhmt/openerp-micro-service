import DateFnsUtils from "@date-io/date-fns";
import { Button, Grid, TextField } from "@material-ui/core";
import { Alert } from "@material-ui/lab";
import SearchIcon from "@mui/icons-material/Search";
import { boxChildComponent, boxComponentStyle } from "./constant";
import {
  FormControl,
  InputAdornment,
  InputLabel,
  ListSubheader,
  MenuItem,
  Select,
  Modal,
  Backdrop,
  Fade,
  Box,
  Typography,
} from "@mui/material";
import React, { useEffect, useMemo, useState } from "react";
import { request } from "../../../api";
import { SubmitSuccess } from "../programmingcontestFE/SubmitSuccess";

const modalStyle = {
  paper: {
    boxSizing: "border-box",
    position: "absolute",
    width: 600,
    maxHeight: 600,
    // border: '2px solid #000',
    borderRadius: "5px",
    boxShadow:
      "0px 3px 1px -2px rgb(0 0 0 / 20%), 0px 2px 2px 0px rgb(0 0 0 / 14%), 0px 1px 5px 0px rgb(0 0 0 / 12%)",
    backgroundColor: "white",
    zIndex: 999,
    left: "50%",
    top: "50%",
    transform: "translate(-50% , -50%)",
    padding: "20px 40px",
  },
};

function CreateDefenseJury({ open, handleClose, handleToggle }) {
  const [name, setName] = React.useState("");
  const [thesisPlanName, setThesisPlanName] = React.useState("");
  const [listProgram, setListProgram] = React.useState([]);
  const [listPlan, setListPlan] = React.useState([]);
  const [nbr, setNbr] = React.useState("");
  const [startDate, setStartDate] = React.useState(new Date());
  const [openAlert, setOpenAlert] = React.useState(false);
  const [searchText, setSearchText] = useState("");
  const [showSubmitSuccess, setShowSubmitSuccess] = React.useState(false);
  const handleFormSubmit = (event) => {
    event.preventDefault();
    let body = {
      name: name,
      // program_name: program,
      // userLoginID: userId,
      maxThesis: nbr,
      thesisPlanName: thesisPlanName,
      defenseDate: startDate,
    };
    setTimeout(() => setOpenAlert(true), 3000);
    request(
      "post",
      "/defense_jury",
      (res) => {
        console.log(res.data);
        setShowSubmitSuccess(true);
        //   history.push(`/thesis/defense_jury/${res.data.id}`);
      },
      {
        onError: (e) => {
          setShowSubmitSuccess(false);
        },
      },
      body
    ).then();
  };

  async function getAllProgram() {
    request(
      // token,
      // history,
      "GET",
      "/program_tranings",
      (res) => {
        console.log(res.data);
        setListProgram(res.data);
      }
    );
  }

  async function getAllPlan() {
    request(
      // token,
      // history,
      "GET",
      "/thesis-defense-plan/get-all",
      (res) => {
        console.log("Plan", res.data);
        setListPlan(res.data);
      }
    );
  }

  useEffect(() => {
    // getAllProgram();
    getAllPlan();
  }, []);
  const containsText = (text, searchText) =>
    text.toLowerCase().indexOf(searchText.toLowerCase()) > -1;
  useMemo(
    () => listProgram.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  const displayedPlanOptions = useMemo(
    () => listPlan.filter((option) => containsText(option.name, searchText)),
    [searchText]
  );
  return (
    <div style={modalStyle.paper}>
      <Modal open={open} onClose={handleClose} closeAfterTransition>
        <Fade in={open}>
          <Box sx={boxComponentStyle}>
            <Typography variant="h2" mb={4} component={"h2"}>
              Thêm mới Hội Đồng
            </Typography>
          </Box>
          <div>
            <form>
              <Box mb={3}>
                <TextField
                  value={name}
                  onChange={(event) => {
                    setName(event.target.value);
                  }}
                  fullWidth={true}
                  id="input-with-icon-grid"
                  label="Tên hội đồng"
                />
                <TextField
                  fullWidth={true}
                  label="Ngày tổ chức"
                  variant="outlined"
                  name="name"
                  value={name}
                  onChange={(event) => {
                    setName(event.target.value);
                  }}
                  // onBlur={(e) => handleInputValidationThesis(e)}
                  // {...register("name", { required: "Thiếu tên luận văn!" })}
                  // error={!!errors?.name}
                  // helperText={errors?.name?.message}
                />
              </Box>

              <Grid container spacing={1} alignItems="flex-end">
                <Grid item xs={9}>
                  <TextField
                    value={name}
                    onChange={(event) => {
                      setName(event.target.value);
                    }}
                    fullWidth={true}
                    id="input-with-icon-grid"
                    label="Tên hội đồng"
                  />
                </Grid>
                <Grid item xs={9}>
                  <FormControl fullWidth style={{ margin: "2% 0px" }}>
                    <InputLabel id="search-select-label">
                      Tên đợt bảo vệ
                    </InputLabel>
                    <Select
                      MenuProps={{ autoFocus: false }}
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
                                <SearchIcon />
                              </InputAdornment>
                            ),
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
                </Grid>
                <Grid item xs={9}>
                  <TextField
                    onChange={(event) => {
                      setNbr(parseInt(event.target.value, 10));
                    }}
                    fullWidth={true}
                    value={nbr}
                    type="number"
                    id="input-with-icon-grid"
                    label="Số lượng tối đa Đồ Án"
                  />
                </Grid>
                {/* <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <Grid item xs={9}>
                <KeyboardDatePicker
                  format="dd/MM/yyyy"
                  margin="normal"
                  label="Chọn ngày bảo vệ"
                  value={startDate}
                  onChange={(event) => {
                    setStartDate(event);
                  }}
                  KeyboardButtonProps={{
                    "aria-label": "change date",
                  }}
                />
              </Grid>
            </MuiPickersUtilsProvider> */}
                <Grid item xs={2}>
                  <Button
                    color="primary"
                    type="submit"
                    onClick={handleFormSubmit}
                    width="100%"
                  >
                    Tạo mới
                  </Button>
                </Grid>
                {openAlert === true ? (
                  <div>
                    {showSubmitSuccess === true ? (
                      <SubmitSuccess
                        showSubmitSuccess={showSubmitSuccess}
                        content={"You have saved defense jury"}
                      />
                    ) : (
                      <Alert severity="error">Failed</Alert>
                    )}
                  </div>
                ) : (
                  <></>
                )}
              </Grid>
            </form>
          </div>
        </Fade>
      </Modal>
    </div>
  );
}

export default CreateDefenseJury;
