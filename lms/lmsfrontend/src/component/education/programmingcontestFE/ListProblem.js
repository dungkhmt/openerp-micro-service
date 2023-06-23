import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {BASE_URL, request} from "../../../api";
import {useTranslation} from "react-i18next";
import {toFormattedDateTime} from "../../../utils/dateutils";
import {Box, Chip, IconButton} from "@mui/material";
import {GetApp} from "@material-ui/icons";
import {getColorLevel} from "./lib";
import {StandardTable} from "erp-hust/lib/StandardTable";
import AddIcon from "@material-ui/icons/Add";

function ListProblem() {
  const [problems, setProblems] = useState([]);

  const {t} = useTranslation("education/programmingcontest/listproblem");

  const onSingleDownload = (problem) => {
    const form = document.createElement("form");

    form.setAttribute("method", "post");
    form.setAttribute("target", "_blank");
    form.setAttribute(
      "action",
      `${BASE_URL}/export-problem/${problem.problemId}`
    );

    document.body.appendChild(form);
    form.submit();
    form.parentNode.removeChild(form);
  };

  const columns = [
    {
      title: "ID",
      field: "problemId",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/manager-view-problem-detail/" +
              rowData["problemId"],
          }}
          style={{
            textDecoration: "none",
            color: "blue",
            cursor: "",
          }}
        >
          {rowData["problemId"]}
        </Link>
      ),
    },
    {title: "Name", field: "problemName"},
    {title: "Created By", field: "userId"},
    {title: "Created At", field: "createdAt"},
    {
      title: "Level",
      field: "levelId",
      render: (rowData) => (
        <span style={{color: getColorLevel(`${rowData.levelId}`)}}>{`${rowData.levelId}`}</span>
      )
    },
    {
      title: "Tags",
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 && rowData.tags.map(tag =>
            <Chip
              size="small"
              label={tag.name}
              sx={{marginRight: "6px", marginBottom: "6px", border: "1px solid lightgray", fontStyle: "italic"}}
            />)}
        </Box>
      ),
    },
    {
      title: "Export",
      render: (rowData) => {
        return (
          <IconButton variant="contained" color="primary" onClick={() => onSingleDownload(rowData)}>
            <GetApp/>
          </IconButton>
        );
      },
    },
    {title: "Appearances in Contests", field: "appearances"},
  ];

  function getProblems() {
    request("get", "/get-all-contest-problems", (res) => {
      const data = res.data.map((problem) => ({
        problemId: problem.problemId,
        problemName: problem.problemName,
        userId: problem.userId,
        createdAt: toFormattedDateTime(problem.createdAt),
        levelId: problem.levelId,
        tags: problem.tags,
        appearances: problem.appearances,
      }));
      setProblems(data);
    }).then();
  }


  useEffect(() => {
    getProblems();
  }, []);

  return (
    <div>
      <StandardTable
        title="Problems"
        columns={columns}
        data={problems}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 20,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return <AddIcon fontSize="large"/>;
            },
            tooltip: t("addNewProblem"),
            isFreeAction: true,
            onClick: () => {
              window.open("/programming-contest/create-problem")
            }
          }
        ]}
      />
    </div>
  );
}

export default ListProblem;
