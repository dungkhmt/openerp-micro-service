import { request } from 'api';
import React, { useEffect, useState } from 'react';
import {StandardTable} from "erp-hust/lib/StandardTable";
import { errorNoti, successNoti } from 'utils/notification';
import {Link as RouterLink} from "react-router-dom";
import { Chip, Link } from '@mui/material';
import { GiSandsOfTime } from 'react-icons/gi';
import { FcApproval, FcDisapprove } from "react-icons/fc";

const MyRequestScreen = () => {
    const [assets, setAssets] = useState([]);
    const [allAssets, setAllAssets] = useState([]);
    const [requests, setRequests] = useState([]);

    const getAllAvailableAssets = async() => {
        request("get", "/asset/get-all-available", (res) => {
            setAssets(res.data);
        }).then();
    };

    const getAllAssets = async() => {
        await request("get", "/asset/get-all", (res) => {
            setAllAssets(res.data);
        }).then();
    };

    const getAllRequests = async() => {
        await request("get", "/request/get-by-creator", (res) => {
            setRequests(res.data);
        }).then();
        
    };

    const convertToDate = (date_time) => {
        const dateString = date_time;
        const dateObj = new Date(dateString);
        const options = { month: '2-digit', day: '2-digit', year: 'numeric' };
        return dateObj.toLocaleDateString('en-US', options);
    };

    useEffect(() => {
        getAllAvailableAssets();
        getAllAssets();
        getAllRequests();
    }, []);

    const columns = [
        {
            title: "Request",
            field: "name",
            render: (rowData) => (
                <Link
                    component={RouterLink}
                    to={`/request/${rowData["id"]}`}
                >
                    {rowData["name"]}
                </Link>
            ),
        },
        {
            title: "Asset",
            render: (rowData) => {
                const asset = allAssets.find(item => item.id === rowData.asset_id);
                return (
                    <div>{asset?.name}</div>
                )
            }
        },
        {
            title: "Status",
            sorting: true,
            render: (rowData) => {
                if(rowData.status === 0){
                    return (
                        <Chip
                            icon={<GiSandsOfTime size={24} />}
                            label="PENDING"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem"}}
                        />
                    )
                } else if(rowData.status === 1){
                    return (
                        <Chip
                            icon={<FcApproval size={24} />}
                            label="APPROVED"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem", color: "green", borderColor: "green"}}
                        />
                    )
                } else if(rowData.status === 2){
                    return (
                        <Chip
                            icon={<FcDisapprove size={24} />}
                            label="REJECTED"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem", color: "red", borderColor: "red"}}
                        />
                    )
                } else if(rowData.status === 3){
                    return (
                        <Chip
                            icon={<FcApproval size={24} />}
                            label="DONE"
                            color="primary"
                            variant="outlined"
                            style={{fontSize: "1rem", color: "green", borderColor: "green"}}
                        />
                    )
                }
            }
        },
        {
            title: "Approver",
            field: "admin_id"
        },
        {
            title: "StartDate",
            render: (rowData) => {
                if(rowData.start_date){
                    return <div>{convertToDate(rowData.start_date)}</div>;
                }
                return <div></div>;
            }
        },
        {
            title: "EndDate",
            render: (rowData) => {
                if(rowData.end_date){
                    return <div>{convertToDate(rowData.end_date)}</div>;
                }
                return <div></div>;
            }
        },
        {
            title: "PaybackDate",
            render: (rowData) => {
                if(rowData.payback_date){
                    return <div>{convertToDate(rowData.payback_date)}</div>;
                }
                return <div></div>;
            }
        },
    ];

    return (
        <div>
        <StandardTable
          title="My Requests"
          columns={columns}
          data={requests}
          options={{
            selection: false,
            pageSize: 20,
            search: true,
            sorting: true,
          }}
        />
      </div>
    )
};

export default MyRequestScreen;
