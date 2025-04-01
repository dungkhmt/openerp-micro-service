import { request } from "api";
import { useCallback, useEffect, useState } from "react";
import { toast } from "react-toastify";

export const useRoomOccupations = (semester, selectedWeek) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState([]);

  const mergePeriods = (periods) => {
    if (!periods || periods.length === 0) return [];
    
    const sortedPeriods = [...periods].sort((a, b) => a.start - b.start);
    const mergedPeriods = [];
    let current = { ...sortedPeriods[0] };

    for (let i = 1; i < sortedPeriods.length; i++) {
      const period = sortedPeriods[i];
      if (period.start < current.start + current.duration) {
        const end = Math.max(
          current.start + current.duration,
          period.start + period.duration
        );
        current.duration = end - current.start;
        current.classCode += `,${period.classCode}`;
      } else {
        mergedPeriods.push(current);
        current = { ...period };
      }
    }
    mergedPeriods.push(current);
    return mergedPeriods;
  };

  const convertSchedule = (schedule) => {
    const periodsMap = {};

    schedule.forEach((item) => {
      const { classRoom, classCode, startPeriod, endPeriod, dayIndex, crew } = item;
      const dayOffset = (dayIndex - 2) * 6;
      const start = dayOffset + startPeriod - 1;
      const duration = endPeriod - startPeriod + 1;

      if (!periodsMap[classRoom]) {
        periodsMap[classRoom] = {
          S: [],
          C: []
        };
      }

      // Add period to the corresponding crew array (S or C)
      periodsMap[classRoom][crew].push({ 
        start, 
        duration, 
        classCode,
        crew 
      });
    });

    return Object.entries(periodsMap).map(([room, crews]) => ({
      room,
      morningPeriods: mergePeriods(crews.S),
      afternoonPeriods: mergePeriods(crews.C)
    }));
  };

  const fetchRoomOccupations = useCallback(() => {
    setLoading(true);
    try {
      request(
        "get",
        `/room-occupation/?semester=${semester}&weekIndex=${selectedWeek.weekIndex}`,
        (res) => {
          setData(convertSchedule(res.data));
          console.log(res.data);
        },
        (error) => {
          console.log(error);
          toast.error("Có lỗi khi tải dữ liệu sử dụng phòng");
        }
      );
    } catch (error) {
      setError(error);
    } finally {
      setLoading(false);
    }
  }, [semester, selectedWeek]);

  useEffect(() => {
    if (!semester || !selectedWeek) { 
      setData([]);
      return;
    }
    fetchRoomOccupations();
  }, [semester, selectedWeek]);

  return { loading, error, data, refresh: fetchRoomOccupations };
};
