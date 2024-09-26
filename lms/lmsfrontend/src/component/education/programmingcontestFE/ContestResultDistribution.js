import {Box, Divider, Grid, LinearProgress, TextField, Typography} from "@mui/material";
import React, {useEffect, useState} from "react";
import {request} from "../../../api";
import {Chart} from "react-google-charts";
import HustContainerCard from "../../common/HustContainerCard";
import {LoadingButton} from "@mui/lab";
import XLSX from "xlsx";

export default function ContestResultDistribution(props) {
  const contestId = props.contestId;
  const [ranking, setRanking] = useState([]);
  const [chartData, setChartData] = useState([]);
  const [maxPoint, setMaxPoint] = useState(0);
  const [numberOfBins, setNumberOfBins] = useState(10);

  const [scaleStart, setScaleStart] = useState(0);
  const [scaleEnd, setScaleEnd] = useState(1000);

  const defaultStats = {
    "Count": 0,
    "Highest": 0,
    "Lowest": 0,
    "Average": 0,
    "Median": 0,
    "Std Dev": 0
  }

  const [statistics, setStatistics] = useState(defaultStats);

  const [loading, setLoading] = useState(false);

  function getRanking() {
    setLoading(true);
    request(
      "get",
      "/contests/ranking/" +
      contestId +
      "?getPointForRankingType=" +
      "HIGHEST",
      (res) => {
        let data = res.data;
        setMaxPoint(Math.max(...data.map(obj => obj.totalPoint), 0))

        let arr = data.map(obj => [obj.userId, obj.totalPoint]);
        setRanking(arr);

        const chartArr = [...arr]
        chartArr.unshift(['User', 'Point']);
        setChartData(chartArr);
      }
    ).then(() => setLoading(false));
  }

  const handleScale = () => {
    const initial_points = ranking.map(item => item[1]);
    const min_point = Math.min(...initial_points);
    const max_point = Math.max(...initial_points);

    const initial_range = max_point - min_point;

    const new_range = scaleEnd - scaleStart;

    const rankingClone = [...ranking]
    rankingClone.forEach(item => {
      const old_point = item[1];
      const new_point = (old_point - min_point) * (new_range / initial_range) + scaleStart;
      item[1] = new_point;
    });

    setRanking(rankingClone);

    const chartArr = [...rankingClone]
    chartArr.unshift(['User', 'Point']);
    setChartData(chartArr);
  }

  const handleExport = () => {
    const wb = XLSX.utils.book_new();

    const ws1 = XLSX.utils.aoa_to_sheet(ranking);
    XLSX.utils.book_append_sheet(wb, ws1, "Result");

    const ws2 = XLSX.utils.json_to_sheet([statistics]);
    XLSX.utils.book_append_sheet(wb, ws2, "Insights");

    // Write the workbook to a file
    XLSX.writeFile(wb, "ContestResult-"+ contestId + ".xlsx");
  }

  useEffect(() => {
    getRanking();
  }, []);

  useEffect(() => {
    if (ranking && ranking.length > 0) {
      let insights = defaultStats;

      insights["Count"] = ranking.length;

      const points = ranking.map((row) => row[1]);

      const sum = points.reduce((acc, val) => acc + val);

      const avg = sum / points.length;
      insights["Average"] = avg.toFixed(2);

      const max = Math.max(...points);
      insights["Highest"] = max;

      const min = Math.min(...points);
      insights["Lowest"] = min;

      const sortedPoints = points.slice().sort((a, b) => a - b);
      const mid = Math.floor(sortedPoints.length / 2);
      const median =
        sortedPoints.length % 2 === 0
          ? (sortedPoints[mid - 1] + sortedPoints[mid]) / 2
          : sortedPoints[mid];
      insights["Median"] = median.toFixed(2);

      const variance =
        points.reduce((acc, val) => acc + (val - avg) ** 2, 0) / points.length;

      const stdDev = Math.sqrt(variance);
      insights["Std Dev"] = stdDev.toFixed(2);

      setStatistics(insights);
    }
  }, [ranking])

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
          <Box sx={{width: "80%"}}>
            <Chart
              chartType="Histogram"
              width="100%"
              height="380px"
              data={chartData}
              options={options}
            />
          </Box>

          <Divider orientation="vertical" flexItem/>
          <Box sx={{width: "18%", marginLeft: "18px"}}>
            <Box sx={{marginBottom: "12px", display: "flex", flexDirection: "row", justifyContent: "space-between"}}>
              <TextField
                label={"From (new)"}
                value={scaleStart}
                type={"number"}
                onChange={(event) => {
                  setScaleStart(parseFloat(event.target.value));
                }}
                sx={{marginRight: "8px"}}
              />
              <TextField
                label={"To (new)"}
                value={scaleEnd}
                type={"number"}
                onChange={(event) => {
                  setScaleEnd(parseFloat(event.target.value));
                }}
                sx={{marginRight: "12px"}}
              />
              <LoadingButton
                variant="contained"
                color={"primary"}
                onClick={handleScale}
                loading={loading}
              >
                Scale
              </LoadingButton>
            </Box>
            <Box sx={{marginBottom: "24px", display: "flex", flexDirection: "row", justifyContent: "space-between"}}>
              <LoadingButton
                variant="contained"
                color={"success"}
                onClick={handleExport}
                loading={loading}
                sx={{width: "100%"}}
              >
                Export
              </LoadingButton>
            </Box>

            <Box sx={{marginBottom: "24px"}}>
              <TextField
                label={"Number of bins (about)"}
                value={numberOfBins}
                type={"number"}
                onChange={(event) => {
                  setNumberOfBins(parseInt(event.target.value));
                }}
                sx={{width: "100%"}}
              />
            </Box>

            <HustContainerCard title={"Insights"}>
              <Grid container rowSpacing={1} columnSpacing={{xs: 1, sm: 2, md: 3}}>
                {Object.keys(statistics).map((idx) => {
                  return <>
                    <Grid item xs={8}>
                      <Typography color="#00acc1">{idx}:</Typography>
                    </Grid>
                    <Grid item xs={4}>
                      <Typography sx={{fontStyle: "italic"}}>{statistics[idx]}</Typography>
                    </Grid>
                  </>
                })}
              </Grid>
            </HustContainerCard>
          </Box>

        </Box>
      }
    </HustContainerCard>
  );
}
