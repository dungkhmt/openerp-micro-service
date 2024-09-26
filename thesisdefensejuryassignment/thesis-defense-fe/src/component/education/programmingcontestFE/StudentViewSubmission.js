import InfoIcon from "@mui/icons-material/Info";
import ReplayIcon from "@mui/icons-material/Replay";
import { IconButton, LinearProgress } from "@mui/material";
import { request } from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import StandardTable from "component/table/StandardTable";
import { forwardRef, useEffect, useImperativeHandle, useState } from "react";
import { useTranslation } from "react-i18next";
import { Link, useParams } from "react-router-dom";
import { localeOption } from "utils/NumberFormat";

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
            pathname: `/programming-contest/contest-problem-submission-detail/${rowData["contestSubmissionId"]}`,
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
      minWidth: "128px",
      align: "left",
    },
    {
      title: "Message",
      field: "message",
      headerStyle: { textAlign: "center" },
      cellStyle: { textAlign: "center", paddingRight: 40 },
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
      headerStyle: { textAlign: "right" },
      cellStyle: { fontWeight: 500, textAlign: "right", paddingRight: 40 },
      render: (row) => `${row.point.toLocaleString("fr-FR", localeOption)}`,
    },
    {
      title: t("submissionList.language"),
      field: "sourceCodeLanguage",
      minWidth: "128px",
    },
    {
      title: t("submissionList.numTestCases"),
      field: "testCasePass",
      align: "right",
      minWidth: "150px",
    },
    { title: t("submissionList.at"), field: "createAt", minWidth: "128px" },
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
        {/* <Box paddingTop={2}>
          <Link
            to={{
              pathname: `/programming-contest/contest-problem-submission-detail/${detailLink}`,
            }}
          >
            View detail here
          </Link>
        </Box> */}
      </HustModal>
    );
  };

  return (
    <>
      {/* <Stack direction={"row"} justifyContent={"flex-end"} sx={{ mb: 2 }}>
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
      </Stack> */}
      <ModalMessage rowData={selectedRowData} />
      {loading && <LinearProgress />}
      <StandardTable
        // title={t("submissionList.title")}
        columns={columns}
        data={submissions}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            disabled: loading,
            icon: () => <ReplayIcon color={loading ? "disabled" : "primary"} />,
            tooltip: "Refresh",
            isFreeAction: true,
            onClick: (event) => {
              setLoading(true);
              setTimeout(handleRefresh, 1000);
            },
          },
        ]}
      />
    </>
  );
});

export default StudentViewSubmission;
