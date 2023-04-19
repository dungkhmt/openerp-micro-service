import {Box, Divider, LinearProgress, TextField, Typography} from "@mui/material";
import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {Chart} from "react-google-charts";
import HustContainerCard from "../../common/HustContainerCard";

export default function ContestResultDistribution(props) {
  const contestId = props.contestId;
  const [ranking, setRanking] = useState([]);
  const [maxPoint, setMaxPoint] = useState(0);
  const [numberOfBins, setNumberOfBins] = useState(10);

  const [loading, setLoading] = useState(false);

  function getRanking() {
    setLoading(true);
    request(
      "get",
      "/get-ranking-contest-new/" +
      contestId +
      "?getPointForRankingType=" +
      "HIGHEST",
      (res) => {
        let data = res.data;
        setMaxPoint(Math.max(...data.map(obj => obj.totalPoint), 0))

        let arr = data.map(obj => [obj.userId, obj.totalPoint]);
        arr.unshift(['User', 'Point']);
        setRanking(arr);
      }
    ).then(() => setLoading(false));
  }

  useEffect(() => {
    getRanking();
  }, []);

  const options = {
    title: 'Score Distribution in Contest',
    legend: {position: 'none'},
    colors: ['#4285F4'],
    hAxis: {
      title: "Point range",
      titleTextStyle: {
        italic: "false"
      },
    },
    vAxis: {
      title: "Number of participants",
      titleTextStyle: {
        italic: "false"
      },
    },
    histogram: {
      bucketSize: maxPoint / numberOfBins,
      maxNumBuckets: numberOfBins,
    }
  };


  return (
    <HustContainerCard title={"Contest Result Distribution"}>
      {loading && <LinearProgress/>}
      {
        ranking.length > 0 &&
        <Box sx={{width: "100%", display: "flex", flexDirection: "row"}}>
          <Box sx={{width: "84%"}}>
            <Chart
              chartType="Histogram"
              width="100%"
              height="400px"
              data={ranking}
              options={options}
            />
          </Box>

          <Divider orientation="vertical" flexItem/>
          <Box sx ={{width: "15%", marginLeft: "18px"}}>
            <Typography
              fontWeight="600"
              variant="h6"
              component="div"
              sx={{marginBottom: "18px"}}
              color="#00acc1"
            >
              Options
            </Typography>
            <TextField
              label={"Number of bins (about)"}
              value={numberOfBins}
              type={"number"}
              onChange={(event) => {
                setNumberOfBins(event.target.value);
              }}
              sx={{width: "100%"}}
            />
          </Box>

        </Box>
      }
    </HustContainerCard>
  );
}
