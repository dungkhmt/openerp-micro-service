import ContentLoader from "react-content-loader";
import {Alert} from "@material-ui/lab";
import * as React from "react";

export function SubmissionExecute(props){
  const loadSubmission = props.loadSubmission;
  const runTime = props.runTime;
  const memory = props.memory;
  const point = props.point;
  const status = props.status;
  const statusContent = props.statusContent;
  const show = props.show;

  if(show){
    if(loadSubmission){
      return(
        <ContentLoader
          speed={3}
          width={"100%"}
          height={"233px"}
          backgroundColor="#f3f3f3"
          foregroundColor="#ecebeb"
        >
          <rect x="0" y="5" rx="3" ry="3" width="100%" height="20" />
          <rect x="0" y="30" rx="3" ry="3" width="100%" height="20" />
          <rect x="0" y="55" rx="3" ry="3" width="100%" height="20" />
          <rect x="0" y="80" rx="3" ry="3" width="100%" height="20" />
          <rect x="0" y="115" rx="3" ry="3" width="100%" height="20" />
          <rect x="0" y="140" rx="3" ry="3" width="100%" height="20" />
          <rect x="0" y="165" rx="3" ry="3" width="100%" height="20" />
        </ContentLoader>
      );
    }else{
      return (
        <div>

          <SubmissionResult
            runTime={runTime}
            memory={memory}
            point={point}
            status={status}
            statusContent={statusContent}
          />
        </div>
      );
    }
  }else{
    return (
      <div></div>
    );
  }

}

function SubmissionResult(props) {
  const runTime = props.runTime;
  const memory = props.memory;
  const point = props.point;
  const status = props.status;
  const statusContent = props.statusContent;
  const getSeverity = (s) => {
    if (s == "Accept") {
      return "success";
    } else if (s == "Wrong Answer") {
      return "warning";
    } else {
      return "error";
    }
  }


  if (status == "Compile Error") {
    return (
      <div>
        <Alert severity="error">{statusContent}</Alert>
      </div>
    );
  } else {
    return (
      <div>
        <div>
          <Alert severity={getSeverity(status)}>{status}</Alert>
        </div>
        RunTime: {runTime} <br/>
        Memory: {memory} <br/>
        Point: {point} <br/>
      </div>
    );
  }


}