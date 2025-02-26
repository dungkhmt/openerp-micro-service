import {Alert} from "@material-ui/lab";
import * as React from "react";
import {Typography} from "@mui/material";
import {useTranslation} from "react-i18next";

export function CompileStatus(props) {
  const {t} = useTranslation([
    "education/programmingcontest/problem",
    "common",
    "validation",
    "education/programmingcontest/testcase"
  ]);
  const {showCompile, statusSuccessful, detail} = props;

  if (!showCompile) {
    return null;
  } else {
    return (
      <>
        <Typography variant="subtitle1">
          {t("status")}
        </Typography>
        <div>
          {
            statusSuccessful ?
              <Alert icon={false} severity="success">{t("common:success")}</Alert> :
              <Alert icon={false} severity="error">{detail.status}</Alert>
          }
        </div>
        {!statusSuccessful &&
          <>
            <Typography variant="subtitle1" sx={{marginTop: "12px"}}>
              {t("compileOutput", {ns: "education/programmingcontest/testcase"})}
            </Typography>
        <div>
          <pre style={{
            marginTop: 0,
            padding: 16,
            borderRadius: 4,
            background: "#fdeded",
            color: "#5f2120"
          }}>{detail.message}</pre>
        </div>
          </>}
      </>
    );
  }
}