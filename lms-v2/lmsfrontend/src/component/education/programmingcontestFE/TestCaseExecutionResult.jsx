import React from "react";
import {Grid, Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import {detail, getStatusColorById} from "./CreateTestCase";
import {localeOption} from "../../../utils/NumberFormat";
import HustCopyCodeBlock from "../../common/HustCopyCodeBlock";

const TestCaseExecutionResult = ({uploadResult, hideTitle}) => {
  const {t: tTestcase} = useTranslation("education/programmingcontest/testcase");

  if (!uploadResult) return null;

  return (
    <>
      {!hideTitle && <Typography
        id="result-of-creating-testcase"
        variant="h6"
        sx={{mb: 1}}
      >
        {tTestcase("result")}
      </Typography>}
      <Grid container spacing={1.5}>
        {[
          [
            tTestcase("status"),
            uploadResult.status.description,
            {
              value: {
                color: getStatusColorById(uploadResult.status.id),
              },
            },
          ],
          [tTestcase("message"), uploadResult.message],
          [
            tTestcase("runtime"),
            uploadResult.time
              ? `${uploadResult.time.toLocaleString("fr-FR", localeOption)} (s)`
              : null,
          ],
          ["Stderr", uploadResult.stderr],
          [
            tTestcase("memory"),
            uploadResult.memory
              ? `${(uploadResult.memory / 1024).toLocaleString("fr-FR", localeOption)} (MB)`
              : null,
          ],
          // ["exit_code", uploadResult.exit_code],
          // [
          //   "exit_signal",
          //   uploadResult.exit_signal,
          // ],
          // ["created_at", displayTime(uploadResult.created_at)],
          // ["finished_at", displayTime(uploadResult.finished_at)],
          // [
          //   "wall_time",
          //   uploadResult.wall_time ? `${uploadResult.wall_time.toLocaleString("fr-FR", localeOption)} (s)` : null,
          // ],
        ].map(([key, value, sx], index) => (
          <Grid item xs={6} key={index}>
            {detail(key, value, sx)}
          </Grid>
        ))}
      </Grid>
      <Typography variant="subtitle1" sx={{marginTop: "12px"}}>
        {tTestcase("compileOutput")}
      </Typography>
      {uploadResult.compile_output ? (
        <div>
          <pre
            style={{
              marginTop: 0,
              padding: 16,
              borderRadius: 4,
              background: "#fdeded",
              color: "#5f2120",
            }}
          >
            {uploadResult.compile_output}
          </pre>
        </div>
      ) : null}
      <Typography variant="subtitle1" sx={{mt: 1.5, mb: 1}}>
        Stdout
      </Typography>
      {uploadResult.stdout && <HustCopyCodeBlock text={uploadResult.stdout}/>}
    </>
  );
};

export default TestCaseExecutionResult;
