import {LoadingButton} from "@mui/lab";
import React, {useEffect, useRef, useState} from "react";
import HustContainerCard from "../common/HustContainerCard";
import XLSX from "xlsx";
import {KC_REALM} from "../../config/keycloak";
import {request} from "../../api";
import {config} from "../../config/config";
import {Button, Divider, Grid, LinearProgress, Switch} from "@mui/material";
import StandardTable from "../table/StandardTable";
import {getColorSuccess} from "../education/programmingcontestFE/lib";
import Box from "@mui/material/Box";
import {defaultDatetimeFormat, toFormattedDateTime} from "../../utils/dateutils";
import Typography from "@mui/material/Typography";

function UploadUser() {
  const fileInputRef = useRef(null);
  const [file, setFile] = useState(null);

  const [result, setResult] = useState([]);
  const [loading, setLoading] = useState(false);

  const [total, setTotal] = useState(0);

  const [continueOnError, setContinueOnError] = useState(true);

  const intervalIdRef = useRef(null);

  const downloadSampleFile = () => {
    window.location.href = '/static/excels/sample-upload-user_v2.xlsx';
  };

  function onFileChange(event) {
    setFile(event.target.files[0]);
  }

  function convertToUnixTimestamp(timeValue) {
    return Math.floor(new Date(timeValue).getTime());
  }

  const handleUpload = async () => {
    await setResult([]);
    await setLoading(true);
    handleUploadExcelUserList();
    if (fileInputRef.current) {
      fileInputRef.current.value = null;
    }
  }

  const handleUploadExcelUserList = () => {
    const reader = new FileReader();

    reader.onload = (e) => {
      const data = new Uint8Array(e.target.result);
      const workbook = XLSX.read(data, {type: 'array'});

      // Assuming the first sheet in the workbook
      const worksheet = workbook.Sheets[workbook.SheetNames[0]];
      const jsonData = XLSX.utils.sheet_to_json(worksheet, {header: 1, blankrows: false});

      const [headerRow, ...dataRows] = jsonData;

      setTotal(dataRows.length);

      let idx = 0;
      intervalIdRef.current = setInterval(() => {
        if (idx < dataRows.length) {
          addUser(dataRows[idx]);
          idx++;
        }
      }, 500);

    };

    reader.readAsArrayBuffer(file);
  }

  const stopUploading = () => {
    setLoading(false);
    clearInterval(intervalIdRef.current)
  }

  const addUser = (row) => {
    // Assuming the column order is: Email, Username, Firstname, Lastname, Password, Enable, Created At
    let email = row[0];
    let username = !row[1] ? email.split('@')[0] : row[1];
    let firstName = row[2] || "";
    let lastName = row[3] || "";
    let password = row[4] || "soict1234";
    let enable = row[5];
    let group = row[6] ? row[6] : "STUDENT";

    let data = {
      "email": email,
      "username": username,
      "firstName": firstName,
      "lastName": lastName,
      "enabled": enable,
      "emailVerified": false,
      "requiredActions": ["VERIFY_EMAIL"],
      "groups": [
        group
      ],
      "credentials": [
        {
          "type": "password",
          // "secretData": '{"value":"' + password + '","salt":""}',
          // "credentialData": '{"hashIterations":10,"algorithm":"bcrypt"}', // default
          "value": password,
          "temporary": true
        }
      ],
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
        data.password = password;
        data.doneAt = defaultDatetimeFormat(new Date());
        setResult(result => [...result, data]);
      },
      {
        409: (err) => {
          data.status = "FAIL";
          data.message = err?.response.data.errorMessage || "";
          data.password = password;
          data.doneAt = defaultDatetimeFormat(new Date());
          setResult(result => [...result, data]);

          if (!continueOnError) {
            clearInterval(intervalIdRef.current);
            setLoading(false);
          }
        }
      },
      data,
      headerConfig
    )
  }

  const columns = [
    {title: "Username", field: "username"},
    {title: "Email", field: "email"},
    // {title: "First name", field: "firstName"},
    // {title: "Last name", field: "lastName"},
    {
      title: "Status",
      field: "status",
      render: (row) => (
        <span style={{color: getColorSuccess(`${row?.status || ""}`)}}>
          {`${row?.status || ""}`}
        </span>
      )
    },
    {title: "Message", field: "message"},
    {title: "Done At", field: "doneAt"},
  ];

  useEffect(() => {
    if (result.length === total) setLoading(false);
  }, [result])

  const downloadHandler = (event) => {
    if (result.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({wpx: 180});
    wbcols.push({wpx: 120});
    wbcols.push({wpx: 100});
    wbcols.push({wpx: 100});
    wbcols.push({wpx: 100});
    wbcols.push({wpx: 60});
    wbcols.push({wpx: 80});
    wbcols.push({wpx: 60});
    wbcols.push({wpx: 180});
    wbcols.push({wpx: 120});

    let datas = [];

    for (let i = 0; i < result.length; i++) {
      let data = {};
      data["Email"] = result[i].email;
      data["Username"] = result[i].username;
      data["First Name"] = result[i].firstName;
      data["Last Name"] = result[i].lastName;
      data["Password"] = result[i].password;
      data["Enabled"] = result[i].enabled;
      data["Group"] = result[i].groups.toString();
      data["Status"] = result[i].status;
      data["Message"] = result[i].message;
      data["Done At"] = result[i].doneAt;

      datas[i] = data;
    }

    var sheet = XLSX.utils.json_to_sheet(datas);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "result");
    XLSX.writeFile(
      wb,
      "upload_user_result.xlsx"
    );
  };

  return (
    <HustContainerCard title="Upload Users">
      <Grid container spacing={1} alignItems="center" sx={{margin: "0 24px 24px 4px"}}>
        <Grid item xs={3}>
          <input type="file" id="selected-upload-file" onChange={onFileChange} ref={fileInputRef}/>
        </Grid>
        <Grid item xs={3}>
          <Button sx={{marginLeft: "18px"}} variant={"outlined"} onClick={downloadSampleFile}>Download Template</Button>
        </Grid>
        <Grid item xs={2}/>
        <Grid item xs={4}>
          Continue when error happen
          <Switch checked={continueOnError} onChange={(event) => setContinueOnError(event.target.checked)}/>
        </Grid>
      </Grid>

      <Box sx={{display: "flex", justifyContent: "center"}}>
        <Divider light={true} sx={{width: "20%"}}/>
      </Box>

      <Grid container spacing={1} alignItems="center" sx={{margin: "12px 0 16px 4px"}}>
        <Grid item xs={2}>
          {!loading &&
            <LoadingButton color="success" variant={"contained"} loading={loading} disabled={!fileInputRef.current}
                           onClick={handleUpload}>
              Start
            </LoadingButton>
          }
          {loading &&
            <LoadingButton color="error" variant={"contained"} onClick={stopUploading}>
              Stop
            </LoadingButton>
          }
        </Grid>
        <Grid item xs={1}>
          <Typography variant="body2" color="text.secondary">Progress:</Typography>
        </Grid>
        <Grid item xs={8}>
          <LinearProgress variant="determinate" value={total === 0 ? 0 : result.length * 100 / total}/>
        </Grid>
        <Grid item xs={1}>
          <Typography variant="body2" color="text.secondary">{`${result.length}/${total}`}</Typography>
        </Grid>
      </Grid>

      <Box sx={{padding: "12px"}}>
        <StandardTable
          title={"Result"}
          hideCommandBar
          columns={columns}
          data={result}
          options={{
            selection: false,
            pageSize: 10,
            search: true,
            sorting: true,
          }}
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
      </Box>

    </HustContainerCard>
  )
}

export default UploadUser;