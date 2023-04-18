import {Alert} from "@material-ui/lab";
import * as React from "react";

export function SubmitWarming(props){
  const showSubmitWarming=props.showSubmitWarming;
  const content=props.content;
  if(!showSubmitWarming){
    return(<div></div>);
  }else {
    return (
      <div>
        <Alert severity="error">{content}</Alert>
      </div>
    );
  }
}