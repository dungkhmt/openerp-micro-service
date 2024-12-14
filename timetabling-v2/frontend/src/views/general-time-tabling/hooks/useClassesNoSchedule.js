import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";
import axios from "axios";

export const useClassesNoSchedule = (group, semester) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);
  const [controller, setController] = useState(new AbortController());

  useEffect(() => {
    if (semester === null) return;
    setLoading(true);
    request(
      "get",
      `/general-classes/?semester=${semester?.semester}&groupName=${
        group?.groupName || ""
      }`,
      (res) => {
        let generalClasses = [];
        res?.data?.forEach((classObj) => {
          delete classObj.timeSlots;
          generalClasses.push(classObj);
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
        console.error(error);
        setError(error);
        setLoading(false);
      },
      null,
      null,
      controller
    );
  }, [semester, group]);

  return { loading, error, classes, setClasses, setLoading };
};
