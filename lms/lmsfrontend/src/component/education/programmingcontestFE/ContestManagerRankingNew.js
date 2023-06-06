import {Box, LinearProgress} from "@mui/material";
import Button from "@mui/material/Button";
import {useEffect, useState} from "react";
import XLSX from "xlsx";
import HustContainerCard from "../../common/HustContainerCard";
import StandardTable from "../../table/StandardTable";
import {request} from "../../../api";

export default function ContestManagerRankingNew(props) {
  const contestId = props.contestId;
  const [ranking, setRanking] = useState([]);
  const [rankingDetail, setRankingDetail] = useState([]);
  const [problemIds, setProblemIds] = useState([]);
  const [loading, setLoading] = useState(false);

  const downloadHandler = (event) => {
    if (ranking.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({wpx: 80});
    wbcols.push({wpx: 120});
    let numOfProblem = ranking[0].mapProblemsToPoints.length;
    for (let i = 0; i < numOfProblem; i++) {
      wbcols.push({wpx: 50});
    }
    wbcols.push({wpx: 50});

    let datas = [];

    for (let i = 0; i < ranking.length; i++) {
      let data = {};
      data["Username"] = ranking[i].userId;
      data["Fullname"] = ranking[i].fullname;
      for (let j = 0; j < numOfProblem; j++) {
        const problem = ranking[i].mapProblemsToPoints[j].problemId;
        const problemPoint = ranking[i].mapProblemsToPoints[j].point;
        data[problem] = problemPoint;
      }
      data["TOTAL"] = ranking[i].totalPoint;

      datas[i] = data;
    }

    var sheet = XLSX.utils.json_to_sheet(datas);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "ranking");
    XLSX.writeFile(
      wb,
      contestId + "-RANKING.xlsx"
    );
  };

  function getRanking() {
    setLoading(true);
    request(
      "get",
      "/get-ranking-contest-new/" +
      contestId +
      "?getPointForRankingType=HIGHEST",
      (res) => {
        let sortedResult = res.data.sort((a, b) => b.totalPoint - a.totalPoint);
        setRanking(sortedResult);
      }
    ).then(() => setLoading(false));
  }

  useEffect(() => {
    if (ranking.length > 0) {
      const problemIdsExtracted = ranking[0].mapProblemsToPoints.map(item => item.problemId);
      setProblemIds(problemIdsExtracted);

      let arr = [];
      ranking.forEach(record => {
        const convertedData = {
          ...Object.fromEntries(record.mapProblemsToPoints.map(item => [item.problemId, item.point])),
          userId: record.userId,
          fullname: record.fullname,
          totalPoint: record.totalPoint
        };
        arr.push(convertedData);
      })
      setRankingDetail(arr);
    }

  }, [ranking])

  const generateColumns = () => {
    let problems = [];
    if (ranking.length > 0) {
      problems = ranking[0].mapProblemsToPoints;
    }
    const columns = [
      {title: "Username", field: "userId"},
      {
        title: "Fullname",
        field: "fullname",
        render: (rankingRecord) => (
          <span style={{width: "150px", display: "block"}}>
            <em>{`${rankingRecord.fullname}`}</em>
          </span>
        )
      },
      {
        title: "TOTAL",
        field: "totalPoint",
        render: (rankingRecord) => (
          <span style={{fontWeight: 600, color: "#2e7d32"}}>
            {`${rankingRecord.totalPoint}`}
          </span>
        )
      }
    ];

    problemIds.length > 0 && problemIds.forEach((problemId) => {
      columns.push({
        title: problemId,
        field: problemId,
      });
    });

    return columns;
  }

  useEffect(() => {
    getRanking();
  }, []);

  return (
    <HustContainerCard title={"Contest Ranking"}>

      <Box>
        {loading && <LinearProgress/>}
        <StandardTable
          title={"Contest: " + contestId}
          columns={generateColumns()}
          data={rankingDetail}
          hideCommandBar
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
          actions={[
            {
              icon: () => {
                return <Button variant="contained" onClick={downloadHandler} color="success"
                               className={"no-background-btn"}>
                  Export
                </Button>
              },
              tooltip: 'Export Ranking as Excel file',
              isFreeAction: true
            }
          ]}
        />
      </Box>
    </HustContainerCard>
  );
}
