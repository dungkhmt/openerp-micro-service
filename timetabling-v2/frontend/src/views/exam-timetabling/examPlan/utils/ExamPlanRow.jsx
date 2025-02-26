import React from 'react';
import {
  Box,
  Grid,
  Typography,
  Button,
  Paper
} from '@mui/material';
import { ArrowForwardIos } from '@mui/icons-material';

const ExamPlanRow = ({ plan, index, onClick, isLast }) => {
  return (
    <Paper
      elevation={2}
      sx={{
        borderRadius: 2,
        mb: 2,
        overflow: 'hidden',
        '&:hover': {
          boxShadow: 4,
          backgroundColor: 'rgba(0, 0, 0, 0.02)',
          cursor: 'pointer',
        }
      }}
      onClick={() => onClick(plan.id)}
    >
      <Box sx={{ p: 2 }}>
        <Grid container alignItems="center" spacing={2}>
          {/* Exam Name & Creation Date */}
          <Grid item xs={12} md={5}>
            <Box>
              <Typography 
                variant="h6" 
                fontWeight={600} 
                color="primary"
              >
                {plan.name}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                🗓️ Tạo ngày: {new Date(plan.createdAt).toLocaleDateString()}
              </Typography>
            </Box>
          </Grid>

          {/* Exam Date Range & Last Update */}
          <Grid item xs={12} md={5}>
            <Box>
              <Typography variant="body1" fontWeight={500}>
                📅 Bắt đầu: {new Date(plan.startTime).toLocaleDateString()} - Kết thúc: {new Date(plan.endTime).toLocaleDateString()}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                🔄 Cập nhật: {new Date(plan.updatedAt).toLocaleDateString()}
              </Typography>
            </Box>
          </Grid>

          {/* Detail Button */}
          <Grid item xs={12} md={2} sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              endIcon={<ArrowForwardIos />}
              color="primary"
              size="small"
              variant="outlined"
              onClick={(e) => {
                e.stopPropagation();
                onClick(plan.id);
              }}
              sx={{
                borderRadius: 10,
                textTransform: 'none',
              }}
            >
              Xem chi tiết
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  );
};

export default ExamPlanRow;
