import InfoIcon from "@mui/icons-material/Info";
import ReplayIcon from "@mui/icons-material/Replay";
import { LoadingButton } from "@mui/lab";
import { Box, IconButton, Stack } from "@mui/material";
import StandardTable from "component/table/StandardTable";
import { forwardRef, useEffect, useImperativeHandle, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import { request } from "../../../api";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";
import HustModal from "../../common/HustModal";

const StudentViewSubmission = forwardRef((props, ref) => {
  const { t } = useTranslation(
    "education/programmingcontest/studentviewcontestdetail"
  );
  const { contestId } = useParams();
  const problemId = props?.problemId || "";
  const [submissions, setSubmissions] = useState([]);
  const [loading, setLoading] = useState(true);

  const [selectedRowData, setSelectedRowData] = useState();
  const [openModalMessage, setOpenModalMessage] = useState(false);

  const getSubmissions = async () => {
    let requestUrl = "";
    if (problemId !== "")
      requestUrl =
        "/contests/users/submissions?contestid=" +
        contestId +
        "&problemid=" +
        problemId;
    else requestUrl = "/contests/" + contestId + "/users/submissions";

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
    { title: t("problem"), field: "problemId" },
    {
      title: t("submissionList.status"),
      field: "status",
      cellStyle: (status) => {
        switch (status) {
          case "Accept":
            return { color: "green" };
          case "In Progress":
            return { color: "gold" };
          case "Pending Evaluation":
            return { color: "goldenrod" };
          case "Evaluated":
            return { color: "darkcyan" };
          default:
            return { color: "red" };
        }
      },
    },
    {
      title: "Message",
      field: "message",
      render: (rowData) => (
        <IconButton
          color="primary"
          onClick={() => {
            setSelectedRowData(rowData);
            setOpenModalMessage(true);
          }}
        >
          <InfoIcon />
        </IconButton>
      ),
    },
    {
      title: t("submissionList.point"),
      field: "point",
      cellStyle: { fontWeight: 500 },
    },
    { title: t("submissionList.language"), field: "sourceCodeLanguage" },
    {
      title: t("submissionList.numTestCases"),
      field: "testCasePass",
      align: "center",
    },
    { title: t("submissionList.at"), field: "createAt" },
  ];

  function handleRefresh() {
    setLoading(true);
    getSubmissions();
  }

  useImperativeHandle(ref, () => ({
    refreshSubmission() {
      handleRefresh();
    },
  }));

  const ModalMessage = ({ rowData }) => {
    let message = "";
    let detailLink = "";
    if (rowData) {
      if (rowData["message"]) message = rowData["message"];
      if (rowData["contestSubmissionId"])
        detailLink = rowData["contestSubmissionId"];
    }
    return (
      <HustModal
        open={openModalMessage}
        onClose={() => setOpenModalMessage(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock title="Response" text={message} />
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
    <Box sx={{ marginTop: "20px" }}>
      <Stack direction={"row"} justifyContent={"flex-end"} sx={{ mb: 2 }}>
        <LoadingButton
          disabled={loading}
          color="primary"
          variant="contained"
          loading={loading}
          loadingPosition="start"
          startIcon={<ReplayIcon />}
          sx={{ mr: 2 }}
          onClick={() => {
            setLoading(true);
            setTimeout(handleRefresh, 2000);
          }}
        >
          <span style={{ textTransform: "none" }}>Refresh</span>
        </LoadingButton>
      </Stack>
      <ModalMessage rowData={selectedRowData} />
      <StandardTable
        title={t("submissionList.title")}
        columns={columns}
        data={submissions}
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
});

export default StudentViewSubmission;
