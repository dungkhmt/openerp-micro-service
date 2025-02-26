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


function FBGroups() {
    const [importedExcelFile, setImportedExcelFile] = useState(null);
    const [groups, setGroups] = useState([]);
    const [isProcessing, setIsProcessing] = useState(false);
    const [batchId, setBatchId] = useState("batchId");

    const columns = [
        { title: "id", field: "id" },
        { title: "Name", field: "groupName" },
        { title: "Type", field: "groupType" },
        { title: "members", field: "numberMembers" },
        { title: "Link", field: "link" },
        { title: "Created At", field: "createStamp" },
      ];

      function importGroupsFromExcel(event) {
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
          setGroups(res.data);
        };
        let errorHandlers = {
          onError: (error) => {
            setIsProcessing(false);
            errorNoti("Exception !!", true);
          },
        };
        request(
          "POST",
          "/import-sync-fb-groups",
          successHandler,
          errorHandlers,
          formData
        );
      }

      function getAllGroups(){
        request("get", "/get-all-fb-groups", (res) => {
            //setLoading(false);
            console.log(
              "getAllGroups res = ",
              res.data,
              " len = ",
              res.data.length
            );
            setGroups(res.data);
          });
      }
      function clearGroups(){

      }

      useEffect(() => {
        getAllGroups();
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
              onClick={importGroupsFromExcel}
            >
              Upload
            </LoadingButton>
          </div>
        </div>
        <div>
          <StandardTable
            title="Groups"
            columns={columns}
            data={groups}
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

export default FBGroups;