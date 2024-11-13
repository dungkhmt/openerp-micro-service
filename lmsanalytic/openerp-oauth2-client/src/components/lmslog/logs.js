import React, {useEffect, useState} from "react";
import {request} from "../../api";
import {StandardTable} from "erp-hust/lib/StandardTable";
import IconButton from "@mui/material/IconButton";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import { toFormattedDateTime } from "../../utils/dateutils";
import UserFeatures from "./userfeatures";
import ContestRanking from "./contestranking";
import ContestProblemRanking from "./contestproblemranking";
import RealtimeRecentLogs from "./realtimerecentlogs";


function LmsLogs() {

    const [logs, setLogs] = useState([]);
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
        },
        
        {
            title: "Edit",
            sorting: false,
            render: (rowData) => (
                <IconButton
                    onClick={() => {
                        demoFunction(rowData)
                    }}
                    variant="contained"
                    color="success"
                >
                    <EditIcon/>
                </IconButton>
            ),
        }
    ];

    useEffect(() => {
        /*
        request("get", "/log/get-logs", (res) => {
            //setLogs(res.data);
            const data = res.data.map((log) => ({
                id: log.id,
                userId: log.userId,
                actionType: log.actionType,
                createdAt: toFormattedDateTime(log.createdStamp)
                
              }));
              setLogs(data);

        }).then();
        */
    }, [])


    const demoFunction = (user) => {
        alert("You clicked on User: " + user.id)
    }

    return (
        <div>
            <StandardTable
                title="LMSLOGS"
                columns={columns}
                //data={logs}
                data = {(query) => new Promise((resolve, reject) => {
                    let url = `/log/get-logs?page=${query.page}&size=${query.pageSize}`;
        
                    query.filters.forEach((element) => {
                      url += `&${element.column.field}=${element.value}`;
                    });
        
                    request(
                      "get",
                      url,
                      (res) => {
                        const data = res.data;
                        const content = data.content.map((log) => ({
                          id: log.id,
                          userId: log.userId,
                          actionType: log.actionType,
                          description: log.description,
                          param1: log.param1,
                          param2: log.param2,
                          param3: log.param3,
                          param4: log.param4,
                          param5: log.param5,
                          createdStamp: toFormattedDateTime(log.createdStamp),
                        }));
        
                        resolve({
                          data: content, // your data array
                          page: data.number, // current page number
                          totalCount: data.totalElements, // total row number
                        });
                      },
                      {
                        onError: (e) => {
                          reject({
                            message:
                              "Đã có lỗi xảy ra trong quá trình tải dữ liệu. Thử lại ",
                            errorCause: "query",
                          });
                        },
                      }
                    );
                  })

                }
                // hideCommandBar
                //options={{
                //    selection: false,
                //    pageSize: 20,
                //    search: true,
                //    sorting: true,
                //}}

                options={{
                    filtering: true,
                    debounceInterval: 500,
                    pageSize: 20,
                    selection: false,
                    search: false,
                  }}
            />

            <UserFeatures/>
            <ContestProblemRanking/>
            <RealtimeRecentLogs/>
        </div>



    );
}

export default LmsLogs;