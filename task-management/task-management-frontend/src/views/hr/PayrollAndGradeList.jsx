import React from "react";
import {Paper, Typography, Box, Button, IconButton, useTheme} from "@mui/material";
import PaidOutlinedIcon from '@mui/icons-material/PaidOutlined';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import ArrowBackIosNewIcon from '@mui/icons-material/ArrowBackIosNew';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';
import { format, parseISO } from "date-fns";
import { vi } from "date-fns/locale";

export function PayrollList({
                              data, page, totalPages, onPrev, onNext, onDetail
                            }) {
  return (
    <Paper elevation={3} sx={{ p: 2, borderRadius: 3, height: '100%', border: `1px solid ${useTheme().palette.divider}` }}>
      <Box display="flex" alignItems="center" mb={1} justifyContent="space-between">
        <Typography variant="h6" fontWeight={600} display="flex" alignItems="center">
          <PaidOutlinedIcon sx={{ mr: 1, color: 'success.main' }} /> Bảng lương gần đây
        </Typography>
        <Box>
          <IconButton
            onClick={onPrev}
            disabled={page === 0}
            size="small"
            color="primary"
            aria-label="Trang trước"
          >
            <ArrowBackIosNewIcon fontSize="small" sx={{ color: page === 0 ? '#ccc' : 'primary.main' }} />
          </IconButton>
          <IconButton
            onClick={onNext}
            disabled={page + 1 >= totalPages}
            size="small"
            color="primary"
            aria-label="Trang sau"
          >
            <ArrowForwardIosIcon fontSize="small" sx={{ color: page + 1 >= totalPages ? '#ccc' : 'primary.main' }} />
          </IconButton>
        </Box>
      </Box>
      <Box>
        {data.length === 0 ? (
          <Typography color="text.secondary" align="center" sx={{ mt: 4 }}>Chưa có dữ liệu.</Typography>
        ) : (
          data.map(p => (
            <Paper variant="outlined" sx={{ my: 1, px: 2, py: 1.5, borderRadius: 2 }} key={p.id}>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="subtitle1" fontWeight={600}>{p.name}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {format(parseISO(p.from_date), "dd/MM/yyyy", { locale: vi })} – {format(parseISO(p.thru_date), "dd/MM/yyyy", { locale: vi })}
                  </Typography>
                </Box>
                <Button size="small" variant="outlined" onClick={() => onDetail(p)}>Chi tiết</Button>
              </Box>
            </Paper>
          ))
        )}
      </Box>
    </Paper>
  );
}

export function GradeList({
                            data, page, totalPages, onPrev, onNext, onDetail
                          }) {
  return (
    <Paper elevation={3} sx={{ p: 2, borderRadius: 3, height: '100%', border: `1px solid ${useTheme().palette.divider}` }}>
      <Box display="flex" alignItems="center" mb={1} justifyContent="space-between">
        <Typography variant="h6" fontWeight={600} display="flex" alignItems="center">
          <StarBorderIcon sx={{ mr: 1, color: 'warning.main' }} /> Kết quả đánh giá
        </Typography>
        <Box>
          <IconButton
            onClick={onPrev}
            disabled={page === 0}
            size="small"
            color="primary"
            aria-label="Trang trước"
          >
            <ArrowBackIosNewIcon fontSize="small" sx={{ color: page === 0 ? '#ccc' : 'primary.main' }} />
          </IconButton>
          <IconButton
            onClick={onNext}
            disabled={page + 1 >= totalPages}
            size="small"
            color="primary"
            aria-label="Trang sau"
          >
            <ArrowForwardIosIcon fontSize="small" sx={{ color: page + 1 >= totalPages ? '#ccc' : 'primary.main' }} />
          </IconButton>
        </Box>
      </Box>
      <Box>
        {data.length === 0 ? (
          <Typography color="text.secondary" align="center" sx={{ mt: 4 }}>Chưa có kỳ đánh giá nào.</Typography>
        ) : (
          data.map(g => (
            <Paper variant="outlined" sx={{ my: 1, px: 2, py: 1.5, borderRadius: 2 }} key={g.id}>
              <Box display="flex" alignItems="center" justifyContent="space-between">
                <Box>
                  <Typography variant="subtitle1" fontWeight={600}>{g.checkpointPeriodName || g.name}</Typography>
                  <Typography variant="caption" color="text.secondary">
                    {g.checkpoint_date
                      ? format(parseISO(g.checkpoint_date), "dd/MM/yyyy", { locale: vi })
                      : ""}
                  </Typography>
                </Box>
                <Button size="small" variant="outlined" onClick={() => onDetail(g)}>Chi tiết</Button>
              </Box>
            </Paper>
          ))
        )}
      </Box>
    </Paper>
  );
}
