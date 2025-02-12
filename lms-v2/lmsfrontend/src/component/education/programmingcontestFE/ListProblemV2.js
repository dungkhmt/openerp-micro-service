import { GetApp } from "@material-ui/icons";
import AddIcon from "@material-ui/icons/Add";
import {
  Box,
  Chip,
  IconButton,
  LinearProgress,
  Tab,
  Tabs,
} from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { BASE_URL, request } from "api";
import { a11yProps } from "component/tab";
import withScreenSecurity from "component/withScreenSecurity";
import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { PROBLEM_STATUS } from "utils/constants";
import { toFormattedDateTime } from "utils/dateutils";
import { errorNoti } from "utils/notification";
import HustContainerCard from "../../common/HustContainerCard";
import StandardTable from "../../table/StandardTable";
import { TabPanelVertical } from "./TabPanel";
import { getColorLevel } from "./lib";
import FilterbyTag from "component/table/FilterbyTag";



function ListProblemV2() {
  const { keycloak } = useKeycloak();
  const [value, setValue] = useState(0);

  const [myProblems, setMyProblems] = useState([]);
  const [sharedProblems, setSharedProblems] = useState([]);
  const [allProblems, setAllProblems] = useState([]);
  const [publicProblems, setPublicProblems] = useState([]);


  const [loading, setLoading] = useState(false);




  const { t } = useTranslation("education/programmingcontest/problem");

  const ACCESS_TOKEN = keycloak?.token;

  const onSingleDownload = async (problem) => {
    try {
      const url = `${BASE_URL}/problems/${problem.problemId}/export`;
      console.log("Download URL:", url);

      const response = await fetch(url, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${ACCESS_TOKEN}`,
        },
      });

      if (!response.ok) throw new Error("Failed to download file");

      const blob = await response.blob();
      const downloadUrl = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = downloadUrl;
      a.download = `${problem.problemId}.zip`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(downloadUrl);
    } catch (error) {
      console.error("Download error:", error);
    }
  };

  const COLUMNS = [
    {
      title: "ID",
      field: "problemId",
      filtering: false,
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/manager-view-problem-detail/" +
              encodeURIComponent(rowData["problemId"]),
          }}
          /*
          onClick={(e) => {
            if (
              rowData["userId"] !== keycloak.tokenParsed.preferred_username &&
              rowData["statusId"] !== PROBLEM_STATUS.OPEN
            ) {
              errorNoti("Problem is not open", 3000);
              e.preventDefault();
            }
          }}
          */
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
    { title: t("problemName"), field: "problemName", filtering: false },
    { title: t("problemList.createdBy"), field: "userId", filtering: false },
    { title: t("problemList.createdAt"), field: "createdAt", filtering: false },
    {
      title: t("problemList.level"),
      field: "levelId",
      filtering: true,
      lookup: { 'easy': 'easy', 'medium': 'medium', 'hard': 'hard' },

      render: (rowData) => (
        <span style={{ color: getColorLevel(`${rowData.levelId}`) }}>
          {`${rowData.levelId}`}
        </span>
      ),
    },
    { title: t("problemList.status"), field: "statusId", filtering: false },

    {
      title: "Tags",
      fields: "tags",

      // Using standard MUI Table filtering function
      filtering: true,
      filterComponent: (props) => <FilterbyTag {...props} />,
      customFilterAndSearch: (term, rowData) => {
        let currentTags = rowData.tags.map(x => x.name)

        // There are two case now
        // User using global table search => term will be string
        // User using Filter by Tag function => term will be array  (coz we using Autocomplete
        // multiple checkbox as input). In either case, rowData.tags always be array
        // Solution here is to check type and handle each case seperately 

        // Using filter by tags search 
        if (Array.isArray(term)) {
          //console.log(term,  currentTags)
          return term.some(t => currentTags.includes(t.name)) || term.length === 0
        }
        // using global table search
        else if (typeof term === 'string') {
          //console.log(term,  currentTags)
          return currentTags.some(tg => {
            return tg.toLowerCase().includes(term.toLowerCase())
          })
        }
        // other case 
        else {
          return false
        }

      }

      // Customize filter component 
      // filterComponent: (props) => <FilterbyTag {...props} />
      ,
      render: (rowData) => (
        <Box>
          {rowData?.tags.length > 0 &&
            rowData.tags.map((tag) => (

              <Chip
                size="small"
                label={tag.name}
                key={tag.tagId}
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
      filtering: false,
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
  /*
  const getAllProblems = () => {
    request("get", "/teacher/all-problems", (res) => {
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
  }
  */
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
    setLoading(true);
    getProblems("/teacher/owned-problems", (data) => {
      setMyProblems(data);
      setLoading(false);
    });
  }, [getProblems]);

  useEffect(() => {
    setLoading(true);
    getProblems("/teacher/shared-problems", (data) => {
      setSharedProblems(data);
      setLoading(false);
    });
  }, [getProblems]);

  useEffect(() => {
    setLoading(true);
    getProblems("/teacher/public-problems", (data) => {
      setPublicProblems(data);
      setLoading(false);
    });
  }, [getProblems]);


  /*
  useEffect(() => {
    request("get", "/grant-owner-role-problem-to-admin", (res) => {
      console.log(res.data);
    }).then();
  }, []);
  */

  /*
  useEffect(() => {
    setLoading(true);
    getProblems(
      "/teacher/all-problems",
      (data) => {
        setAllProblems(data);
        setLoading(false);
      }
    );
  }, [getProblems]);
  */
  return (
    <HustContainerCard>
      {loading && <LinearProgress />}
      <Box sx={{ borderBottom: 2, borderColor: "divider" }}>
        <Tabs
          value={value}
          onChange={(e, value) => setValue(value)}
          variant="fullWidth"
        >
          <Tab label={t("problemList.myProblems")} {...a11yProps(0)} />
          <Tab label={t("problemList.sharedProblems")} {...a11yProps(1)} />
          {<Tab label={t("problemList.publicProblems")} {...a11yProps(2)} />}
          {/*<Tab label={t("problemList.allProblems")} {...a11yProps(2)} />*/}
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
            filtering: true,
          }}
          actions={[
            {
              icon: () => {
                return <AddIcon fontSize="large" />;
              },
              tooltip: t("createProblem"),
              isFreeAction: true,
              onClick: () => {
                window.open("/programming-contest/create-problem");
              },
            },
          ]}
          sx={{ marginTop: "8px" }}
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
            filtering: true,
          }}
          sx={{ marginTop: "8px" }}
        />
      </TabPanelVertical>
      {
        <TabPanelVertical value={value} index={2}>
          <StandardTable
            title="Public Problems"
            columns={COLUMNS}
            data={publicProblems}
            hideCommandBar
            key="publicProblems"
            options={{
              selection: false,
              pageSize: 10,
              search: true,
              sorting: true,
            }}
            sx={{ marginTop: "8px" }}
          />
        </TabPanelVertical>
      }
      {/*
      <TabPanelVertical value={value} index={2}>
        <StandardTable
          title="All Problems"
          columns={COLUMNS}
          data={allProblems}
          hideCommandBar
          key="allProblems"
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
          sx={{marginTop: "8px"}}
        />
      </TabPanelVertical>
        */}

    </HustContainerCard>
  );
}

const screenName = "SCR_MANAGE_PROBLEMS";
export default withScreenSecurity(ListProblemV2, screenName, true);
