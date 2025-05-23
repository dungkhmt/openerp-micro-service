import { useRef, useState } from "react";
import { request } from "../../../api";
import { errorNoti } from "../../../utils/notification";

import { Box, Button, Link, Stack, Typography } from "@mui/material";
import { utils, writeFile } from "xlsx";
import HustModal from "../../common/HustModal";
import StandardTable from "../../table/StandardTable";
import { InputFileUpload } from "./StudentViewProgrammingContestProblemDetailV2";

export default function UploadUserToContestDialog(props) {
  const { isOpen, onClose, contestId } = props;
  const [isProcessing, setIsProcessing] = useState(false);

  const inputRef = useRef(null);
  const [file, setFile] = useState(null);
  const [uploadedUsers, setUploadedUsers] = useState([]);

  function onFileChange(event) {
    setFile(event.target.files[0]);
  }

  const downloadSampleFile = () => {
    window.location.href = "/static/excels/sample-upload-user-contest.xlsx";
  };

  const handleUpload = () => {
    setIsProcessing(true);
    let body = {
      contestId: contestId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", file);

    const config = {
      headers: {
        "content-Type": "multipart/form-data",
      },
    };

    request(
      "post",
      "/contests/students/upload-list",
      (res) => {
        setUploadedUsers(res.data);
        setIsProcessing(false);
      },
      {
        onError: (e) => {
          setIsProcessing(false);
          errorNoti("An error happened", 5000);
        },
      },
      formData,
      config
    );
  };

  const downloadHandler = (event) => {
    if (uploadedUsers.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({ wpx: 200 });
    wbcols.push({ wpx: 120 });
    wbcols.push({ wpx: 280 });

    let datas = [];

    for (let i = 0; i < uploadedUsers.length; i++) {
      let data = {};
      data["User ID"] = uploadedUsers[i].userId;
      data["Role"] = uploadedUsers[i].roleId;
      data["Status"] = uploadedUsers[i].status;

      datas[i] = data;
    }

    var sheet = utils.json_to_sheet(datas);
    var wb = utils.book_new();
    sheet["!cols"] = wbcols;

    utils.book_append_sheet(wb, sheet, "result");
    writeFile(wb, "upload_user_contest-" + contestId + ".xlsx");
  };

  const getUploadStatusColor = (status) => {
    if (status === "Successful" || status === "Added") return "green";
    return "red";
  };

  const columns = [
    { title: "User ID", field: "userId" },
    { title: "Role", field: "roleId" },
    {
      title: "Status",
      field: "status",
      render: (rowData) => (
        <span style={{ color: getUploadStatusColor(`${rowData.status}`) }}>
          {`${rowData.status}`}
        </span>
      ),
    },
  ];

  return (
    <HustModal
      open={isOpen}
      title={"Upload Users to Contest"}
      onOk={handleUpload}
      onClose={onClose}
      textOk="Upload"
      textClose="Cancel"
      isLoading={isProcessing}
      maxWidthPaper={uploadedUsers.length > 0 ? 840 : 600}
    >
      <Box sx={{ mb: 1 }}>
        <Stack direction="row" spacing={2} alignItems="center">
          <Stack
            direction={"row"}
            spacing={1}
            alignItems="center"
            sx={{ flexGrow: 1 }}
          >
            <InputFileUpload
              id="selected-upload-file"
              label="Select file"
              accept=".xlsx"
              onChange={onFileChange}
              ref={inputRef}
              buttonProps={{ variant: "contained" }}
            />
            {file && (
              <Typography noWrap variant="body1" sx={{ maxWidth: "240px" }}>
                {file.name}
              </Typography>
            )}
          </Stack>
          <Link
            href={`/static/excels/sample-upload-user-contest.xlsx`}
            variant="subtitle2"
            underline="none"
            download
            // sx={{ alignSelf: "flex-end" }}
          >
            Download template
          </Link>
        </Stack>

        {uploadedUsers.length > 0 && (
          <StandardTable
            title={
              uploadedUsers.filter((user) => user.status === "Successful")
                .length +
              "/" +
              uploadedUsers.length +
              " Successful"
            }
            columns={columns}
            data={uploadedUsers}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            hideCommandBar
            actions={[
              {
                icon: () => {
                  return (
                    <Button
                      variant="contained"
                      onClick={downloadHandler}
                      color="primary"
                    >
                      Export
                    </Button>
                  );
                },
                tooltip: "Export Result as Excel file",
                isFreeAction: true,
              },
            ]}
          />
        )}
      </Box>
    </HustModal>
  );
}
