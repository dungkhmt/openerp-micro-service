import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import axios from "axios";

export const useClasses = (group, semester, refresh) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);
  const [controller, setController] = useState(new AbortController());

  useEffect(() => {
    if (!semester && !group) {
      setClasses([]);
      setLoading(false);
      return;
    }
    setLoading(true);

    request(
      "get",
      `/general-classes/?semester=${semester?.semester}&groupName=${
        group?.groupName || ""
      }`,
      (res) => {
        let generalClasses = [];
        res?.data?.forEach((classObj) => {
          classObj.timeSlots.forEach((timeSlot, index) => {
            const cloneObj = JSON.parse(
              JSON.stringify({
                ...classObj,
                ...timeSlot,
                classCode: classObj.classCode,
                roomReservationId: timeSlot.id,
                id: classObj.id + `-${index + 1}`,
                crew: classObj.crew,
              })
            );
            delete cloneObj.timeSlots;
            generalClasses.push(cloneObj);
          });
        });
        setClasses(generalClasses);
        setLoading(false);
        controller.abort();
        setController(new AbortController());
      },
      (error) => {
        if (axios.isCancel(error)) {
          setController(new AbortController());
          setLoading(false);
          return console.log(error);
        }
        toast.error(error.response?.data);
        setError(error);
        setLoading(false);
      },
      null,
      null,
      controller
    );
  }, [semester, group, refresh]);

  return { loading, error, classes, setClasses, setLoading };
};
