import React, { useEffect, useState } from "react";
import Button from "@mui/material/Button";
import { request } from "@/api";
import toast from "react-hot-toast";

const Timesheet = () => {
  const [currentTime, setCurrentTime] = useState(new Date());
  const [checkData, setCheckData] = useState([]);
  const [checkInTime, setCheckInTime] = useState(null);
  const [checkOutTime, setCheckOutTime] = useState(null);
  const [hoursWorked, setHoursWorked] = useState("0");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (checkInTime) {
      const interval = setInterval(() => {
        const now = new Date();
        const diff = now - new Date(checkInTime);
        const hours = (diff / (1000 * 60 * 60)).toFixed(2);
        setHoursWorked(hours);
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [checkInTime]);

  const fetchData = async () => {
    const today = currentTime.toISOString().split("T")[0];
    const payload = { date: today };

    try {
      request(
        "post",
        "/get-checkinout",
        (res) => {
          const data = res.data?.data || [];
          setCheckData(data);

          if (data.length > 0) {
            const checkin = data[0];
            if (checkin?.type === "CHECKIN") {
              setCheckInTime(checkin.point_time);
            }
          }

          if (data.length > 1) {
            const last = data[data.length - 1];
            if (last?.type === "CHECKOUT") {
              setCheckOutTime(last.point_time);
            } else {
              setCheckOutTime(null);
            }
          } else {
            setCheckOutTime(null);
          }
        },
        {
          onError: (err) => {
            console.error(err);
          },
        },
        payload
      );
    } catch (err) {
      console.error(err);
    }
  };

  const handleButtonClick = () => {
    setIsLoading(true);
    try {
      request(
        "post",
        "/checkinout",
        (res) => {
          setIsLoading(false);
          fetchData();
          toast.success("Chấm công thành công");
        },
        {
          onError: (err) => {
            setIsLoading(false);
            console.error(err);
          },
        }
      );
    } catch (err) {
      setIsLoading(false);
      console.error(err);
    }
  };

  const formatTime = (timeString) => {
    const date = new Date(timeString);
    return date.toLocaleTimeString("en-US", {
      hour: "2-digit",
      minute: "2-digit",
      hour12: false,
    });
  };

  const formattedDate = currentTime.toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric",
  });

  const formattedClock = currentTime.toLocaleTimeString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
  });

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <div style={styles.header}>
          <div><strong>Timesheet</strong> {formattedDate}</div>
          <div style={styles.clock}>{formattedClock}</div>
        </div>

        <div style={styles.circle}>
          <div>{checkInTime ? `${hoursWorked} Hours` : "0 Hours"}</div>
        </div>

        <Button
          variant="contained"
          onClick={handleButtonClick}
          disabled={isLoading}
          sx={{ backgroundColor: "#006cb3", mt: 2 }}
        >
          {isLoading ? "Loading..." : checkInTime ? "Clock Out" : "Check In"}
        </Button>

        {checkInTime && (
          checkOutTime ? (
            <div style={styles.dualTimeBox}>
              <div style={styles.timeBox}>
                Checkin At<br />
                <strong>{formatTime(checkInTime)}</strong>
              </div>
              <div style={styles.timeBox}>
                Checkout At<br />
                <strong>{formatTime(checkOutTime)}</strong>
              </div>
            </div>
          ) : (
            <div style={styles.startTime}>
              Started At<br />
              <strong>{formatTime(checkInTime)}</strong>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const styles = {
  page: {
    height: "85vh",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "#f2f3f8",
  },
  container: {
    width: 500,
    padding: 20,
    border: "2px solid #eee",
    borderRadius: 12,
    textAlign: "center",
    backgroundColor: "white",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    marginBottom: 20,
  },
  clock: {
    fontWeight: "bold",
  },
  circle: {
    width: 120,
    height: 120,
    borderRadius: "50%",
    border: "4px solid #eee",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    margin: "0 auto 20px",
    fontSize: 16,
  },
  startTime: {
    marginTop: 20,
    backgroundColor: "#f9f9f9",
    padding: 10,
    borderRadius: 4,
    textAlign: "center",
  },
  dualTimeBox: {
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    gap: 20,
    marginTop: 20,
  },
  timeBox: {
    backgroundColor: "#f9f9f9",
    padding: 10,
    borderRadius: 4,
    minWidth: 180,
    textAlign: "center",
  },
};

export default Timesheet;
