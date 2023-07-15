import React, { useCallback, useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { BASE_URL, request } from "../../../api";
import { useTranslation } from "react-i18next";
import { toFormattedDateTime } from "../../../utils/dateutils";
import { Box, Chip, IconButton, TablePagination } from "@mui/material";
import { GetApp } from "@material-ui/icons";
import { getColorLevel } from "./lib";
import { StandardTable } from "erp-hust/lib/StandardTable";
import AddIcon from "@material-ui/icons/Add";
import { TablePaginationActions } from "component/table/StandardTable";
import { a11yProps } from "component/tab";
import { TabPanelVertical } from "./TabPanel";
import { Tab, Tabs } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { PROBLEM_STATUS } from "utils/constants";
import { errorNoti } from "utils/notification";

function ListProblemV2() {
  const { keycloak } = useKeycloak();
  console.log(keycloak.profile);
  const [value, setValue] = useState(0);
  const [myProblems, setMyProblems] = useState([]);

  const [sharedProblems, setSharedProblems] = useState([]);

  const { t } = useTranslation("education/programmingcontest/listproblem");

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

  const COLUMNS = [
    {
      title: "ID",
      field: "problemId",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/manager-view-problem-detail/" +
              encodeURIComponent(rowData["problemId"]),
          }}
          onClick={(e) => {
            if (rowData["userId"] !== keycloak.tokenParsed.preferred_username && rowData["statusId"] !== PROBLEM_STATUS.OPEN) {
              errorNoti("Problem is not open", 3000);
              e.preventDefault();
            }
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
    { title: "Name", field: "problemName" },
    { title: "Created By", field: "userId" },
    { title: "Created At", field: "createdAt" },
    {
      title: "Level",
      field: "levelId",
      render: (rowData) => (
        <span
          style={{ color: getColorLevel(`${rowData.levelId}`) }}
        >{`${rowData.levelId}`}</span>
      ),
    },
    { title: "Status", field: "statusId" },
    {
      title: "Tags",
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 &&
            rowData.tags.map((tag) => (
              <Chip
                size="small"
                label={tag.name}
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
    {
      title: "Contests Used",
      field: "appearances",
      render: (rowData) => {
        return (
          <span style={{ marginLeft: "24px" }}>{rowData.appearances}</span>
        );
      },
    },
    {
      title: "Export",
      render: (rowData) => {
        return (
          <IconButton
            variant="contained"
            color="primary"
            onClick={() => onSingleDownload(rowData)}
          >
            <GetApp />
          </IconButton>
        );
      },
    },
  ];
  const PAGE_SIZES = [10, 20, 50];

  const getProblems = useCallback((path, setData) => {
    request("get", path, (res) => {
      const data = res.data.map((problem) => ({
        problemId: problem.problemId,
        problemName: problem.problemName,
        userId: problem.userId,
        createdAt: toFormattedDateTime(problem.createdAt),
        levelId: problem.levelId,
        tags: problem.tags,
        appearances: problem.appearances,
        statusId: problem.statusId,
      }));
      setData(data);
    }).then();
  }, []);

  useEffect(() => {
    getProblems(
      "/get-all-my-problems",
      (data) => {
        setMyProblems(data);
      }
    );
  }, [getProblems]);

  useEffect(() => {
    getProblems(
      "/get-all-shared-problems",
      (data) => {
        setSharedProblems(data);
      }
    );
  }, [getProblems]);

  return (
    <div>
      <Tabs
        value={value}
        onChange={(e, value) => setValue(value)}
        indicatorColor={"primary"}
        autoFocus
        sx={{
          width: "100%",
          display: "inline-table",
          border: "1px solid transparent ",
          position: "relative",
          borderBottom: "none",
          marginBottom: "6px",
        }}
        aria-label="basic tabs example"
      >
        <Tab label="My problems" {...a11yProps(0)} style={{ width: "11%" }} />
        <Tab
          label="Shared problems"
          {...a11yProps(1)}
          style={{ width: "11%" }}
        />
      </Tabs>

      <TabPanelVertical value={value} index={0}>
        <StandardTable
          title="My Problems"
          columns={COLUMNS}
          data={myProblems}
          hideCommandBar
          key="my-problems"
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
          actions={[
            {
              icon: () => {
                return <AddIcon fontSize="large" />;
              },
              tooltip: t("addNewProblem"),
              isFreeAction: true,
              onClick: () => {
                window.open("/programming-contest/create-problem");
              },
            },
          ]}
        />
      </TabPanelVertical>
      <TabPanelVertical value={value} index={1}>
        <StandardTable
          title="Shared Problems"
          columns={COLUMNS}
          data={sharedProblems}
          hideCommandBar
          key="sharedProblems"
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
        />
      </TabPanelVertical>
    </div>
  );
}

export default ListProblemV2;
