import { request } from "api";
import { useEffect, useState } from "react";

export const useRoomOccupations = (semester) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [data, setData] = useState(null);
  useEffect(() => {
    setLoading(true);
    request(
      "get",
      `/room-occupation/get-all?semester=${semester}`,
      (res) => {
        console.log(res);
        setData(res?.data?.map((timeSlot) => {
          switch (timeSlot?.crew) {
            case "S":
              return [
                timeSlot?.classRoom,
                timeSlot?.classCode,
                12*((timeSlot?.weekIndex-1)*7 + timeSlot?.dayIndex-1) + timeSlot?.startPeriod,
                12*((timeSlot?.weekIndex-1)*7 + timeSlot?.dayIndex-1) + timeSlot?.endPeriod,
              ]
            case "C":
              return [
                timeSlot?.classRoom,
                timeSlot?.classCode,
                12*((timeSlot?.weekIndex-1)*7 + timeSlot?.dayIndex-1) + timeSlot?.startPeriod + 6,
                12*((timeSlot?.weekIndex-1)*7 + timeSlot?.dayIndex-1) + timeSlot?.endPeriod + 6,
              ]
          }
        }));
        setLoading(false);
      },
      (error) => {
        console.log(error);
        setError(error);
      }
    );
  }, [semester]);
  return { loading, error, data };
};
