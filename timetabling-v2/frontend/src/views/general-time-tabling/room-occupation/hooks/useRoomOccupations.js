import { request } from "api";
import { useCallback, useEffect, useState } from "react";

export const useRoomOccupations = (semester, weekIndex) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState([]);

  const mergePeriods = (periods) => {
    // Sort periods by start time
    const sortedPeriods = periods
      .sort((a, b) => a.start - b.start);

    const mergedPeriods = [];
    let current = { ...sortedPeriods[0] };

    for (let i = 1; i < sortedPeriods.length; i++) {
      const period = sortedPeriods[i];
      if (period.start < current.start + current.duration) {
        // Overlapping periods, merge them
        const end = Math.max(
          current.start + current.duration,
          period.start + period.duration
        );
        current.duration = end - current.start;
        current.classCode += `,${period.classCode}`;
      } else {
        // No overlap, push the current period and update the current period
        mergedPeriods.push(current);
        current = { ...period };
      }
    }
    // Push the last period
    mergedPeriods.push(current);

    return mergedPeriods;
  };

  const convertSchedule = (schedule) => {
    const periodsMap = {};

    schedule.forEach((item) => {
      const { classRoom, classCode, startPeriod, endPeriod, dayIndex, crew } =
        item;
      const dayOffset = (dayIndex - 2) * 6; // Convert dayIndex to zero-based and calculate offset

      const start = dayOffset + startPeriod - 1; // Calculate start index
      const duration = endPeriod - startPeriod + 1; // Calculate duration

      // Initialize room entry if not present
      if (!periodsMap[classRoom]) {
        periodsMap[classRoom] = [];
      }

      // Add period to the respective room
      periodsMap[classRoom].push({ start, duration, classCode, crew });
    });

    // Convert the map to the desired array format
    return Object.entries(periodsMap).map(([room, periods]) => ({
      room,
      periods: mergePeriods(periods),
    }));
  };

  const fetchRoomOccupations = useCallback(() => {
    setLoading(true);
    try {
      request(
        "get",
        `/room-occupation/?semester=${semester}&weekIndex=${weekIndex}`,
        (res) => {
          setData(convertSchedule(res.data));
          console.log(res.data);
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
    if (!semester || weekIndex === undefined) { 
      setData([]);
      return;
    }
    fetchRoomOccupations();
  }, [semester, weekIndex]);

  return { loading, error, data, refresh: fetchRoomOccupations };
};
