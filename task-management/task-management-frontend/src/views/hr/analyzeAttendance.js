import { eachDayOfInterval, isWeekend, format } from 'date-fns';

function analyzeAttendance(attendanceData, holidays, monthDateStr) {
  let absentDays = 0, incompleteDays = 0, presentDays = 0;
  const [year, month] = monthDateStr.split('-').map(Number);

  // Lấy mảng tất cả ngày trong tháng
  const daysInMonth = eachDayOfInterval({
    start: new Date(year, month - 1, 1),
    end: new Date(year, month, 0)
  });

  for (let day of daysInMonth) {
    const dateStr = format(day, 'yyyy-MM-dd');
    const isHoliday = holidays?.[dateStr];
    if (isWeekend(day) || isHoliday) continue; // Bỏ qua ngày lễ/cuối tuần

    const attend = attendanceData[dateStr];
    if (!attend || attend.attendanceType === "ABSENT" || attend.attendanceType === "MISSING") {
      absentDays += 1;
    } else if (attend.attendanceType === "INCOMPLETE") {
      incompleteDays += 1;
    } else if (attend.attendanceType === "PRESENT") {
      presentDays += 1;
    }
  }
  return { absentDays, incompleteDays, presentDays };
}
