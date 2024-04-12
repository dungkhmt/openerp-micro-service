import { request } from "api";
import { useEffect, useState } from "react";

export const useRoomOccupations = (semester, startDate) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);
  useEffect(() => {
    if (!semester || !startDate) return;

    const fetchRoomOccupations = async () => {
      setLoading(true);
      try {
        request(
          "get",
          `/room-occupation/get-all?semester=${semester}`,
          (res) => {
            console.log(res.data);
            const formattedData = res.data?.map((timeSlot) =>
              formatTimeSlot(timeSlot, startDate)
            );
            console.log(formattedData);
            setData(formattedData);
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
    };

    fetchRoomOccupations();
  }, [semester, startDate]);

  const getTimeByPeriod = (period) => {
    switch (period) {
      case 1:
        return { hours: 6, minutes: 45 };
      case 2:
        return { hours: 7, minutes: 30 };
      case 3:
        return { hours: 8, minutes: 25 };
      case 4:
        return { hours: 9, minutes: 10 };
      case 5:
        return { hours: 10, minutes: 5 };
      case 6:
        return { hours: 10, minutes: 50 };
      case 7:
        return { hours: 12, minutes: 30 };
      case 8:
        return { hours: 13, minutes: 15 };
      case 9:
        return { hours: 14, minutes: 15 };
      case 10:
        return { hours: 15, minutes: 0 };
      case 11:
        return { hours: 15, minutes: 45 };
      case 12:
        return { hours: 16, minutes: 30 };
      default:
        return null;
    }
  };

  const formatTimeSlot = (timeSlot, startDate) => {
    const offset = timeSlot.crew === "C" ? 6 : 0;
    const start = new Date(startDate);
    const { hours: startHours, minutes: startMinutes } = getTimeByPeriod(
      timeSlot.startPeriod + offset
    );
    const { hours: endHours, minutes: endMinutes } = getTimeByPeriod(
      timeSlot.endPeriod + offset
    );
    start.setHours(startHours);
    start.setDate(
      start.getDate() + (Number(timeSlot.weekIndex) - 1) * 7 + Number(timeSlot.dayIndex) - 2
    );
    start.setMinutes(startMinutes);
    const end = new Date(startDate);
    end.setHours(endHours);
    end.setDate(
      end.getDate() + (Number(timeSlot.weekIndex) - 1) * 7 + Number(timeSlot.dayIndex) - 2
    );
    end.setMinutes(endMinutes);
    return [timeSlot.classRoom, timeSlot.classCode, start, end];
  };

  return { loading, error, data };
};
