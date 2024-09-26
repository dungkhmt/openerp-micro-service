import React, { useMemo, useState } from "react";
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
  Grid
} from "@mui/material";
import Checkbox from "@mui/material/Checkbox";
import ListItemText from "@mui/material/ListItemText";
import OutlinedInput from "@mui/material/OutlinedInput";
import { useForm } from "react-hook-form";
import { request } from "api";
import { boxChildComponent, boxComponentStyle } from "components/thesisdefensejury/constant";
import { useFetch } from "hooks/useFetch";
import { successNoti, errorNoti } from "utils/notification";
import { useKeycloak } from "@react-keycloak/web";
import ModalLoading from "components/common/ModalLoading";
import PrimaryButton from "components/button/PrimaryButton";
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
// Màn sinh viên thêm đồ án mới 
function StudentCreateThesis(props) {
  const { keycloak } = useKeycloak();
  const [searchProgramText, setSearchProgramText] = useState("");
  const [searchTeacherText, setSearchTeacherText] = useState("");
  const [searchThesisPlanText, setSearchThesisPlanText] = useState("");
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

  const handleFormSubmit = (data) => {
    let body = {
      thesisKeyword: keyword,
      ...data
    };
    request(
      "post",
      "/thesis/save",
      (res) => {
        successNoti('Thêm đồ án mới thành công', true)
      },
      {
        onError: (e) => {
          errorNoti('Thêm đồ án mới thất bại', true);
        },
      },
      body
    ).then();
  };
  const { data: listProgram } = useFetch('/thesis/training-program');
  const { loading, data: listTeacher } = useFetch('/defense-jury/teachers');
  const { data: listPlan } = useFetch('/thesis-defense-plan/get-all');
  const { data: keywords } = useFetch('/academic_keywords/get-all')
  const displayedProgramOptions = useMemo(
    () => listProgram?.filter((option) => containsText(option.name, searchProgramText)),
    [searchProgramText, listProgram]
  );

  const displayedTeacherOptions = useMemo(
    () =>
      searchTeacherText !== "" ? listTeacher?.filter((option) =>
        containsText(option.teacherName, searchTeacherText)
      ) : listTeacher,
    [searchTeacherText, listTeacher]
  );
  const displayedPlanOptions = useMemo(
    () => searchThesisPlanText !== "" ? listPlan?.filter((option) => containsText(option.name, searchThesisPlanText)) : listPlan,
    [searchThesisPlanText, listPlan]
  );

  return (
    <>
      <Box sx={boxComponentStyle}>
        {loading && <ModalLoading />}
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
              <PrimaryButton
                type="submit"
              >
                Thêm luận văn
              </PrimaryButton>
            </Box>

            {/* </Grid> */}
          </form>
        </div>
      </Box>
    </>
  );
}

export default StudentCreateThesis;
