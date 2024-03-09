import { useEffect, useState } from "react";

import UploadFileIcon from "@mui/icons-material/UploadFile";
import {
  Autocomplete,
  Box,
  Button,
  Grid,
  MenuItem,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { request } from "../../api";
import { errorNoti, processingNoti } from "../../utils/notification";
import { boxChildComponent, boxComponentStyle } from "../utils/constant";

import { Controller, useForm } from "react-hook-form";
import { useNavigate, useParams } from "react-router";
import { Link } from "react-router-dom";
import DateTimePickerBasic from "../datetimepicker/DateTimePickerBasic";
import { CustomEditor } from "../editor/CustomEditor";

export default function CreateTask() {
  const navigate = useNavigate();
  const { projectIdUrl } = useParams();
  const [categories, setCategories] = useState([]);
  const [priorities, setPriorities] = useState([]);
  const [persons, setPersons] = useState([]);
  const [projects, setProjects] = useState([]);
  const [projectName, setProjectName] = useState("");

  const { register, errors, handleSubmit, control, setValue } = useForm();

  const [categoryId, setCategoryId] = useState("TASK");
  const [priorityId, setPriorityId] = useState("HIGH");
  const [person, setPerson] = useState("");
  const [projectId, setProjectId] = useState("");
  const [dueDate, setDueDate] = useState(new Date());

  const [selectedFile, setSelectedFile] = useState(null);

  const [skills, setSkills] = useState([]);
  const [selectedSkills, setSelectedSkills] = useState([]);
  const [suggestAssign, setSuggestAssign] = useState([]);

  useEffect(() => {
    request(
      "get",
      "/task-categories",
      (res) => {
        setCategories(res.data);
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      "/task-priorities",
      (res) => {
        setPriorities(res.data);
      },
      (err) => {
        console.log(err);
      }
    );

    request(
      "get",
      "/skills",
      (res) => {
        setSkills(res.data);
      },
      (err) => {
        console.log(err);
      }
    );

    if (projectIdUrl) {
      request(
        "get",
        `/projects/${projectIdUrl}`,
        (res) => {
          setProjectName(res.data.name);
        },
        (err) => {
          console.log(err);
        }
      );

      request(
        "get",
        `/projects/${projectIdUrl}/members`,
        (res) => {
          setPersons(res.data);
          setPerson(res.data[0]);
        },
        (err) => {
          console.log(err);
        }
      );
    } else {
      request(
        "get",
        "/task-persons",
        (res) => {
          setPersons(res.data);
          setPerson(res.data[0]);
        },
        (err) => {
          console.log(err);
        }
      );

      request(
        "get",
        "/projects",
        (res) => {
          setProjects(res.data);
        },
        (err) => {
          console.log(err);
        }
      );
    }
  }, []);

  const onHandleData = (data, fileId = "") => {
    let projectIdForm = projectIdUrl ? projectIdUrl : projectId;
    const dataForm = {
      ...data,
      assigneeId: person.id,
      categoryId,
      dueDate,
      priorityId,
      attachmentPaths:
        selectedFile == null
          ? "Không có tệp đính kèm"
          : `${selectedFile.name},${fileId}`,
      statusId: "TASK_OPEN",
      projectId: projectIdForm,
      skillIds: selectedSkills.map((item) => item.skillId),
    };
    processingNoti(
      () =>
        request(
          "post",
          `/projects/${projectIdForm}/tasks`,
          () => {},
          () => {},
          dataForm
        ),
      {
        loading: "Đang thêm mới...",
        success: "Thêm mới thành công",
        error: "Thêm mới thất bại",
      }
    ).then((res) => {
      setTimeout(() => {
        navigate(`/tasks/${res.data.id}`);
      }, 2000);
    });
  };

  const uploadFile = (file) => {
    const body = {
      id: `file_${new Date().getTime().toString()}`,
    };
    const formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", file);

    return processingNoti(
      () =>
        request("post", "/content/create", () => {}, {}, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }),
      {
        loading: "Đang tải lên file",
        success: "Tải lên file thành công",
        error: "Tải lên file thất bại",
      }
    );
  };

  const onSubmit = (data) => {
    if (selectedFile != null) {
      uploadFile(selectedFile).then((res) => {
        onHandleData(data, res.data.id);
      });
    } else {
      onHandleData(data);
    }
  };

  const handleSuggest = () => {
    const projectIdTmp = projectIdUrl ? projectIdUrl : projectId;
    if (projectIdTmp == "") {
      errorNoti("Cần chọn dự án trước!", true);
    }
    const dataForm = {
      projectId: projectIdTmp,
      skillIds: selectedSkills.map((item) => item.skillId),
    };

    request(
      "post",
      "/suggest-assign-task",
      (res) => {
        setSuggestAssign(res.data);
      },
      (err) => {
        console.log(err);
      },
      dataForm
    );
  };

  return (
    <Box sx={boxComponentStyle}>
      <Box mb={4}>
        <Typography variant="h4" component={"h4"}>
          Thêm nhiệm vụ mới
        </Typography>
        {projectIdUrl && (
          <Link
            to={`/project/${projectIdUrl}/tasks`}
            style={{ textDecoration: "none" }}
          >
            <Typography variant="caption" mb={3} color="primary">
              Dự án: {projectName}
            </Typography>
          </Link>
        )}
      </Box>
      <form>
        <Grid container mb={3}>
          <Grid item={true} xs={3}>
            <TextField
              select
              fullWidth
              label={"Danh mục"}
              defaultValue="TASK"
              value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)}
              required
            >
              {categories.map((item) => (
                <MenuItem key={item.categoryId} value={item.categoryId}>
                  {item.categoryName}
                </MenuItem>
              ))}
            </TextField>
          </Grid>
        </Grid>
        <Box mb={3}>
          <TextField
            fullWidth={true}
            label="Tên nhiệm vụ"
            variant="outlined"
            name="name"
            inputRef={register({ required: "Thiếu tên nhiệm vụ!" })}
            error={!!errors.name}
            helperText={errors.name?.message}
          />
        </Box>
        <Box sx={boxChildComponent} mb={3}>
          <label htmlFor="description">Mô tả</label>
          <Controller
            name="description"
            control={control}
            defaultValue=""
            render={(field) => (
              <CustomEditor
                {...field}
                setValue={(value) => setValue("description", value)}
              />
            )}
          />
          <Grid container spacing={2} p={4}>
            <Grid item={true} xs={6} p={2}>
              <Grid container>
                <Grid item={true} xs={4}>
                  <Typography variant="body1">Trạng thái</Typography>
                </Grid>
                <Grid item={true} xs={8}>
                  <Typography variant="body1" color={"InfoText"}>
                    Open
                  </Typography>
                </Grid>
              </Grid>
            </Grid>
            <Grid item={true} xs={6} p={2}>
              <Grid container>
                <Grid item={true} xs={4}>
                  <Typography variant="body1">Mức ưu tiên</Typography>
                </Grid>
                <Grid item={true} xs={8}>
                  <TextField
                    select
                    fullWidth
                    label={"Độ ưu tiên"}
                    defaultValue="HIGH"
                    value={priorityId}
                    onChange={(e) => setPriorityId(e.target.value)}
                    required
                  >
                    {priorities.map((item) => (
                      <MenuItem key={item.priorityId} value={item.priorityId}>
                        {item.priorityName}
                      </MenuItem>
                    ))}
                  </TextField>
                </Grid>
              </Grid>
            </Grid>
            <Grid item={true} xs={6} p={2}>
              <Grid container>
                <Grid item={true} xs={4}>
                  <Typography variant="body1">Gán cho</Typography>
                </Grid>
                <Grid item={true} xs={8}>
                  <Autocomplete
                    disablePortal
                    options={persons}
                    value={person}
                    onChange={(e, value) => setPerson(value)}
                    getOptionLabel={(option) => {
                      return `${option.id} (${option.firstName} ${option.lastName})`;
                    }}
                    fullWidth
                    renderInput={(params) => (
                      <TextField {...params} label="Danh mục" placeholder="" />
                    )}
                  />
                </Grid>
              </Grid>
            </Grid>
            <Grid item={true} xs={6} p={2}>
              <Grid container>
                <Grid item={true} xs={4}>
                  <Typography variant="body1">Thời hạn</Typography>
                </Grid>
                <Grid item={true} xs={8}>
                  <DateTimePickerBasic
                    label={"Chọn thời hạn"}
                    onChange={(date) => {
                      setDueDate(date);
                    }}
                    value={dueDate}
                  />
                </Grid>
              </Grid>
            </Grid>
            {!projectIdUrl && (
              <Grid item={true} xs={6} p={2}>
                <Grid container>
                  <Grid item={true} xs={4}>
                    <Typography variant="body1">Dự án</Typography>
                  </Grid>
                  <Grid item={true} xs={8}>
                    <TextField
                      select
                      fullWidth
                      label={"Danh sách dự án"}
                      defaultValue=""
                      value={projectId}
                      onChange={(e) => setProjectId(e.target.value)}
                      required
                    >
                      {projects.map((item) => (
                        <MenuItem key={item.id} value={item.id}>
                          {item.name}
                        </MenuItem>
                      ))}
                    </TextField>
                  </Grid>
                </Grid>
              </Grid>
            )}
            <Grid item={true} xs={12} p={2}>
              <Grid container spacing={2}>
                <Grid item={true} xs={12}>
                  <Typography variant="body1">Yêu cầu các kỹ năng</Typography>
                </Grid>
                <Grid item={true} xs={10}>
                  <Stack spacing={3}>
                    <Autocomplete
                      multiple
                      id="required-skills"
                      options={skills}
                      getOptionLabel={(option) => option.name}
                      defaultValue={[]}
                      onChange={(e, value) => setSelectedSkills(value)}
                      filterSelectedOptions
                      fullWidth={true}
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          label="Danh sách các kĩ năng"
                          placeholder=""
                        />
                      )}
                    />
                  </Stack>
                </Grid>
                <Grid item={true} xs={12}>
                  <Box display={"flex"} alignItems={"center"}>
                    <Button variant="contained" onClick={handleSuggest}>
                      Gợi ý gán
                    </Button>
                  </Box>
                </Grid>
                <Grid item={true} xs={12}>
                  {suggestAssign.map((item, index) => (
                    <Box key={index}>
                      {item.fullName} ({item.userLoginId})
                    </Box>
                  ))}
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </Box>
        <Box sx={boxChildComponent} mb={3}>
          <Grid container>
            <Grid item={true} xs={2}>
              <Typography variant="body1">Tài liệu đính kèm</Typography>
            </Grid>
            <Grid
              item={true}
              xs={10}
              sx={{ display: "flex", alignItem: "center" }}
            >
              <Button
                component="label"
                variant="contained"
                startIcon={<UploadFileIcon />}
                sx={{ marginRight: "1rem" }}
                color="primary"
              >
                Upload file
                <input
                  type="file"
                  hidden
                  onChange={(e) => setSelectedFile(e.target.files[0])}
                />
              </Button>
              {selectedFile && (
                <Typography variant="inherit" color={"primary"}>
                  {selectedFile.name}
                </Typography>
              )}
            </Grid>
          </Grid>
        </Box>
        <Box sx={{ display: "flex", justifyContent: "end" }}>
          <Button
            variant="contained"
            color="primary"
            onClick={handleSubmit(onSubmit)}
            sx={{ marginRight: 2 }}
            // disable when form is invalid
            disabled={Object.keys(errors).length > 0}
          >
            Thêm nhiệm vụ
          </Button>
          <Button
            variant="contained"
            color="success"
            onClick={() => navigate(-1)}
          >
            Hủy
          </Button>
        </Box>
      </form>
    </Box>
  );
}
