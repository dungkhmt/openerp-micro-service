import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
//import IconButton from "@mui/material/IconButton";
//import EditIcon from "@mui/icons-material/Edit";
//import DeleteIcon from "@mui/icons-material/Delete";
//import { toFormattedDateTime } from "../../utils/dateutils";
//import { TextField, MenuItem } from "@mui/material";
import { useParams } from "react-router-dom";
function ContestRanking(){

    const [data, setData] = useState([]);
    const { contestId } = useParams();
    
    const columns = [
        {
            title: "User",
            field: "userId",
        },
        {
            title: "Contest",
            field: "contestId",
        },
        {
            title: "Point",
            field: "point",
        }
    ];
    function getData(){
       console.log('getData, contest ', contestId);
        request("get", "/get-contest-ranking/" + contestId, (res) => {
            setData(res.data);           
        }).then();
        
 
    }
   
    
    useEffect(() => {
        try {
          var refreshIntervalId = setInterval(async () => {
            getData();
            
          }, 3000);
        } catch (e) {
          console.log("FOUND exception", e);
        }
    
        return function cleanInterval() {
          clearInterval(refreshIntervalId);
        };
    
        //getResults();
        
      }, []);
    
    


    return (

        <div>
            <div>
            <StandardTable
                title={contestId}
                columns={columns}
                data={data}
                

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

export default ContestRanking;