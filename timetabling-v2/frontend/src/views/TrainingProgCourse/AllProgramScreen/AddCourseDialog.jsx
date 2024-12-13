import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  List,
  ListItem,
  ListItemText,
  Checkbox,
  TextField,
  Typography,
  IconButton,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import { request } from 'api';
import { successNoti, errorNoti } from 'utils/notification';
import { programUrl, courseUrl } from '../apiURL';

const AddCourseDialog = ({ open, onClose, programId, onCourseAdded, existingCourses }) => {
  const [availableCourses, setAvailableCourses] = useState([]);
  const [selectedCourses, setSelectedCourses] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [coursePrerequisites, setCoursePrerequisites] = useState({});

  useEffect(() => {
    if (open) {
      request("get", `${programUrl.getAvailableCourse}/${programId}`, (res) => {
        setAvailableCourses(res.data);
        const prerequisitesMap = res.data.reduce((map, course) => {
          map[course.id] = course.prerequisites || [];
          return map;
        }, {});
        setCoursePrerequisites(prerequisitesMap);
      });
    }
  }, [open, programId]);

  const handleCourseToggle = (course) => {
    setSelectedCourses((prevSelected) => {
      if (prevSelected.some(c => c.id === course.id)) {
        return prevSelected.filter(c => c.id !== course.id);
      } else {
        const newSelected = [...prevSelected, course];
        const prerequisites = coursePrerequisites[course.id] || [];
        prerequisites.forEach(prereqId => {
          if (!existingCourses.some(c => c.id === prereqId) && !newSelected.some(c => c.id === prereqId)) {
            const prereqCourse = availableCourses.find(c => c.id === prereqId);
            if (prereqCourse) {
              newSelected.push(prereqCourse);
            }
          }
        });
        return newSelected;
      }
    });
  };

  const handleRemoveCourse = (courseId) => {
    setSelectedCourses(prevSelected => prevSelected.filter(c => c.id !== courseId));
  };

  const handleAddCourses = () => {
    if (selectedCourses.length > 0) {
      const courseIds = selectedCourses.map(course => course.id);
      request(
        'post',
        `${programUrl.addCoursesToProgram}/${programId}`,
        (res) => {
          if (res.status === 200) {
            successNoti("Thêm môn học thành công!", 5000);
            onCourseAdded(selectedCourses);
            onClose();
          } else {
            errorNoti("Thêm môn học không thành công!", 5000);
          }
        },
        (error) => {
          console.error("Error adding courses:", error);
          errorNoti("Thêm môn học không thành công!", 5000);
        },
        courseIds
      );
    } else {
      errorNoti("Bạn phải chọn ít nhất 1 môn để thêm", 5000);
    }
  };

  const handleSearch = (e) => {
    setSearchText(e.target.value);
  };

  const filteredCourses = availableCourses.filter(course =>
    course.courseName.toLowerCase().includes(searchText.toLowerCase()) ||
    course.id.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
      <DialogTitle>Thêm Môn Học</DialogTitle>
      <DialogContent>
        <TextField
          fullWidth
          label="Tìm kiếm môn học"
          variant="outlined"
          value={searchText}
          onChange={handleSearch}
          margin="normal"
        />
        <Typography variant="subtitle1" gutterBottom>
          Danh sách môn học
        </Typography>
        {filteredCourses.length === 0 ? (
          <Typography variant="body1" color="textSecondary">
            Không có môn học nào để thêm.
          </Typography>
        ) : (
          <List style={{ maxHeight: '300px', overflow: 'auto' }}>
            {filteredCourses.map((course) => (
              <ListItem key={course.id} dense button onClick={() => handleCourseToggle(course)}>
                <Checkbox
                  checked={selectedCourses.some(c => c.id === course.id)}
                  edge="start"
                  size="small"
                />
                <ListItemText
                  primary={`${course.courseName} (${course.id})`}
                  secondary={coursePrerequisites[course.id]?.length > 0 ? `Tiên quyết: ${coursePrerequisites[course.id].join(", ")}` : "Không có môn tiên quyết"}
                  primaryTypographyProps={{ variant: 'body2' }}
                  secondaryTypographyProps={{ variant: 'caption' }}
                />
              </ListItem>
            ))}
          </List>
        )}
        <Typography variant="subtitle1" gutterBottom style={{ marginTop: '20px' }}>
          Môn học đã chọn
        </Typography>
        <List dense>
          {selectedCourses.map((course) => (
            <ListItem key={course.id} secondaryAction={
              <IconButton edge="end" aria-label="delete" size="small" onClick={() => handleRemoveCourse(course.id)}>
                <DeleteIcon fontSize="small" />
              </IconButton>
            }>
              <ListItemText
                primary={`${course.courseName} (${course.id})`}
                primaryTypographyProps={{ variant: 'body2' }}
              />
            </ListItem>
          ))}
        </List>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="primary">
          Hủy
        </Button>
        <Button onClick={handleAddCourses} color="primary">
          Thêm
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AddCourseDialog;
