import React, { useState, useEffect } from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper, 
  Typography 
} from '@mui/material';

const CourseInfoPage = () => {
  const [courses, setCourses] = useState([]);

  useEffect(() => {
    // Giả sử bạn có một hàm fetchCourses để lấy dữ liệu từ API
    const fetchCourses = async () => {
      // const response = await api.getCourses();
      // setCourses(response.data);
      
      // Dữ liệu mẫu
      setCourses([
        {
          id: "CS101",
          courseName: "Introduction to Computer Science",
          credit: 3,
          status: "Active",
          createStamp: new Date("2023-01-01"),
          lastUpdated: new Date("2023-06-15"),
          prerequisites: [
            { id: "MATH101", courseName: "Basic Mathematics" },
            { id: "ENG101", courseName: "English Composition" }
          ]
        },
        // Thêm các khóa học khác ở đây
      ]);
    };

    fetchCourses();
  }, []);

  return (
    <div style={{ padding: '20px' }}>
      <Typography variant="h4" gutterBottom>
        Thông tin Khóa học
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Tên khóa học</TableCell>
              <TableCell>Tín chỉ</TableCell>
              <TableCell>Trạng thái</TableCell>
              <TableCell>Ngày tạo</TableCell>
              <TableCell>Cập nhật lần cuối</TableCell>
              <TableCell>Môn học tiên quyết</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {courses.map((course) => (
              <TableRow key={course.id}>
                <TableCell>{course.id}</TableCell>
                <TableCell>{course.courseName}</TableCell>
                <TableCell>{course.credit}</TableCell>
                <TableCell>{course.status}</TableCell>
                <TableCell>{course.createStamp.toLocaleDateString()}</TableCell>
                <TableCell>{course.lastUpdated.toLocaleDateString()}</TableCell>
                <TableCell>
                  {course.prerequisites.map(pre => pre.courseName).join(", ")}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default CourseInfoPage;