import { Button, Typography } from "@mui/material";
import { useState } from "react";

const RoomScheduleScreen = () => {
  // ...replace with room-specific logic as needed...
  const [selectedRoom, setSelectedRoom] = useState(null);

  return (
    <div className="p-4">
      <Typography variant="h5" gutterBottom>
        Room Schedule
      </Typography>
      {/* Room selection controls */}
      <div className="flex flex-row gap-4 mb-4">
        {/* ... Add room selectors or other room-specific controls here ... */}
        <Button variant="outlined" onClick={() => setSelectedRoom("Room A")}>
          Chọn Phòng A
        </Button>
        <Button variant="outlined" onClick={() => setSelectedRoom("Room B")}>
          Chọn Phòng B
        </Button>
      </div>
      {/* Room schedule content */}
      <div className="border p-4 rounded">
        {/* ... Render room schedule details based on selectedRoom ... */}
        {selectedRoom ? (
          <Typography>
            Bảng xếp lịch cho {selectedRoom}
          </Typography>
        ) : (
          <Typography>Vui lòng chọn phòng để xem lịch</Typography>
        )}
      </div>
    </div>
  );
};

export default RoomScheduleScreen;
