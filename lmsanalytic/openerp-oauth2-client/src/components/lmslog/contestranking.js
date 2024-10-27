import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { toFormattedDateTime } from "../../utils/dateutils";

function ContestRanking(){

    const [data, setData] = useState([]);
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
       
        request("get", "/get-contest-ranking", (res) => {
            setData(res.data);
           
        }).then();
        
 
    }
    useEffect(() => {
        getData();
    }, [])

    return (
        <div>
            <StandardTable
                title="Contest Ranking"
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

    );

}

export default ContestRanking;