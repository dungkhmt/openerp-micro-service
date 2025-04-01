import { useState } from 'react';
import { useHistory } from 'react-router-dom';
import {
  Box,
  Button,
  CircularProgress,
  IconButton,
  LinearProgress,
  Paper,
  Typography
} from '@mui/material';
import {
  Add,
  GetApp,
  Visibility
} from '@mui/icons-material';
import { DataGrid } from '@mui/x-data-grid';
import { format } from 'date-fns';
import AddTimetableModal from './AddTimeTableModal'

const TimetableList = ({ 
  planId, 
  timetables, 
  isLoading, 
  onCreateTimetable 
}) => {
  const history = useHistory();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const handleCreateTimetable = () => {
    setIsCreateModalOpen(true);
  };

  const handleCloseCreateModal = () => {
    setIsCreateModalOpen(false);
  };

  const handleCreateTimetableSubmit = async (data) => {
    return await onCreateTimetable(data);
  };

  // Columns for timetable list
  const columns = [
    { 
      field: 'name', 
      headerName: 'Tên lịch thi', 
      flex: 6,
      renderCell: (params) => (
        <Typography 
          variant="body2" 
          sx={{ 
            fontWeight: 500,
            cursor: 'pointer',
            color: 'primary.main',
            '&:hover': { textDecoration: 'underline' },
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
            maxWidth: '100%'
          }}
          onClick={() => history.push(`/exam-time-tabling/exam-timetable/${params.row.id}`)}
        >
          {params.value}
        </Typography>
      )
    },
    { 
      field: 'createdAt', 
      headerName: 'Ngày tạo', 
      flex: 3,
      renderCell: (params) => (
        <Typography variant="body2">
          {format(new Date(params.value), 'dd/MM/yyyy HH:mm')}
        </Typography>
      )
    },
    { 
      field: 'progressPercentage', 
      headerName: 'Tiến độ', 
      flex: 2,
      renderCell: (params) => (
        <Box sx={{ width: '100%', mr: 1 }}>
          <LinearProgress 
            variant="determinate" 
            value={params.value} 
            sx={{ 
              height: 8, 
              borderRadius: 5,
              backgroundColor: '#e0e0e0'
            }} 
          />
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 0.5 }}>
            <Typography variant="body2" color="text.secondary">
              {`${Math.round(params.value)}%`}
            </Typography>
          </Box>
        </Box>
      )
    },
    {
      field: 'actions',
      headerName: 'Thao tác',
      flex: 2,
      renderCell: (params) => (
        <Box sx={{ display: 'flex', gap: 1 }}>
          <IconButton size="small" color="success">
            <GetApp fontSize="small" />
          </IconButton>
          <IconButton 
            size="small" 
            color="primary" 
            onClick={() => history.push(`/exam-time-tabling/exam-timetable/${params.row.id}`)}
          >
            <Visibility fontSize="small" />
          </IconButton>
        </Box>
      )
    }
  ];

  return (
    <>
      <Paper elevation={2} sx={{ 
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        borderRadius: 2, 
        overflow: 'hidden' 
      }}>
        <Box 
          sx={{ 
            p: 2, 
            display: 'flex', 
            justifyContent: 'space-between', 
            alignItems: 'center', 
            background: 'linear-gradient(90deg, #1976D2, #2196F3)',
            color: 'white'
          }}
        >
          <Typography variant="h6" fontWeight={600}>
            Danh sách lịch thi
          </Typography>
          <Button 
            variant="contained"
            color="secondary"
            startIcon={<Add />}
            onClick={handleCreateTimetable}
            size="small"
          >
            Tạo lịch thi
          </Button>
        </Box>

        <Box sx={{ 
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          maxHeight: '500px',
        }}>
          {isLoading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
              <CircularProgress />
            </Box>
          ) : timetables && timetables.length > 0 ? (
            <DataGrid
              rows={timetables}
              columns={columns}
              rowHeight={40}
              pageSize={5}
              rowsPerPageOptions={[5, 10, 25]}
              pagination
              density="comfortable"
              disableRowSelectionOnClick
              autoHeight={false}
              sx={{
                flex: 1,
                height: '100%',
                border: 'none',
                '& .MuiDataGrid-columnHeaders': {
                  backgroundColor: '#f5f5f5',
                  fontWeight: 'bold',
                },
                '& .MuiDataGrid-row:nth-of-type(even)': {
                  backgroundColor: '#f9f9f9',
                },
                '& .MuiDataGrid-cell': {
                  padding: '12px 16px',
                },
                '& .MuiDataGrid-cell:focus': {
                  outline: 'none'
                },
                '& .MuiDataGrid-footerContainer': {
                  borderTop: '1px solid #e0e0e0',
                  justifyContent: 'flex-end',
                  minHeight: '52px'
                }
              }}
            />
          ) : (
            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%' }}>
              <Typography variant="body1" color="textSecondary" sx={{ mb: 2 }}>
                Chưa có lịch thi nào được tạo
              </Typography>
              <Button 
                variant="contained" 
                startIcon={<Add />}
                onClick={handleCreateTimetable}
              >
                Tạo lịch thi
              </Button>
            </Box>
          )}
        </Box>
      </Paper>
    
      <AddTimetableModal
        open={isCreateModalOpen}
        onClose={handleCloseCreateModal}
        planId={planId}
        onCreateTimetable={handleCreateTimetableSubmit}
      />
    </>
  );
};

export default TimetableList;
