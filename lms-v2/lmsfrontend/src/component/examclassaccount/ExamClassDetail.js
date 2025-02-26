import { useParams } from "react-router";
import { useEffect, useState, useRef } from "react";
import FileSaver from "file-saver";
import { pdf } from "@react-pdf/renderer";
import { useHistory } from "react-router-dom";
import { request } from "../../api";
import { errorNoti, successNoti } from "../../utils/notification";
import { LoadingButton } from "@mui/lab";
import {
  Button,
  Card,
  CardContent,
  TextField,
  Chip,
  MenuItem,
  Select,
  SelectChangeEvent,
} from "@mui/material";
import PublishIcon from "@mui/icons-material/Publish";
import SendIcon from "@mui/icons-material/Send";
import StandardTable from "../table/StandardTable";
import SelectItem from "../common/form/SelectItem";
import XLSX from "xlsx";
import { config } from "../../config/config";
import { KC_REALM } from "../../config/keycloak";
import ExportUsersPDF from "./pdf/ExportUsersPDF";
import { toast } from "react-toastify";
function ExamClassDetail() {
  const params = useParams();
  const examClassId = params.id;
  const [name, setName] = useState();
  const [description, setDescription] = useState();
  const [executeDate, setExecuteDate] = useState();
  const [statusList, setStatusList] = useState([]);
  const [status, setStatus] = useState();
  const [importedExcelFile, setImportedExcelFile] = useState(null);
  const [isProcessing, setIsProcessing] = useState(false);
  const [mapUserLogins, setMapUserLogins] = useState([]);
  const intervalIdRef = useRef(null);
  const [continueOnError, setContinueOnError] = useState(true);

  const columns = [
    { title: "UserName", field: "realUserLoginId" },
    { title: "StudentCode", field: "studentCode" },
    { title: "FullName", field: "fullname" },
    { title: "Random username", field: "randomUserLoginId" },
    { title: "Password", field: "password" },
    { title: "Status", field: "status" },
  ];
  function importStudentsFromExcel(event) {
    event.preventDefault();

    setIsProcessing(true);
    let formData = new FormData();
    formData.append("inputJson", JSON.stringify({ examClassId }));
    formData.append("file", importedExcelFile);

    let successHandler = (res) => {
      setIsProcessing(false);
      setImportedExcelFile(undefined);
      successNoti("Import sinh viên thành công", true);
      console.log(res.data);
      setMapUserLogins(res.data);
    };
    let errorHandlers = {
      onError: (error) => {
        setIsProcessing(false);
        errorNoti("Đã xảy ra lỗi khi import sinh viên", true);
      },
    };
    request(
      "POST",
      "/create-exam-accounts-map",
      successHandler,
      errorHandlers,
      formData
    );
  }

  function getExamClassDetail() {
    request("get", "/get-exam-class-detail/" + examClassId, (res) => {
      //setLoading(false);
      console.log(
        "get-exam-class-detail res = ",
        res.data,
        " len = ",
        res.data.accounts.length
      );
      setName(res.data.name);
      setDescription(res.data.description);
      setExecuteDate(res.data.executeDate);
      setStatusList(res.data.statusList);
      setStatus(res.data.status);
      setMapUserLogins(res.data.accounts);
    });
  }
  const downloadHandler = (event) => {
    if (mapUserLogins.length === 0) {
      return;
    }

    var wbcols = [];

    wbcols.push({ wpx: 80 });
    wbcols.push({ wpx: 120 });
    let rows = mapUserLogins.length;
    for (let i = 0; i < rows; i++) {
      wbcols.push({ wpx: 50 });
    }
    wbcols.push({ wpx: 50 });

    let datas = [];

    for (let i = 0; i < mapUserLogins.length; i++) {
      let data = {};
      data["Original"] = mapUserLogins[i].realUserLoginId;
      data["Fullname"] = mapUserLogins[i].fullname;
      data["MSSV"] = mapUserLogins[i].studentCode;
      data["UserName"] = mapUserLogins[i].randomUserLoginId;
      data["Password"] = mapUserLogins[i].password;

      datas[i] = data;
    }

    var sheet = XLSX.utils.json_to_sheet(datas);
    var wb = XLSX.utils.book_new();
    sheet["!cols"] = wbcols;

    XLSX.utils.book_append_sheet(wb, sheet, "students");
    XLSX.writeFile(wb, examClassId + ".xlsx");
  };

  async function generatePdfDocument() {
    toast.info("Đang chuẩn bị tệp");

    const blob = await pdf(<ExportUsersPDF data={mapUserLogins} />).toBlob();

    toast.success("Lưu file thành công");

    FileSaver.saveAs(blob, `${examClassId}.pdf`);
  }

  function handleUpdateStatus() {
    let body = {
      examClassId: examClassId,
      status: status,
    };

    request(
      "post",
      "/update-status-exam-class",
      (res) => {
        console.log("Update Status Exam Class = ", res.data);
        history.push("/exam-class/list");
      },
      {},
      body
    );
  }

  const handleChangeStatus = (event) => {
    setStatus(event.target.value);
  };

  function clearAccount(){
    let body = {
      examClassId: examClassId
      
    };

    request(
      "post",
      "/clear-account-exam-class",
      (res) => {
        console.log("Clear accounts of Exam Class = ", res.data);
        getExamClassDetail();
        //history.push("/exam-class/list");
      },
      {},
      body
    );
  }
  const randomNumberInRange = (min, max) => {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  };
  function genPass(L){
    let r = randomNumberInRange(10000,1000000);
    let s = r.toString();    
    while(len(s) < L) s = '0' + s;
    return s;
  }

  function updateFullnameOfAUserId(userId, fullname){
    // PUT
    // {{keycloak_url}}/admin/realms/{{realm}}/users/{{userId}}

    // {
    // “id”: “56f6c53f-5150-4b42-9757-4c3dd4e7d947”,
    // “enabled”: false,
    // “firstName”: “Clark”,
    // “lastName”: “Kent”
    // }
    let data = {
      id: userId,
      enabled: true,
      firstName: fullname,
      lastName: ""
    };
    const headerConfig = {
      headers: {
        "content-Type": "application/json",
      },
    };

    //  API: /auth/admin/realms/{realm}/users/{id}/reset-password
    request(
      "PUT",
      //`${config.url.KEYCLOAK_BASE_URL}/auth/admin/realms/${KC_REALM}/users/${userId}/reset-password`,
      `${config.url.KEYCLOAK_BASE_URL}/admin/realms/${KC_REALM}/users/${userId}`,
      (res) => {
        //data.status = "SUCCESS";
        //data.message = "";
        //data.password = password;
        //data.doneAt = defaultDatetimeFormat(new Date());
        //setResult(result => [...result, data]);
      },
      {
        409: (err) => {
          //data.status = "FAIL";
          //data.message = err?.response.data.errorMessage || "";
          //data.password = password;
          //data.doneAt = defaultDatetimeFormat(new Date());
          //setResult(result => [...result, data]);

          if (!continueOnError) {
            clearInterval(intervalIdRef.current);
            //setLoading(false);
          }
        },
      },
      data,
      headerConfig
    );

  }

  function updateFullnameOfAUserName(userName, fullname){
    const headerConfig = {
      headers: {
        "content-Type": "application/json",
      },
    };

    // get user_id (based on userName)
    let userId = "";
    request(
      "GET",
      //`${config.url.KEYCLOAK_BASE_URL}/auth/admin/realms/${KC_REALM}/users/${userId}/reset-password`,
      `${config.url.KEYCLOAK_BASE_URL}/admin/realms/${KC_REALM}/ui-ext/brute-force-user?briefRepresentation=true&first=0&max=11&q=&search=${userName}`,
      (res) => {
        console.log("res = ", res);
        console.log("GOT userId = ", res.data[0].id);
        //return res.data[0].id;
        updateFullnameOfAUserId(res.data[0].id, fullname);
      },
      {
        409: (err) => {
          //data.status = "FAIL";
          if (!continueOnError) {
            clearInterval(intervalIdRef.current);
            //setLoading(false);
          }
        },
      },
      headerConfig
    );

  }

  function synRandomUserInfo(){
    //alert('syn, REST API below');
    // PUT
    // {{keycloak_url}}/admin/realms/{{realm}}/users/{{userId}}

    // {
    // “id”: “56f6c53f-5150-4b42-9757-4c3dd4e7d947”,
    // “enabled”: false,
    // “firstName”: “Clark”,
    // “lastName”: “Kent”
    // }

    let idx = 0;
    intervalIdRef.current = setInterval(() => {
      if (idx < mapUserLogins.length) {
        //if (idx < 3) {
        var userName = mapUserLogins[idx].randomUserLoginId;
        //let userId = getUserId(userName);
        //console.log('found id = ',userId,' of username ',userName);
        //resetPasswordOfUser(mapUserLogins[idx].randomUserLoginId,mapUserLogins[idx].password);
        
        updateFullnameOfAUserName(
          mapUserLogins[idx].randomUserLoginId,
          mapUserLogins[idx].fullname
        );
        console.log('synchronize fullname -> done ', idx, '/',mapUserLogins.length,' : ', mapUserLogins[idx].randomUserLoginId,':',mapUserLogins[idx].password);
        idx++;
      }
    }, 500);

  }
  function resetPassword() {
    //for(i = 0; i < mapUserLogins.length; i++){
    // resetPasswordOfUser(mapUserLogins[i].randomUserLoginId,mapUserLogins[i].password);
    //}

    let idx = 0;
    intervalIdRef.current = setInterval(() => {
      if (idx < mapUserLogins.length) {
        //if (idx < 3) {
        var userName = mapUserLogins[idx].randomUserLoginId;
        //let userId = getUserId(userName);
        //console.log('found id = ',userId,' of username ',userName);
        //resetPasswordOfUser(mapUserLogins[idx].randomUserLoginId,mapUserLogins[idx].password);
        
        resetPasswordOfUserName(
          mapUserLogins[idx].randomUserLoginId,
          mapUserLogins[idx].password
        );
        console.log('reset password done ', idx, '/', mapUserLogins.length,': ', mapUserLogins[idx].randomUserLoginId,':',mapUserLogins[idx].password);
        idx++;
      }
    }, 500);
  }
  function resetPasswordOfUserName(userName, password) {
    const headerConfig = {
      headers: {
        "content-Type": "application/json",
      },
    };

    // get user_id (based on userName)
    let userId = "";
    request(
      "GET",
      //`${config.url.KEYCLOAK_BASE_URL}/auth/admin/realms/${KC_REALM}/users/${userId}/reset-password`,
      `${config.url.KEYCLOAK_BASE_URL}/admin/realms/${KC_REALM}/ui-ext/brute-force-user?briefRepresentation=true&first=0&max=11&q=&search=${userName}`,
      (res) => {
        console.log("res = ", res);
        console.log("GOT userId = ", res.data[0].id);
        //return res.data[0].id;
        resetPasswordOfUser(res.data[0].id, password);
      },
      {
        409: (err) => {
          //data.status = "FAIL";
          if (!continueOnError) {
            clearInterval(intervalIdRef.current);
            //setLoading(false);
          }
        },
      },
      headerConfig
    );
  }

  function resetPasswordOfUser(userId, password) {
    let data = {
      type: "password",
      temporary: false,
      value: password,
    };
    const headerConfig = {
      headers: {
        "content-Type": "application/json",
      },
    };

    //  API: /auth/admin/realms/{realm}/users/{id}/reset-password
    request(
      "PUT",
      //`${config.url.KEYCLOAK_BASE_URL}/auth/admin/realms/${KC_REALM}/users/${userId}/reset-password`,
      `${config.url.KEYCLOAK_BASE_URL}/admin/realms/${KC_REALM}/users/${userId}/reset-password`,
      (res) => {
        //data.status = "SUCCESS";
        //data.message = "";
        //data.password = password;
        //data.doneAt = defaultDatetimeFormat(new Date());
        //setResult(result => [...result, data]);
      },
      {
        409: (err) => {
          //data.status = "FAIL";
          //data.message = err?.response.data.errorMessage || "";
          //data.password = password;
          //data.doneAt = defaultDatetimeFormat(new Date());
          //setResult(result => [...result, data]);

          if (!continueOnError) {
            clearInterval(intervalIdRef.current);
            //setLoading(false);
          }
        },
      },
      data,
      headerConfig
    );
  }
  useEffect(() => {
    getExamClassDetail();
  }, []);
  return (
    <>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          columnGap: "10px",
          marginBottom: "10px",
          marginTop: "10px",
        }}
      >
        <TextField
          required
          id="name"
          label="Exam Class Name"
          placeholder="Exam Class Name"
          value={name}
          onChange={(event) => {
            setName(event.target.value);
          }}
        />
        <TextField
          required
          id="description"
          label="Description"
          placeholder="Description"
          value={description}
          onChange={(event) => {
            setDescription(event.target.value);
          }}
        />
        <TextField
          required
          id="execute_date"
          label="Date"
          placeholder="Date"
          value={executeDate}
          onChange={(event) => {
            setExecuteDate(event.target.value);
          }}
        />
      </div>

      <div>
        <Select
          labelId="status-select-label"
          id="status-select"
          value={status}
          label="status"
          onChange={handleChangeStatus}
        >
          {statusList.map((option) => (
            <MenuItem value={option} key={option}>
              {option}
            </MenuItem>
          ))}
        </Select>

        <Button
          variant="contained"
          color="primary"
          style={{ marginLeft: "45px" }}
          onClick={handleUpdateStatus}
        >
          Update Status
        </Button>
      </div>
      <div>
        <div
          style={{
            display: "flex",
            alignItems: "center",
            columnGap: "10px",
            marginBottom: "10px",
            marginTop: "10px",
          }}
        >
          <Button
            variant="contained"
            color="primary"
            onClick={generatePdfDocument}
          >
            EXPORT PDF
          </Button>
          <Button variant="contained" color="primary" onClick={downloadHandler}>
            EXPORT EXCEL
          </Button>
          <Button variant="contained" color="primary" onClick={resetPassword}>
            RESET PASSWORD KEY CLOAK
          </Button>
          <Button variant="contained" color="primary" onClick={synRandomUserInfo}>
            Synchronize random user info
          </Button>
          

          <Button variant="contained" color="primary" onClick={clearAccount}>
            CLEAR
          </Button>

          <Button color="primary" variant="contained" component="label">
            <PublishIcon /> Select excel file to import
            <input
              type="file"
              hidden
              onChange={(event) => setImportedExcelFile(event.target.files[0])}
            />
          </Button>
          {importedExcelFile && (
            <Chip
              color="success"
              variant="outlined"
              label={importedExcelFile.name}
              onDelete={() => setImportedExcelFile(undefined)}
            />
          )}
          <LoadingButton
            loading={isProcessing}
            endIcon={<SendIcon />}
            disabled={!importedExcelFile}
            color="primary"
            variant="contained"
            onClick={importStudentsFromExcel}
          >
            Upload
          </LoadingButton>
        </div>
      </div>
      <div>
        <StandardTable
          title="Mapped UserLogin"
          columns={columns}
          data={mapUserLogins}
          hideCommandBar
          options={{
            selection: false,
            search: true,
            sorting: true,
          }}
        />
      </div>
    </>
  );
}

export default ExamClassDetail;
