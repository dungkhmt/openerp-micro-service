import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchHubId } from '../state/authSlice';

const HubIdLoader = () => {
    const dispatch = useDispatch();
    const { user, isAuthenticated } = useSelector(state => state.auth);

    useEffect(() => {
        // Check if user is authenticated and has one of the specified roles
        if (isAuthenticated && user && user.username) {
            const role = user.role;

            // If role is one of these, fetch the hub ID
            if (role === 'SHIPPER' || role === 'COLLECTOR' || role === 'HUB_STAFF' || role === 'HUB_MANAGER') {
                dispatch(fetchHubId());
            }
        }
    }, [isAuthenticated, user, dispatch]);

    // This is a utility component, it doesn't render anything
    return null;
};

export default HubIdLoader;