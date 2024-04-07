import { useEffect, useState } from "react";
import { request } from "api";

export const useClasses = (group, semester) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);

  useEffect(() => {
    setLoading(true);
    request(
      "get",
      `/general-classes/?semester=${semester?.semester}`,
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
                  id: classObj.id + `-${index+1}`
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
        console.error(error);
        setError(error);
      },
      { group, semester }
    );
  }, [group, semester]);

  return { loading, error, classes, setClasses };
};
