import React, {useCallback, useEffect, useState} from "react";
import {Link} from "react-router-dom";
import {BASE_URL, request} from "../../../api";
import {useTranslation} from "react-i18next";
import {toFormattedDateTime} from "../../../utils/dateutils";
import {Box, Chip, IconButton, Tab, Tabs} from "@mui/material";
import {GetApp} from "@material-ui/icons";
import {getColorLevel} from "./lib";
import {StandardTable} from "erp-hust/lib/StandardTable";
import AddIcon from "@material-ui/icons/Add";
import {a11yProps} from "component/tab";
import {TabPanelVertical} from "./TabPanel";
import {useKeycloak} from "@react-keycloak/web";
import {PROBLEM_STATUS} from "utils/constants";
import {errorNoti} from "utils/notification";
import HustContainerCard from "../../common/HustContainerCard";

function ListProblemV2() {
  const {keycloak} = useKeycloak();
  const [value, setValue] = useState(0);
  const [myProblems, setMyProblems] = useState([]);

  const [sharedProblems, setSharedProblems] = useState([]);

  const {t} = useTranslation("education/programmingcontest/problem");

  const onSingleDownload = (problem) => {
    const form = document.createElement("form");

    form.setAttribute("method", "post");
    form.setAttribute("target", "_blank");
    form.setAttribute(
      "action",
      `${BASE_URL}/problems/${problem.problemId}/export`
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
    {title: t("problemName"), field: "problemName"},
    {title: t("problemList.createdBy"), field: "userId"},
    {title: t("problemList.createdAt"), field: "createdAt"},
    {
      title: t("problemList.level"),
      field: "levelId",
      render: (rowData) => (
        <span style={{color: getColorLevel(`${rowData.levelId}`)}}>
          {`${rowData.levelId}`}
        </span>
      ),
    },
    {title: t("problemList.status"), field: "statusId"},
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
      title: t("problemList.appearances"),
      field: "appearances",
      render: (rowData) => {
        return (
          <span style={{marginLeft: "24px"}}>{rowData.appearances}</span>
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
            <GetApp/>
          </IconButton>
        );
      },
    },
  ];

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
      "/teacher/owned-problems",
      (data) => {
        setMyProblems(data);
      }
    );
  }, [getProblems]);

  useEffect(() => {
    getProblems(
      "/teacher/shared-problems",
      (data) => {
        setSharedProblems(data);
      }
    );
  }, [getProblems]);

  return (
    <HustContainerCard>
      <Box sx={{borderBottom: 2, borderColor: "divider"}}>
        <Tabs
          value={value}
          onChange={(e, value) => setValue(value)}
          variant="fullWidth"
        >
          <Tab label={t("problemList.myProblems")} {...a11yProps(0)} />
          <Tab label={t("problemList.sharedProblems")} {...a11yProps(1)} />
        </Tabs>
      </Box>

      <TabPanelVertical value={value} index={0}>
        <StandardTable
          title="Problems"
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
                return <AddIcon fontSize="large"/>;
              },
              tooltip: t("createProblem"),
              isFreeAction: true,
              onClick: () => {
                window.open("/programming-contest/create-problem");
              },
            },
          ]}
          sx={{marginTop: "8px"}}
        />
      </TabPanelVertical>
      <TabPanelVertical value={value} index={1}>
        <StandardTable
          title="Problems"
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
          sx={{marginTop: "8px"}}
        />
      </TabPanelVertical>
    </HustContainerCard>
  );
}

export default ListProblemV2;
