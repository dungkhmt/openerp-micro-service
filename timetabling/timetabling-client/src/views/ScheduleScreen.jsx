import React, {useEffect, useState} from "react";
import {request} from "../api";
import MaterialTable, {MTableToolbar} from "material-table";
import {Card} from "@material-ui/core";

function ScheduleScreen() {

    const [schedules, setSchedules] = useState([]);
    const [openLoading, setOpenLoading] = React.useState(false);
    const [toggle, setToggle] = useState(false)

    useEffect(() => {
        request("get", "/excel/schedules", (res) => {
            setSchedules(res.data);
        }).then();
    }, [])

    const columns = [
        {
            title: "Schedule ID",
            field: "id",
        },
        {
            title: "Học kỳ",
            field: "semester",
        },
        {
            title: "Trường",
            field: "institute",
        },
        {
            title: "Mã lớp",
            field: "classCode",
        },
        {
            title: "Mã lớp kèm",
            field: "bundleClassCode",
        },
        {
            title: "Mã học phần",
            field: "moduleCode",
        },
        {
            title: "Tền học phần",
            field: "moduleName",
        },
        {
            title: "Tên học phần bằng TA",
            field: "moduleNameByEnglish",
        },
        {
            title: "Khối lượng",
            field: "mass",
        },
        {
            title: "Ghi chú",
            field: "notes",
        },
        {
            title: "Buổi số",
            field: "sessionNo",
        },
        {
            title: "Thứ",
            field: "weekDay",
        },
        {
            title: "Thời gian",
            field: "studyTime",
        },
        {
            title: "BĐ",
            field: "start",
        },
        {
            title: "KT",
            field: "finish",
        },
        {
            title: "Kíp",
            field: "crew",
        },
        {
            title: "Tuần",
            field: "studyWeek",
        },
        {
            title: "Phòng",
            field: "classRoom",
        },
        {
            title: "Cần TN",
            field: "isNeedExperiment",
        },
        {
            title: "SLĐK",
            field: "numberOfRegistrations",
        },
        {
            title: "SL MAX",
            field: "maxQuantity",
        },
        {
            title: "Trạng thái",
            field: "state",
        },
        {
            title: "Loại lớp",
            field: "classType",
        },
        {
            title: "Đợt mở",
            field: "openBatch",
        },
        {
            title: "Mã QL",
            field: "managementCode",
        },
    ];

    async function getAllSchedule() {
        setOpenLoading(true)
        request(
          "GET",
          "/excel/schedule",
          (res) => {
            console.log(res.data.content)
            setOpenLoading(false)
            setSchedules(res.data.content);
          }
        );
      }

      useEffect(() => {
        getAllSchedule();
      }, [toggle]);

    return (
        <Card>
        <MaterialTable
          title={"Danh sách thời khóa biểu"}
          columns={columns}
          data={schedules}
          components={{
            Toolbar: (props) => (
              <div style={{position: "relative"}}>
                <MTableToolbar {...props} />
                <div
                  style={{position: "absolute", top: "16px", right: "350px"}}
                >
                </div>
              </div>
            ),
          }}
        />
      </Card>

    );
}

export default ScheduleScreen;