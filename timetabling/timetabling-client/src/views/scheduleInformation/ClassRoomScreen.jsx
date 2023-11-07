import React, { useEffect, useState, forwardRef } from "react";
import { request } from "../../api";
import MaterialTable, { MTableToolbar } from "material-table";
import AddBox from '@material-ui/icons/AddBox';
import ArrowDownward from '@material-ui/icons/ArrowDownward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';
import SyncIcon from '@mui/icons-material/Sync';

function ClassRoomScreen() {

    const [classrooms, seClassrooms] = useState([]);

    useEffect(() => {
        request("get", "/classroom/get-all", (res) => {
            seClassrooms(res.data);
        }).then();
    }, [])

    const columns = [
        {
            title: "Classroom ID",
            field: "id",
        },
        {
            title: "Phòng học",
            field: "classroom",
        }
    ];

    const tableIcons = {
        Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
        Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
        Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
        Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
        DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
        Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
        Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
        Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
        FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
        LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
        NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
        PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
        ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
        Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
        SortArrow: forwardRef((props, ref) => <ArrowDownward {...props} ref={ref} />),
        ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
        ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
    };

    const handleUpdateData = () => {
        // Call your API for updating data
        request("get", "/classroom/update")
            .then(() => {
                // Reload the page upon successful update
                window.location.reload();
            })
            .catch((error) => {
                // Handle error if needed
                console.error("Error updating data:", error);
            });
    };

    return (
        <div>
            <MaterialTable
                title={"Danh sách phòng học"}
                columns={columns}
                data={classrooms}
                icons={tableIcons}
                components={{
                    Toolbar: (props) => (
                        <div style={{
                            position: "relative"
                        }}>
                            <MTableToolbar {...props} />
                            <div
                                style={{ position: "absolute", top: "16px", right: "350px" }}
                            >
                            </div>
                        </div>
                    ),
                }}
                actions={[
                    {
                        icon: () => <SyncIcon><input type="file" style={{ display: 'none' }} /></SyncIcon>,
                        tooltip: "Update data",
                        onClick: handleUpdateData,
                        isFreeAction: true,
                    },
                ]}
            />
        </div>

    );
}

export default ClassRoomScreen;