import { useEffect, useState } from "react";
import { request } from "api";
import { toast } from "react-toastify";

export const useClasses = (group, semester) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);
  useEffect(() => {
    if (semester === null) return;
    if (group === null) {
      setLoading(true);
      request(
        "get",
        `/general-classes/?semester=${semester?.semester}`,
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
        },
        (error) => {
          toast.error(error.response.data);
          console.error(error);
          setError(error);
          setLoading(false);
        },
        { group, semester }
      );
    } else {
      setClasses((classes) =>
        classes.filter((generalClass) => {
          return generalClass?.groupName == group?.groupName;
        })
      );
    }
  }, [semester, group]);

  return { loading, error, classes, setClasses, setLoading };
};
