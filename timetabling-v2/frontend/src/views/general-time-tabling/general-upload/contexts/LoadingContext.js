import { createContext, useContext, useState } from 'react';

// Create a context for loading state
const LoadingContext = createContext();

// Custom hook to access loading context
export function useLoadingContext() {
  return useContext(LoadingContext);
}

// LoadingProvider component to manage loading state
export function LoadingProvider({ children }) {
  const [isLoading, setIsLoading] = useState(false);

  // Function to set loading state
  const setLoading = (loading) => {
    setIsLoading(loading);
  };

  return (
    <LoadingContext.Provider value={{ isLoading, setLoading }}>
      {children}
    </LoadingContext.Provider>
  );
}
