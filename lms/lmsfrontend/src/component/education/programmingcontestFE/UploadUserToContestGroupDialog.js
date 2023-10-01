import React, {useRef, useState} from "react";
import {request} from "../../../api";
import {errorNoti} from "../../../utils/notification";

import {Box, Button} from "@mui/material";
import HustModal from "../../common/HustModal";
import StandardTable from "../../table/StandardTable";
import XLSX from "xlsx";

export default function UploadUserToContestGroupDialog(props) {
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
      "/contests/students/upload-group-list",
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

  const downloadHandler = (event) => {
    if (uploadedUsers.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({wpx: 200});
    wbcols.push({wpx: 120});
    wbcols.push({wpx: 280});

    let datas = [];

    for (let i = 0; i < uploadedUsers.length; i++) {
      let data = {};
      data["User ID"] = uploadedUsers[i].participantId;
      data["Role"] = uploadedUsers[i].roleId;
      data["Status"] = uploadedUsers[i].status;

      datas[i] = data;
    }

    var sheet = XLSX.utils.json_to_sheet(datas);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "result");
    XLSX.writeFile(
      wb,
      "upload_user_contest-" + contestId +".xlsx"
    );
  };

  const getUploadStatusColor = (status) => {
    if (status === 'Successful' || status === 'Added') return 'green';
    return 'red';
  }

  const columns = [
    {title: "User ID", field: "participantId"},
    {title: "Role", field: "roleId",},
    {
      title: "Status",
      field: "status",
      render: (rowData) => (
        <span style={{color: getUploadStatusColor(`${rowData.status}`)}}>
          {`${rowData.status}`}
        </span>
      ),
    },
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
      maxWidthPaper={uploadedUsers.length > 0 ? 840 : 600}
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
            actions={[
              {
                icon: () => {
                  return <Button variant="contained" onClick={downloadHandler} color="primary">
                    Export
                  </Button>
                },
                tooltip: 'Export Result as Excel file',
                isFreeAction: true
              }
            ]}
          />
        }
      </Box>
    </HustModal>
  );
}
