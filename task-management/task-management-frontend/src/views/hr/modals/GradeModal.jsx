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
  TextField,
  Typography,
  IconButton,
  Paper,
  Avatar, Tooltip
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import {request}from "@/api";
import toast from "react-hot-toast";
import { useTheme } from '@mui/material/styles';
import EventNoteIcon from '@mui/icons-material/EventNote';

const MAX_POINT = 10;

const GradeModal = ({ open, onClose, staff, period, titleProps }) => {
  const theme = useTheme();
  const [configures, setConfigures] = useState([]);
  const [loading, setLoading] = useState(false);
  const [periodDetails, setPeriodDetails] = useState(null);
  const [totalPointDisplay, setTotalPointDisplay] = useState("N/A");

  const fetchPeriodDetails = useCallback(async () => {
    if (!staff || !period || !period.id || !staff.user_login_id) return;
    setLoading(true);
    try {
      const [periodResponse, checkpointResponse] = await Promise.all([
        new Promise((resolve, reject) => request("get", `/checkpoints/periods/${period.id}`, (res) => resolve(res), { onError: reject })),
        new Promise((resolve, reject) => request("get", `/checkpoints/${period.id}/${staff.user_login_id}`, (res) => resolve(res), { onError: reject }))
      ]);

      const periodData = periodResponse.data?.data;
      const checkpointData = checkpointResponse.data?.data;

      if (!periodData) {
        toast.error("Không thể tải thông tin chi tiết kỳ checkpoint.");
        setLoading(false);
        return;
      }
      setPeriodDetails(periodData);

      const periodConfigs = periodData.configures || [];
      const checkpointConfigs = checkpointData?.configure_points || [];

      const configsWithPoints = periodConfigs.map((pConfig) => ({
        configure_id: pConfig.configure?.code,
        name: pConfig.configure?.name || "N/A",
        description: pConfig.configure?.description || "-",
        coefficient: parseFloat(pConfig.coefficient) || 0,
        point: checkpointConfigs.find((c) => c.configure_id === pConfig.configure?.code)?.point ?? "",
      }));
      setConfigures(configsWithPoints);

      if (checkpointData && checkpointData.total_point !== null && checkpointData.total_point !== undefined) {
        setTotalPointDisplay(Number(checkpointData.total_point).toFixed(2));
      } else {
        calculateTotalPoints(configsWithPoints);
      }

    } catch (error) {
      console.error("Error fetching details:", error);
      toast.error("Lỗi khi tải chi tiết đánh giá.");
      setConfigures([]);
      setPeriodDetails(null);
    } finally {
      setLoading(false);
    }
  }, [staff, period]);

  const calculateTotalPoints = useCallback((currentConfigs) => {
    if (!currentConfigs || currentConfigs.length === 0) {
      setTotalPointDisplay("N/A");
      return;
    }
    const totalPointsSum = currentConfigs.reduce((sum, config) => {
      const point = parseFloat(config.point || 0);
      const coefficient = parseFloat(config.coefficient || 0);
      return sum + (point * coefficient);
    }, 0);

    const totalCoefficientSum = currentConfigs.reduce((sum, config) => {
      return sum + parseFloat(config.coefficient || 0);
    }, 0);

    const result = totalCoefficientSum > 0 ? (totalPointsSum / totalCoefficientSum).toFixed(2) : "N/A";
    setTotalPointDisplay(result);
  }, []);


  useEffect(() => {
    if (open) {
      fetchPeriodDetails();
    } else {
      setPeriodDetails(null);
      setConfigures([]);
      setTotalPointDisplay("N/A");
    }
  }, [open, fetchPeriodDetails]);

  useEffect(() => {
    if (configures.length > 0) {
      calculateTotalPoints(configures);
    }
  }, [configures, calculateTotalPoints]);


  const handleSave = async () => {
    setLoading(true);
    const allPointsEntered = configures.every(config => config.point !== "" && !isNaN(parseFloat(config.point)));
    if (!allPointsEntered) {
      toast.error("Vui lòng nhập điểm cho tất cả các tiêu chí.");
      setLoading(false);
      return;
    }

    try {
      const payload = {
        period_id: period.id,
        user_id: staff.user_login_id,

        checkpoint_configures: configures.map((config) => ({
          configure_id: config.configure_id,
          point: parseFloat(config.point),
        })),
      };
      await request("post", "/checkpoints",
        (res) => {
          toast.success(res.data?.message || "Lưu đánh giá thành công!");
          onClose();
        },
        {
          onError: (err) => {
            toast.error(err.response?.data?.message || "Lưu đánh giá thất bại.");
          }
        },
        payload);
    } catch (error) {
      console.error("API Save Error:", error)
      toast.error("Lỗi khi lưu đánh giá.");
    } finally {
      setLoading(false);
    }
  };

  const handlePointChange = (index, value) => {
    const updatedConfigs = [...configures];
    let point = parseFloat(value);
    if (isNaN(point)) {
      updatedConfigs[index].point = "";
    } else {
      point = Math.min(Math.max(0, point), MAX_POINT);
      updatedConfigs[index].point = String(point);
    }
    setConfigures(updatedConfigs);
  };


  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md" PaperProps={{sx: {height: 'calc(100% - 64px)', borderRadius: 2}}}>
      <DialogTitle sx={{
        ...titleProps,
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        backgroundColor: 'primary.main',
        color: 'primary.contrastText',
        py:1.5, px:2.5 // Tăng padding
      }}>
        <Typography variant="h6" component="div" sx={{fontWeight: 600, fontSize: titleProps?.sx?.fontSize || '1.2rem'}}> {/* Tăng fontSize title */}
          Đánh giá Checkpoint
        </Typography>
        <IconButton aria-label="đóng" onClick={onClose} sx={{color: 'primary.contrastText', p:0.5}}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers sx={{p:0, display: 'flex', flexDirection: 'column', bgcolor: theme.palette.grey[50]}}>
        {loading && !periodDetails ? (
          <Box sx={{display: 'flex', justifyContent:'center', alignItems:'center', flexGrow: 1}}>
            <CircularProgress />
            <Typography sx={{ml:2}}>Đang tải chi tiết...</Typography>
          </Box>
        ) : !periodDetails ? (
          <Box sx={{display: 'flex', justifyContent:'center', alignItems:'center', flexGrow: 1, p:3}}>
            <Typography color="text.secondary">Không tải được chi tiết kỳ checkpoint hoặc thông tin nhân viên.</Typography>
          </Box>
        ): (
          <>
            {/* Phần thông tin nhân viên và kỳ */}
            <Box sx={{p: 2.5, backgroundColor: 'background.paper', borderBottom: `1px solid ${theme.palette.divider}`}}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} md={6}>
                  <Box display="flex" alignItems="center">
                    <Avatar
                      src={`https://ui-avatars.com/api/?name=${encodeURIComponent(staff?.fullname || 'NV')}&background=random&color=fff&size=128&font-size=0.35`}
                      sx={{width: 56, height: 56, mr: 2, border: `2px solid ${theme.palette.primary.main}`}}
                    />
                    <Box>
                      <Typography variant="h6" sx={{fontWeight: 600, color: 'text.primary', lineHeight:1.3}}>
                        {staff?.fullname}
                      </Typography>
                      <Typography variant="body2" color="textSecondary">
                        Mã NV: {staff?.staff_code || "N/A"} | Email: {staff?.email || "N/A"}
                      </Typography>
                    </Box>
                  </Box>
                </Grid>
                <Grid item xs={12} md={6}>
                  <Box display="flex" alignItems="center" justifyContent={{md: 'flex-end'}}>
                    <EventNoteIcon sx={{ color: 'text.secondary', mr: 1, fontSize:'1.8rem' }}/>
                    <Box>
                      <Typography variant="subtitle1" sx={{fontWeight: 600, color: 'text.primary'}}>
                        Kỳ: {periodDetails?.name || period?.name}
                      </Typography>
                      {periodDetails?.description &&
                        <Tooltip title={periodDetails.description}>
                          <Typography variant="caption" color="textSecondary" sx={{ display:'block', whiteSpace: "nowrap", overflow:'hidden', textOverflow:'ellipsis', maxWidth: 250 }}>
                            {periodDetails.description}
                          </Typography>
                        </Tooltip>
                      }
                    </Box>
                  </Box>
                </Grid>
              </Grid>
            </Box>

            <Box sx={{flexGrow:1, overflowY:'auto', p:2.5,
              '&::-webkit-scrollbar': { width: '6px' },
              '&::-webkit-scrollbar-track': { background: theme.palette.grey[100] },
              '&::-webkit-scrollbar-thumb': { background: theme.palette.grey[400] },
              '&::-webkit-scrollbar-thumb:hover': { background: theme.palette.grey[500] }
            }}>
              {configures.length === 0 && !loading && <Typography sx={{textAlign:'center', fontStyle:'italic', color:'text.secondary'}}>Không có tiêu chí nào cho kỳ này.</Typography>}
              <Grid container spacing={2}>
                {configures.map((config, index) => (
                  <Grid item xs={12} md={6} key={config.configure_id || index}>
                    <Paper elevation={1} sx={{p:2, height:'100%', display:'flex', flexDirection:'column', borderRadius: 1.5}}>
                      <Typography variant="subtitle1" sx={{ fontWeight: '600', color: 'primary.dark', fontSize: '1.05rem' }}> {/* Tăng size, màu */}
                        {index + 1}. {config.name}
                      </Typography>
                      {config.description &&
                        <Tooltip title={config.description} placement="top-start">
                          <Typography variant="caption" color="textSecondary" sx={{
                            whiteSpace: "pre-wrap",
                            mb:1, display:'block',
                            maxHeight: '3em', // Giới hạn 2 dòng
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            WebkitLineClamp: 2,
                            display: '-webkit-box',
                            WebkitBoxOrient: 'vertical',
                          }}>
                            {config.description}
                          </Typography>
                        </Tooltip>
                      }
                      <Typography variant="body2" color="textSecondary" sx={{mb:1.5, fontSize: '0.85rem'}}>
                        Hệ số: <Typography component="span" sx={{fontWeight: 'bold', fontSize: '0.9rem'}}>{config.coefficient}</Typography>
                      </Typography>
                      <TextField
                        fullWidth
                        type="number"
                        label={`Điểm (0 - ${MAX_POINT})`}
                        value={config.point}
                        onChange={(e) => handlePointChange(index, e.target.value)}
                        size="small"
                        InputProps={{
                          inputProps: { min: 0, max: MAX_POINT, step: 0.1 },
                          sx: {fontSize: '0.95rem', fontWeight: 500}
                        }}
                        sx={{mt:'auto'}}
                      />
                    </Paper>
                  </Grid>
                ))}
              </Grid>
            </Box>

            <Box sx={{p:2, pt:1.5, borderTop: `1px solid ${theme.palette.divider}`, backgroundColor: theme.palette.background.paper}}>
              <Typography variant="h5" align="right" sx={{fontWeight: 'bold', color: 'primary.dark'}}>
                Tổng Điểm: {totalPointDisplay}
              </Typography>
            </Box>
          </>
        )}
      </DialogContent>
      <DialogActions sx={{p:1.5, borderTop: `1px solid ${theme.palette.divider}`, backgroundColor: theme.palette.grey[50]}}>
        <Button onClick={onClose} color="inherit" variant="outlined" disabled={loading} sx={{minWidth: 100}}>
          Hủy
        </Button>
        <Button onClick={handleSave} color="primary" variant="contained" disabled={loading || !periodDetails || configures.length === 0} sx={{minWidth: 120}}>
          {loading ? <CircularProgress size={24} color="inherit"/> : "Lưu Điểm"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default GradeModal;