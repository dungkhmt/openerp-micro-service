import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import axios from "axios";

export const useClasses = (group, semester) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);
  const [token, setToken] = useState(null);

  useEffect(() => {
    if (semester === null) return;
    setToken(null);
    setLoading(true);
    request(
      "get",
      `/general-classes/?semester=${semester?.semester}&groupName=${group?.groupName || ""}`,
      (res) => {
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  roomReservationId: timeSlot.id,
                  id: classObj.id + `-${index + 1}`,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        setClasses(generalClasses);
        setLoading(false);
        setToken(axios.CancelToken.source());
      },
      (error) => {
        toast.error(error.response.data);
        console.error(error);
        setError(error);
        setLoading(false);
        setToken(axios.CancelToken.source());
      },
      { group, semester },
      null,
      token
    );
  }, [semester, group]);

  return { loading, error, classes, setClasses, setLoading };
};
