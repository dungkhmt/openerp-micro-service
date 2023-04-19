import {Box, Button, CircularProgress, IconButton} from "@mui/material";
import InfoIcon from "@mui/icons-material/Info";
import StandardTable from "component/table/StandardTable";
import React, {forwardRef, useEffect, useImperativeHandle, useState} from "react";
import {useTranslation} from "react-i18next";
import {Link, useParams} from "react-router-dom";
import {request} from "../../../api";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";
import HustModal from "../../common/HustModal";

const commandBarStyles = {
  position: "sticky",
  top: 60,
  zIndex: 11,
  mt: -3,
  mb: 3,
};
const StudentViewSubmission = forwardRef((props, ref) => {
  const {t} = useTranslation(
    "education/programmingcontest/studentviewcontestdetail"
  );
  const {contestId} = useParams();
  const problemId = props?.problemId || "";
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);

  const [selectedRowData, setSelectedRowData] = useState();
  const [openModalMessage, setOpenModalMessage] = useState(false);

  const getSubmissions = async () => {
    let requestUrl = "";
    if (problemId !== "")
      requestUrl = "/get-contest-submission-in-problem-paging-of-a-user-and-contest?contestid=" + contestId + "&problemid=" + problemId;
    else requestUrl = "/get-contest-submission-paging-of-a-user-and-contest/" + contestId;

    await request(
      "get",
      requestUrl,
      (res) => {
        setSubmissions(res.data.content);
        setLoading(false);
      },
      {}
    );
  };

  useEffect(() => {
    getSubmissions().then((res) => {
      if (res && res.data) setSubmissions(res.data.content);
    });
  }, []);

  const columns = [
    {
      title: t("ID"),
      field: "contestSubmissionId",
      render: (rowData) => (
        <Link
          to={{
            pathname:
              "/programming-contest/contest-problem-submission-detail/" +
              rowData["contestSubmissionId"],
          }}
        >
          {rowData["contestSubmissionId"].substring(0, 6)}
        </Link>
      ),
    },
    {title: t("problem"), field: "problemId"},
    {
      title: t("submissionList.status"),
      field: "status",
      cellStyle: (status) => {
        switch (status) {
          case "Accept":
            return {color: "green"};
          case "In Progress":
            return {color: "gold"};
          case "Pending Evaluation":
            return {color: "goldenrod"};
          case "Evaluated":
            return {color: "darkcyan"};
          default:
            return {color: "red"};
        }
      },
    },
    {
      title: "Message", field: "message", render: (rowData) => (
        <IconButton
          color="primary"
          onClick={() => {
            setSelectedRowData(rowData);
            setOpenModalMessage(true);
          }}
        >
          <InfoIcon/>
        </IconButton>
      ),
    },
    {title: t("submissionList.point"), field: "point", cellStyle: {fontWeight: 500}},
    {title: t("submissionList.language"), field: "sourceCodeLanguage"},
    {
      title: t("submissionList.numTestCases"),
      field: "testCasePass",
      align: "center",
    },
    {title: t("submissionList.at"), field: "createAt"},
  ];

  function handleRefresh() {
    setLoading(true);
    getSubmissions();
  }

  useImperativeHandle(ref, () => ({
    refreshSubmission() {
      handleRefresh()
    }
  }));


  const ModalMessage = ({rowData}) => {
    let message = "";
    let detailLink = "";
    if (rowData) {
      if (rowData["message"]) message = rowData["message"];
      if (rowData["contestSubmissionId"]) detailLink = rowData["contestSubmissionId"];
    }
    return (
      <HustModal
        open={openModalMessage}
        onClose={() => setOpenModalMessage(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock
          title="Response"
          text={message}
        />
        <Box paddingTop={2}>
          <Link
            to={{
              pathname:
                "/programming-contest/contest-problem-submission-detail/" +
                detailLink,
            }}
          >
            View detail here
          </Link>
        </Box>
      </HustModal>
    );
  };

  return (
    <Box sx={{marginTop: "20px"}}>
      <div>
        <StandardTable
          title={t("submissionList.title")}
          columns={columns}
          data={submissions}
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
          sx={{
            commandBar: commandBarStyles,
          }}
          commandBarComponents={
            <>
              <Button
                variant="contained"
                disabled={loading}
                onClick={() => {
                  setLoading(true);
                  setTimeout(handleRefresh, 2000);
                }}
              >
                {" "}
                REFRESH
              </Button>
              {loading && <CircularProgress/>}
              <ModalMessage rowData={selectedRowData}/>
            </>
          }
        />
      </div>
    </Box>
  );
})

export default StudentViewSubmission;