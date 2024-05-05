import { CardContent, CardHeader } from "@mui/material";
import Card from "@mui/material/Card";
import Typography from "@mui/material/Typography";
import React, { useEffect, useState } from "react";
import { problems } from "./problem-list";
import Box from "@mui/material/Box";

export default function DailyProgramming() {
  const [todayProblem, setTodayProblem] = useState(problems[0]);

  useEffect(() => {
    const date = new Date().getDate();
    setTodayProblem(problems[date % problems.length]);
  }, []);

  return (
    <Card elevation={5} sx={{ borderRadius: "18px" }}>
      <CardHeader
        title={
          <Typography variant="h6" color="#00acc1">
            Challenge Problem
          </Typography>
        }
      />
      <CardContent sx={{ paddingTop: 0 }}>
        <Typography
          variant="subtitle1"
          color="#00acc1"
          paddingBottom="4px"
          sx={{ fontSize: "18px" }}
        >
          Truck trailer scheduling for transporting containers
        </Typography>

        <Box sx={{ display: "flex", paddingLeft: "20%", paddingY: "12px" }}>
          <img
            src="../../assets/img/container-transportation.png"
            alt="programming-challenge"
            width="60%"
          />
        </Box>

        <Box
          sx={{
            textAlign: "justify",
            // fontStyle: "italic",
            fontSize: "14px",
            opacity: 0.8,
          }}
        >
          <Typography>
            In the field of logistics and supply chain management, transporting
            goods between ports and warehouses is essential, and this is done
            using containers. Customers place requests for moving goods either
            from warehouses to ports or between different warehouses. In this
            process, trucks, trailers, and containers are distinct entities.
            Here's how the process typically includes:
          </Typography>
          <ol>
            <li>
              <Typography>
                A truck goes to a depot to pick up a trailer.
              </Typography>
            </li>
            <li>
              <Typography>
                The truck with trailer attached (truck-trailer) heads to another
                depot to collect an empty container.
              </Typography>
            </li>
            <li>
              <Typography>
                The truck-trailer proceeds to a warehouse to load goods into the
                container.
              </Typography>
            </li>
            <li>
              <Typography>
                Once loaded, the truck-trailer transports the container (one or
                two) with goods loaded, to another warehouse or a port. Upon
                arrival, the container is unloaded using specialized equipment.
              </Typography>
            </li>
            <li>
              <Typography>
                If this equipment is unavailable, the trailer (with the
                container still on it) is detached from the truck. The truck (or
                truck-trailer) will continue to travel for serving other
                requests.
              </Typography>
            </li>
          </ol>
        </Box>
      </CardContent>
      {/*<Divider/>*/}
      {/*<CardActions sx={{justifyContent: "flex-end"}}>*/}
      {/*  <Button*/}
      {/*    endIcon={(*/}
      {/*      <SvgIcon fontSize="small">*/}
      {/*        <ArrowRightIcon/>*/}
      {/*      </SvgIcon>*/}
      {/*    )}*/}
      {/*    size="small"*/}
      {/*    onClick={() => warningNoti("This function will be updated soon", 5000)}*/}
      {/*    sx={{color: "#00acc1"}}*/}
      {/*  >*/}
      {/*    Solve it*/}
      {/*  </Button>*/}
      {/*</CardActions>*/}
    </Card>
  );
}
