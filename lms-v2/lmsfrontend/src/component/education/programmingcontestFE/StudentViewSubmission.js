import InfoIcon from "@mui/icons-material/Info";
import ReplayIcon from "@mui/icons-material/Replay";
import {Box, IconButton, LinearProgress, Typography} from "@mui/material";
import {request} from "api";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import StandardTable from "component/table/StandardTable";
import {forwardRef, useEffect, useImperativeHandle, useState} from "react";
import {useTranslation} from "react-i18next";
import {Link, useParams} from "react-router-dom";
import {localeOption} from "utils/NumberFormat";

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
  const [comments, setComments] = useState([]);
  const [loadingComments, setLoadingComments] = useState(false);

  const getCommentsBySubmissionId = async (submissionId) => {
    setLoadingComments(true);
      const res = await request("get", `/submissions/${submissionId}/comments`);
      console.log(res.data); 
      setComments(res.data); 
      setLoadingComments(false);
  };

  const handleCommentClick = (rowData) => {
    setSelectedRowData(rowData);
    setOpenModalMessage(true);
    getCommentsBySubmissionId(rowData["contestSubmissionId"]);
  };

  const getSubmissions = async () => {
      let requestUrl = "";
      if (problemId !== "") {
        requestUrl = "/contests/users/submissions?contestid=" + contestId + "&problemid=" + problemId;
      } else {
        requestUrl = "/contests/" + contestId + "/users/submissions";
      }
      const res = await request("get", requestUrl);
      setSubmissions(res.data.content);
      setLoading(false);
  };

  useEffect(() => {
    getSubmissions();
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
          case "Accepted":
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
          onClick={() => handleCommentClick(rowData)} 
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

  const handleRefresh = () => {
    setLoading(true);
    getSubmissions();
  };

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
      if (rowData["contestSubmissionId"]) detailLink = rowData["contestSubmissionId"];
    }

    return (
      <HustModal
        open={openModalMessage}
        onClose={() => setOpenModalMessage(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock title="Response" text={message} />
        <Typography variant="h6" sx={{ mt: 2 }}>Comments:</Typography>
        <Box sx={{ maxHeight: "400px", overflowY: "auto" }}>
          {loadingComments && <LinearProgress />}
          {comments.length > 0 ? (
            comments.map((comment) => (
              <Typography key={comment.id} variant="body2" sx={{ mb: 1 }}>
                <strong>{comment.username}:</strong> {comment.comment} {/* In dam ten nguoi comment */}
              </Typography>
            ))
          ) : (
            <Typography variant="body2" sx={{ mb: 1 }}>No comments available.</Typography>
          )}
        </Box>
      </HustModal>
    );
  };


  return (
    <>
      <StandardTable
        // title={t("submissionList")}
        columns={columns}
        data={submissions}
        loading={loading}
        actions={[
          {
            icon: () => <ReplayIcon />,
            tooltip: t("Refresh"),
            isFreeAction: true,
            onClick: handleRefresh,
          },
        ]}
      />
      <ModalMessage rowData={selectedRowData} />
    </>
  );
});

export default StudentViewSubmission;
