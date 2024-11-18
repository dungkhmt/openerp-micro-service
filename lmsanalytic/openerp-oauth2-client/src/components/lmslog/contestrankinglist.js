import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
//import IconButton from "@mui/material/IconButton";
//import EditIcon from "@mui/icons-material/Edit";
//import DeleteIcon from "@mui/icons-material/Delete";
//import { toFormattedDateTime } from "../../utils/dateutils";
//import { TextField, MenuItem } from "@mui/material";
import { Link } from "react-router-dom";
import { Button, TextField } from "@mui/material";
function ContestListForRanking(){

    const [data, setData] = useState([]);
    //const [contestId, setContestId] = useState("");
    const [contestIds, setContestIds] = useState([]);

    const [length, setLength] = useState(10);

    const columnContests = [
        {
            title: "ContestID",
            field: "contestId",
            render: (rowData) => (
                <Link
                  to={{
                    pathname:
                      "/contest/ranking/" + rowData["contestId"],
                  }}
                >
                  {rowData["contestId"]}
                </Link>
              ),
        },
    ];
    
    function getContestIds(){
        console.log('getContestIds');
       
        request("get", "/get-contestids", (res) => {
            setContestIds(res.data);
           
        }).then();        
 
    }
    
    useEffect(() => {
        getContestIds();
    }, [])

    function synchronizeContestSubmission(){
        request("get", "/synchronize-contest-submission/" + length, (res) => {
            //setContestIds(res.data);
           alert(res.data);
        }).then();  
    }
    return (

        <div>
            <div>
            <TextField
                    fullWidth
                    type=" number"
                    size="small"
                    id="length"
                    label="number max items"
                    value={length}
                    onChange={(event) => {
                      setLength(event.target.value);
                    }}
                    
                  />,
                <Button onClick={synchronizeContestSubmission}>
                    Synchronize
                </Button>
            </div>
            <div>

            <StandardTable
                title="Contests"
                columns={columnContests}
                data={contestIds}
                

                options={{
                    filtering: true,
                    debounceInterval: 500,
                    pageSize: 20,
                    selection: true,
                    search: true,
                  }}
            />
            </div>            
        </div>

    );

}

export default ContestListForRanking;