import { request } from "api";
import { useEffect, useState } from "react";

export const useAcademicWeeks = (selectedSemester) => {
  const [weeks, setWeeks] = useState([]);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if(selectedSemester === null) return;
    setLoading(true);
    request(
      "get",
      `/academic-weeks/?semester=${selectedSemester}`,
      (res) => {
        console.log(res.data);
        setWeeks(res.data);
        setLoading(false);
      },
      (error) => {
        console.log(error);
        setError(error);
      }
    );
  }, [selectedSemester]);
  return { isLoading, error, weeks, setWeeks };
};
