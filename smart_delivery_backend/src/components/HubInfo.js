import React from 'react';
import { useHubId } from '../hooks/useHubId';

export const HubInfo = () => {
  const { hubId, name, loading, error, isHubUser } = useHubId();

  if (!isHubUser) {
    return null;
  }

  if (loading) {
    return <div>Loading hub information...</div>;
  }

  if (error) {
    return <div>Error: {error}</div>;
  }

  return (
    <div>
      <h3>Hub Information</h3>
      {hubId && (
        <>
          <p>Hub ID: {hubId}</p>
          <p>Hub Name: {name}</p>
        </>
      )}
    </div>
  );
}; 