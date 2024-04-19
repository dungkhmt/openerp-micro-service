import {useParams} from "react-router";
import {useEffect, useState} from "react";
import {useHistory} from "react-router-dom";
import {request} from "../../api";
import {errorNoti, successNoti} from "../../utils/notification";
import {LoadingButton} from "@mui/lab";
import {Button, Card, CardContent, TextField,Chip,MenuItem ,Select,SelectChangeEvent} from "@mui/material";
import PublishIcon from '@mui/icons-material/Publish';
import SendIcon from '@mui/icons-material/Send';
import StandardTable from "../table/StandardTable";
import SelectItem from "../common/form/SelectItem";
import XLSX from "xlsx";
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
      { title: "Random username", field: "randomUserLoginId" },
      { title: "Password", field: "password" },
      { title: "Status", field: "status" }
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

      const handleChangeStatus = (event) => {
        setStatus(event.target.value);
      };

      useEffect(() =>{
        getExamClassDetail();
      },[])
    return(
        <>
        <div>
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
             </div>
             <div> 
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
              </div>
              <div>
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
          { statusList.map(option => (
          <MenuItem value={option} key={option}>
            { option}
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
        <div><Button
            variant="contained"
            color="primary"
            style={{ marginLeft: "45px" }}
            onClick={downloadHandler}
          >
            EXPORT EXCEL 
          </Button> </div>
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