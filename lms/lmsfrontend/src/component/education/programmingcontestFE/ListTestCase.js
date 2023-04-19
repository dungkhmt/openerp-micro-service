import {makeStyles} from "@material-ui/core/styles";
import {Button, IconButton} from "@mui/material";
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
import {copyAllTestCases, downloadAllTestCases} from "./service/TestCaseService";

const useStyles = makeStyles((theme) => ({}));

export default function ListTestCase() {
  const params = useParams();
  const classes = useStyles();

  const history = useHistory();

  const problemId = params.problemId;
  const [testCases, setTestCases] = useState([]);
  const [openModal, setOpenModal] = useState(false);
  const [selectedTestcase, setSelectedTestcase] = useState();

  function getTestCases() {
    request(
      "GET",
      "/get-test-case-list-by-problem/" + problemId,

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
      "GET",
      "/rerun-create-testcase-solution/" + problemId + "/" + testCaseId,

      () => {
        getTestCases();
      },
      {}
    );
  }

  const testcaseColumns = [
      {
        title: "Input (Preview)",
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
        render: (testCase) => (
          <IconButton
            color="primary"
            onClick={() => {
              setSelectedTestcase(testCase);
              setOpenModal(true);
            }}
          >
            <InfoIcon/>
          </IconButton>
        )
      },

      {title: "Point", field: "point"},
      {title: "Public", field: "public"},
      {title: "Description", field: "description"},
      {title: "Status", field: "status"},
      {
        render: (testCase) => (
          <Button
            variant="contained"
            onClick={() => {
              rerunTestCase(problemId, testCase.testCaseId);
            }}
          >
            Rerun
          </Button>
        ),
      },
      {
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
        render: (testCase) => (
          <IconButton
            variant="contained"
            color="error"
            onClick={() => {
              request(
                "delete",
                "/delete-test-case/" + testCase.testCaseId,

                () => {
                  request(
                    "GET",
                    "/get-test-case-list-by-problem/" + problemId,

                    (res) => {
                      setTestCases(res.data);
                    },
                    {}
                  ).then();
                },
                {}
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
        open={openModal}
        onClose={() => setOpenModal(false)}
        isNotShowCloseButton
        showCloseBtnTitle={false}
      >
        <HustCopyCodeBlock
          title="Input"
          text={chosenTestcase?.chosenTestcase?.testCase}
        />
        <HustCopyCodeBlock
          title="Output"
          text={chosenTestcase?.chosenTestcase?.correctAns}
          mt={2}
        />
      </HustModal>
    );
  };

  function addTestCase() {
    history.push(
      "/programming-contest/problem-detail-create-test-case/" + problemId
    );
  }

  return (
    <Box sx={{marginTop: "12px"}}>
      <StandardTable
        title={"Problem's Testcases"}
        columns={testcaseColumns}
        data={testCases}
        hideCommandBar
        options={{
          selection: false,
          pageSize: 5,
          search: true,
          sorting: true,
        }}
        actions={[
          {
            icon: 'add',
            tooltip: 'Add new Testcase',
            iconProps: {
              color: 'action',
            },
            isFreeAction: true,
            onClick: () => addTestCase()
          }
        ]}
      />
      <Box sx={{mt: 1, display: "flex", flexDirection: "row", justifyContent: "end"}}>
        <Button
          variant="contained"
          onClick={() => {
            copyAllTestCases(testCases);
          }}
          sx={{mr: 2}}
        >
          Copy all Testcases
        </Button>
        <Button
          variant="contained"
          onClick={() => {
            downloadAllTestCases(testCases);
          }}
        >
          Download all Testcases
        </Button>
      </Box>
      <ModalPreview chosenTestcase={selectedTestcase}/>
    </Box>
  );
}
