import React from 'react';

function AnalyseCV() {
  const apiUrl = process.env.REACT_APP_CV_SCANNER_HOST;
  console.log(apiUrl)
  return (
    <div>
      <h1>Analyse CV</h1>
      <iframe
        src= {`${apiUrl}`}
        title="Streamlit App"
        style={{ width: '100%', height: '800px', border: 'none' }}
      />
    </div>
  );
}

export default AnalyseCV;