import { forwardRef, useImperativeHandle, useState, useMemo, useCallback } from 'react';
import {
  Box,
  CircularProgress,
  FormControl,
  InputAdornment,
  MenuItem,
  Select,
  TextField,
  Typography,
  Checkbox,
  Button,
  Popover,
  List,
  ListItem,
  ListItemText,
  ListItemIcon
} from '@mui/material';
import {
  CheckCircle,
  Error,
  HourglassEmpty,
  Search,
  FilterList,
  ViewColumn
} from '@mui/icons-material';
import { DataGrid, GridToolbarContainer } from '@mui/x-data-grid';
import { checkForConflicts } from '../utils/ConflictValidation';

// Custom toolbar component with column visibility control
function CustomToolbar(props) {
  const { visibleColumns, setVisibleColumns, allColumns } = props;
  const [anchorEl, setAnchorEl] = useState(null);

  const handleColumnVisibilityClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleColumnToggle = (field) => {
    if (visibleColumns.includes(field)) {
      setVisibleColumns(visibleColumns.filter(col => col !== field));
    } else {
      setVisibleColumns([...visibleColumns, field]);
    }
  };

  return (
    <GridToolbarContainer>
      <Button 
        startIcon={<ViewColumn />}
        onClick={handleColumnVisibilityClick}
        size="small"
        sx={{ ml: 1 }}
      >
        Hiển thị cột
      </Button>
      <Popover
        open={Boolean(anchorEl)}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: 'bottom',
          horizontal: 'left',
        }}
      >
        <List sx={{ width: 250 }}>
          {allColumns.map((column) => (
            <ListItem 
              key={column.field} 
              button 
              onClick={() => handleColumnToggle(column.field)}
              dense
            >
              <ListItemIcon>
                <Checkbox
                  edge="start"
                  checked={visibleColumns.includes(column.field)}
                  tabIndex={-1}
                  disableRipple
                />
              </ListItemIcon>
              <ListItemText primary={column.headerName} />
            </ListItem>
          ))}
        </List>
      </Popover>
    </GridToolbarContainer>
  );
}

/**
 * A data grid component for managing exam class assignments
 * Optimized for performance with virtualization and memoization
 */
const ClassesTable = forwardRef(({ 
  classesData, 
  isLoading,
  rooms,
  weeks,
  dates,
  slots
}, ref) => {
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [assignmentChanges, setAssignmentChanges] = useState({});
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(25);
  
  // Control which columns are visible
  const [visibleColumns, setVisibleColumns] = useState([
    'roomId', 'weekId', 'dateId', 'slotId', 'examClassId', 
    'className', 'numberOfStudents', 'status'
  ]);

  const handleSearchChange = (event) => {
    setSearchText(event.target.value);
    setPage(0); // Reset to first page on search
  };
  
  const handleStatusFilterChange = (event) => {
    setStatusFilter(event.target.value);
    setPage(0); // Reset to first page on filter change
  };
  
  const handleRoomChange = useCallback((classId, roomId) => {
    setAssignmentChanges(prev => ({
      ...prev,
      [classId]: {
        ...prev[classId],
        roomId
      }
    }));
  }, []);
  
  const handleWeekChange = useCallback((classId, weekId) => {
    // When week changes, clear date and slot since they depend on week
    setAssignmentChanges(prev => ({
      ...prev,
      [classId]: {
        ...prev[classId],
        weekId,
        dateId: '',  // Reset date when week changes
        slotId: ''   // Reset slot when week changes
      }
    }));
  }, []);
  
  const handleDateChange = useCallback((classId, dateId) => {
    setAssignmentChanges(prev => ({
      ...prev,
      [classId]: {
        ...prev[classId],
        dateId,
        slotId: ''  // Reset slot when date changes
      }
    }));
  }, []);
  
  const handleSlotChange = useCallback((classId, slotId) => {
    setAssignmentChanges(prev => ({
      ...prev,
      [classId]: {
        ...prev[classId],
        slotId
      }
    }));
  }, []);
  
  // Memoize filtered classes to prevent unnecessary re-filtering
  const filteredClasses = useMemo(() => {
    return classesData.filter(classItem => {
      // Apply search filter
      const matchesSearch = !searchText || 
        classItem.className?.toLowerCase().includes(searchText.toLowerCase()) || 
        classItem.examClassId?.toLowerCase().includes(searchText.toLowerCase()) ||
        classItem.courseId?.toLowerCase().includes(searchText.toLowerCase());
      
      // Apply status filter
      const matchesStatus = statusFilter === 'all' || classItem.status === statusFilter;
      
      return matchesSearch && matchesStatus;
    });
  }, [classesData, searchText, statusFilter]);
  
  // Use memoization to optimize cell renderers and prevent unnecessary re-renders
  const renderRoomCell = useCallback((params) => {
    const classId = params.row.id;
    const currentValue = assignmentChanges[classId]?.roomId !== undefined 
      ? assignmentChanges[classId]?.roomId 
      : params.value;
    
    return (
      <FormControl fullWidth size="small">
        <Select
          value={currentValue || ''}
          onChange={(e) => handleRoomChange(classId, e.target.value)}
          displayEmpty
          inputProps={{ 'aria-label': 'Chọn phòng thi' }}
        >
          <MenuItem value="" disabled>
            <em>Chọn phòng</em>
          </MenuItem>
          {rooms.map((room) => (
            <MenuItem key={room.id} value={room.id}>
              {room.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    );
  }, [rooms, assignmentChanges, handleRoomChange]);

  const renderWeekCell = useCallback((params) => {
    const classId = params.row.id;
    const currentValue = assignmentChanges[classId]?.weekId !== undefined 
      ? assignmentChanges[classId]?.weekId 
      : '';
      
    return (
      <FormControl fullWidth size="small">
        <Select
          value={currentValue || ''}
          onChange={(e) => handleWeekChange(classId, e.target.value)}
          displayEmpty
          inputProps={{ 'aria-label': 'Chọn tuần' }}
        >
          <MenuItem value="" disabled>
            <em>Tuần</em>
          </MenuItem>
          {weeks.map((week) => (
            <MenuItem key={week.id} value={week.id}>
              {week.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    );
  }, [weeks, assignmentChanges, handleWeekChange]);

  const renderDateCell = useCallback((params) => {
    const classId = params.row.id;
    const currentWeekId = assignmentChanges[classId]?.weekId || '';
    const currentValue = assignmentChanges[classId]?.dateId !== undefined 
      ? assignmentChanges[classId]?.dateId 
      : '';
    
    // Filter dates based on selected week
    const availableDates = currentWeekId 
      ? dates.filter(date => date.weekId === currentWeekId)
      : [];
      
    return (
      <FormControl fullWidth size="small" disabled={!currentWeekId}>
        <Select
          value={currentValue || ''}
          onChange={(e) => handleDateChange(classId, e.target.value)}
          displayEmpty
          inputProps={{ 'aria-label': 'Chọn ngày' }}
        >
          <MenuItem value="" disabled>
            <em>Ngày</em>
          </MenuItem>
          {availableDates.map((date) => (
            <MenuItem key={date.id} value={date.id}>
              {date.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    );
  }, [dates, assignmentChanges, handleDateChange]);

  const renderSlotCell = useCallback((params) => {
    const classId = params.row.id;
    const currentDateId = assignmentChanges[classId]?.dateId || '';
    const currentValue = assignmentChanges[classId]?.slotId !== undefined 
      ? assignmentChanges[classId]?.slotId 
      : '';
      
    return (
      <FormControl fullWidth size="small" disabled={!currentDateId}>
        <Select
          value={currentValue || ''}
          onChange={(e) => handleSlotChange(classId, e.target.value)}
          displayEmpty
          inputProps={{ 'aria-label': 'Chọn ca thi' }}
        >
          <MenuItem value="" disabled>
            <em>Kíp</em>
          </MenuItem>
          {slots.map((slot) => (
            <MenuItem key={slot.id} value={slot.id}>
              {slot.name}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
    );
  }, [slots, assignmentChanges, handleSlotChange]);

  const renderStatusCell = useCallback((params) => {
    const status = params.value;
    
    let icon;
    let color;
    let label;
    
    switch (status) {
      case 'assigned':
        icon = <CheckCircle fontSize="small" sx={{ color: 'success.main' }} />;
        color = 'success.main';
        label = 'Đã xếp';
        break;
      case 'unassigned':
        icon = <HourglassEmpty fontSize="small" sx={{ color: 'text.secondary' }} />;
        color = 'text.secondary';
        label = 'Chưa xếp';
        break;
      case 'conflict':
        icon = <Error fontSize="small" sx={{ color: 'error.main' }} />;
        color = 'error.main';
        label = 'Xung đột';
        break;
      default:
        icon = <HourglassEmpty fontSize="small" sx={{ color: 'text.secondary' }} />;
        color = 'text.secondary';
        label = 'Chưa xếp';
    }
    
    return (
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        {icon}
        <Typography 
          variant="body2" 
          sx={{ ml: 1, color }}
        >
          {label}
        </Typography>
      </Box>
    );
  }, []);
  
  // Generate columns for DataGrid with memoized cell renderers
  const columns = useMemo(() => [
    { 
      field: 'roomId', 
      headerName: 'Phòng thi', 
      width: 120,
      renderCell: renderRoomCell
    },
    { 
      field: 'weekId', 
      headerName: 'Tuần', 
      width: 100,
      renderCell: renderWeekCell
    },
    { 
      field: 'dateId', 
      headerName: 'Ngày', 
      width: 180,
      renderCell: renderDateCell
    },
    { 
      field: 'slotId', 
      headerName: 'Ca thi', 
      width: 120,
      renderCell: renderSlotCell
    },
    { 
      field: 'examClassId', 
      headerName: 'Mã lớp thi', 
      width: 80, 
    },
    { 
      field: 'className', 
      headerName: 'Tên lớp thi', 
      width: 120,
    },
    { 
      field: 'numberOfStudents', 
      headerName: 'Số SV', 
      width: 70,
      type: 'number'
    },
    { 
      field: 'courseId', 
      headerName: 'Mã học phần', 
      width: 120 
    },
    {
      field: "classId",
      headerName: "Mã lớp học",
      width: 100,
    },
    { 
      field: 'school', 
      headerName: 'Khoa', 
      width: 180 
    },
    {
      field: "groupId",
      headerName: "Nhóm",
      width: 70,
    },
    {
      field: "period",
      headerName: "Đợt",
      width: 50,
    },
    {
      field: "managementCode",
      headerName: "Mã quản lý",
      width: 100,
    },
    { 
      field: 'status', 
      headerName: 'Trạng thái', 
      width: 150,
      renderCell: renderStatusCell
    },
  ], [renderRoomCell, renderWeekCell, renderDateCell, renderSlotCell, renderStatusCell]);

  // Expose methods to the parent component
  useImperativeHandle(ref, () => ({
    getAssignmentChanges: () => assignmentChanges,
    checkForAssignmentConflicts: () => checkForConflicts(classesData, assignmentChanges)
  }));

  // We'll no longer need the manual pagination function as DataGrid will handle it

  return (
    <Box sx={{ height: '100%', width: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Search and Filter Bar */}
      <Box sx={{ 
        p: 2, 
        display: 'flex', 
        justifyContent: 'space-between', 
        borderBottom: '1px solid #eee'
      }}>
        <TextField
          placeholder="Tìm kiếm lớp thi..."
          value={searchText}
          onChange={handleSearchChange}
          sx={{ width: 300 }}
          size="small"
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">
                <Search fontSize="small" />
              </InputAdornment>
            ),
          }}
        />
        
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <FilterList sx={{ mr: 1, color: 'text.secondary' }} />
          <FormControl size="small" sx={{ minWidth: 150 }}>
            <Select
              value={statusFilter}
              onChange={handleStatusFilterChange}
              displayEmpty
            >
              <MenuItem value="all">Tất cả trạng thái</MenuItem>
              <MenuItem value="assigned">Đã xếp</MenuItem>
              <MenuItem value="unassigned">Chưa xếp</MenuItem>
              <MenuItem value="conflict">Xung đột</MenuItem>
            </Select>
          </FormControl>
        </Box>
      </Box>
      
      {/* Data Grid */}
      <Box sx={{ flex: 1, height: 500 }}>
        {isLoading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
            <CircularProgress />
          </Box>
        ) : (
          <DataGrid
            rows={filteredClasses}
            columns={columns}
            pagination
            pageSizeOptions={[10, 25, 50]}
            initialState={{
              pagination: { paginationModel: { pageSize: 25 } },
            }}
            rowHeight={52}
            getRowId={(row) => row.id}
            getRowClassName={() => 'datagrid-row'}
            columnBuffer={8}
            rowBuffer={100}
            density="standard"
            disableColumnFilter
            disableColumnMenu
            disableSelectionOnClick
            columnVisibilityModel={
              Object.fromEntries(columns.map(col => [col.field, visibleColumns.includes(col.field)]))
            }
            components={{
              Toolbar: CustomToolbar
            }}
            componentsProps={{
              toolbar: {
                visibleColumns,
                setVisibleColumns,
                allColumns: columns
              }
            }}
            sx={{
              height: '100%',
              width: '100%',
              border: 'none',
              '& .MuiDataGrid-columnHeaders': {
                backgroundColor: '#f5f5f5',
                fontWeight: 'bold',
              },
              '& .MuiDataGrid-cell': {
                borderBottom: '1px solid #f0f0f0',
                padding: '8px',
                overflow: 'hidden',
                whiteSpace: 'nowrap',
                textOverflow: 'ellipsis',
              },
              '& .MuiDataGrid-row:nth-of-type(even)': {
                backgroundColor: '#fafafa',
              },
              '& .MuiDataGrid-row:hover': {
                backgroundColor: '#f5f5f5',
              },
              '& .MuiDataGrid-cell:focus, & .MuiDataGrid-cell:focus-within': {
                outline: 'none',
              },
              '& .datagrid-row': {
                cursor: 'pointer',
              },
              '& .MuiDataGrid-virtualScroller': {
                overflowX: 'hidden',
                scrollbarWidth: 'thin',
                '&::-webkit-scrollbar': {
                  width: '8px',
                },
                '&::-webkit-scrollbar-track': {
                  background: '#f1f1f1',
                },
                '&::-webkit-scrollbar-thumb': {
                  backgroundColor: '#c1c1c1',
                  borderRadius: '4px',
                },
              },
            }}
          />
        )}
      </Box>
    </Box>
  );
});

export default ClassesTable;
