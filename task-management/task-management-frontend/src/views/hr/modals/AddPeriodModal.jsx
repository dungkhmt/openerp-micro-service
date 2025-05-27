import React, {useEffect, useState, useCallback} from "react";
import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  TextField,
  Typography,
  Stack,
  Paper,
  Tooltip,
  Divider
} from "@mui/material";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import CloseIcon from "@mui/icons-material/Close";
import DeleteOutlineIcon from '@mui/icons-material/DeleteOutline';
import Autocomplete from "@mui/material/Autocomplete";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import {request}from "@/api";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import { useTheme } from '@mui/material/styles';

const AddPeriodModal = ({ open, onClose, onSubmit, initialValues, titleProps }) => {
  const theme = useTheme();
  const defaultFormValues = {
    name: "",
    description: "",
    checkpoint_date: null,
    configures: [],
  };
  const [formValues, setFormValues] = useState(defaultFormValues);
  const [availableConfigures, setAvailableConfigures] = useState([]);
  const [loadingConfigures, setLoadingConfigures] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [loadingInitialPeriod, setLoadingInitialPeriod] = useState(false);

  // Effect để fetch danh sách các configures có sẵn khi modal mở
  useEffect(() => {
    if (open) {
      setLoadingConfigures(true);
      const payload = { status: "ACTIVE", page: 0, pageSize: 1000 };
      request(
        "get",
        "/checkpoints/configures",
        (res) => {
          setAvailableConfigures(res.data.data || []);
        },
        { onError: (err) => {
            console.error("Error fetching available configures:", err);
            toast.error("Không thể tải danh sách tiêu chí.");
            setAvailableConfigures([]);
          }
        },
        null,
        {params: payload}
      ).finally(() => {
        setLoadingConfigures(false);
      });
    }
  }, [open]); // Chỉ chạy khi 'open' thay đổi

  // Effect để thiết lập form cho thêm mới hoặc tải chi tiết cho chỉnh sửa
  useEffect(() => {
    if (open) {
      if (initialValues?.id && availableConfigures.length > 0) { // Chỉ fetch detail khi có ID và availableConfigures
        setLoadingInitialPeriod(true);
        request(
          "get",
          `/checkpoints/periods/${initialValues.id}`,
          (res) => {
            const { data } = res.data;
            if (!data) {
              toast.error("Không tìm thấy chi tiết kỳ checkpoint.");
              setFormValues(defaultFormValues);
              return;
            }
            setFormValues({
              name: data.name || "",
              description: data.description || "",
              checkpoint_date: data.checkpoint_date ? dayjs(data.checkpoint_date) : null,
              configures: (data.configures || []).map((item) => {
                const foundConfig = availableConfigures.find(ac => ac.code === (item.configure?.code || item.configure_id));
                return {
                  configure_id: item.configure?.code || item.configure_id || "",
                  coefficient: item.coefficient !== null && item.coefficient !== undefined ? String(item.coefficient) : "",
                  description: foundConfig?.description || item.configure?.description || "",
                };
              }),
            });
          },
          {
            onError: (err) => {
              console.error("Error fetching period details:", err);
              toast.error("Lỗi khi tải chi tiết kỳ checkpoint.");
              setFormValues(defaultFormValues);
            }
          }
        ).finally(() => {
          setLoadingInitialPeriod(false);
        });
      } else if (!initialValues?.id) {
        // Reset form cho trường hợp thêm mới
        setFormValues(defaultFormValues);
        setLoadingInitialPeriod(false); // Không có gì để load
      }
    } else {
      // Reset form khi modal đóng hoàn toàn (nếu cần, hoặc để useEffect trên tự xử lý khi open lại)
      // setFormValues(defaultFormValues);
    }
  }, [open, initialValues?.id, availableConfigures]); // Re-run khi initialValues hoặc availableConfigures thay đổi


  const handleAddConfigure = () => {
    setFormValues((prev) => ({
      ...prev,
      configures: [
        ...prev.configures,
        { configure_id: "", coefficient: "0.1", description: "" },
      ],
    }));
  };

  const handleConfigureChange = (index, selectedOption) => {
    setFormValues((prev) => {
      const updatedConfigures = [...prev.configures];
      updatedConfigures[index] = {
        ...updatedConfigures[index],
        configure_id: selectedOption?.code || "",
        description: selectedOption?.description || "",
      };
      return { ...prev, configures: updatedConfigures };
    });
  };

  const handleCoefficientChange = (index, value) => {
    const newCoeff = value.replace(/[^0-9.]/g, '').replace(/(\..*)\./g, '$1');
    setFormValues((prev) => {
      const updatedConfigures = [...prev.configures];
      updatedConfigures[index].coefficient = newCoeff;
      return { ...prev, configures: updatedConfigures };
    });
  };

  const handleRemoveConfigure = (index) => {
    setFormValues((prev) => {
      const updatedConfigures = prev.configures.filter((_, i) => i !== index);
      return { ...prev, configures: updatedConfigures };
    });
  };

  const handleSubmit = async () => {
    if (!formValues.name.trim()) { toast.error("Tên kỳ không được để trống."); return; }
    if (!formValues.checkpoint_date) { toast.error("Ngày checkpoint không được để trống."); return; }
    if (!dayjs(formValues.checkpoint_date).isValid()){ toast.error("Ngày checkpoint không hợp lệ."); return; }

    for (const config of formValues.configures) {
      if (!config.configure_id) { toast.error("Vui lòng chọn tiêu chí cho tất cả các dòng."); return; }
      if (config.coefficient === "" || isNaN(parseFloat(config.coefficient)) || parseFloat(config.coefficient) <= 0) {
        const selectedConfig = availableConfigures.find(c=>c.code === config.configure_id);
        toast.error(`Hệ số cho tiêu chí "${selectedConfig?.name || config.configure_id}" phải là số dương.`); return;
      }
    }

    setLoadingSubmit(true);
    const payload = {
      name: formValues.name.trim(),
      description: formValues.description.trim(),
      checkpoint_date: dayjs(formValues.checkpoint_date).format("YYYY-MM-DD"),
      status: "ACTIVE",
      configures: formValues.configures.map((config) => ({
        configure_id: config.configure_id,
        coefficient: parseFloat(config.coefficient),
      })),
      ...(initialValues?.id && {id: initialValues.id})
    };

    try {
      const endpoint = initialValues?.id
        ? `/checkpoints/periods/${initialValues.id}`
        : "/checkpoints/periods";
      const methodURL = initialValues?.id ? "put" : "post";

      await request(
        methodURL,
        endpoint,
        () => {
          toast.success(initialValues?.id ? "Cập nhật kỳ thành công!" : "Thêm kỳ thành công!");
          onSubmit();
          onClose();
        },
        {
          onError: (err) => {
            console.error("API Error:", err.response?.data || err.message);
            toast.error(err.response?.data?.message || "Thao tác thất bại.");
          }
        },
        payload
      );
    } catch (error) {
      console.error("Error saving period:", error);
      toast.error("Lỗi khi lưu kỳ checkpoint.");
    } finally {
      setLoadingSubmit(false);
    }
  };

  const isLoadingPage = loadingConfigures || (initialValues?.id && loadingInitialPeriod);

  return (
    <Dialog open={open} onClose={loadingSubmit ? () => {} : onClose} fullWidth maxWidth="md" PaperProps={{sx: {height: 'calc(100% - 64px)'}}}>
      <DialogTitle sx={{...titleProps, display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: 'primary.main', color: 'primary.contrastText', py:1.5, px:2}}>
        {initialValues?.id ? "Chỉnh sửa Kỳ Checkpoint" : "Thêm Kỳ Checkpoint Mới"}
        <IconButton aria-label="đóng" onClick={loadingSubmit ? () => {} : onClose} sx={{color: 'primary.contrastText', p:0.5}} disabled={loadingSubmit}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{p:0, display:'flex', flexDirection:'column'}}>
        { isLoadingPage ? (
          <Box sx={{display:'flex', justifyContent:'center', alignItems:'center', flexGrow: 1, p:3}}>
            <CircularProgress /> <Typography sx={{ml:2}}>Đang tải dữ liệu...</Typography>
          </Box>
        ) : (
          <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="vi">
            <>
              <Box sx={{p:2.5}}>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={8}>
                    <TextField
                      autoFocus
                      fullWidth
                      label="Tên kỳ checkpoint (*)"
                      value={formValues.name}
                      onChange={(e) => setFormValues((prev) => ({ ...prev, name: e.target.value }))}
                      size="small"
                      disabled={loadingSubmit}
                    />
                  </Grid>
                  <Grid item xs={12} sm={4}>
                    <DatePicker
                      label="Ngày checkpoint (*)"
                      value={formValues.checkpoint_date ? dayjs(formValues.checkpoint_date) : null}
                      onChange={(newValue) => {
                        setFormValues((prev) => ({ ...prev, checkpoint_date: newValue ? newValue.format("YYYY-MM-DD") : null }));
                      }}
                      slotProps={{ textField: { size: 'small', fullWidth: true, InputLabelProps: { shrink: true } } }}
                      format="DD/MM/YYYY"
                      disabled={loadingSubmit}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Mô tả"
                      value={formValues.description}
                      onChange={(e) => setFormValues((prev) => ({ ...prev, description: e.target.value }))}
                      multiline
                      rows={2}
                      size="small"
                      disabled={loadingSubmit}
                    />
                  </Grid>
                </Grid>
              </Box>

              <Divider sx={{my:0}}/>

              <Box sx={{p:2.5, flexGrow:1, overflowY:'auto',
                '&::-webkit-scrollbar': { width: '6px' },
                '&::-webkit-scrollbar-track': { background: theme.palette.grey[100] },
                '&::-webkit-scrollbar-thumb': { background: theme.palette.grey[300] },
              }}>
                <Box sx={{display:'flex', justifyContent:'space-between', alignItems:'center', mb: 1.5}}>
                  <Typography variant="subtitle1" sx={{ fontWeight: '600' }}>
                    Các Tiêu chí Đánh giá
                  </Typography>
                  <Button
                    onClick={handleAddConfigure}
                    startIcon={<AddCircleOutlineIcon />}
                    variant="outlined"
                    size="small"
                    color="primary"
                    disabled={loadingSubmit || loadingConfigures}
                  >
                    Thêm tiêu chí
                  </Button>
                </Box>

                {formValues.configures.length === 0 && (
                  <Typography sx={{textAlign:'center', color:'text.secondary', fontStyle:'italic', my:2}}>
                    Chưa có tiêu chí nào được thêm.
                  </Typography>
                )}

                <Stack spacing={2}>
                  {formValues.configures.map((config, index) => (
                    <Paper key={index} variant="outlined" sx={{ p: 1.5, borderColor: 'rgba(0,0,0,0.12)' }}>
                      <Grid container alignItems="center" spacing={1.5}>
                        <Grid item xs={12} sm={5} md={5}>
                          <Autocomplete
                            fullWidth
                            options={availableConfigures}
                            getOptionLabel={(option) => `${option.name} (${option.code})` || ""}
                            value={ availableConfigures.find((c) => c.code === config.configure_id) || null }
                            isOptionEqualToValue={(option, value) => option.code === value?.code}
                            onChange={(e, value) => handleConfigureChange(index, value)}
                            loading={loadingConfigures && availableConfigures.length === 0}
                            disabled={loadingSubmit}
                            renderInput={(params) => (
                              <TextField
                                {...params}
                                label={`Tiêu chí ${index + 1} (*)`}
                                size="small"
                                InputProps={{
                                  ...params.InputProps,
                                  endAdornment: (
                                    <>
                                      {(loadingConfigures && availableConfigures.length === 0 && index === formValues.configures.length -1) ? <CircularProgress color="inherit" size={20} /> : null}
                                      {params.InputProps.endAdornment}
                                    </>
                                  ),
                                }}
                              />
                            )}
                            noOptionsText="Không tìm thấy"
                            size="small"
                          />
                        </Grid>
                        <Grid item xs={12} sm={5} md={3}>
                          <TextField
                            fullWidth
                            label="Hệ số (*)"
                            type="text"
                            size="small"
                            value={config.coefficient}
                            onChange={(e) => handleCoefficientChange(index, e.target.value)}
                            inputProps={{ inputMode: 'decimal' }}
                            placeholder="VD: 0.5"
                            disabled={loadingSubmit}
                          />
                        </Grid>
                        <Grid item xs={10} sm={10} md={3}>
                          <Tooltip title={config.description || "Chọn tiêu chí để xem mô tả"} placement="top">
                            <Typography variant="h7" sx={{display:'block', overflow:'hidden', wordBreak: 'break-word', whiteSpace:'pre-wrap'}} >
                              {config.description || (config.configure_id && availableConfigures.length > 0 ? "..." : "Mô tả tiêu chí")}
                            </Typography>
                          </Tooltip>
                        </Grid>
                        <Grid item xs={2} sm={2} md={1} sx={{textAlign:'right'}}>
                          <Tooltip title="Xóa tiêu chí này">
                        <span>
                        <IconButton color="error" onClick={() => handleRemoveConfigure(index)} size="small" sx={{p:0.5}} disabled={loadingSubmit}>
                            <DeleteOutlineIcon fontSize="small"/>
                        </IconButton>
                        </span>
                          </Tooltip>
                        </Grid>
                      </Grid>
                    </Paper>
                  ))}
                </Stack>
              </Box>
            </>
          </LocalizationProvider>
        )}
      </DialogContent>
      <DialogActions sx={{p:1.5, borderTop: `1px solid ${theme.palette.divider}`}}>
        <Button onClick={loadingSubmit ? () => {} : onClose} color="inherit" variant="outlined" disabled={loadingSubmit}>
          Hủy
        </Button>
        <Button
          onClick={handleSubmit}
          color="primary"
          variant="contained"
          disabled={loadingSubmit || isLoadingPage}
        >
          {loadingSubmit ? <CircularProgress size={24} color="inherit"/> : (initialValues?.id ? "Lưu thay đổi" : "Thêm mới")}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddPeriodModal;