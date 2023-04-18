import * as React from "react";
import {useEffect, useState} from "react";
import {useParams} from "react-router-dom";
import {request} from "./Request";

import {Tab, Tabs} from "@mui/material";
import {a11yProps, TabPanelVertical} from "./TabPanel";

import ContestManagerViewListContestProblemSubmissionDetailByTestCase
  from "./ContestManagerViewListContestProblemSubmissionDetailByTestCase";
import CodeSimilarityCheck from "./CodeSimilarityCheck";
import {ContestManagerListProblem} from "./ContestManagerListProblem";
import ContestManagerListParticipant from "./ContestManagerListParticipant";
import ContestManagerListMember from "./ContestManagerListMember";
import ContestManagerListRequestingParticipant from "./ContestManagerListRequestingParticipant";
import ContestManagerListRegisteredParticipant from "./ContestManagerListRegisteredParticipant";

import ContestManagerAddMember from "./ContestManagerAddMember";
import ContestManagerAddMember2Contest from "./ContestManagerAddMember2Contest";

import ContestManagerUserSubmission from "./ContestManagerUserSubmission";
import ContestManagerRankingNew from "./ContestManagerRankingNew";
import ContestResultDistribution from "./ContestResultDistribution";
import {ContestManagerManageProblem} from "./ContestManagerManageProblem";

export function ContestManager() {
  const { contestId } = useParams();
  const [contestName, setContestName] = useState();
  const [contestTime, setContestTime] = useState();
  const [problems, setProblems] = useState([]);
  const [timeLimit, setTimeLimit] = useState();
  /*
  const [pendings, setPendings] = useState([]);
  const [pagePendingSize, setPagePendingSize] = useState(10);
  const [pageSuccessfulSize, setPageSuccessfulSize] = useState(10);
  const pageSizes = [10, 20, 50, 100, 150];
  const [totalPagePending, setTotalPagePending] = useState(0);
  const [totalPageSuccessful, setTotalPageSuccessful] = useState(0);
  const [pagePending, setPagePending] = useState(1);
  const [pageSuccessful, setPageSuccessful] = useState(1);
  const [successful, setSuccessful] = useState([]);
  const [load, setLoad] = useState(true);
  const [pageRanking, setPageRanking] = useState(1);
  const [ranking, setRanking] = useState([]);
  const [totalPageRanking, setTotalPageRanking] = useState(0);
  const [pageRankingSize, setPageRankingSize] = useState(10);

  const [searchUsers, setSearchUsers] = useState([]);
  const [pageSearchSize, setPageSearchSize] = useState(10);
  const [totalPageSearch, setTotalPageSearch] = useState(0);
  const [pageSearch, setPageSearch] = useState(1);
  const [keyword, setKeyword] = useState("");
  const [roles, setRoles] = useState([]);

  const [contestSubmissions, setContestSubmissions] = useState([]);
  const [pageSubmissionSize, setPageSubmissionSize] = useState(10);
  const [totalPageSubmission, setTotalPageSubmission] = useState(0);
  const [pageSubmission, setPageSubmission] = useState(1);
  */

  const [isProcessing, setIsProcessing] = useState(false);
  const [value, setValue] = useState(0);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  /*
  const handlePageSubmissionSizeChange = (event) => {
    setPageSubmissionSize(event.target.value);
    setPageSubmission(1);
    getSubmission(event.target.value, 1);
  };

  const handlePageSearchSizeChange = (event) => {
    setPageSearchSize(event.target.value);
    setPageSearch(1);
    searchUser(keyword, event.target.value, 1);
  };

  const handlePagePendingSizeChange = (event) => {
    setPagePendingSize(event.target.value);
    setPagePending(1);
    getUserPending(event.target.value, 1);
    // getProblemContestList();
  };

  const handlePageRankingSizeChange = (event) => {
    setPageRankingSize(event.target.value);
    setPageRanking(1);
    getRanking(event.target.value, 1);
  };

  const handlePageSuccessfulSizeChange = (event) => {
    setPageSuccessfulSize(event.target.value);
    setPageSuccessful(1);
    getUserSuccessful(event.target.value, 1);
  };

  function getSubmission(s, p) {
    request(
      "get",
      "/get-contest-submission-paging/" +
        contestId +
        "?size=" +
        s +
        "&page=" +
        (p - 1),
      (res) => {
        console.log("res submission", res.data);
        setContestSubmissions(res.data.content);
        console.log("contest submission", contestSubmissions);
        setTotalPageSubmission(res.data.totalPages);
      }
    ).then();
  }

  function getUserPending(s, p) {
    request(
      "get",
      "/get-user-register-pending-contest/" +
        contestId +
        "?size=" +
        s +
        "&page=" +
        (p - 1),
      (res) => {
        console.log("res pending", res.data);
        setPendings(res.data.contents.content);
        setTotalPagePending(res.data.contents.totalPages);
      }
    ).then();
  }

  function getUserSuccessful(s, p) {
    request(
      "get",
      "/get-user-register-successful-contest/" +
        contestId +
        "?size=" +
        s +
        "&page=" +
        (p - 1),
      (res) => {
        console.log("res pending", res.data);
        setSuccessful(res.data.contents.content);
        setTotalPageSuccessful(res.data.contents.totalPages);
      }
    ).then();
  }

  function getRanking(s, p) {
    request(
      "get",
      "/get-ranking-contest/" + contestId + "?size=" + s + "&page=" + (p - 1),
      (res) => {
        console.log("ranking ", res.data);
        setTotalPageRanking(res.data.totalPages);
        setRanking(res.data.content);
      }
    ).then();
  }

  function recalculatedRanking() {
    request("post", "/recalculate-ranking/" + contestId).then(() => {
      getRanking(pageRankingSize, pageRanking);
    });
  }

  function searchUser(keyword, s, p) {
    request(
      "get",
      "/search-user/" +
        contestId +
        "?size=" +
        s +
        "&page=" +
        (p - 1) +
        "&keyword=" +
        keyword,
      (res) => {
        console.log("res search", res);
        setSearchUsers(res.data.contents.content);
        setTotalPageSearch(res.data.contents.totalPages);
      }
    ).then();
  }

  function getRoles() {
    request("get", "/get-list-roles-contest", (res) => {
      console.log("getRoles, res.data = ", res.data);
      setRoles(res.data);
    }).then();
  }
  */

  useEffect(() => {
    request("get", "/get-contest-detail/" + contestId, (res) => {
      setContestTime(res.data.contestTime);
      setProblems(res.data.list);
      setContestName(res.data.contestName);
      setTimeLimit(res.data.contestTime);
    }).then();

    //getUserPending(pagePendingSize, 1);
    //getUserSuccessful(pageSuccessfulSize, 1);
    //getRanking(pageRankingSize, 1);
    //searchUser(keyword, pageSearchSize, 1);
    //getSubmission(pageSubmissionSize, 1);
    //getRoles();
  }, []);

  function handleRejudgeContest(event) {
    //alert("Rejudge");
    event.preventDefault();
    setIsProcessing(true);
    request(
      "post",
      "/evaluate-batch-submission-of-contest/" + contestId,
      (res) => {
        console.log("handleRejudgeContest", res.data);
        //alert("Rejudge DONE!!!");
        setIsProcessing(false);
        //setSuccessful(res.data.contents.content);
        //setTotalPageSuccessful(res.data.contents.totalPages);
      }
    ).then();
  }
  return (
    <div>
      <Tabs
        value={value}
        onChange={handleChange}
        indicatorColor={"primary"}
        autoFocus
        sx={{
          width: "100%",
          display: "inline-table",
          border: "1px solid transparent ",
          position: "relative",
          borderBottom: "none",
        }}
        // variant={"fullWidth"}
        aria-label="basic tabs example"
      >
        <Tab
          label="Contest Detail"
          {...a11yProps(0)}
          style={{ width: "10%" }}
        />
        <Tab label="List User" {...a11yProps(1)} style={{ width: "10%" }} />
        <Tab label="Register User" {...a11yProps(2)} style={{ width: "10%" }} />
        <Tab label="Add User" {...a11yProps(3)} style={{ width: "10%" }} />
        <Tab label="Ranking" {...a11yProps(4)} style={{ width: "10%" }} />
        {/*<Tab*/}
        {/*  label="User Submission (NA)"*/}
        {/*  {...a11yProps(5)}*/}
        {/*  style={{ width: "10%" }}*/}
        {/*/>*/}
        <Tab
          label="Result Distribution"
          {...a11yProps(5)}
          style={{ width: "10%" }}
        />
        <Tab
          label="User Submission"
          {...a11yProps(6)}
          style={{ width: "10%" }}
        />
        <Tab
          label="Submission Detail by TestCase"
          {...a11yProps(7)}
          style={{ width: "10%" }}
        />
        <Tab
          label="Check Code Plagiarism"
          {...a11yProps(8)}
          style={{ width: "10%" }}
        />
        <Tab
          label="Manage Problems"
          {...a11yProps(9)}
          style={{ width: "10%" }}
        />
      </Tabs>

      <TabPanelVertical value={value} index={0}>
        <ContestManagerListProblem contestId={contestId} />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={1}>
        <ContestManagerListMember contestId={contestId} />
        <ContestManagerListParticipant contestId={contestId} />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={2}>
        <ContestManagerListRegisteredParticipant contestId={contestId} />
        <ContestManagerListRequestingParticipant contestId={contestId} />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={3}>
        <ContestManagerAddMember2Contest contestId={contestId} />
        <ContestManagerAddMember contestId={contestId} />
      </TabPanelVertical>

      {/*
      <TabPanelVertical value={value} index={4}>
        <ContestManagerRanking contestId={contestId} />
      </TabPanelVertical>
      */}

      <TabPanelVertical value={value} index={4}>
        <ContestManagerRankingNew contestId={contestId} />
      </TabPanelVertical>

      {/*<TabPanelVertical value={value} index={5}>*/}
      {/*  <ContestManagerUserSubmissionNotEvaluated contestId={contestId} />*/}
      {/*</TabPanelVertical>*/}
      <TabPanelVertical value={value} index={5}>
        <ContestResultDistribution contestId={contestId} />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={6}>
        <ContestManagerUserSubmission contestId={contestId} />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={7}>
        <ContestManagerViewListContestProblemSubmissionDetailByTestCase />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={8}>
        <CodeSimilarityCheck contestId={contestId} />
      </TabPanelVertical>

      <TabPanelVertical value={value} index={9}>
        <ContestManagerManageProblem contestId={contestId} />
      </TabPanelVertical>
    </div>
  );
}
