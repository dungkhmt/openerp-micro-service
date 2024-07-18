import { useMemo } from "react";
import { useLocation } from "react-router-dom";
/**
 * Custom hook để lấy query params trên URL
 *
 *
 */

const useQuery = () => {
  const { search } = useLocation();
  return useMemo(() => new URLSearchParams(search), [search]);
};

export default useQuery;
