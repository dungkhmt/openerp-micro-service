import {Alert} from "@material-ui/lab";
import * as React from "react";
import {Markup} from "interweave";

export function CompileStatus(props){
  const showCompile=props.showCompile;
  const statusSuccessful=props.statusSuccessful
  const message = props.message;
  if(!showCompile){
    return(
      <div>
      <br/><br/>
    </div>);
  }else {
    if(statusSuccessful){
      return (
        <div>
          <Alert severity="success">Successful</Alert>
        </div>
      );
    }else{
      return (

        <div>
          <Alert severity="error">
            <Markup content={message}/>
          </Alert>
        </div>
      );
    }

  }
}