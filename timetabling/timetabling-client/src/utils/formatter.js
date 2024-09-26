function convertToSchedule(number) {
  if (number < 1 || number > 60) {
    return "Số không hợp lệ";
  }

  var day = Math.ceil(number / 12) + 1;
  var partOfDay = Math.ceil((number % 12) / 6);
  var lesson = number % 6 === 0 ? 6 : number % 6;

  var schedule = "";
  if (partOfDay === 1) {
    schedule += "Sáng";
  } else {
    schedule += "Chiều";
  }

  schedule += " thứ " + day + " tiết " + lesson;

  return schedule;
}

function datetimeForFN(date) {
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, "0");
  const day = date.getDate().toString().padStart(2, "0");
  const hours = date.getHours().toString().padStart(2, "0");
  const minutes = date.getMinutes().toString().padStart(2, "0");
  const seconds = date.getSeconds().toString().padStart(2, "0");

  return `${year}-${month}-${day}_${hours}-${minutes}-${seconds}`;
}
const weeks_Of_Semester = Array.from({length: 20}, (_, index) => ({ id: index + 1, name: (index + 1).toString() }));
const slots_Of_Period = Array.from({length: 6}, (_, index) => ({ id: index + 1, name: (index + 1).toString() }));

const days_Of_Week = [
  { id: 2, name: "Thứ 2" },
  { id: 3, name: "Thứ 3" },
  { id: 4, name: "Thứ 4" },
  { id: 5, name: "Thứ 5" },
  { id: 6, name: "Thứ 6" },
  { id: 7, name: "Thứ 7" },
  { id: 8, name: "Chủ Nhật" },
];

const periods_Of_Day = [
    { id: 1, name: "Sáng" },
    { id: 2, name: "Chiều" },
]

const time_By_Slot = [
  {id: 1, time: "6h45"},
  {id: 2, time: "7h35"},
  {id: 3, time: "8h30"},
  {id: 4, time: "9h20"},
  {id: 5, time: "10h15"},
  {id: 6, time: "11h05"},
  {id: 7, time: "12h30"},
  {id: 8, time: "13h20"},
  {id: 9, time: "14h15"},
  {id: 10, time: "15h05"},
  {id: 11, time: "16h00"},
  {id: 12, time: "16h50"},
]

const submission_status = [
  {id: 0, name: "Chờ xử lý"},
  {id: 1, name: "Hoàn thành"},
]

const time_limit_input = [
  {id: -1, name: "Không giới hạn"},
  {id: 30, name: "30 giây"},
  {id: 60, name: "60 giây"},
  {id: 90, name: "90 giây"},
]
export { time_limit_input, time_By_Slot, convertToSchedule, datetimeForFN, weeks_Of_Semester, days_Of_Week, periods_Of_Day, slots_Of_Period , submission_status};