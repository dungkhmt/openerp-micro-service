import { useRef, useState } from 'react';
import { useParams, useHistory } from 'react-router-dom';
import {
  Box,
  Button,
  Card,
  CardContent,
  Container,
  CircularProgress,
  Grid,
  Paper,
  Typography,
} from '@mui/material';
import {
  Edit,
  Delete,
  Visibility,
  AssignmentTurnedIn,
  School,
  CheckCircle,
  Error
} from '@mui/icons-material';
import ClassesTable from './components/ClassAssignTable';
import { format } from 'date-fns';
import DeleteConfirmModal from './components/DeleteExamTimetableModal'
import UpdateTimetableModal from './components/UpdateTimeTableModal'
import ConflictDialog from './components/ConflictSaveDialog'
import { useExamTimetableData } from 'services/useExamTimetableData'
import { time } from 'echarts'

// Mock data fetching hook
// const useTimetableDetail = (id) => {
//   // This would be replaced with actual API call
//   const [isLoading, setIsLoading] = useState(true);
//   const [timetable, setTimetable] = useState(null);
  
//   // Simulate API call
//   useState(() => {
//     setTimeout(() => {
//       setTimetable({
//         id,
//         name: "Lịch thi học kỳ 1 (2023-2024)",
//         createdAt: "2023-12-15T09:30:00",
//         createdBy: "Nguyễn Văn A",
//         totalClasses: 245,
//         assignedClasses: 210,
//         progress: 85,
//         status: "in_progress", // 'not_started', 'in_progress', 'completed', 'conflict'
//         examPlanId: "plan123"
//       });
//       setIsLoading(false);
//     }, 1000);
//   }, [id]);
  
//   return { timetable, isLoading };
// };

// Mock rooms and sessions data
const useOptionsData = () => {
  return {
    rooms: [
      { id: "R101", name: "P.101" },
      { id: "R102", name: "P.102" },
      { id: "R103", name: "P.103" },
      { id: "R104", name: "P.104" }
    ],
    weeks: [
      { id: "W41", name: "T41" },
      { id: "W42", name: "T42" },
      { id: "W43", name: "T43" }
    ],
    dates: [
      { id: "D0101", weekId: "W41", name: "T2 (01/01/2025)" },
      { id: "D0201", weekId: "W41", name: "T3 (02/01/2025)" },
      { id: "D0301", weekId: "W41", name: "T4 (03/01/2025)" },
      { id: "D0801", weekId: "W42", name: "T2 (08/01/2025)" },
      { id: "D0901", weekId: "W42", name: "T3 (09/01/2025)" }
    ],
    slots: [
      { id: "S1", name: "Kíp 1 (7:00 - 9:00)" },
      { id: "S2", name: "Kíp 2 (9:30 - 11:30)" },
      { id: "S3", name: "Kíp 3 (13:00 - 15:00)" },
      { id: "S4", name: "Kíp 4 (15:30 - 17:30)" }
    ]
  };
};


const TimetableDetailPage = () => {
  const { id } = useParams();
  const history = useHistory();
  // const { timetable, isLoading } = useTimetableDetail(id);
  // ACtual API call
  const { timetable, isLoadingDetail, errorDetail } = useExamTimetableData(null, id);

  console.log(timetable);
  // const { classesData, isLoading: isLoadingClasses } = useClassesData(id);
  const optionsData = useOptionsData();
  
  const [isRenameDialogOpen, setIsRenameDialogOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  // Add state for conflict handling
  const [isConflictModalOpen, setIsConflictModalOpen] = useState(false);
  const [conflicts, setConflicts] = useState([]);

  // Use ref to access ClassesTable methods
  const classesTableRef = useRef();

  const handleSaveAndCheck = () => {
    // Get the current assignments and check for conflicts
    const assignmentChanges = classesTableRef.current.getAssignmentChanges();
    const foundConflicts = classesTableRef.current.checkForAssignmentConflicts();

    if (foundConflicts.length > 0) {
      // We have conflicts, show the dialog
      setConflicts(foundConflicts);
      setIsConflictModalOpen(true);
    } else {
      // No conflicts, save directly
      saveAssignments(assignmentChanges);
    }
  };

  const handleContinueSaveWithConflicts = () => {
    setIsConflictModalOpen(false);
    const assignmentChanges = classesTableRef.current.getAssignmentChanges();
    saveAssignments(assignmentChanges, conflicts);
  };

  const saveAssignments = async (assignments, conflictsToLog = []) => {
    try {
      // Call your API to save assignments
      console.log('Saving assignments:', assignments);
      console.log('With conflicts:', conflictsToLog);
      
      // Handle success
    } catch (error) {
      // Handle error
      console.error('Error saving assignments:', error);
    }
  };
  
  if (isLoadingDetail) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!timetable) {
    return (
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', p: 4 }}>
        <Error sx={{ fontSize: 80, color: 'gray', mb: 2 }} />
        <Typography variant="h6" color="textSecondary">Không tìm thấy lịch thi</Typography>
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

  const getStatusLabel = (status) => {
    switch (status) {
      case 'not_started':
        return 'Chưa bắt đầu';
      case 'in_progress':
        return 'Đang xếp lịch';
      case 'completed':
        return 'Hoàn thành';
      case 'conflict':
        return 'Có xung đột';
      default:
        return 'Không xác định';
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'completed':
        return 'success.main';
      case 'conflict':
        return 'error.main';
      case 'in_progress':
        return 'primary.main';
      default:
        return 'text.secondary';
    }
  };

  const handleRenameTimetable = () => {
    setIsRenameDialogOpen(true);
  };

  const handleDeleteTimetable = () => {
    setIsDeleteDialogOpen(true);
  };

  const handleViewTimetable = () => {
    history.push(`/exam-timetables/${id}/view`);
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Timetable Information Card */}
      <Card sx={{ 
        mb: 4, 
        boxShadow: 2, 
        borderRadius: 2,
        overflow: 'hidden',
        border: '1px solid #ddd',
      }}>
        {/* Header with Gradient */}
        <Box sx={{ 
          background: 'linear-gradient(90deg, #1976D2, #2196F3)', 
          color: 'white',
          p: 2,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <Typography variant="h5" fontWeight={600}>
            {timetable.name}
          </Typography>
          <Box>
            <Button 
              variant="contained" 
              color="secondary"
              startIcon={<Visibility />} 
              size="small"
              sx={{ 
                mr: 1, 
                backgroundColor: 'secondary.main', 
                '&:hover': { backgroundColor: '#FFB74D' }
              }}
              onClick={handleViewTimetable}
            >
              Xem
            </Button>

            <Button 
              variant="contained" 
              color="info" 
              startIcon={<Edit />} 
              size="small"
              sx={{ 
                mr: 1, 
                backgroundColor: 'info.main', 
                '&:hover': { backgroundColor: '#4FC3F7' }
              }}
              onClick={handleRenameTimetable}
            >
              Đổi tên
            </Button>

            <Button 
              variant="contained" 
              color="error" 
              startIcon={<Delete />} 
              size="small"
              sx={{ 
                backgroundColor: 'error.main', 
                '&:hover': { backgroundColor: '#E57373' }
              }}
              onClick={handleDeleteTimetable}
            >
              Xóa
            </Button>
          </Box>
        </Box>

        <CardContent sx={{ p: 3 }}>
          <Grid container spacing={3}>
            <Grid item xs={12} md={6}>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Typography variant="body1" fontWeight={600} color="text.primary">
                  📅 Tạo ngày: {format(new Date(timetable.createdAt), 'dd/MM/yyyy HH:mm')}
                </Typography>
              </Box>
              <Box sx={{ display: 'flex', alignItems: 'center' }}>
                <Typography variant="body1" fontWeight={500} color="text.primary">
                📅 Bắt đầu: {format(new Date(timetable.planStartTime), 'dd/MM/yyyy')}(Tuần {timetable.planStartWeek}) đến {format(new Date(timetable.planEndTime), 'dd/MM/yyyy')}
                </Typography>
              </Box>
            </Grid>
            
            <Grid item xs={12} md={6}>
              <Box sx={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', height: '100%' }}>
                <Box sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center',
                  mr: 4
                }}>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <School sx={{ color: 'primary.main', mr: 1 }} />
                    <Typography variant="h6" fontWeight={600} color="primary.main">
                      {timetable.assignedClasses}/{timetable.totalClasses}
                    </Typography>
                  </Box>
                  <Typography variant="body2" color="text.secondary">
                    Lớp đã xếp
                  </Typography>
                </Box>
                
                <Box sx={{ 
                  display: 'flex', 
                  flexDirection: 'column', 
                  alignItems: 'center',
                  p: 1,
                  borderRadius: 1,
                  bgcolor: timetable.status === 'completed' ? '#e8f5e9' : 
                           timetable.status === 'conflict' ? '#ffebee' : 
                           timetable.status === 'in_progress' ? '#e3f2fd' : '#f5f5f5'
                }}>
                  {timetable.status === 'completed' ? (
                    <CheckCircle sx={{ color: 'success.main' }} />
                  ) : timetable.status === 'conflict' ? (
                    <Error sx={{ color: 'error.main' }} />
                  ) : (
                    <AssignmentTurnedIn sx={{ color: 'primary.main' }} />
                  )}
                  <Typography variant="body2" fontWeight={500} sx={{ color: getStatusColor(timetable.status) }}>
                    {getStatusLabel(timetable.status)}
                  </Typography>
                </Box>
              </Box>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Classes Table */}
      <Paper 
        elevation={2} 
        sx={{ 
          borderRadius: 2, 
          overflow: 'hidden',
          height: 'calc(100vh - 100px)', // Adjustable based on your layout
          display: 'flex',
          flexDirection: 'column'
        }}
      >
        <Box sx={{ 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center',
          background: 'linear-gradient(90deg, #1976D2, #2196F3)',
          color: 'white',
          p: 2
        }}>
          <Typography variant="h6" fontWeight={600}>
            Danh sách lớp thi
          </Typography>
          
          <Button
            variant="contained"
            color="success"
            startIcon={<AssignmentTurnedIn />}
            onClick={handleSaveAndCheck}
            sx={{ 
              bgcolor: 'success.main',
              '&:hover': { bgcolor: '#2E7D32' }
            }}
          >
            Lưu và kiểm tra xung đột
          </Button>
        </Box>

        <Box sx={{ flex: 1, overflow: 'hidden' }}>
          <ClassesTable 
            ref={classesTableRef}
            classesData={timetable.assignments} 
            isLoading={isLoadingDetail}
            rooms={optionsData.rooms}
            weeks={timetable.weeks}
            dates={timetable.dates}
            slots={timetable.slots}
          />

          <ConflictDialog
            open={isConflictModalOpen}
            conflicts={conflicts}
            onClose={() => setIsConflictModalOpen(false)}
            onContinue={handleContinueSaveWithConflicts}
          />  
        </Box>
      </Paper>

      <DeleteConfirmModal
        open={isDeleteDialogOpen}
        onClose={() => setIsDeleteDialogOpen(false)}
        onConfirm={() => {
          setIsDeleteDialogOpen(false);
          // This would be implemented with actual API call
          console.log("Delete confirmed");

        }}
        timetableName={timetable.name}
        isDeleting={false}
      ></DeleteConfirmModal>

      <UpdateTimetableModal
        open={isRenameDialogOpen}
        onClose={() => setIsRenameDialogOpen(false)}
        tiemtableId={timetable.id}
        onUpdateTimetable={handleRenameTimetable}
      ></UpdateTimetableModal>
    </Container>
  );
};

export default TimetableDetailPage;
