import React, {useEffect, useState} from "react";
import {request} from "api";
import {Link} from "react-router-dom";
import StandardTable from "../../table/StandardTable";
import {Box, Paper, Stack, Typography} from "@mui/material";
import {defaultDatetimeFormat} from "../../../utils/dateutils";
import {useTranslation} from "react-i18next";

export default function ContestsUsingAProblem(props) {
  const {t} = useTranslation(["education/programmingcontest/contest", "education/programmingcontest/problem", 'common']);
  const problemId = props.problemId;
  const [contests, setContests] = useState([]);
  const columns = [
    {
      title: 'ID',
      field: "contestId",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/contest-manager/" + rowData["contestId"],
          }}
        >
          {rowData["contestId"]}
        </Link>
      ),
    },
    {
      title: t("contestName"),
      field: "contestName",
    },
    {
      title: t('common:manager'),
      field: "userId",
    },
    {
      title: t("common:status"),
      field: "statusId",
    },
    {
      title: t("common:createdTime"),
      field: "createdAt",
      render: (contest) => defaultDatetimeFormat(contest.createdAt)
    },
  ];

  function getContests() {
    request("get", "problems/" + problemId + "/contests", (res) => {
      setContests(res.data);
    });
  }

  useEffect(() => {
    getContests();
  }, []);
  return (
    <Box sx={{marginTop: "36px"}}>
      <Stack direction="row" justifyContent='space-between'>
        <Typography variant="h6" sx={{mb: 1.5}}>{t("education/programmingcontest/problem:contestUsingProblem")}</Typography>
      </Stack>
      <StandardTable
        columns={columns}
        data={contests}
        hideCommandBar
        hideToolBar
        options={{
          selection: false,
          pageSize: 5,
          search: false,
          sorting: true,
        }}
        components={{
          Container: (props) => <Paper {...props} elevation={0}/>,
        }}
      />
    </Box>
  );
}
