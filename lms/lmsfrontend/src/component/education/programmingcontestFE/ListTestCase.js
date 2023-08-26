import {makeStyles} from "@material-ui/core/styles";
import {Button, IconButton, Typography} from "@mui/material";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
import HustModal from "component/common/HustModal";
import React, {useEffect, useState} from "react";
import {useParams} from "react-router";
import {Link, useHistory} from "react-router-dom";
import {request} from "../../../api";
import StandardTable from "../../table/StandardTable";
import InfoIcon from "@mui/icons-material/Info";
import Box from "@mui/material/Box";
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import {copyTestCasesWithPublicMode, downloadAllTestCasesWithPublicMode} from "./service/TestCaseService";
import FileCopyIcon from '@mui/icons-material/FileCopy';
import AddIcon from "@material-ui/icons/Add";
import {RiCodeSSlashLine} from "react-icons/ri";
import {errorNoti} from "../../../utils/notification";

const useStyles = makeStyles((theme) => ({}));

export default function ListTestCase() {
  const params = useParams();

  const history = useHistory();

  const problemId = params.problemId;
  const [testCases, setTestCases] = useState([]);
  const [openModalPreviewTestcase, setOpenModalPreviewTestcase] = useState(false);
  const [openModalCopyTestcase, setOpenModalCopyTestcase] = useState(false);
  const [openModalDownloadTestcase, setOpenModalDownloadTestcase] = useState(false);

  const [selectedTestcase, setSelectedTestcase] = useState();

  function getTestCases() {
    request(
      "GET",
      "/problems/" + problemId + "/testcases",

      (res) => {
        // setTestCases(res.data.filter((item) => item.isPublic === "Y"));
        setTestCases(res.data);
      },
      {}
    );
  }

  useEffect(() => {
    getTestCases();
  }, []);

  function rerunTestCase(problemId, testCaseId) {
    request(
      "post",
      "/problems/" + problemId + "/testcases/" + testCaseId + "/solution",
      () => {
        getTestCases();
      },
      {}
    );
  }

  const testcaseColumns = [
      {
        title: "Input (Preview)",
        sortable: false,
        render: (testCase) => (
          <>
            {testCase?.testCase && testCase.testCase.length > 20
              ? testCase.testCase.substring(0, 19) + "..."
              : testCase.testCase}
          </>
        )
      },
      {
        title: "Output (Preview)",
        sortable: false,
        render: (testCase) => (
          <>
            {testCase?.correctAns && testCase.correctAns.length > 20
              ? testCase.correctAns.substring(0, 19) + "..."
              : testCase.correctAns}
          </>
        )
      },
      {
        title: "Detail",
        sortable: false,
        render: (testCase) => (
          <IconButton
            color="primary"
            onClick={() => {
              setSelectedTestcase(testCase);
              setOpenModalPreviewTestcase(true);
            }}
          >
            <InfoIcon/>
          </IconButton>
        )
      },

      {title: "Point", field: "point"},
      {title: "Public", field: "isPublic"},
      {title: "Description", field: "description"},
      {title: "Status", field: "status"},
      {
        title: "Rerun",
        sortable: false,
        render: (testCase) => (
          <IconButton
            variant="contained"
            color="primary"
            onClick={() => {
              rerunTestCase(problemId, testCase.testCaseId);
            }}
          >
            <RiCodeSSlashLine/>
          </IconButton>
        ),
      },
      {
        title: "Edit",
        sortable: false,
        render: (testCase) => (
          <Link
            to={
              "/programming-contest/edit-testcase/" +
              problemId +
              "/" +
              testCase.testCaseId
            }
          >
            <IconButton variant="contained" color="success">
              <EditIcon/>
            </IconButton>
          </Link>
        ),
      },
      {
        title: "Delete",
        sortable: false,
        render: (testCase) => (
          <IconButton
            variant="contained"
            color="error"
            onClick={() => {
              request(
                "delete",
                "/testcases/" + testCase.testCaseId,
                () => {
                  request(
                    "GET",
                    "/problems/" + problemId + "/testcases",
                    (res) => {
                      setTestCases(res.data);
                    },
                    {}
                  ).then();
                },
                {onError: (e) => {
                  errorNoti(e?.response?.data?.message || "An error happened", 5000)
                }}
              ).then();
            }}
          >
            <DeleteIcon/>
          </IconButton>
        ),
      }
      ,
    ]
  ;

  const ModalPreview = (chosenTestcase) => {
    return (
      <HustModal
        open={openModalPreviewTestcase}
        onClose={() => setOpenModalPreviewTestcase(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock
          title="Input"
          text={chosenTestcase?.chosenTestcase?.testCase}
        />
        <Box sx={{height: "18px"}}/>
        <HustCopyCodeBlock
          title="Output"
          text={chosenTestcase?.chosenTestcase?.correctAns}
          mt={2}
        />
      </HustModal>
    );
  };

  const ModalCopy = () => {
    return (
      <HustModal
        open={openModalCopyTestcase}
        onClose={() => setOpenModalCopyTestcase(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
        maxWidthPaper={500}
      >
        <Typography variant="subtitle2">Do you want to copy all testcases or only public testcases?</Typography>
        <Box sx={{
          marginTop: "28px",
          marginBottom: "-24px",
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between"
        }}>
          <Button
            variant="outlined"
            onClick={() => {
              copyTestCasesWithPublicMode(testCases, false);
              setOpenModalCopyTestcase(false);
            }}
          >
            Copy all Testcases
          </Button>
          <Button
            variant="contained"
            onClick={() => {
              copyTestCasesWithPublicMode(testCases, true);
              setOpenModalCopyTestcase(false);
            }}
          >
            Copy public Testcases
          </Button>
        </Box>
      </HustModal>
    );
  };

  const ModalDownload = () => {
    return (
      <HustModal
        open={openModalDownloadTestcase}
        onClose={() => setOpenModalDownloadTestcase(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
        maxWidthPaper={580}
      >
        <Typography variant="subtitle2">Do you want to download all testcases or only public testcases?</Typography>
        <Box sx={{
          marginTop: "28px",
          marginBottom: "-24px",
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between"
        }}>
          <Button
            variant="outlined"
            onClick={() => {
              downloadAllTestCasesWithPublicMode(testCases, false);
              setOpenModalDownloadTestcase(false);
            }}
          >
             Download All Testcases
          </Button>
          <Button
            variant="contained"
            onClick={() => {
              downloadAllTestCasesWithPublicMode(testCases, true);
              setOpenModalDownloadTestcase(false);
            }}
          >
            Download Public Testcases
          </Button>
        </Box>
      </HustModal>
    );
  };

  function addTestCase() {
    history.push(
      "/programming-contest/problem-detail-create-test-case/" + problemId
    );
  }

  return (
    <Box sx={{marginTop: "36px"}}>
      <StandardTable
        title={"Problem's testcases"}
        columns={testcaseColumns}
        data={testCases}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: false,
          sorting: true,
        }}
        actions={[
          {
            icon: () => {
              return <AddIcon fontSize="large"/>;
            },
            tooltip: 'Add new Testcase',
            isFreeAction: true,
            onClick: () => addTestCase()
          },
          {
            icon: () => {
              return <FileCopyIcon/>;
            },
            tooltip: 'Copy all Testcase',
            isFreeAction: true,
            onClick: () => setOpenModalCopyTestcase(true)
          },
          {
            icon: 'download',
            tooltip: 'Download all Testcase',
            isFreeAction: true,
            onClick: () => setOpenModalDownloadTestcase(true)
          }
        ]}
      />

      <ModalPreview chosenTestcase={selectedTestcase}/>
      <ModalCopy/>
      <ModalDownload/>
    </Box>
  );
}
