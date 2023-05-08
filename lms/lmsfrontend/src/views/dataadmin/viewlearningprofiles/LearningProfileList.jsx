import React from 'react';
import StudentList from "./StudentList";
import LearningStatisticResults from "./LearningStatisticResults";

export default function LearningProfileList(props) {
  return (
    <>
      <div style={{ marginBottom: '20px'}}>
        <StudentList/>
      </div>
      <div>
        <LearningStatisticResults/>
      </div>
    </>
  );
}