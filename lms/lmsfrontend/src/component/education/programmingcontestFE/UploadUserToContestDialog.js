import React, {useRef, useState} from "react";
import {request} from "../../../api";
import {errorNoti} from "../../../utils/notification";

import {Box, Button} from "@mui/material";
import HustModal from "../../common/HustModal";
import StandardTable from "../../table/StandardTable";

export default function UploadUserToContestDialog(props) {
  const {isOpen, onClose, contestId} = props;
  const [isProcessing, setIsProcessing] = useState(false);
  const fileInputRef = useRef(null);
  const [filename, setFilename] = useState(null);
  const [uploadedUsers, setUploadedUsers] = useState([]);

  function onFileChange(event) {
    setFilename(event.target.files[0]);
  }

  const downloadSampleFile = () => {
    window.location.href = '/static/excels/sample-upload-user-contest.xlsx';
  };

  const handleUpload = () => {
    setIsProcessing(true);
    let body = {
      contestId: contestId,
    };
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify(body));
    formData.append("file", filename);

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
          errorNoti("An error happened", 5000)
        },
      },
      formData,
      config
    );
  }

  const getUploadStatusColor = (status) => {
    if (status === 'SUCCESSFUL') return 'green';
    return 'red';
  }

  const columns = [
    {title: "User ID", field: "userId"},
    {title: "Role", field: "roleId",},
    {title: "Status",
      field: "status",
      render: (rowData) => (
        <span style={{color: getUploadStatusColor(`${rowData.status}`)}}>
          {`${rowData.status}`}
        </span>
      ),},
  ];

  return (
    <HustModal
      open={isOpen}
      title={'Upload Users to Contest'}
      onOk={handleUpload}
      onClose={onClose}
      textOk="Upload"
      textClose="Cancel"
      isLoading={isProcessing}
      maxWidthPaper={uploadedUsers.length > 0 ? 800 : 600}
    >
      <Box sx={{mb: 1}}>
        <Box display="flex" justifyContent="flex-start" alignItems="center" width="100%" sx={{mb: 2}}>
          <input type="file" id="selected-upload-file" onChange={onFileChange} ref={fileInputRef}/>
          <Button sx={{marginLeft: "24px"}} variant={"outlined"} onClick={downloadSampleFile}>Download Template</Button>
        </Box>

        {uploadedUsers.length > 0 &&
          <StandardTable
            title={uploadedUsers.filter(user => user.status === 'SUCCESSFUL').length + "/" + uploadedUsers.length + " Successful"}
            columns={columns}
            data={uploadedUsers}
            options={{
              selection: false,
              pageSize: 5,
              search: true,
              sorting: true,
            }}
            hideCommandBar
          />
        }
      </Box>
    </HustModal>
  );
}
