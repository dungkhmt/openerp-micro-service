import { useQuery } from 'react-query';
import { roomOccupationRepository } from 'repositories/roomOccupationRepository';
import ReactDOMServer from 'react-dom/server';

const getTimeByPeriod = (period) => {
  switch (period) {
    case 1: return { hours: 6, minutes: 45 };
    case 2: return { hours: 7, minutes: 30 };
    case 3: return { hours: 8, minutes: 25 };
    case 4: return { hours: 9, minutes: 10 };
    case 5: return { hours: 10, minutes: 5 };
    case 6: return { hours: 10, minutes: 50 };
    case 7: return { hours: 12, minutes: 30 };
    case 8: return { hours: 13, minutes: 15 };
    case 9: return { hours: 14, minutes: 15 };
    case 10: return { hours: 15, minutes: 0 };
    case 11: return { hours: 15, minutes: 45 };
    case 12: return { hours: 16, minutes: 30 };
    default:
      console.log("Wrong period: " + period);
      return null;
  }
};

const formatDate = (date) => {
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const year = date.getFullYear();
  return `${hours}:${minutes}, ngày ${day}/${month}/${year}`;
};

const convertToToolTip = (classCode, startTime, endTime) => (
  <div className="custom-tooltip">
    <div className="header-title">Mã lớp: {classCode}</div>
    <div className="divider"></div>
    <div className="body-content">
      <div>Thời gian bắt đầu: {formatDate(startTime)}</div>
      <div>Thời gian kết thúc: {formatDate(endTime)}</div>
      <div>
        Thời gian kéo dài:{" "}
        {Math.floor(Math.abs(endTime - startTime) / (1000 * 60))} phút
      </div>
    </div>
  </div>
);

const formatTimeSlot = (timeSlot, startDate) => {
  const offset = timeSlot.crew === "C" ? 6 : 0;
  const start = new Date(startDate);

  const { hours: startHours, minutes: startMinutes } = getTimeByPeriod(
    timeSlot.startPeriod + offset
  );
  const { hours: endHours, minutes: endMinutes } = getTimeByPeriod(
    timeSlot.endPeriod + offset
  );

  start.setHours(startHours);
  start.setDate(
    start.getDate() +
      (Number(timeSlot.weekIndex) - 1) * 7 +
      Number(timeSlot.dayIndex) -
      2
  );
  start.setMinutes(startMinutes);

  const end = new Date(startDate);
  end.setHours(endHours);
  end.setDate(
    end.getDate() +
      (Number(timeSlot.weekIndex) - 1) * 7 +
      Number(timeSlot.dayIndex) -
      2
  );
  end.setMinutes(endMinutes);

  return [
    timeSlot.classRoom,
    timeSlot.classCode,
    ReactDOMServer.renderToStaticMarkup(
      convertToToolTip(timeSlot.classCode, start, end)
    ),
    start,
    end,
  ];
};

export const useRoomOccupationData = (semester, selectedWeek, startDate) => {
  return useQuery(
    ['roomOccupations', semester, selectedWeek],
    () => roomOccupationRepository.getRoomOccupations(semester, selectedWeek),
    {
      enabled: Boolean(semester && startDate), 
      staleTime: 5 * 60 * 1000, 
      cacheTime: 30 * 60 * 1000, 
      select: (res) => {
        return res.data
          ?.map(timeSlot => {
            if (timeSlot?.startTime !== null && 
                timeSlot?.endTime !== null && 
                timeSlot?.weekIndex !== null && 
                timeSlot?.dayIndex !== null) {
              return formatTimeSlot(timeSlot, startDate);
            }
            return null;
          })
          ?.filter(x => x !== null);
      }
    }
  );
};
