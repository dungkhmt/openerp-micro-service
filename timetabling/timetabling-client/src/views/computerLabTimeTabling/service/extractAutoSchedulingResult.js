import { request } from "api";
import { string } from "prop-types";
import writeXlsxFile from "write-excel-file";

const { datetimeForFN, periods_Of_Day } = require("utils/formatter");

function createFixed2DArray(rows, columns) {
    const arr = [];
    for (let i = 0; i < rows; i++) {
      const row = [];
      for (let j = 0; j < columns; j++) {
        row.push(null); // Giá trị ban đầu của mỗi phần tử có thể là gì tùy thuộc vào yêu cầu của bạn
      }
      arr.push(row);
    }
    return arr;
  }

// const download_result_file = async (submission, result_array) =>{  
//     const data = await produce_result_data(submission, result_array)
//     writeXlsxFile(data, {
//         fileName: `result_${datetimeForFN(new Date())}.xlsx`,
//     });
// }

const download_result_file = async (data) =>{  
    writeXlsxFile(data, {
        fileName: `result_${datetimeForFN(new Date())}.xlsx`,
    });
}

const produce_result_data = async (submission, result_array) =>{
    var class_list = []
    var room_list = []
    await request("get", `/lab-timetabling/class/semester/${submission.semester_id}`, (res)=>{
        class_list = res.data;
    }).then()  
    await request("get", `/lab-timetabling/room/get-all`, (res)=>{
        room_list = res.data;
    }).then()
    const groupedData = result_array.reduce((acc, currentValue) => {
        const classId = currentValue.class_id;
        const existingGroup = acc.find(item => item.class_id === classId);
    
        if (existingGroup) {
            existingGroup.weeks.push(Number(currentValue.week)+1);
        } else {
            acc.push({
                class_id: classId,
                weeks: [Number(currentValue.week)+1],
                day_of_week: currentValue.day_of_week,
                period: currentValue.period,
                start_slot: currentValue.start_slot,
                room_id: currentValue.room_id,
            });
        }
    
        return acc;
    }, []);
    
    groupedData.map(item=>{
        item.class_name = class_list.find(c=>c.id == item.class_id)?.note;
        item.room_name = room_list.find(r=>r.id == item.room_id).name;
    })
    
    var data = createFixed2DArray(groupedData.length+2, 7)
    data[0][0] = {
        value: "Lịch thí nghiệm dự kiến kì "+submission.semester.semester,
        span: 7,
        fontWeight: 'bold',
        align: 'center'
    }
    data[1] = ["Id", "Tên lớp", "Tuần", "Thứ", "Buổi", "Tiết", "Phòng"].map((item)=>{
        return {
            value: item,
            fontWeight: 'bold',
            align: 'center'
        }
    })
    for(var i=2;i<groupedData.length+2;i++){
        const period_id = groupedData[i-2].period
        data[i] = [''+(i-1), groupedData[i-2].class_name, groupedData[i-2].weeks.join(", "), groupedData[i-2].day_of_week, periods_Of_Day.find(i=>i.id==period_id).name, groupedData[i-2].start_slot, groupedData[i-2].room_name].map(item=>{
            return {
                value: item
            }
        })
    }
    return data
}
export {download_result_file, produce_result_data}