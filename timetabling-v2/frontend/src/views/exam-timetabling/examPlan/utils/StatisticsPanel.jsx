import {
  Box,
  Divider,
  Grid,
  IconButton,
  Paper,
  Tooltip,
  Typography,
  CircularProgress
} from '@mui/material';
import {
  School,
  Room
} from '@mui/icons-material';
import { PieChart } from '@mui/x-charts/PieChart';
import { useHistory } from 'react-router-dom';

const StatisticsPanel = ({ planId, statistics, isLoading }) => {
  const history = useHistory();

  // Colors for the pie chart
  const chartColors = [
    '#d32f2f',  // Red
    '#fbc02d',  // Yellow
    '#7b1fa2',  // Purple
    '#1976d2',  // Blue
    '#388e3c',  // Green
    '#f57c00',  // Orange
    '#00796b',  // Teal
    '#5d4037'   // Brown
  ];

  // If statistics isn't available yet, show loading or placeholder
  if (isLoading) {
    return (
      <Paper elevation={1} sx={{ height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <CircularProgress />
      </Paper>
    );
  }

  if (!statistics) {
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
        <Box sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="body1" color="text.secondary">
            Chưa có dữ liệu thống kê
          </Typography>
        </Box>
      </Paper>
    );
  }

  // Format the school distribution data for the chart
  const schoolData = statistics.topSchools.map((school, index) => ({
    id: index,
    value: school.count,
    label: school.schoolName,
    color: chartColors[index % chartColors.length]
  }));

  // Add "Other schools" if available
  if (statistics.otherSchools && statistics.otherSchools.count > 0) {
    schoolData.push({
      id: schoolData.length,
      value: statistics.otherSchools.count,
      label: 'Khoa khác',
      color: chartColors[schoolData.length % chartColors.length]
    });
  }

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
        <Grid container spacing={2} justifyContent="center" alignItems="center">
          <Grid item xs={6}>
            <Box 
              sx={{ 
                display: 'flex', 
                flexDirection: 'column', 
                alignItems: 'center', 
                justifyContent: 'center', 
                textAlign: 'center',
                p: 1, 
                cursor: 'pointer' 
              }} 
              onClick={() => history.push(`/exam-time-tabling/class-list`)}
            >
              <Tooltip title="Xem chi tiết lớp thi" arrow>
                <IconButton sx={{ color: 'primary.main' }}>
                  <School fontSize="large" color="primary" />
                </IconButton>
              </Tooltip>
              <Typography variant="h5" fontWeight={600} sx={{ color: 'primary.main', mt: 0.5 }}>
                {statistics.totalExamClasses || 0}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Lớp thi
              </Typography>
            </Box>
          </Grid>
          <Grid item xs={6}>
            <Box 
              sx={{ 
                display: 'flex', 
                flexDirection: 'column', 
                alignItems: 'center', 
                justifyContent: 'center', 
                textAlign: 'center',
                p: 1 
              }}
            >
              <Room fontSize="large" color="primary" />
              <Typography variant="h5" fontWeight={600} sx={{ color: 'primary.main', mt: 1.5 }}>
                {statistics.totalRooms || 0}
              </Typography>
              <Typography variant="body2" color="text.secondary">
                Phòng thi
              </Typography>
            </Box>
          </Grid>
        </Grid>
        <Divider sx={{ my: 2 }} />

        {/* Show chart only if we have school data */}
        {schoolData.length > 0 ? (
          <>
            <Typography variant="subtitle1" sx={{ mb: 2, textAlign: 'center', fontWeight: 600 }}>
              Phân bố lớp thi theo khoa
            </Typography>

            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
              <PieChart
                series={[
                  {
                    data: schoolData,
                    highlightScope: { faded: 'global', highlighted: 'item' },
                    faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                  },
                ]}
                height={250}
                margin={{ top: 0, bottom: 20, left: 0, right: 0 }}
                slotProps={{
                  legend: { hidden: true }, // Hide the default legend
                }}
              />

              <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'stretch' }}>
                {schoolData.map((item, index) => (
                  <Box key={index} sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                    <Box
                      sx={{
                        width: 14,
                        height: 14,
                        backgroundColor: item.color,
                        mr: 1,
                      }}
                    />
                    <Typography variant="body2">{item.label} ({item.value})</Typography>
                  </Box>
                ))}
              </Box>
            </Box>
          </>
        ) : (
          <Box sx={{ p: 2, textAlign: 'center' }}>
            <Typography variant="body1" color="text.secondary">
              Chưa có dữ liệu phân bố theo khoa
            </Typography>
          </Box>
        )}
      </Box>
    </Paper>
  );
};

export default StatisticsPanel;
