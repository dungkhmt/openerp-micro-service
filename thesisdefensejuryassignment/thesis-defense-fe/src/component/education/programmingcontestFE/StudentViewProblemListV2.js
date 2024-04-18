import DoneIcon from "@mui/icons-material/Done";
import { Box, Chip, LinearProgress } from "@mui/material";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import { localeOption } from "utils/NumberFormat";
import { request } from "../../../api";
import StandardTable from "../../table/StandardTable";

export default function StudentViewProblemList() {
  const { t } = useTranslation(
    "education/programmingcontest/studentviewcontestdetail"
  );

  const { contestId } = useParams();
  const [problems, setProblems] = useState([]);

  const [loading, setLoading] = useState(true);

  function getContestDetail() {
    request(
      "get",
      "/contests/" + contestId + "/problems/v2",
      (res) => {
        setProblems(res.data);
        for (let i = 0; i < res.data.length; i++) {
          let idSource = contestId + "-" + res.data[i].problemId + "-source";
          let tmpSource = localStorage.getItem(idSource);
          let idLanguage =
            contestId + "-" + res.data[i].problemId + "-language";
          let tmpLanguage = localStorage.getItem(idLanguage);
          if (tmpSource == null) {
            localStorage.setItem(idSource, "");
          }
          if (tmpLanguage == null) {
            localStorage.setItem(idLanguage, "CPP");
          }
        }
      },
      {}
    ).then(() => setLoading(false));
  }

  useEffect(() => {
    getContestDetail();
  }, []);

  const columns = [
    {
      title: "Code",
      field: "problemCode",
    },
    {
      title: t("problem"),
      field: "problemName",
      render: (rowData) => (
        <Link
          to={
            "/programming-contest/student-view-contest-problem-detail/" +
            contestId +
            "/" +
            rowData.problemId
          }
          style={{
            textDecoration: "none",
            color: "blue",
            cursor: "",
          }}
        >
          {rowData["problemName"]}
        </Link>
      ),
    },
    {
      title: t("level"),
      field: "levelId",
    },
    {
      title: t("maxPoint"),
      field: "maxSubmittedPoint",
      render: (rowData) => (
        <>
          {
            rowData.maxSubmittedPoint &&
              rowData.maxSubmittedPoint.toLocaleString("fr-FR", localeOption)
            // (
            //   <Chip
            //     size="small"
            //     color="primary"
            //     variant="outlined"
            //     label={rowData.maxSubmittedPoint}
            //     sx={{
            //       padding: "4px",
            //       border: "2px solid lightgray",
            //       width: "52px",
            //     }}
            //   />
            // )
          }
        </>
      ),
      align: "right",
      minWidth: 160,
    },
    {
      title: t("accepted"),
      field: "accepted",
      cellStyle: { paddingRight: 40 },
      render: (rowData) => rowData.accepted && <DoneIcon color="success" />,
      align: "center",
      minWidth: 160,
    },
    {
      title: t("tags"),
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 &&
            rowData.tags.map((tag) => (
              <Chip
                size="small"
                label={tag}
                sx={{
                  marginRight: "6px",
                  marginBottom: "6px",
                  border: "1px solid lightgray",
                  fontStyle: "italic",
                }}
              />
            ))}
        </Box>
      ),
    },
  ];
  return (
    <>
      {loading && <LinearProgress />}
      <StandardTable
        // title={t("problemList.title")}
        columns={columns}
        data={problems}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
      />
    </>
  );
}
