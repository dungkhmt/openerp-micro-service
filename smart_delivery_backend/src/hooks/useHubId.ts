import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchHubIdByUsername, clearHubInfo } from '../store/slices/hubSlice';
import { RootState } from '../store';

export const useHubId = () => {
  const dispatch = useDispatch();
  const { hubId, name, loading, error } = useSelector((state: RootState) => state.hub);
  const { user, isAuthenticated } = useSelector((state: RootState) => state.auth);

  useEffect(() => {
    if (isAuthenticated && user && user.role !== 'CUSTOMER') {
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