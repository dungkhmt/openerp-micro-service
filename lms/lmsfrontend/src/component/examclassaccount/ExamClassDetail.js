import {useParams} from "react-router";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../api";
import {errorNoti, successNoti} from "../../utils/notification";
import {LoadingButton} from "@mui/lab";
import {Button, Card, CardContent, TextField,Chip} from "@mui/material";
import PublishIcon from '@mui/icons-material/Publish';
import SendIcon from '@mui/icons-material/Send';
import StandardTable from "../table/StandardTable";
import SelectItem from "../common/form/SelectItem";
function ExamClassDetail(){
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

    const columns = [
      { title: "UserName", field: "realUserLoginId" },
      { title: "StudentCode", field: "studentCode" },
      { title: "FullName", field: "fullname" },
      { title: "random username", field: "randomUserLoginId" },
      { title: "password", field: "password" }

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
            errorNoti("Đã xảy ra lỗi khi import sinh viên", true)
          }
        }
        request("POST", "/create-exam-accounts-map", successHandler, errorHandlers, formData);
      };
    
      function getExamClassDetail(){
        request("get", "/get-exam-class-detail/" + examClassId, (res) => {
          //setLoading(false);
          console.log('get-exam-class-detail res = ',res.data);
          setName(res.data.name);
          setDescription(res.data.description);
          setExecuteDate(res.data.executeDate);
          setStatusList(res.data.statusList);
          setStatus(res.data.status);
        });
      }

      function handleUpdateStatus(){
        let body = {
          examClassId: examClassId,
          status: status
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
      useEffect(() =>{
        getExamClassDetail();
      },[])
    return(
        <>
        Exam Class Detail {examClassId}
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
            <SelectItem
                label="Status"
                style={{ width: "100%" }}
                value={status}
                options={statusList}
                onChange={(value) => setStatus(value)}
              />
             <Button
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={handleUpdateStatus}
          >
            Update Status 
          </Button> 
        <div>
        <Button color="primary" variant="contained" component="label">
          <PublishIcon/> Select excel file to import
          <input type="file" hidden
                 onChange={event => setImportedExcelFile(event.target.files[0])} />
        </Button>
        { importedExcelFile && (
          <Chip color="success" variant="outlined"
                label={importedExcelFile.name}
                onDelete={() => setImportedExcelFile(undefined)}/>
        )}
        <LoadingButton loading={isProcessing}
                       endIcon={<SendIcon/>}
                       disabled={!importedExcelFile}
                       color="primary" variant="contained"
                       onClick={importStudentsFromExcel}>
          Upload
        </LoadingButton>
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