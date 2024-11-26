import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { toFormattedDateTime } from "../../utils/dateutils";
function UserFeatures() {
    const [data, setData] = useState([]);
    const columns = [
        {
            title: "User",
            field: "userId",
        },
        {
            title: "Feature",
            field: "featureId",
        },
        {
            title: "Value",
            field: "value",
        }
    ];
    function getUserFeatures(){
       
        request("get", "/user-features/get-user-features", (res) => {
            setData(res.data);
            /*
            const data = res.data.map((log) => ({
                id: log.id,
                userId: log.userId,
                actionType: log.actionType,
                createdAt: toFormattedDateTime(log.createdStamp)
                
              }));
              setLogs(data);
            */
        }).then();
        
 
    }
    useEffect(() => {
        getUserFeatures();
    }, [])

    return (
        <div>
            <StandardTable
                title="Features"
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

export default UserFeatures;