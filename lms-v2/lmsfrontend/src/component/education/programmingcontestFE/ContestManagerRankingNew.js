import { Box, LinearProgress, Tooltip } from "@mui/material";
import PrimaryButton from "component/button/PrimaryButton";
import TertiaryButton from "component/button/TertiaryButton";
import { MTableToolbar } from "material-table";
import { useEffect, useState } from "react";
import { localeOption } from "utils/NumberFormat";
import XLSX from "xlsx";
import { request } from "../../../api";
import { successNoti } from "../../../utils/notification";
import HustContainerCard from "../../common/HustContainerCard";
import StandardTable from "../../table/StandardTable";

import { makeStyles } from "@material-ui/core/styles";
import ContestManagerRankingPercentageNew from "./ContestManagerRankingPercentageNew";

const useStyles = makeStyles({
  actions: {
    minWidth: 180,
  },
});

export default function ContestManagerRankingNew(props) {
  const contestId = props.contestId;
  const classes = useStyles();

  //
  const [ranking, setRanking] = useState([]);
  const [rankingDetail, setRankingDetail] = useState([]);
  const [problemIds, setProblemIds] = useState([]);
  const [loading, setLoading] = useState(false);

  const copyLinkHandler = () => {
    navigator.clipboard
      .writeText(
        window.location.host +
          "/programming-contest/public/" +
          contestId +
          "/ranking"
      )
      .then(() => successNoti("URL copied to clipboard", 1000));
  };
  const downloadHandler = (event) => {
    if (ranking.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({ wpx: 80 });
    wbcols.push({ wpx: 120 });
    let numOfProblem = ranking[0].mapProblemsToPoints.length;
    for (let i = 0; i < numOfProblem; i++) {
      wbcols.push({ wpx: 50 });
    }
    wbcols.push({ wpx: 50 });

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
      data["PERCENTAGE"] = ranking[i].stringTotalPercentagePoint;

      datas[i] = data;
    }

    var sheet = XLSX.utils.json_to_sheet(datas);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "ranking");
    XLSX.writeFile(wb, contestId + "-RANKING.xlsx");
  };

  function getRanking() {
    setLoading(true);
    request(
      "get",
      "/contests/ranking/" + contestId + "?getPointForRankingType=HIGHEST",
      (res) => {
        let sortedResult = res.data.sort((a, b) => b.totalPoint - a.totalPoint);
        setRanking(sortedResult);
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
          totalPercentagePoint: record.stringTotalPercentagePoint,
        };
        arr.push(convertedData);
      });
      setRankingDetail(arr);
    }
  }, [ranking]);

  const generateColumns = () => {
    const columns = [
      { title: "Username", field: "userId" },
      {
        title: "Fullname",
        field: "fullname",
        cellStyle: { minWidth: "170px" },
        render: (rankingRecord) => (
          <span style={{ display: "block" }}>
            <em>{`${rankingRecord.fullname}`}</em>
          </span>
        ),
      },
      {
        title: "TOTAL",
        field: "totalPoint",
        headerStyle: { textAlign: "right" },
        cellStyle: { paddingRight: 40 },
        render: (rankingRecord) => (
          <span
            style={{
              fontWeight: 600,
              color: "#2e7d32",
              width: "100%",
              display: "inline-block",
              textAlign: "right",
            }}
          >
            {`${rankingRecord.totalPoint.toLocaleString(
              "fr-FR",
              localeOption
            )}`}
          </span>
        ),
      },
      {
        title: "PERCENT",
        field: "totalPercentagePoint",
        headerStyle: { textAlign: "right" },
        cellStyle: { paddingRight: 40 },
        render: (rankingRecord) => (
          <span
            style={{
              fontWeight: 600,
              color: "#2e7d32",
              width: "100%",
              display: "inline-block",
              textAlign: "right",
            }}
          >
            {`${rankingRecord.totalPercentagePoint.toLocaleString("en-US")}`}
          </span>
        ),
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
          title={contestId}
          columns={generateColumns()}
          data={rankingDetail}
          hideCommandBar
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
          components={{
            Toolbar: (props) => (
              <MTableToolbar
                {...props}
                classes={{ actions: classes.actions }}
              />
            ),
            Action: (props) => {
              if (props.action.icon === "download") {
                return (
                  <Tooltip arrow title="Export Ranking as Excel file">
                    <PrimaryButton
                      sx={{ ml: 1 }}
                      onClick={(event) =>
                        props.action.onClick(event, props.data)
                      }
                    >
                      Export
                    </PrimaryButton>
                  </Tooltip>
                );
              } else if (props.action.icon === "getLink") {
                return (
                  <Tooltip arrow title="Get public URL to this ranking">
                    <TertiaryButton
                      variant="outlined"
                      sx={{ ml: 1 }}
                      onClick={(event) =>
                        props.action.onClick(event, props.data)
                      }
                    >
                      Get link
                    </TertiaryButton>
                  </Tooltip>
                );
              }
            },
          }}
          actions={[
            {
              icon: "download",
              isFreeAction: true,
              onClick: downloadHandler,
            },
            {
              icon: "getLink",
              isFreeAction: true,
              onClick: copyLinkHandler,
            },
          ]}
        />
      </Box>
  
    </HustContainerCard>
  );
}
