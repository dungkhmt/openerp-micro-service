import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
//import IconButton from "@mui/material/IconButton";
//import EditIcon from "@mui/icons-material/Edit";
//import DeleteIcon from "@mui/icons-material/Delete";
//import { toFormattedDateTime } from "../../utils/dateutils";
//import { TextField, MenuItem } from "@mui/material";
import { Link } from "react-router-dom";
function ContestListForRanking(){

    const [data, setData] = useState([]);
    //const [contestId, setContestId] = useState("");
    const [contestIds, setContestIds] = useState([]);

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

    return (

        <div>
            <div>
            <StandardTable
                title="Contests"
                columns={columnContests}
                data={contestIds}
                

                options={{
                    filtering: true,
                    debounceInterval: 500,
                    pageSize: 20,
                    selection: false,
                    search: false,
                  }}
            />
            </div>            
        </div>

    );

}

export default ContestListForRanking;