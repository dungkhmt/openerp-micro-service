import {
  Box,
  Button,
  Card,
  FormControl,
  FormHelperText,
  TextField,
  Typography,
} from "@mui/material";
import { Controller, useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import { Helmet } from "react-helmet";
import { Link } from "react-router-dom";
import { ProjectService } from "../../../services/api/project.service";

const defaultValues = {
  name: "",
  code: "",
};

const NewProject = () => {
  const navigate = useNavigate();

  const { handleSubmit, errors, control } = useForm({
    defaultValues,
    mode: "onChange",
  });

  const onSubmit = async (data) => {
    try {
      await ProjectService.createProject(data);
      toast.success("Thêm mới dự án thành công!");
      navigate("/projects");
    } catch (error) {
      // TODO: handle error
      toast.error("Thêm mới dự án thất bại!");
    }
  };

  return (
    <>
      <Helmet>
        <title>Thêm mới dự án | Task management</title>
      </Helmet>

      <Box sx={{ px: 6 }}>
        <Box sx={{ mb: 3 }}>
          <Typography variant="h3" mb={4} component={"h3"}>
            Thêm dự án mới
          </Typography>
        </Box>
        <Card sx={{ p: 8 }}>
          <Box
            sx={{ display: "flex", flexDirection: "column", gap: 8 }}
            component="form"
            onSubmit={handleSubmit(onSubmit)}
          >
            <FormControl mb={3}>
              <Typography variant="body1">
                Tên dự án <span style={{ color: "#FF4C51" }}>*</span>
              </Typography>
              <Controller
                name="name"
                control={control}
                rules={{ required: true }}
                render={({ value, onChange }) => (
                  <TextField
                    variant="standard"
                    value={value}
                    onChange={onChange}
                    placeholder="Điền tên dự án ..."
                    error={Boolean(errors.name)}
                    aria-describedby="validation-name"
                  />
                )}
              />
              {errors.name && (
                <FormHelperText
                  sx={{ color: "error.main" }}
                  id="validation-name"
                >
                  Tên dự án không được để trống
                </FormHelperText>
              )}
            </FormControl>
            <FormControl mb={3}>
              <Typography variant="body1">
                Mã dự án <span style={{ color: "#FF4C51" }}>*</span>
              </Typography>
              <Controller
                name="code"
                control={control}
                rules={{ required: true }}
                defaultValue=""
                render={({ value, onChange }) => (
                  <TextField
                    variant="standard"
                    value={value}
                    onChange={onChange}
                    placeholder="Điền mã dự án ..."
                    error={Boolean(errors.code)}
                    aria-describedby="validation-code"
                  />
                )}
              />
              {errors.code && (
                <FormHelperText
                  sx={{ color: "error.main" }}
                  id="validation-code"
                >
                  Mã dự án không được để trống
                </FormHelperText>
              )}
            </FormControl>
            <Box
              mb={2}
              sx={{
                backgroundColor: (theme) => theme.palette.action.focus,
                display: "flex",
                alignItems: "center",
              }}
            >
              <Typography paragraph={true} px={2}>
                Mã dự án là một chuỗi kí tự được chỉ định cho dự án đó, vì vậy
                nó nên là duy nhất!
              </Typography>
            </Box>
            <Box>
              <Button
                variant="contained"
                color="primary"
                sx={{ mr: 2 }}
                type="submit"
              >
                Thêm mới
              </Button>
              <Button variant="contained" color="error">
                <Link
                  to={"/projects"}
                  style={{ textDecoration: "none", color: "#fff" }}
                >
                  Hủy
                </Link>
              </Button>
            </Box>
          </Box>
        </Card>
      </Box>
    </>
  );
};

export default NewProject;
