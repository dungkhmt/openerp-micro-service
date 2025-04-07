import React from "react";
import Timeline from "@mui/lab/Timeline";
import TimelineItem, { timelineItemClasses } from "@mui/lab/TimelineItem";
import TimelineSeparator from "@mui/lab/TimelineSeparator";
import TimelineConnector from "@mui/lab/TimelineConnector";
import TimelineContent from "@mui/lab/TimelineContent";
import TimelineDot from "@mui/lab/TimelineDot";
import Typography from "@mui/material/Typography";

const CustomTimeline = ({ title, items }) => {
  return (
    <div
      style={{
        flex: 1,
        border: "1px solid #ddd",
        borderRadius: "8px",
        padding: "20px",
        backgroundColor: "#fff",
        boxShadow: "0px 4px 8px rgba(0, 0, 0, 0.1)",
      }}
    >
      <Typography
        variant="h6"
        style={{ fontSize: "1.2rem", marginBottom: "15px", color: "#333", fontWeight: "bold" }}
      >
        {title}
      </Typography>
      <Timeline
        sx={{
          padding: "0",
          [`& .${timelineItemClasses.root}:before`]: {
            flex: 0, // Removes the opposite side space
            padding: 0,
          },
        }}
      >
        {items.map((item, index) => (
          <TimelineItem key={index}>
            <TimelineSeparator>
              <TimelineDot
                sx={{
                  backgroundColor: "#818181",
                  width: "14px",
                  height: "14px",
                }}
              />
              {index < items.length - 1 && (
                <TimelineConnector
                  sx={{
                    backgroundColor: "#818181",
                    width: "2.2px",
                  }}
                />
              )}
            </TimelineSeparator>
            <TimelineContent
              sx={{
                textAlign: "left", // Aligns text to the left
                marginLeft: "10px", // Adds space between text and dot
              }}
            >
              <Typography variant="body1" sx={{ fontWeight: "bold", color: "#333" }}>
                {item.title}
              </Typography>
              <Typography variant="body2" sx={{ color: "#555" }}>
                {item.fromDate} - {item.thruDate || "Present"}
              </Typography>
            </TimelineContent>
          </TimelineItem>
        ))}
      </Timeline>
    </div>
  );
};

export default CustomTimeline;
