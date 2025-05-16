import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchHubIdByUsername, clearHubInfo } from '../store/slices/hubSlice';

export const useHubId = () => {
  const dispatch = useDispatch();
  const { hubId, name, loading, error } = useSelector((state) => state.hub);
  const { isAuthenticated, user } = useSelector((state) => state.auth);

  useEffect(() => {
    if (isAuthenticated && user?.username && user?.role !== 'CUSTOMER') {
      dispatch(fetchHubIdByUsername(user.username));
    } else {
      dispatch(clearHubInfo());
    }
  }, [isAuthenticated, user, dispatch]);

  return {
    hubId,
    name,
    loading,
    error,
    isHubUser: user?.role !== 'CUSTOMER'
  };
}; 