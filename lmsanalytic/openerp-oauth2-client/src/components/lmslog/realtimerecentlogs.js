import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import { toFormattedDateTime } from "../../utils/dateutils";
function RealtimeRecentLogs(){

    const [data, setData] = useState([]);

    const columns = [
        {
            title: "id",
            field: "id",
        },
        {
            title: "User",
            field: "userId",
        },
        {
            title: "Action",
            field: "actionType",
        },
        {
            title: "Date",
            field: "createdStamp",
        }
    ];

    function getData(){
        request("get", "/log/get-mostrecent-logs", (res) => {
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
            <StandardTable
                title="Realtime Logs"
                columns={columns}
                data={data}
                

                options={{
                    filtering: false,
                    debounceInterval: 500,
                    pageSize: 20,
                    selection: false,
                    search: true,
                  }}
            />
        </div>

    );

}

export default RealtimeRecentLogs;