import { useKeycloak } from '@react-keycloak/web';
import { useState } from 'react';
import { createContext } from 'react';

export const MyContext = createContext({});

export const AppProvider = ({children}) => {
    const { keycloak } = useKeycloak();
    const token = keycloak.tokenParsed;
    const preferred_username = token.preferred_username;
    const [tripsCreate, setTripCreate] = useState([]);
    const [truckScheduler, setTruckScheduler] = useState([]);
    const [ordersScheduler, setOrderScheduler] = useState([]);
    return (
        <MyContext.Provider value={{tripsCreate, setTripCreate, truckScheduler,
         setTruckScheduler, ordersScheduler, setOrderScheduler, preferred_username}}>
            {children}
        </MyContext.Provider>
    );
}