import { request } from "api";
import { useEffect, useState, useMemo } from "react";

export const useClassrooms = (groupName, maxAmount) => {
  const [loading, setLoading] = useState(false);
  const [classrooms, setClassrooms] = useState([]);
  const [error, setError] = useState(null);

  // Memoize the request body to prevent unnecessary re-renders
  const body = useMemo(
    () => ({
      groupName: groupName || "",
      maxAmount: maxAmount || null,
    }),
    [groupName, maxAmount]
  );

  useEffect(() => {
    setLoading(true);
    request(
      "post",
      "/classroom/",
      (res) => {
        console.log(res);
        setClassrooms(res.data || []);
        setLoading(false);
      },
      (error) => {
        console.error(error);
        setError(error);
        setLoading(false);
      },
      body
    );
  }, [body]);

  return { loading, error, classrooms };
};
