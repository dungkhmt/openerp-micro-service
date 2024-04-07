import React, { useState } from "react";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";
import GeneralUploadTable from "./components/GeneralUploadTable";
import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";
import { CloudUpload } from "@mui/icons-material";

const VisuallyHiddenInput = styled("input")({
  clip: "rect(0 0 0 0)",
  clipPath: "inset(50%)",
  height: 1,
  overflow: "hidden",
  position: "absolute",
  bottom: 0,
  left: 0,
  whiteSpace: "nowrap",
  width: 1,
});


const GeneralUploadScreen = () => {

  return (
    <div className="flex flex-col gap-2">
      <div className="flex flex-col gap-2 items-end">
        <GeneralSemesterAutoComplete />
        <div className="flex gap-2">
          <Button
            sx={{ width: "200px" }}
            variant="contained"
            tabIndex={-1}
            startIcon={<CloudUpload/>}
          >
            Tải danh sách
            <VisuallyHiddenInput type="file" />
          </Button>
          <Button sx={{ width: "200px" }} variant="contained" color="error">
            Xóa danh sách
          </Button>
        </div>
      </div>
      <GeneralUploadTable />
    </div>
  );
};

export default GeneralUploadScreen;
