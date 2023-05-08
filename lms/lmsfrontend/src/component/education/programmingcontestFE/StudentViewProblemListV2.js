import {Box, Checkbox, Chip} from "@mui/material";
import React, {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import {Link, useParams} from "react-router-dom";
import {request} from "../../../api";
import StandardTable from "../../table/StandardTable";

export default function StudentViewProblemList() {
  const {t} = useTranslation(
    "education/programmingcontest/studentviewcontestdetail"
  );

  const {contestId} = useParams();
  const [problems, setProblems] = useState([]);

  function getContestDetail() {
    request(
      "get",
      "/get-list-contest-problem-student-V2/" + contestId,
      (res) => {
        setProblems(res.data);
        for (let i = 0; i < res.data.length; i++) {
          let idSource =
            contestId + "-" + res.data[i].problemId + "-source";
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
    );
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
          {rowData.maxSubmittedPoint != null &&
            <Chip
              size="small"
              color="primary"
              variant="outlined"
              label={rowData.maxSubmittedPoint}
              sx={{padding: "4px", border: "2px solid lightgray", width: "52px"}}
            />
          }
        </>

      ),
    },
    {
      title: t("accepted"),
      field: "accepted",
      render: (rowData) => (
        <Checkbox
          onChange={() => {}}
          checked={rowData.accepted} color="success"
        />
      ),
    },
    {
      title: "Tags",
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 && rowData.tags.map(tag =>
            <Chip
              size="small"
              label={tag}
              sx={{marginRight: "6px", marginBottom: "6px", border: "1px solid lightgray", fontStyle: "italic"}}
            />)}
        </Box>
      ),
    },
  ];
  return (
    <Box>
      <StandardTable
        title={t("problemList.title")}
        columns={columns}
        data={problems}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 10,
          search: true,
          sorting: true,
        }}
      />
    </Box>
  );
}
