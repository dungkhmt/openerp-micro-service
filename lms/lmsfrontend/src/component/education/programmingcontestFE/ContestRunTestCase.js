import ContentLoader from "react-content-loader";
import * as React from "react";
import {Typography} from "@mui/material";
import Box from "@mui/material/Box";
import {Grid} from "@material-ui/core";
import {getStatusColor} from "./lib";

export default function ContestRunTestCase(props){
  const load = props.load;
  const show = props.show;
  const submitResult = props.testCaseResult;
  if(load){
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
    if(show){
      return (
        <div>

          <Typography variant={"h4"}>
            Submit Result
          </Typography>
          <Typography variant={"h6"}>
            {submitResult != undefined ? submitResult.problemName : ""}
          </Typography>
          <Box sx={{ width: '100%', bgcolor: 'background.paper', height:"120px" , border: "1px solid black", padding: "10px", justifyItems:"center", justifySelf:"center", marginRight:10}}>
            <Grid container alignItems="center">
              <Grid item xs>
                <Typography variant="h5" >
                  status: <span  style={{color:getStatusColor(`${submitResult != undefined ? submitResult.status : ""}`)}}>{`${submitResult != undefined ? submitResult.status : ""}`}</span>
                </Typography>

              </Grid>
              <Grid item xs>
                <Typography variant="h6" align="right">
                  <b>{submitResult != undefined ? submitResult.testCasePass : ""}</b> test cases passed.
                </Typography>
              </Grid>

            </Grid>
            <Grid container alignItems="center">
              <Grid item xs>
                <Typography variant="h6" >
                  Run Time: <i>{submitResult != undefined ? submitResult.runtime : ""} ms</i><br/>
                  Memory Usage: <i>{submitResult != undefined ? submitResult.memoryUsage : ""} kb</i>
                </Typography>

              </Grid>
              <Grid item xs>
                <Typography variant="h6" align="right" >
                  point: <b>{submitResult != undefined ? submitResult.score : ""}</b>
                </Typography>

              </Grid>
            </Grid>
          </Box>
        </div>
      );
    }else{
      return (
        <div></div>
      );
    }
  }
}