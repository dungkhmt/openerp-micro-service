import { Box, Button, IconButton } from "@mui/material";
import { useEffect, useState } from "react"
import { HustModal } from "erp-hust/lib/HustModal/HustModal";
import { request } from "api";
import { Divider } from '@mui/material';
import { DataGrid } from "@mui/x-data-grid";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { toast } from "react-toastify";
function SchedulingScreen(){
    const [selectedRooms, setSelectedRooms] = useState([])
    const [selectedRoomIds, setSelectedRoomIds] = useState([])
    const [selectRoomModalVisible, setSelectRoomModalVisible] = useState(false)
    const [rooms, setRooms] = useState([])

    const [selectedClasses, setSelectedClasses] = useState([])
    const [selectedClassIds, setSelectedClassIds] = useState([])
    const [selectClassModalVisible, setSelectClassModalVisible] = useState(false)
    const [classes, setClasses] = useState([])

    useEffect(() => {
        request("get", "/lab-timetabling/room/get-all", (res) => {
            setRooms(res.data);
        }).then();

        request("get", "/lab-timetabling/class/get-all", (res) => {
            setClasses(res.data);
        }).then();
    }, [])

    const room_table_columns = [
        {
            field: 'name',
            headerName: 'Room',
        },
        {
            field: 'capacity',
            headerName: 'Capacity',
        },
    ];

    const class_table_columns = [
        {
            field: 'id',
            headerName: 'Class ID',
        },
        {
            field: 'quantity',
            headerName: 'Quantity',
        },
        {
            field: 'period',
            headerName: 'Period',
        },
    ];
    
    const add_room_btn_onclick = ()=>{
        setSelectRoomModalVisible(true)
    }
    const save_selected_rooms = ()=>{
        setSelectedRooms(rooms.filter(room => selectedRoomIds.includes(room.id)))
        setSelectRoomModalVisible(false)
    }

    const add_class_btn_onclick = ()=>{
        setSelectClassModalVisible(true)
    }
    const save_selected_classes = ()=>{
        setSelectedClasses(classes.filter(_class => selectedClassIds.includes(_class.id)))
        setSelectClassModalVisible(false)
    }
    const update_callback = (type, msg)=>{
        toast(msg, {
            position: toast.POSITION.TOP_RIGHT,
            hideProgressBar: false,
            type: type
        });
    }
    const submit_btn_click=()=>{
        var data = {
            classList: selectedClasses,
            roomList: selectedRooms
        }
        console.log(data)
        request("post", "/lab-timetabling/submit", (res)=>{
            update_callback("success",  `post successful`)
        }, (err)=>{
            update_callback("error", "Failed on post")
        }, data)
    }
    return (
        <div>
            {/* Room selection */}
            <div style={{padding: '4px', display:'flex', flexDirection: 'row-reverse'}}>
                <Button variant="contained" onClick={add_room_btn_onclick}>Select rooms</Button>
            </div>
            <HustModal
                open={selectRoomModalVisible}
                onClose={()=>{setSelectRoomModalVisible(false)}}
                title="Select rooms"
                // isLoading={loading}
                textOk="Save"
                onOk={save_selected_rooms}
                >
                <Box sx={{ height: 400, width: '100%' }}>
                    <DataGrid
                        rows={rooms}
                        columns={room_table_columns}
                        initialState={{
                        pagination: {
                            paginationModel: {
                            pageSize: 5,
                            },
                        },
                        }}
                        pageSizeOptions={[5]}
                        checkboxSelection={true}
                        disableRowSelectionOnClick
                        onRowSelectionModelChange={(newRowSelectionModel)=>{
                            setSelectedRoomIds(newRowSelectionModel)
                        }}
                        rowSelectionModel={selectedRoomIds}
                    />
                </Box>
            </HustModal>
            <StandardTable
                title="Selected rooms"
                columns={
                    [{
                        title: "Room",
                        field: "name",
                    },
                    {
                        title: "Capacity",
                        field: "capacity",
                    }]
                }
                data={selectedRooms}
                hideCommandBar={true}
                options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                }}s
            >

            </StandardTable>
            <Divider style={{padding: '4px'}}/>
            {/* Class selection */}
            <div style={{padding: '4px', display:'flex', flexDirection: 'row-reverse'}}>
                <Button variant="contained" onClick={add_class_btn_onclick}>Select classes</Button>
            </div>
            <HustModal
                open={selectClassModalVisible}
                onClose={()=>{setSelectClassModalVisible(false)}}
                title="Select classes"
                // isLoading={loading}
                textOk="Save"
                onOk={save_selected_classes}
                >
                <Box sx={{ height: 400, width: '100%' }}>
                    <DataGrid
                        rows={classes}
                        columns={class_table_columns}
                        initialState={{
                        pagination: {
                            paginationModel: {
                            pageSize: 5,
                            },
                        },
                        }}
                        pageSizeOptions={[5]}
                        checkboxSelection={true}
                        disableRowSelectionOnClick
                        onRowSelectionModelChange={(newRowSelectionModel)=>{
                            setSelectedClassIds(newRowSelectionModel)
                        }}
                        rowSelectionModel={selectedClassIds}
                    />
                </Box>
            </HustModal>
            <StandardTable
                title="Selected classes"
                columns={
                    [{
                        title: "Class ID",
                        field: "id",
                    },
                    {
                        title: "Quantity",
                        field: "quantity",
                    },
                    {
                        title: "Period",
                        field: "period",
                    }]
                }
                data={selectedClasses}
                hideCommandBar={true}
                options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                }}s
            >
            </StandardTable>
            <Divider style={{padding: '4px'}}/>
            <div style={{padding: '4px', display:'flex', flexDirection: 'row-reverse'}}>
                <Button color="success" variant="contained" onClick={submit_btn_click}>Schedule</Button>
            </div>
        </div>
    )
}

export default SchedulingScreen