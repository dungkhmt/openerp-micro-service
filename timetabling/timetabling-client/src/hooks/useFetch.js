import { useState, useEffect } from "react";
import { request } from "api";
/**
 * Custom hook để lấy các dữ liệu cho các trang tĩnh
 * @Input: API endpoint
 *
 */
export function useFetch(url, method = "GET") {
  const [data, setData] = useState();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);
  useEffect(() => {
    let isFetched = false;
    if (url) {
      if (!isFetched) {
        request(
          method,
          url,
          (res) => {
            setLoading(false);
            setData(res.data);
            setError(false);
          },
          {
            onError: (e) => {
              setLoading(false);
              setError(true);
            },
          }
        );
      }
    }
    return () => {
      isFetched = true;
    };
  }, [url, method]);
  return { loading, data, error };
}
