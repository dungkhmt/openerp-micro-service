import React, { useState, useEffect } from "react";
import { request } from "api";
export function useFetchData(url) {
  const [data, setData] = useState();

  useEffect(() => {
    let isFetched = false;
    if (url) {
      if (!isFetched) {
        request("GET", url, (res) => {
          setData(res.data);
        });
      }
      console.log("fetching");
    }
    return () => {
      isFetched = true;
    };
  }, [url]);
  return data;
}
