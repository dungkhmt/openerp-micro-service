import React from 'react';

function AnalyseCV() {
  return (
    <div>
      <h1>Analyse CV</h1>
      <iframe
        src="http://localhost:8501/"
        title="Streamlit App"
        style={{ width: '100%', height: '800px', border: 'none' }}
      />
    </div>
  );
}

export default AnalyseCV;