// import HustModal from "component/common/HustModal";
import {HustModal} from "erp-hust/lib/HustModal";
import React, {useState} from "react";
import {MenuItem, TextField} from "@mui/material";
import {useTranslation} from "react-i18next";
import {saveProblemToContest} from "./service/ContestProblemService";
import {SUBMISSION_MODE_SOLUTION_OUTPUT, SUBMISSION_MODE_SOURCE_CODE} from "./Constant";

const ModalAddProblemToContest = (props) => {
  const {contestId, chosenProblem, isOpen, handleSuccess, handleClose} = props;

  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );

  const [problemRename, setProblemRename] = useState("");
  const [problemRecode, setProblemRecode] = useState("");
  const [submissionMode, setSubmissionMode] = useState(SUBMISSION_MODE_SOURCE_CODE)
  const [loading, setLoading] = useState(false);

  const handleAddProblemToContest = () => {
    let body = {
      contestId: contestId,
      problemId: chosenProblem.problemId,
      problemName: chosenProblem.problemName,
      problemRename: problemRename,
      problemRecode: problemRecode,
      submissionMode: submissionMode,
    };

    setLoading(true);

    saveProblemToContest(
      body,
      handleSuccess,
      () => {
        setLoading(false);
        handleClose();
        resetField();
      },
    )
  }

  const resetField= () => {
    setProblemRename("");
    setProblemRecode("");
    setSubmissionMode(SUBMISSION_MODE_SOURCE_CODE);
  }

  return (
    <HustModal
      open={isOpen}
      onOk={handleAddProblemToContest}
      textOk={t("common:save")}
      onClose={handleClose}
      isLoading={loading}
      title={t("common:addNew")}
    >
      <TextField
        fullWidth
        required
        disabled
        label={"Problem"}
        value={chosenProblem?.problemName}
      />
      <TextField
        fullWidth
        autoFocus
        label={"Problem name in this contest"}
        placeholder={"If this field is left blank, the original problem name will be taken"}
        value={problemRename}
        onChange={(event) => {
          setProblemRename(event.target.value);
        }}
        sx={{marginTop: "16px"}}
      />
      <TextField
        fullWidth
        label={"Problem code in this contest"}
        placeholder={"If this field is left blank, a default value will be generated"}
        value={problemRecode}
        onChange={(event) => {
          setProblemRecode(event.target.value);
        }}
        sx={{marginTop: "16px"}}
      />
      <TextField
        fullWidth
        select
        id="Submission Mode"
        label="Submission Mode"
        onChange={(event) => {
          setSubmissionMode(event.target.value);
        }}
        value={submissionMode}
        sx={{marginTop: "16px"}}
      >
        <MenuItem value={SUBMISSION_MODE_SOURCE_CODE}>
          {"NORMAL"}
        </MenuItem>
        <MenuItem value={SUBMISSION_MODE_SOLUTION_OUTPUT}>
          {"SUBMIT OUTPUT"}
        </MenuItem>
      </TextField>
    </HustModal>
  );
}

export default React.memo(ModalAddProblemToContest);