import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";

export const useClasses = (group, semester) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);
  console.log(group, semester);
  useEffect(() => {
    if (group === null && semester === null) return;
    setLoading(true);
    request(
      "get",
      `/general-classes/?semester=${semester?.semester}&group=${group?.group}`,
      (res) => {
        console.log(res?.data);
        let generalClasses = [];
        res.data?.forEach((classObj) => {
          if (classObj?.classCode !== null && classObj?.timeSlots) {
            classObj.timeSlots.forEach((timeSlot, index) => {
              const cloneObj = JSON.parse(
                JSON.stringify({
                  ...classObj,
                  ...timeSlot,
                  classCode: classObj.classCode,
                  id: classObj.id + `-${index + 1}`,
                })
              );
              delete cloneObj.timeSlots;
              generalClasses.push(cloneObj);
            });
          }
        });
        console.log(generalClasses);
        setClasses(generalClasses);
        setLoading(false);
      },
      (error) => {
        toast.error(error.response.data);
        console.error(error);
        setError(error);
        setLoading(false);
      },
      { group, semester }
    );
  }, [group, semester]);

  return { loading, error, classes, setClasses, setLoading };
};
