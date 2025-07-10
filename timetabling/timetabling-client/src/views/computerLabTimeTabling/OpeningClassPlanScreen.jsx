import { Box, Button, Input } from "@mui/material";
import { DataGrid } from "@mui/x-data-grid";
import { request } from "api";
import { HustModal } from "erp-hust/lib/HustModal";
import { StandardTable } from "erp-hust/lib/StandardTable";
import { useEffect, useRef, useState } from "react";
import { toast } from "react-toastify";
import readXlsxFile from "read-excel-file";

function OpeningClassPlanScreen(){
    const [semesters, setSemesters] = useState([])
    const [currentSemester, setCurrentSemester] = useState("")
    const [classes, setClasses] = useState([])

    const [selectedFile, setSelectedFile] = useState()
    const [fileData, setFileData] = useState([])
    const [gridData, setGridData] = useState([])

    const [insertModalVisible, setInsertModalVisible] = useState(false)
    const [isLoading, setIsLoading] = useState(false)

    const [selectedClassIds, setSelectedClassIds] = useState([])
    const [semesterInput, setSemesterInput] = useState("")
    const columns = [ 
        {headerName: 'ID', field: 'id'},
        {headerName: 'Khoa/TT', field: 'department_id'}, 
        {headerName: 'Mã HP', field: 'course_code'}, 
        {headerName: 'Tên HP', field: 'course_name'}, 
        {headerName: 'Khối lượng', field: 'amount'}, 
        {headerName: 'Note lớp', field: 'note'}, 
        {headerName: 'CTĐT', field: 'program'}, 
        {headerName: 'Khóa', field: 'year'}, 
        {headerName: 'số lớp tham khảo kỳ 2022.1', field: 'number_of_prev_sem_classes'}, 
        {headerName: 'số sv đã đk', field: 'registered_quantity'}, 
        {headerName: 'Số lớp dự kiến kỳ 2023.1', field: 'expected_number_of_classes'}, 
        {headerName: 'số tiết', field: 'number_of_lessons'}, 
        {headerName: 'Tổng tiết', field: 'total_lessons'}, 
        {headerName: 'Dự kiến xếp', field: 'expected_schedule'}, 
        {headerName: 'Lịch tránh trùng', field: 'avoid'}
    ]

    const table_columns = [
        {title: 'ID', field: 'id'},
        {title: 'Khoa/TT', field: 'department_id'}, 
        {title: 'Mã HP', field: 'course_code'}, 
        {title: 'Tên HP', field: 'course_name'}, 
        {title: 'Khối lượng', field: 'amount'}, 
        {title: 'Note lớp', field: 'note'}, 
        {title: 'CTĐT', field: 'program'}, 
        {title: 'Khóa', field: 'year'}, 
        {title: 'số lớp tham khảo kỳ 2022.1', field: 'number_of_prev_sem_classes'}, 
        {title: 'số sv đã đk', field: 'registered_quantity'}, 
        {title: 'Số lớp dự kiến kỳ 2023.1', field: 'expected_number_of_classes'}, 
        {title: 'số tiết', field: 'number_of_lessons'}, 
        {title: 'Tổng tiết', field: 'total_lessons'}, 
        {title: 'Dự kiến xếp', field: 'expected_schedule'}, 
        {title: 'Lịch tránh trùng', field: 'avoid'}
    ]
    useEffect(() => {
        function fetch_initial_data (){
            request("get", "/class-planning/get-all", (res) => {
                setClasses(res.data.classes)
                setSemesters(res.data.semesters)
                setCurrentSemester(res.data.semesters[res.data.semesters.length-1])
            }, (err)=>{
                console.log(err)
            }).then();
        }
        fetch_initial_data()
    }, [])
    // ref: https://stackoverflow.com/questions/69313599/how-to-open-an-input-type-file-with-a-button-click-in-react
    const ref = useRef()
    const select_file_on_click = (e) => {
        ref.current.click()
        e.preventDefault();
        
    }
    //
    const file_on_change=(e)=>{
        var table_data
        if (ref.current.files[0]!=null){
            setSelectedFile(ref.current.files[0])
            readXlsxFile(ref.current.files[0]).then((rows) => {
                    for (var i=0;i<rows.length;i++){
                        if (rows[i].includes('table')){
                            table_data = rows.slice(i+1, rows.length)
                            //
                            setFileData(table_data)
                            //
                            var grid_data = []
                            table_data.slice(1).forEach((row, idx)=>{
                                var obj = {}
                                for(var k=1;k<columns.length;k++){
                                    obj[columns[k].field] = row[k-1]
                                }
                                grid_data.push({"semester": semesterInput, "id": idx, ...obj})
                            })
                            //
                            setGridData(grid_data)
                            //
                            break;
                        }
                    }
                }
            )
        }
        setInsertModalVisible(true)
    }
    const closeModal = ()=>{
        setInsertModalVisible(false)
        setIsLoading(false)
        setSelectedClassIds([])
    }
    const update_callback = (type, msg)=>{
        toast(msg, {
            position: toast.POSITION.TOP_RIGHT,
            hideProgressBar: false,
            type: type
        });
    }

    const submit_handler = (http_method, url, data)=>{
        request(http_method, url, 
        ()=>{
            update_callback("success", `${http_method} successful`)
        },
        {
            onError: (err) => {
                update_callback("error", `Failed on ${http_method} \n ${err}`)
            }
        }, data).then(()=>{
            closeModal()
        });
    }
    const insert_btn_onclick=()=>{
        var req_data = gridData.slice()
        req_data = req_data?.filter(row=>selectedClassIds.includes(row.id))?.map(row=>{
            var {id, ...rest} = row
            rest['semester'] = semesterInput 
            return rest
        })
        console.log(req_data)
        request("post", "/lab-timetabling/class-planning/batch", (res)=>{
            update_callback("success", `${res.data} success, ${req_data.length-res.data} failure`)
        }, (err)=>{}, req_data)
        closeModal()
    }
    const semester_on_change=(value)=>{
        var sem = value.target.value
        setCurrentSemester(sem)
        request("get", `/lab-timetabling/class-planning/semester/${sem}`, (res) => {
            setClasses(res.data);
        }, (err)=>{
            console.log(err)
        }).then();     
    }
    return (
        <div>
            <div style={{display:'flex', flexDirection: 'row', justifyContent: 'space-between'}}>
                <div style={{display:'flex', flexDirection: 'row', alignItems: 'center'}}>
                    <div style={{padding: "0 12px"}}>Semester</div>
                    <select onChange={value=>semester_on_change(value)}>
                        {semesters?.map(sem=>(
                            <option selected={semesters.indexOf(sem)==semesters.length-1} value={sem}>{sem}</option>
                        ))}
                    </select>
                </div>
                <div style={{display:'flex', flexDirection: 'row', alignItems: 'center'}}>
                    {selectedFile?.name}
                    <div style={{padding: "0"}}>
                        <Button color="success" variant="contained" onClick={select_file_on_click}>Import from excel</Button>
                        {/* ref: https://stackoverflow.com/questions/39484895/how-to-allow-input-type-file-to-select-the-same-file-in-react-component */}
                        <input onClick={(event)=> {event.target.value = null}} onChange={(value)=>file_on_change(value)} accept='.xlsx,.xls' style={{display: "none"}} ref={ref} type="file" />
                    </div>
                </div>
            </div>
            <StandardTable
                title="Opening Class Plan"
                columns={table_columns}
                data={classes}
                hideCommandBar={true}
                options={{
                    selection: false,
                    pageSize: 5,
                    search: true,
                    sorting: true,
                }}
            />
            <HustModal
                open={insertModalVisible}
                onClose={closeModal}
                // textClose="Update"
                title="Insert planning data"
                isLoading={isLoading}
                textOk="Save"
                onOk={insert_btn_onclick}
            >
                Semester
                <Input value={semesterInput} onChange={value=>setSemesterInput(value.target.value)}/>
                <br/>
                <Box sx={{ height: 400, width: '100%' }}>
                    <DataGrid
                        rows={gridData}
                        columns={columns}
                        initialState={{
                        pagination: {
                            paginationModel: {
                            pageSize: 10,
                            },
                        },
                        }}
                        checkboxSelection={true}
                        disableRowSelectionOnClick
                        onRowSelectionModelChange={(newRowSelectionModel)=>{
                            setSelectedClassIds(newRowSelectionModel)
                        }}
                        rowSelectionModel={selectedClassIds}
                    />
                </Box>
            </HustModal>
        </div>
    )
}

export default OpeningClassPlanScreen