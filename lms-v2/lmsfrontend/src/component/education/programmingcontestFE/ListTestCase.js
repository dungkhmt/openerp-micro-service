import {IconButton, Paper, Stack, Tooltip, Typography} from "@mui/material";
import HustCopyCodeBlock from "component/common/HustCopyCodeBlock";
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
import AddIcon from "@material-ui/icons/Add";
import {RiCodeSSlashLine} from "react-icons/ri";
import {errorNoti, successNoti} from "../../../utils/notification";
import TestCaseExecutionResult from "./TestCaseExecutionResult";
import CustomizedDialogs from "../../dialog/CustomizedDialogs";
import {makeStyles} from "@material-ui/core/styles";
import {useTranslation} from "react-i18next";
import PrimaryButton from "../../button/PrimaryButton";
import TertiaryButton from "../../button/TertiaryButton";
import DownloadIcon from "@mui/icons-material/Download";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import {FacebookCircularProgress} from "../../common/progressBar/CustomizedCircularProgress";
import {ConfirmDeleteDialog} from "../../dialog/ConfirmDeleteDialog";

const useStyles = makeStyles((theme) => ({
  paper: {
    minWidth: 800,
  },
  dialogContent: {
    padding: theme.spacing(2),
  },
}));

export default function ListTestCase({mode}) {
  const params = useParams();
  const history = useHistory();

  const classes = useStyles();
  const {t} = useTranslation(["education/programmingcontest/testcase", 'common']);

  const problemId = params.problemId;
  const [testCases, setTestCases] = useState([]);
  const [openModalPreviewTestcase, setOpenModalPreviewTestcase] = useState(false);
  const [openModalCopyTestcase, setOpenModalCopyTestcase] = useState(false);
  const [openModalDownloadTestcase, setOpenModalDownloadTestcase] = useState(false);
  const [openModalReGenerateResult, setOpenModalReGenerateResult] = useState(false);
  const [executionResult, setExecutionResult] = useState(null);
  const [loadingTestCaseDetail, setLoadingTestCaseDetail] = useState(false);
  const [testCasesDetail, setTestCasesDetail] = useState({});
  const [selectedTestCase, setSelectedTestCase] = useState();
  const [openModalConfirmDelete, setOpenModalConfirmDelete] = useState(false);

  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [totalCount, setTotalCount] = useState(0);

  const handleChangePage = (newPage) => {
    setPage(newPage);
  };

  const handleChangePageSize = (newSize) => {
    setPage(0)
    setPageSize(newSize)
  }

  const getTestCases = () => {
    setLoading(true);

    request(
      "GET",
      `/problems/${problemId}/testcases?page=${page}&size=${pageSize}`,
      (res) => {
        setLoading(false);
        const data = res.data

        if (data.numberOfElements === 0 && data.number > 0) {
          setPage(0)
        } else {
          setTestCases(data.content);
          setTotalCount(data.totalElements)
        }
      },
      {
        onError: (e) => {
          setLoading(false);
          errorNoti(t("common:error", 3000))
        },
      },
    );
  }

  const addTestCase = () => {
    history.push(
      "/programming-contest/problem-detail-create-test-case/" + problemId
    );
  }

  const rerunTestCase = (problemId, testCaseId) => {
    setExecutionResult(null);
    request(
      "post",
      "/problems/" + problemId + "/testcases/" + testCaseId + "/solution",
      (res) => {
        setExecutionResult(res.data);
        setOpenModalReGenerateResult(true)

        if (res.data.status.id === 3) {
          getTestCases();
        }

        setTestCasesDetail((prev) => ({
          ...prev,
          [testCaseId]: null,
        }));
      },
      {
        onError: (e) => {
          errorNoti(t("common:error"))
        },
      },
    );
  }

  const handleCloseConfirmDeleteModal = () => {
    setOpenModalConfirmDelete(false)
  }

  const handleDelete = () => {
    handleCloseConfirmDeleteModal()
    request(
      "delete",
      "/testcases/" + selectedTestCase.testCaseId,
      () => {
        getTestCases()
        successNoti(t('common:deleteSuccess', {name: 'Test case'}))
      },
      {
        onError: (e) => {
          errorNoti(e?.response?.data?.message || t('common:error'), 3000)
        }
      }
    );
  }

  let testcaseColumns = [
    {
      title: t("inputPreview"),
      cellStyle: {minWidth: 200},
      render: (testCase) => (
        <>
          {testCase?.testCase && testCase.testCase.length > 20
            ? testCase.testCase.substring(0, 19) + "..."
            : testCase.testCase}
        </>
      )
    },
    {
      title: t("outputPreview"),
      cellStyle: {minWidth: 200},
      render: (testCase) => (
        <>
          {testCase?.correctAns && testCase.correctAns.length > 20
            ? testCase.correctAns.substring(0, 19) + "..."
            : testCase.correctAns}
        </>
      )
    },
    {title: t('point'), field: "point"},
    {
      title: t("common:public"),
      field: "isPublic",
      render: (testCase) => testCase.isPublic === 'Y' ? t("common:yes") : t("common:no")
    },
    {title: t("status"), field: "status"},
    {title: t("common:description"), field: "description"}
  ];

  if (mode !== 2) {
    testcaseColumns.push({
      title: t("common:action"),
      align: "center",
      render: (testCase) => (
        <Stack spacing={1} direction="row">
          <Tooltip title={t('common:viewDetail')}>
            <IconButton
              color="primary"
              onClick={() => handleViewDetail(testCase)}
            >
              <InfoIcon/>
            </IconButton>
          </Tooltip>
          <Tooltip title={t('reGenerate')}>
            <IconButton
              variant="contained"
              color="primary"
              onClick={() => {
                rerunTestCase(problemId, testCase.testCaseId);
              }}
            >
              <RiCodeSSlashLine/>
            </IconButton>
          </Tooltip>

          <Tooltip title={t('common:edit', {name: ''})}>
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
          </Tooltip>

          <Tooltip title={t('common:delete')}>
            <IconButton
              variant="contained"
              color="error"
              onClick={() => {
                setSelectedTestCase(testCase)
                setOpenModalConfirmDelete(true)
              }}
            >
              <DeleteIcon/>
            </IconButton>
          </Tooltip>
        </Stack>),
    })
  } else {
    testcaseColumns.push({
      title: t("common:action"),
      align: "center",
      render: (testCase) => (
        <Tooltip title={t('common:viewDetail')}>
          <IconButton
            color="primary"
            onClick={() => handleViewDetail(testCase)}
          >
            <InfoIcon/>
          </IconButton>
        </Tooltip>
      ),
    })
  }

  const handleViewDetail = (rowData) => {
    const testCaseId = rowData.testCaseId;

    setOpenModalPreviewTestcase(true);

    if (testCasesDetail[testCaseId]) {
      setSelectedTestCase(testCasesDetail[testCaseId]);
    } else {
      setLoadingTestCaseDetail(true);

      request(
        "GET",
        `/testcases/${testCaseId}`,
        (res) => {
          setLoadingTestCaseDetail(false);
          setTestCasesDetail((prev) => ({
            ...prev,
            [testCaseId]: res.data || {},
          }));

          setSelectedTestCase(res.data);
        },
        {
          onError: (e) => {
            setLoadingTestCaseDetail(false);
            errorNoti(t('common:error'), 3000)
          },
        }
      );
    }
  }

  const ModalPreview = ({testCase}) => (
    <CustomizedDialogs
      open={openModalPreviewTestcase}
      title={t('common:viewDetail')}
      contentTopDivider
      classNames={{
        paper: (loadingTestCaseDetail || testCase?.testCase != null) ? classes.paper : null,
        content: classes.dialogContent
      }}
      handleClose={() => setOpenModalPreviewTestcase(false)}
      content={
        loadingTestCaseDetail ? (
          <Stack
            direction="row"
            justifyContent="center"
            alignItems="center"
            sx={{minHeight: 128}}
          >
            <FacebookCircularProgress/>
          </Stack>
        ) : (testCase?.testCase == null ?
          <Stack sx={{minHeight: 64}}>
            <Typography variant="subtitle2">{t('cannotShowTestCaseDetail')}</Typography>
          </Stack>
          :
          <>
            <HustCopyCodeBlock
              title={t("input")}
              text={testCase?.testCase}
            />
            <Box sx={{height: "16px"}}/>
            <HustCopyCodeBlock
              title={t("output")}
              text={testCase?.correctAns}
              mt={2}
            />
          </>)
      }
    />
  );

  const getFullTestCases = (publicOnly, successHandler) => {
    request(
      "GET",
      `/problems/${problemId}/testcases?fullView=true${publicOnly ? "&publicOnly=true" : ""}`,
      (res) => {
        successHandler(res)
      },
      {
        onError: (e) => {
          errorNoti(t("common:error", 3000))
        },
      },
    );
  }

  const handleCopyTestCase = (publicOnly, successHandler) => {
    setOpenModalCopyTestcase(false);
    getFullTestCases(publicOnly, successHandler);
  }

  const ModalCopy = () => (
    <CustomizedDialogs
      open={openModalCopyTestcase}
      handleClose={() => setOpenModalCopyTestcase(false)}
      title={t('common:copy')}
      contentTopDivider
      content={<Typography variant="subtitle2">{t('confirmCopy')}</Typography>}
      actions={
        <>
          <TertiaryButton
            variant="outlined"
            onClick={() => handleCopyTestCase(
              false,
              (res) => {
                copyTestCasesWithPublicMode(res.data.content, t);
              })}
          >
            {t('allTestCases')}
          </TertiaryButton>
          <PrimaryButton
            onClick={() => handleCopyTestCase(
              true,
              (res) => {
                copyTestCasesWithPublicMode(res.data.content, t);
              })}
          >
            {t('publicTestCases')}
          </PrimaryButton>
        </>}
      classNames={{content: classes.dialogContent}}
    />
  );

  const handleDownloadTestCase = (publicOnly, successHandler) => {
    setOpenModalDownloadTestcase(false);
    getFullTestCases(publicOnly, successHandler);
  }

  const ModalDownload = () => (
    <CustomizedDialogs
      open={openModalDownloadTestcase}
      handleClose={() => setOpenModalDownloadTestcase(false)}
      title={t('common:download')}
      contentTopDivider
      content={<Typography variant="subtitle2">{t('confirmDownload')}</Typography>}
      actions={
        <>
          <TertiaryButton
            variant="outlined"
            onClick={() => handleDownloadTestCase(
              false,
              (res) => {
                downloadAllTestCasesWithPublicMode(res.data.content, t);
              })}
          >
            {t('allTestCases')}
          </TertiaryButton>
          <PrimaryButton
            onClick={() => handleDownloadTestCase(
              true,
              (res) => {
                downloadAllTestCasesWithPublicMode(res.data.content, t);
              })}
          >
            {t('publicTestCases')}
          </PrimaryButton>
        </>
      }
      classNames={{content: classes.dialogContent}}
    />
  );

  useEffect(() => {
    getTestCases()
  }, [page, pageSize]);

  return (
    <Box sx={{marginTop: "36px"}}>
      <Stack direction="row" justifyContent='space-between' mb={1.5}>
        <Typography variant="h6">{t("Test Case")}</Typography>

        <Stack direction='row' spacing={2}>
          {mode !== 2 && <PrimaryButton
            startIcon={<AddIcon/>}
            onClick={() => addTestCase()}
          >
            {t("common:create", {name: ''})}
          </PrimaryButton>}
          {testCases?.length > 0 && <>
            <TertiaryButton
              variant="outlined"
              startIcon={<ContentCopyIcon/>}
              onClick={() => setOpenModalCopyTestcase(true)}
            >
              {t('common:copy')}
            </TertiaryButton>
            <TertiaryButton
              variant="outlined"
              startIcon={<DownloadIcon/>}
              onClick={() => setOpenModalDownloadTestcase(true)}
            >
              {t('common:download')}
            </TertiaryButton>
          </>}
        </Stack>
      </Stack>
      <StandardTable
        columns={testcaseColumns}
        data={testCases}
        hideCommandBar
        hideToolBar
        options={{
          selection: false,
          pageSize: 5,
          search: false,
          sorting: false,
        }}
        components={{
          Container: (props) => <Paper {...props} elevation={0}/>,
        }}
        isLoading={loading}
        page={page}
        totalCount={totalCount}
        onChangePage={handleChangePage}
        onChangeRowsPerPage={handleChangePageSize}
      />

      <ModalCopy/>
      <ModalDownload/>
      <ModalPreview testCase={selectedTestCase}/>
      <CustomizedDialogs
        open={openModalReGenerateResult}
        handleClose={() => setOpenModalReGenerateResult(false)}
        title={t("reGenerateResult")}
        contentTopDivider
        content={<TestCaseExecutionResult uploadResult={executionResult} hideTitle/>}
        classNames={{paper: classes.paper, content: classes.dialogContent}}
      />
      <ConfirmDeleteDialog open={openModalConfirmDelete}
                           handleClose={handleCloseConfirmDeleteModal}
                           handleDelete={handleDelete}
                           entity='test case'
                           name={selectedTestCase?.description}
      />
    </Box>
  );
}
