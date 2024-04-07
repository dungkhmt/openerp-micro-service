import {Alert} from "@material-ui/lab";
import * as React from "react";

export function SubmitSuccess(props){
  const showSubmitSuccess=props.showSubmitSuccess;
  const content=props.content;
  if(!showSubmitSuccess){
    return(<div></div>);
  }else {
    return (
      <div>
        <Alert severity="success">{content}</Alert>
      </div>
    );
  }
}