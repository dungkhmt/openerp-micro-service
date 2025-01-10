import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import { LoadingButton } from "@mui/lab";
import { toFormattedDateTime } from "../../utils/dateutils";
import PublishIcon from "@mui/icons-material/Publish";
import SendIcon from "@mui/icons-material/Send";
import { errorNoti, successNoti } from "../../utils/notification";
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


function FBUsers() {
    const [importedExcelFile, setImportedExcelFile] = useState(null);
    const [users, setUsers] = useState([]);
    const [isProcessing, setIsProcessing] = useState(false);
    const [batchId, setBatchId] = useState("batchId");

    const columns = [
        { title: "id", field: "id" },
        { title: "Name", field: "name" },
        { title: "Link", field: "link" },
        { title: "Created At", field: "createStamp" },
      ];

      function importUsersFromExcel(event) {
        event.preventDefault();
    
        setIsProcessing(true);
        let formData = new FormData();
        formData.append("inputJson", JSON.stringify({ batchId }));
        formData.append("file", importedExcelFile);
    
        let successHandler = (res) => {
          setIsProcessing(false);
          setImportedExcelFile(undefined);
          successNoti("Import data succsessfully", true);
          console.log(res.data);
          setUsers(res.data);
        };
        let errorHandlers = {
          onError: (error) => {
            setIsProcessing(false);
            errorNoti("Exception !!", true);
          },
        };
        request(
          "POST",
          "/import-sync-fb-users",
          successHandler,
          errorHandlers,
          formData
        );
      }

      function getAllUsers(){
        request("get", "/get-all-fb-users", (res) => {
            //setLoading(false);
            console.log(
              "getAllUsers res = ",
              res.data,
              " len = ",
              res.data.length
            );
            setUsers(res.data);
          });
      }
      function clearGroups(){

      }

      useEffect(() => {
        getAllUsers();
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
        <div>        
            <Button variant="contained" color="primary" onClick={clearGroups}>
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
              onClick={importUsersFromExcel}
            >
              Upload
            </LoadingButton>
          </div>
        </div>
        <div>
          <StandardTable
            title="Groups"
            columns={columns}
            data={users}
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

export default FBUsers;