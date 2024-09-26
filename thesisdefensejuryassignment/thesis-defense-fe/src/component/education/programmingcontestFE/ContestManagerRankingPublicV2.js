import { Box, LinearProgress } from "@mui/material";
import { useEffect, useState } from "react";
import { useParams } from "react-router";
import { localeOption } from "utils/NumberFormat";
import { request } from "../../../api";
import { errorNoti } from "../../../utils/notification";
import HustContainerCard from "../../common/HustContainerCard";
import StandardTable from "../../table/StandardTable";

export default function ContestManagerRankingPublicV2(props) {
  const { contestId } = useParams();
  const [ranking, setRanking] = useState([]);
  const [rankingDetail, setRankingDetail] = useState([]);
  const [problemIds, setProblemIds] = useState([]);
  const [loading, setLoading] = useState(false);

  function getRanking() {
    setLoading(true);
    request(
      "get",
      "contests/public-ranking/" +
        contestId +
        "?getPointForRankingType=HIGHEST",
      (res) => {
        let sortedResult = res.data.sort((a, b) => b.totalPoint - a.totalPoint);
        setRanking(sortedResult);
      },
      {
        onError: (err) => {
          errorNoti(err?.response?.data, 5000);
        },
      }
    ).then(() => setLoading(false));
  }

  useEffect(() => {
    if (ranking.length > 0) {
      const problemIdsExtracted = ranking[0].mapProblemsToPoints.map(
        (item) => item.problemId
      );
      setProblemIds(problemIdsExtracted);

      let arr = [];
      ranking.forEach((record) => {
        const convertedData = {
          ...Object.fromEntries(
            record.mapProblemsToPoints.map((item) => [
              item.problemId,
              item.point.toLocaleString("fr-FR", localeOption),
            ])
          ),
          userId: record.userId,
          fullname: record.fullname,
          totalPoint: record.totalPoint,
        };
        arr.push(convertedData);
      });
      setRankingDetail(arr);
    }
  }, [ranking]);

  const generateColumns = () => {
    let problems = [];
    if (ranking.length > 0) {
      problems = ranking[0].mapProblemsToPoints;
    }
    const columns = [
      { title: "Username", field: "userId" },
      {
        title: "Fullname",
        field: "fullname",
        render: (rankingRecord) => (
          <span style={{ width: "150px", display: "block" }}>
            <em>{`${rankingRecord.fullname}`}</em>
          </span>
        ),
      },
      {
        title: "TOTAL",
        field: "totalPoint",
        headerStyle: { textAlign: "right" },
        cellStyle: {
          fontWeight: 600,
          color: "#2e7d32",
          textAlign: "right",
          paddingRight: 40,
        },
        render: (rankingRecord) =>
          `${rankingRecord.totalPoint.toLocaleString("fr-FR", localeOption)}`,
      },
    ];

    problemIds.length > 0 &&
      problemIds.forEach((problemId) => {
        columns.push({
          title: problemId,
          field: problemId,
          headerStyle: { textAlign: "right" },
          cellStyle: { textAlign: "right", paddingRight: 40 },
        });
      });

    return columns;
  };

  useEffect(() => {
    getRanking();
  }, []);

  return (
    <HustContainerCard title={"Contest Ranking"}>
      <Box>
        {loading && <LinearProgress />}
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
        />
      </Box>
    </HustContainerCard>
  );
}
