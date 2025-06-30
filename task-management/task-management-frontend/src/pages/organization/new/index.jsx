import {
  Box,
  Button,
  Container,
  FormControl,
  FormHelperText,
  TextField,
  Typography,
} from "@mui/material";
import { useForm, Controller, useWatch } from "react-hook-form";
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { snakeCase } from "lodash";
import { clearErrors, createOrganization } from "../../../store/organization";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";
import { useTranslation } from "react-i18next";

const NewOrganization = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const {
    control,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm({
    defaultValues: {
      name: "",
      code: "",
    },
  });

  const name = useWatch({ control, name: "name" });
  const { fetchLoading, createErrors: serverErrors } = useSelector(
    (state) => state.organization
  );

  useEffect(() => {
    if (name) {
      const generatedCode = snakeCase(name).substring(0, 100);
      setValue("code", generatedCode);
    } else {
      setValue("code", "");
    }
  }, [name, setValue]);

  const onSubmitForm = async (data) => {
    try {
      const trimmedData = {
        name: data.name.trim(),
        code: data.code.trim(),
      };
       await dispatch(createOrganization(trimmedData)).unwrap();
      navigate(`/dashboard`);
      toast.success("Tạo tổ chức mới thành công!");
    } catch (error) {
      toast.error("Lỗi khi tạo tổ chức!");
      console.error(error);
    }
  };

  useEffect(() => {
    return () => {
      dispatch(clearErrors());
    };
  }, [dispatch]);

  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 4 }}>
        <Typography variant="h5" gutterBottom>
          Tạo Tổ Chức Mới
        </Typography>

        <form onSubmit={handleSubmit(onSubmitForm)} noValidate>
          <FormControl fullWidth sx={{ mt: 5 }}>
            <Box sx={{ display: "flex", alignItems: "baseline", mb: 1 }}>
              <Typography variant="body1">Tên tổ chức</Typography>
              <Typography sx={{ color: "error.main", ml: 0.5 }}>*</Typography>
            </Box>
            <Controller
              name="name"
              control={control}
              rules={{ required: "Tên tổ chức không được để trống" }}
              render={({ value, onChange }) => (
                <TextField
                  value={value}
                  onChange={(e) => {
                    const newName = e.target.value;
                    onChange(newName);
                    const generatedCode = snakeCase(newName).substring(0, 100);
                    setValue("code", generatedCode);
                  }}
                  variant="outlined"
                  fullWidth
                  error={Boolean(errors.name)}
                  helperText={errors.name?.message}
                  spellCheck={false}
                  placeholder="Nhập tên tổ chức"
                />
              )}
            />
          </FormControl>

          <FormControl fullWidth sx={{ mt: 3 }}>
            <Box sx={{ display: "flex", alignItems: "baseline", mb: 1 }}>
              <Typography variant="body1">Mã tổ chức</Typography>
              <Typography sx={{ color: "error.main", ml: 0.5 }}>*</Typography>
            </Box>
            <Controller
              name="code"
              control={control}
              rules={{ required: "Mã tổ chức không được để trống" }}
              render={({ value, onChange }) => (
                <TextField
                  value={value}
                  onChange={(e) => onChange(e.target.value)}
                  variant="outlined"
                  fullWidth
                  inputProps={{ maxLength: 100 }}
                  error={Boolean(errors.code)}
                  helperText={errors.code?.message}
                  spellCheck={false}
                  placeholder="Nhập mã tổ chức"
                />
              )}
            />
            {serverErrors.find((e) => e?.data?.message) && (
              <FormHelperText error>
                {t(serverErrors.find((e) => e?.data?.message)?.data?.message)}
              </FormHelperText>
            )}
          </FormControl>

          <Box sx={{ mt: 4, display: "flex", justifyContent: "flex-end" }}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              disabled={fetchLoading}
              sx={{ textTransform: "none" }}
            >
              {fetchLoading ? "Đang tạo..." : "Tạo tổ chức"}
            </Button>
          </Box>
        </form>
      </Box>
    </Container>
  );
};

export default NewOrganization;
