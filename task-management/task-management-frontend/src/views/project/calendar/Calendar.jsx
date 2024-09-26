import bootstrap5Plugin from "@fullcalendar/bootstrap5";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import listPlugin from "@fullcalendar/list";
import FullCalendar from "@fullcalendar/react";
import timeGridPlugin from "@fullcalendar/timegrid";
import dayjs from "dayjs";
import PropTypes from "prop-types";
import { useRef } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { setRange, setView } from "../../../store/project/calendar";
import { getStatusColor } from "../../../utils/color.util";

const Calendar = (props) => {
  const { handleAddEventSidebarToggle } = props;
  const { id: projectId } = useParams();
  const navigate = useNavigate();
  const calendarRef = useRef();
  const { view, tasks, range } = useSelector((state) => state.calendar);
  const dispatch = useDispatch();

  const buildEventFromTask = (tasks) =>
    tasks.map((task) => ({
      ...tasks,
      title: task.name,
      start: dayjs(task.fromDate ?? task.createdStamp).toDate(),
      end: task.dueDate
        ? dayjs(task.dueDate).toDate()
        : dayjs(task.fromDate ?? task.createdStamp)
            .add(1, "minute")
            .toDate(),
      allDay: false,
      extendedProps: {
        bg: getStatusColor(task.statusId),
        url: `/project/${projectId}/task/${task.id}`,
      },
    }));

  /**
   * @type import("@fullcalendar/core").CalendarOptions
   */
  const calendarOptions = {
    events: buildEventFromTask(tasks),
    plugins: [
      interactionPlugin,
      dayGridPlugin,
      timeGridPlugin,
      listPlugin,
      bootstrap5Plugin,
    ],
    locale: "vi",
    initialView: view,
    initialDate: dayjs(range.startDate).endOf("week").toDate(),
    headerToolbar: {
      start: "prev, next, title",
      end: "dayGridMonth,timeGridWeek,timeGridDay,listMonth",
    },
    views: {
      week: {
        titleFormat: { year: "numeric", month: "long", day: "numeric" },
      },
    },
    customButtons: {
      addTask: {
        text: "Thêm việc",
        click: function () {
          handleAddEventSidebarToggle();
        },
        hint: "Thêm nhiệm vụ",
      },
    },
    datesSet: (event) => {
      console.log(event);
      dispatch(setRange({ startDate: event.start, endDate: event.end }));
      dispatch(setView(event.view.type));
    },
    /*
      Enable dragging and resizing event
      ? Docs: https://fullcalendar.io/docs/editable
    */
    editable: true,

    /*
      Enable resizing event from start
      ? Docs: https://fullcalendar.io/docs/eventResizableFromStart
    */
    eventResizableFromStart: true,

    /*
      Automatically scroll the scroll-containers during event drag-and-drop and date selecting
      ? Docs: https://fullcalendar.io/docs/dragScroll
    */
    dragScroll: true,

    /*
      Max number of events within a given day
      ? Docs: https://fullcalendar.io/docs/dayMaxEvents
    */
    dayMaxEvents: 2,

    /*
      Determines if day names and week names are clickable
      ? Docs: https://fullcalendar.io/docs/navLinks
    */
    navLinks: true,

    eventClassNames({ event: calendarEvent }) {
      const colorName = calendarEvent._def.extendedProps.bg;

      return [
        // Background Color
        `bg-${colorName}`,
      ];
    },

    eventClick({ event: clickedEvent }) {
      // TODO: set event clicked

      console.log(clickedEvent);

      // handleAddEventSidebarToggle();
      navigate(clickedEvent._def.extendedProps.url);

      // * Only grab required field otherwise it goes in infinity loop
      // ! Always grab all fields rendered by form (even if it get `undefined`) otherwise due to Vue3/Composition API you might get: "object is not extensible"
      // event.value = grabEventDataFromEventApi(clickedEvent)

      // isAddNewEventSidebarActive.value = true
    },

    dateClick(info) {
      // const ev = { ...blankEvent };
      // ev.start = info.date;
      // ev.end = info.date;
      // ev.allDay = true;

      // TODO: set event clicked

      console.log(info);

      handleAddEventSidebarToggle();
    },

    /*
      Handle event drop (Also include dragged event)
      ? Docs: https://fullcalendar.io/docs/eventDrop
      ? We can use `eventDragStop` but it doesn't return updated event so we have to use `eventDrop` which returns updated event
    */
    eventDrop({ event: droppedEvent }) {
      // update here

      console.log(droppedEvent);
    },

    /*
      Handle event resize
      ? Docs: https://fullcalendar.io/docs/eventResize
    */
    eventResize({ event: resizeEvent }) {
      // update here

      console.log(resizeEvent);
    },

    ref: calendarRef,
  };

  return <FullCalendar {...calendarOptions} />;
};

Calendar.propTypes = {
  handleAddEventSidebarToggle: PropTypes.func.isRequired,
};

export default Calendar;
