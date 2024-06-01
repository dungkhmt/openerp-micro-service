import { request } from "api";
import { useCallback, useEffect, useState } from "react";
import ReactDOMServer from "react-dom/server";
import { toast } from "react-toastify";

export const useRoomOccupations = (semester, weekIndex) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState([]);

  const convertSchedule = (schedule) => {
    const periodsMap = {};
  
    schedule.forEach((item) => {
      const { classRoom, classCode, startPeriod, endPeriod, dayIndex, crew } = item;
      const dayOffset = (dayIndex - 2) * 6; // Convert dayIndex to zero-based and calculate offset
  
      const start = dayOffset + startPeriod - 1 ; // Calculate start index
      const duration = endPeriod - startPeriod + 1; // Calculate duration
  
      // Initialize room entry if not present
      if (!periodsMap[classRoom]) {
        periodsMap[classRoom] = [];
      }
  
      // Add period to the respective room
      periodsMap[classRoom].push({ start, duration, classCode, crew });
    });
  
    // Convert the map to the desired array format
    return Object.entries(periodsMap).map(([room, periods]) => ({ room, periods }));
  };

  const fetchRoomOccupations = useCallback(() => {
    setLoading(true);
    try {
      request(
        "get",
        `/room-occupation/?semester=${semester}&weekIndex=${weekIndex}`,
        (res) => {
          toast.success("Truy vấn thành công!");
          setData(convertSchedule(res.data));
        },
        (error) => {
          console.log(error);
        }
      );
    } catch (error) {
      setError(error);
    } finally {
      setLoading(false);
    }
  }, [semester, weekIndex]);

  useEffect(() => {
    if (!semester) return;
    fetchRoomOccupations();
  }, [semester, weekIndex]);

  return { loading, error, data, refresh: fetchRoomOccupations };
};
