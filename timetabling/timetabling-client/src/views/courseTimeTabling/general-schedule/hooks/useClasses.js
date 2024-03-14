import { useEffect, useState } from "react";
import { request } from "api";

export const useClasses = ({ group, semeter }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [classes, setClasses] = useState([]);

  useEffect(() => {
    setLoading(true);
    request(
      "post",
      "/class-opened/search",
      (res) => {
        console.log(res.data);
        setClasses(res.data);
        setLoading(false);
      },
      (error) => {
        console.error(error);
        setError(error);
      },
      { group, semeter }
    );
  }, [group, semeter]);

  return { loading, error, classes };
};
