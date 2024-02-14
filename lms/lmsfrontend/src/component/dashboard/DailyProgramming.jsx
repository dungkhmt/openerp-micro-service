import {Button, CardActions, CardContent, CardHeader, Divider, SvgIcon} from "@mui/material";
import Card from "@mui/material/Card";
import Typography from "@mui/material/Typography";
import {ArrowRightIcon} from "@heroicons/react/16/solid";
import React, {useEffect, useState} from "react";
import {warningNoti} from "../../utils/notification";
import {problems} from "./problem-list";

export default function DailyProgramming() {

  const [todayProblem, setTodayProblem] = useState(problems[0]);

  useEffect(() => {
    const date = new Date().getDate();
    setTodayProblem(problems[date % problems.length]);
  }, []);

  return <Card elevation={5} sx={{borderRadius: "18px"}}>
    <CardHeader
      title={<Typography variant="h6" color="#00acc1">Daily Programming Challenge</Typography>}
    />
    <CardContent sx={{paddingTop: 0}}>
      <Typography variant="subtitle1" color="#00acc1" paddingBottom="4px"
                  sx={{fontSize: "18px"}}>{todayProblem.title}</Typography>
      <Typography sx={{
        textAlign: "justify",
        fontStyle: "italic",
        fontSize: "15px",
        opacity: 0.8
      }}>{todayProblem.description}</Typography>
    </CardContent>
    <Divider/>
    <CardActions sx={{justifyContent: "flex-end"}}>
      <Button
        endIcon={(
          <SvgIcon fontSize="small">
            <ArrowRightIcon/>
          </SvgIcon>
        )}
        size="small"
        onClick={() => warningNoti("This function will be updated soon", 5000)}
        sx={{color: "#00acc1"}}
      >
        Solve it
      </Button>
    </CardActions>
  </Card>;
}