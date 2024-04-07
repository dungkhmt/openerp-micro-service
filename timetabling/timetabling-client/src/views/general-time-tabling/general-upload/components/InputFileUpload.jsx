import * as React from "react";
import { styled } from "@mui/material/styles";
import Button from "@mui/material/Button";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import { FacebookCircularProgress } from "components/common/progressBar/CustomizedCircularProgress";
import { request } from "api";
import { width } from "@mui/system";

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

export default function InputFileUpload({
  selectedFile,
  setSelectedFile,
  selectedSemester,
}) {
  const inputRef = React.useRef();
  console.log(selectedSemester);
  const [isLoading, setIsLoading] = React.useState(false);
  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
    console.log(selectedFile);
  };
  const handleSubmitFile = () => {
    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);
      setIsLoading(true);
      request(
        "post",
        `/excel/upload-general?semester=${selectedSemester?.semester}`,
        (res) => {
          console.log(res);
          setIsLoading(false);
        },
        (err) => {
          setIsLoading(false);
          console.log(err);
        },
        formData,
        {
          "Content-Type": "multipart/form-data",
        }
      );
    }
  };

  return (
    <div className="flex flex-row gap-2">
      <Button
        sx={{ width: "200px" }}
        disabled={selectedSemester === null || isLoading === true}
        component="label"
        color={selectedFile ? "success" : "primary"}
        variant="contained"
        startIcon={
          !isLoading ? <CloudUploadIcon /> : <FacebookCircularProgress />
        }
      >
        {selectedFile === null
          ? "Tải danh sách lớp"
          : `TKB-${selectedSemester.semester}`}
        <VisuallyHiddenInput
          ref={inputRef}
          type="file"
          onChange={handleFileChange}
        />
      </Button>
      <Button
        disabled={selectedFile === null}
        variant="outlined"
        onClick={handleSubmitFile}
      >
        Submit
      </Button>
    </div>
  );
}
