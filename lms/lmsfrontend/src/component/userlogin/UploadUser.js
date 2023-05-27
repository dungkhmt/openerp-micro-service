import {LoadingButton} from "@mui/lab";
import React, {useRef, useState} from "react";
import HustContainerCard from "../common/HustContainerCard";
import XLSX from "xlsx";
import {KC_REALM} from "../../config/keycloak";
import {request} from "../../api";
import {config} from "../../config/config";
import {Button} from "@mui/material";
import StandardTable from "../table/StandardTable";
import {getColorSuccess} from "../education/programmingcontestFE/lib";
import Box from "@mui/material/Box";

function UploadUser() {
  const fileInputRef = useRef(null);
  const [file, setFile] = useState(null);

  const [result, setResult] = useState([]);
  const [loading, setLoading] = useState(false);

  const downloadSampleFile = () => {
    window.location.href = '/static/excels/sample-upload-user.xlsx';
  };

  function onFileChange(event) {
    setFile(event.target.files[0]);
  }

  const handleUpload = () => {
    setResult([]);
    setLoading(true);
    handleUploadExcelUserList();
    if (fileInputRef.current) {
      fileInputRef.current.value = null;
    }
    setLoading(false);
  }

  const handleUploadExcelUserList = () => {
    const reader = new FileReader();

    reader.onload = (e) => {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, {type: 'array'});

      // Assuming the first sheet in the workbook
      const worksheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, {header: 1});

      const [headerRow, ...dataRows] = jsonData;

      dataRows.forEach((row) => {
        addUser(row);
      });
    };

    reader.readAsArrayBuffer(file);
  }

  const addUser = (row) => {
    // Assuming the column order is: Email, Username, Firstname, Lastname, Password
    let email = row[0];
    let username = !row[1] ? email.split('@')[0] : row[1];
    let firstName = row[2] || "";
    let lastName = row[3] || "";
    let password = row[4] || "password";

    let data = {
      "username": username,
      "email": email,
      "firstName": firstName,
      "lastName": lastName,
      "enabled": true,
      "emailVerified": false,
      "requiredActions": [],
      "groups": [
        "/STUDENT"
      ],
      "credentials": [
        {
          "type": "password",
          "value": password,
          "temporary": true
        }
      ]
    };

    const headerConfig = {
      headers: {
        "content-Type": "application/json",
      },
    };

    request(
      "post",
      `${config.url.KEYCLOAK_BASE_URL}/admin/realms/${KC_REALM}/users`,
      (res) => {
        data.status = "SUCCESS";
        data.message = "";
        setResult(result => [...result, data]);
      },
      {
        409: (err) => {
          data.status = "FAIL";
          data.message = err?.response.data.errorMessage || "";
          setResult(result => [...result, data]);

        }
      },
      data,
      headerConfig
    )
  }

  const columns = [
    {title: "Username", field: "username"},
    {title: "Email", field: "email"},
    {title: "First name", field: "firstName"},
    {title: "Last name", field: "lastName"},
    {
      title: "Status",
      field: "status",
      render: (row) => (
        <span style={{color: getColorSuccess(`${row?.status || ""}`)}}>
          {`${row?.status || ""}`}
        </span>
      )
    },
    {title: "Message", field: "message"}
  ];

  return (
    <HustContainerCard>
      <Box sx={{margin: "24px 0 12px 24px"}}>
        <input type="file" id="selected-upload-file" onChange={onFileChange} ref={fileInputRef}/>
        <LoadingButton variant={"contained"} loading={loading} disabled={!fileInputRef.current} onClick={handleUpload}>
          Upload
        </LoadingButton>
        <Button sx={{marginLeft: "18px"}} variant={"outlined"} onClick={downloadSampleFile}>Download Template</Button>
      </Box>
      <Box sx={{padding: "12px"}}>
        <StandardTable
          title={"Result"}
          hideCommandBar
          columns={columns}
          data={result}
          options={{
            selection: false,
            pageSize: 20,
            search: true,
            sorting: true,
          }}
        />
      </Box>

    </HustContainerCard>
  )
}

export default UploadUser;