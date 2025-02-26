import {
  Box,
  Divider,
  Grid,
  IconButton,
  Paper,
  Tooltip,
  Typography
} from '@mui/material';
import {
  School,
  Room
} from '@mui/icons-material';
import { PieChart } from '@mui/x-charts/PieChart';
import { useHistory } from 'react-router-dom';

const StatisticsPanel = ({ planId }) => {
  const history = useHistory();
  // Sample data for statistics
  const stats = {
    totalClasses: 245,
    totalRooms: 38,
    schoolDistribution: [
      { id: 0, value: 35, label: 'CNTT', color: '#d32f2f' }, // Red
      { id: 1, value: 28, label: 'Điện - Điện tử', color: '#fbc02d' }, // Yellow
      { id: 2, value: 22, label: 'Cơ khí', color: '#388e3c' }, // Green
      { id: 3, value: 15, label: 'Kinh tế', color: '#0288d1' }, // Blue
      { id: 4, value: 10, label: 'Môi trường', color: '#512da8' } // Purple
    ]
  };

  return (
    <Paper elevation={1} sx={{ height: '100%' }}>
      <Box sx={{
        p: 2,
        borderBottom: '1px solid #e0e0e0', 
        background: 'linear-gradient(90deg, #1976D2, #2196F3)',
        borderRadius: '4px 4px 0 0',
      }}>
        <Typography variant="h6" fontWeight={500} color={'white'}>
          Thống kê
        </Typography>
      </Box>
      
      <Box sx={{ p: 2 }}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Box 
              sx={{ textAlign: 'center', p: 1, cursor: 'pointer' }} 
              onClick={() => history.push(`/exam-time-tabling/class-list/${planId}`)} // Replace with your actual page
            >
              <Tooltip title="Xem chi tiết lớp thi" arrow>
                <IconButton sx={{ color: 'primary.main' }}>
                  <School fontSize="large" color="primary" />
                </IconButton>
              </Tooltip>
              <Typography variant="h5" fontWeight={600} sx={{ mt: 1, color: 'primary.main' }}>
                {stats.totalClasses}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Lớp thi
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={6}>
            <Box sx={{ textAlign: 'center', p: 1 }}>
              <Room fontSize="large" color="primary" />
              <Typography variant="h5" fontWeight={600} sx={{ mt: 1, color: 'primary.main' }}>
                {stats.totalRooms}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Phòng thi
              </Typography>
            </Box>
          </Grid>
        </Grid>

        <Divider sx={{ my: 2 }} />

        {/* Improved title */}
        <Typography variant="subtitle1" sx={{ mb: 2 }}>
          Phân bố số lượng lớp thi theo từng khoa
        </Typography>

        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <PieChart
            series={[
              {
                data: stats.schoolDistribution,
                highlightScope: { faded: 'global', highlighted: 'item' },
                faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
              },
            ]}
            height={250}
            margin={{ top: 0, bottom: 50, left: 0, right: 0 }}
            slotProps={{
              legend: { hidden: true }, // Hide the default legend
            }}
          />

          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'stretch', mt: 2 }}>
            {stats.schoolDistribution.map((item, index) => (
              <Box key={index} sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Box
                  sx={{
                    width: 14,
                    height: 14,
                    backgroundColor: ['#d32f2f', '#fbc02d', '#7b1fa2', '#1976d2', '#388e3c'][index], // Customize colors if needed
                    mr: 1,
                  }}
                />
                <Typography variant="body2">{item.label}</Typography>
              </Box>
            ))}
          </Box>

        </Box>
      </Box>
    </Paper>
  );
};

export default StatisticsPanel;
