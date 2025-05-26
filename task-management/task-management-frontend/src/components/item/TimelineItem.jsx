// CustomTimeline.jsx
import React from "react";
import Timeline from "@mui/lab/Timeline";
import TimelineItem, { timelineItemClasses } from "@mui/lab/TimelineItem";
import TimelineSeparator from "@mui/lab/TimelineSeparator";
import TimelineConnector from "@mui/lab/TimelineConnector";
import TimelineContent from "@mui/lab/TimelineContent";
import TimelineDot from "@mui/lab/TimelineDot";
import Typography from "@mui/material/Typography";
import { Paper, Box } from "@mui/material";
import { useTheme } from '@mui/material/styles';

const CustomTimeline = ({ title, items }) => {
  const theme = useTheme();

  return (
    <Paper
      sx={{
        p: {xs: 1.5, md:2},
        height: "100%",
        display: 'flex',
        flexDirection: 'column',
        boxShadow: 'none',
        border: `1px solid ${theme.palette.divider}`
      }}
    >
      <Typography
        variant="h6"
        sx={{
          mb: 1.5,
          fontWeight: 600,
          color: "text.primary",
          borderBottom: `1px solid ${theme.palette.divider}`,
          pb: 1
        }}
      >
        {title}
      </Typography>
      {items && items.length > 0 ? (
        <Box sx={{
          flexGrow: 1,
          overflowY: 'auto',
          maxHeight: {xs: 280, md: 320},
          pr: 0.5,
          '&::-webkit-scrollbar': { width: '5px' },
          '&::-webkit-scrollbar-track': { background: theme.palette.grey[100], borderRadius: '3px' },
          '&::-webkit-scrollbar-thumb': { background: theme.palette.grey[300], borderRadius: '3px' },
          '&::-webkit-scrollbar-thumb:hover': { background: theme.palette.grey[400] }
        }}>
          <Timeline
            sx={{
              p: 0,
              m:0,
              [`& .${timelineItemClasses.root}`]: {
                minHeight: 'auto', // Để item co giãn
                display: 'flex', // Đảm bảo TimelineItem là flex container
                position: 'relative', // Cần thiết cho một số trường hợp căn chỉnh separator
                '&::before': {
                  display: 'none', // Hoặc flex: 0, padding: 0 như trước
                },
              },
            }}
          >
            {items.map((item, index) => {
              let displayThruDate = item.thruDate || "Hiện tại";

              if ((item.thruDate === null || item.thruDate === undefined || item.thruDate.toLowerCase() === "present" || item.thruDate.toLowerCase() === "hiện tại")) {
                if (index > 0 && items[index - 1] && items[index - 1].fromDate) {
                  displayThruDate = items[index - 1].fromDate;
                } else if (index === 0) {
                  displayThruDate = "Hiện tại";
                }
              }

              return (
                <TimelineItem
                  key={index}
                  sx={{
                    // Không cần alignItems: 'center' ở đây nữa nếu điều chỉnh bên trong Separator
                  }}
                >
                  <TimelineSeparator
                    sx={{
                      // Quan trọng: Cho phép Separator co giãn theo chiều dọc
                      // và căn chỉnh Dot ở giữa nếu cần
                      display: 'flex',
                      flexDirection: 'column',
                      alignItems: 'center', // Căn Dot ở giữa Separator
                      // flexGrow: 1, // Thử nghiệm nếu cần Separator chiếm toàn bộ chiều cao của Item
                    }}
                  >
                    <TimelineDot
                      variant="outlined"
                      color="grey"
                      sx={{
                        borderColor: theme.palette.grey[400],
                        // Không cần margin nếu căn chỉnh bằng flex trên Separator
                      }}
                    />
                    {index < items.length - 1 && (
                      <TimelineConnector
                        sx={{
                          bgcolor: theme.palette.grey[300],
                          flexGrow: 1, // Cho phép connector lấp đầy không gian còn lại trong Separator
                          width: '2px', // Độ dày của connector
                        }}
                      />
                    )}
                  </TimelineSeparator>
                  <TimelineContent
                    sx={{
                      ml: 1.5,
                      py: '8px', // Padding dọc để tạo không gian và giúp căn chỉnh
                      // Giá trị này nên đủ lớn để nội dung không bị quá sát dot
                      display: 'flex', // Sử dụng flex để căn chỉnh nội dung bên trong content
                      flexDirection: 'column',
                      justifyContent: 'center', // Căn giữa theo chiều dọc bên trong content
                    }}
                  >
                    <Typography variant="subtitle1" sx={{ fontWeight: "600", color: "text.primary", lineHeight: 1.35 }}>
                      {item.title}
                    </Typography>
                    <Typography variant="body2" sx={{ color: "text.secondary", fontSize: '0.8rem', lineHeight: 1.35 }}>
                      {item.fromDate} - {displayThruDate}
                    </Typography>
                  </TimelineContent>
                </TimelineItem>
              );
            })}
          </Timeline>
        </Box>
      ) : (
        <Box sx={{display: 'flex', justifyContent: 'center', alignItems: 'center', flexGrow: 1, height: 100}}>
          <Typography variant="body2" color="text.secondary" sx={{mt:1}}>
            Không có lịch sử để hiển thị.
          </Typography>
        </Box>
      )}
    </Paper>
  );
};

export default CustomTimeline;
