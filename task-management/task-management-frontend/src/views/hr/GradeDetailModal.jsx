import React, { useEffect, useState } from "react";
import {
  Dialog, DialogTitle, DialogContent, IconButton, CircularProgress,
  Typography, Box, Paper, Grid, Tooltip
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import hrService from "@/services/api/hr.service";
import EventNoteIcon from '@mui/icons-material/EventNote';

export default function GradeDetailModal({ open, onClose, checkpointId, userId, checkpointName }) {
  const [loading, setLoading] = useState(true);
  const [periodDetails, setPeriodDetails] = useState(null);
  const [checkpointData, setCheckpointData] = useState(null);
  const [criteriaList, setCriteriaList] = useState([]);

  // Lấy thông tin checkpoint (kỳ) và kết quả của nhân viên
  useEffect(() => {
    if (!open || !checkpointId || !userId) return;
    setLoading(true);
    Promise.all([
      hrService.getCheckpointPeriodById(checkpointId),
      hrService.getCheckpointDetails(checkpointId, userId),
    ]).then(([periodRes, checkpointRes]) => {
      // 1. Dữ liệu kỳ
      const period = periodRes.response?.data?.data || null;
      setPeriodDetails(period);

      // 2. Dữ liệu điểm
      const userCheckpoint = checkpointRes.response?.data?.data || null;
      setCheckpointData(userCheckpoint);

      // 3. Tạo danh sách tiêu chí có điểm
      const configs = period?.configures || [];
      const userPoints = userCheckpoint?.configure_points || [];
      setCriteriaList(
        configs.map(c => {
          const userPoint = userPoints.find(p => p.configure_id === c.configure?.code);
          return {
            name: c.configure?.name || "N/A",
            description: c.configure?.description || "-",
            coefficient: c.coefficient,
            point: userPoint?.point ?? null,
          };
        })
      );
    }).finally(() => setLoading(false));
  }, [open, checkpointId, userId]);

  // Tổng điểm
  const totalPointDisplay = checkpointData && checkpointData.total_point !== undefined && checkpointData.total_point !== null
    ? Number(checkpointData.total_point).toFixed(2)
    : "N/A";

  // Thông tin kỳ đánh giá
  function CheckpointInfo() {
    if (!periodDetails) return null;
    return (
      <Paper
        elevation={2}
        sx={{
          mb: 3,
          p: 2,
          background: "#f8fafb",
          borderRadius: 2,
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          flexWrap: "wrap",
          gap: 3,
        }}
      >
        <Box display="flex" alignItems="center" gap={1}>
          <EventNoteIcon sx={{ color: "primary.main", fontSize: 36 }} />
          <Box>
            <Typography color="primary" fontWeight={700}>
              {periodDetails.name || checkpointName}
            </Typography>
            {periodDetails.description && (
              <Tooltip title={periodDetails.description}>
                <Typography variant="caption" color="text.secondary" sx={{ whiteSpace: "nowrap", textOverflow: 'ellipsis', maxWidth: 250, overflow: 'hidden', display: 'block' }}>
                  {periodDetails.description}
                </Typography>
              </Tooltip>
            )}
          </Box>
        </Box>
        {periodDetails.checkpoint_date &&
          <Typography color="text.secondary" fontWeight={500}>
            Ngày checkpoint: {periodDetails.checkpoint_date}
          </Typography>
        }
      </Paper>
    );
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        Kết quả đánh giá {checkpointName ? <>- <span style={{ fontWeight: 500 }}>{checkpointName}</span></> : ""}
        <IconButton onClick={onClose} sx={{ position: 'absolute', right: 8, top: 8 }}>
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent dividers>
        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', py: 3 }}>
            <CircularProgress /> <Typography sx={{ ml: 2 }}>Đang tải dữ liệu...</Typography>
          </Box>
        ) : !periodDetails ? (
          <Typography align="center" sx={{ py: 3 }}>Không có dữ liệu chi tiết.</Typography>
        ) : (
          <Box>
            <CheckpointInfo />

            <Paper sx={{ p: 2, mb: 2, background: "#f6f8fa", borderRadius: 2 }}>
              <Typography variant="subtitle2" color="text.secondary" gutterBottom>
                Các tiêu chí đánh giá:
              </Typography>
              <Grid container spacing={2}>
                {criteriaList.map((c, idx) => (
                  <Grid item xs={12} md={6} key={idx}>
                    <Paper elevation={1} sx={{ p: 2, borderRadius: 2, height: "100%" }}>
                      <Typography variant="subtitle1" fontWeight={600} sx={{ color: "primary.main" }}>
                        {idx + 1}. {c.name}
                      </Typography>
                      {c.description &&
                        <Tooltip title={c.description} placement="top-start">
                          <Typography variant="caption" color="text.secondary" sx={{
                            whiteSpace: "pre-wrap",
                            display: '-webkit-box',
                            WebkitBoxOrient: 'vertical',
                            WebkitLineClamp: 2,
                            overflow: 'hidden',
                            textOverflow: 'ellipsis',
                            mb: 1
                          }}>
                            {c.description}
                          </Typography>
                        </Tooltip>
                      }
                      <Typography variant="body2" sx={{ mb: 0.5 }}>
                        Hệ số: <b>{c.coefficient}</b>
                      </Typography>
                      <Typography variant="body2" sx={{ mb: 0.5 }}>
                        Điểm: <b>{c.point !== null && c.point !== undefined ? c.point : "-"}</b>
                      </Typography>
                    </Paper>
                  </Grid>
                ))}
              </Grid>
            </Paper>

            <Box sx={{
              mt: 2,
              display: 'flex',
              justifyContent: 'flex-end'
            }}>
              <Typography variant="h5" sx={{ fontWeight: 'bold', color: 'primary.dark' }}>
                Tổng Điểm: {totalPointDisplay}
              </Typography>
            </Box>
          </Box>
        )}
      </DialogContent>
    </Dialog>
  );
}
