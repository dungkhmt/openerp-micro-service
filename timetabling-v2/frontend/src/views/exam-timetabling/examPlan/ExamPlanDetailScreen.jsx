// ExamPlanDetailPage.jsx
import { useState } from 'react';
import { useHistory, useParams } from 'react-router-dom';
import { useExamPlanData } from 'services/useExamPlanData';
import {
  Box,
  Button,
  Card,
  CardContent,
  CircularProgress,
  Container,
  Grid,
  Typography
} from '@mui/material';
import {
  Delete,
  Edit,
  CalendarMonth,
  SentimentDissatisfied
} from '@mui/icons-material';
import TimetableList from './utils/TimetableList';
import StatisticsPanel from './utils/StatisticsPanel';
import { format } from 'date-fns';

const ExamPlanDetailPage = () => {
  const history = useHistory();
  const { id } = useParams();
  const { examPlan, isLoading, deleteExamPlan } = useExamPlanData(id);

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!examPlan) {
    return (
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', p: 4 }}>
        <SentimentDissatisfied sx={{ fontSize: 80, color: 'gray', mb: 2 }} />
        <Typography variant="h6" color="textSecondary">Không tìm thấy kế hoạch thi</Typography>
        <Button 
          variant="contained" 
          sx={{ mt: 2 }} 
          onClick={() => history.push('/exam-plans')}
        >
          Quay lại danh sách
        </Button>
      </Box>
    );
  }

  const handleEditPlan = () => {
    history.push(`/exam-plans/${id}/edit`);
  };

  const handleDeletePlan = async () => {
    try {
      await deleteExamPlan(id);
      history.push('/exam-plans');
    } catch (error) {
      console.error('Error deleting plan:', error);
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Grid container spacing={3} sx={{ minHeight: 'calc(100vh - 200px)' }}> {/* Set a minimum height */}
        <Grid item xs={12} md={8} sx={{ display: 'flex', flexDirection: 'column' }}>
          {/* Plan Information Card */}
          <Card sx={{ 
            mb: 3, 
            boxShadow: 2, 
            borderRadius: 2,
            overflow: 'hidden',
            border: '1px solid #ddd',
          }}>
            {/* Improved Header with Gradient */}
            <Box sx={{ 
              background: 'linear-gradient(90deg, #1976D2, #2196F3)', 
              color: 'white',
              p: 2,
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <Typography variant="h5" fontWeight={600}>
                {examPlan.name}
              </Typography>
              <Box>
              <Button 
                variant="contained" 
                color="secondary"
                startIcon={<Edit />} 
                size="small"
                sx={{ 
                  mr: 1, 
                  backgroundColor: 'secondary.main', 
                  '&:hover': { backgroundColor: '#FFB74D' } // Softer orange on hover
                }}
                onClick={handleEditPlan}
              >
                SỬA
              </Button>

              <Button 
                variant="contained" 
                color="error" 
                startIcon={<Delete />} 
                size="small"
                sx={{ 
                  backgroundColor: 'error.main', 
                  '&:hover': { backgroundColor: '#E57373' } // Softer red on hover
                }}
                onClick={handleDeletePlan}
              >
                XÓA
              </Button>

              </Box>
            </Box>
            <CardContent sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Typography variant="body1" fontWeight={600} color="#1976D2">
                  📅 {format(new Date(examPlan.startTime), 'dd MMM yyyy')} → {format(new Date(examPlan.endTime), 'dd MMM yyyy')}
                </Typography>
              </Box>
              <Typography variant="body1" color="text.secondary">
                {examPlan.description || 'Chưa có mô tả'}
              </Typography>
            </CardContent>
          </Card>


          {/* Timetable List */}
          <Box sx={{ flex: 1, display: 'flex', flexDirection: 'column', width: '100%' }}>
            <TimetableList planId={id} />
          </Box>
        </Grid>

        {/* Right Column - Statistics Panel */}
        <Grid item xs={12} md={4}>
          <StatisticsPanel planId={id} />
        </Grid>
      </Grid>
    </Container>
  )
};

export default ExamPlanDetailPage;
