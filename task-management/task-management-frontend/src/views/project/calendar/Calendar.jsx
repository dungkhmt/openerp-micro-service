import FullCalendar from "@fullcalendar/react";
import listPlugin from "@fullcalendar/list";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import bootstrap5Plugin from "@fullcalendar/bootstrap5";
import interactionPlugin from "@fullcalendar/interaction";
import { useRef } from "react";
import PropTypes from "prop-types";

const Calendar = (props) => {
  const { handleAddEventSidebarToggle } = props;
  const calendarRef = useRef();

  const calendarOptions = {
    events: [],
    plugins: [
      interactionPlugin,
      dayGridPlugin,
      timeGridPlugin,
      listPlugin,
      bootstrap5Plugin,
    ],
    initialView: "dayGridMonth",
    headerToolbar: {
      start: "addTask, prev, next, title",
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
      const colorName =
        calendarsColor[calendarEvent._def.extendedProps.calendar];

      return [
        // Background Color
        `bg-${colorName}`,
      ];
    },

    eventClick({ event: clickedEvent }) {
      // TODO: set event clicked

      console.log(clickedEvent);

      handleAddEventSidebarToggle();

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
