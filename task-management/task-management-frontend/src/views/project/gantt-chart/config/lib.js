import GSTC from "gantt-schedule-timeline-calendar/dist/gstc.wasm.esm.min.js";

export const GSTCID = GSTC.api.GSTCID;
export const iterations = 100;
export const addDays = 30;
export const startDate = GSTC.api.date("2020-02-01");
export const startTime = startDate.valueOf();
export const endDate = GSTC.api.date("2020-03-31").endOf("day");

let snapTime = true;
let hideWeekends = false;

export function snapStart({ startTime, vido }) {
  if (!snapTime) return startTime;
  const date = vido.api.time.findOrCreateMainDateAtTime(startTime.valueOf());
  return date.leftGlobalDate;
}

export function snapEnd({ endTime, vido }) {
  if (!snapTime) return endTime;
  const date = vido.api.time.findOrCreateMainDateAtTime(endTime.valueOf());
  return date.rightGlobalDate;
}

export function canMove(/* item */) {
  // const row = gstc.api.getRow(item.rowId);
  // if (row.vacations) {
  //   for (const vacation of row.vacations) {
  //     const vacationStart = vacation.from;
  //     const vacationEnd = vacation.to;
  //     // item start time inside vacation
  //     if (item.time.start >= vacationStart && item.time.start <= vacationEnd) {
  //       return false;
  //     }
  //     // item end time inside vacation
  //     if (item.time.end >= vacationStart && item.time.end <= vacationEnd) {
  //       return false;
  //     }
  //     // vacation is between item start and end
  //     if (item.time.start <= vacationStart && item.time.end >= vacationEnd) {
  //       return false;
  //     }
  //     // item start and end time is inside vacation
  //     if (item.time.start >= vacationStart && item.time.end <= vacationEnd) {
  //       return false;
  //     }
  //   }
  // }
  return true;
}

export function myItemSlot(vido, props) {
  const { onChange } = vido;

  function onClick() {
    console.log("Item click from slot", props.item);
  }

  onChange((changedProps) => {
    // if current element is reused to display other item data just update your data so when you click you will display right alert
    props = changedProps;
  });

  // return render function
  return (content) =>
    vido.html`<div class="my-item-wrapper" @click=${onClick} style="width:100%;display:flex;overflow:hidden;pointer-events:none;">${content}</div>`;
}

export function itemSlot(vido, props) {
  const { html, onChange, update } = vido;

  let imageSrc = "";
  let description = "";
  onChange((newProps) => {
    props = newProps;
    if (!props || !props.item) return;
    imageSrc = props.item.img;
    description = props.item.description;
    update();
  });

  return (content) =>
    html`<div
        class="item-image"
        style="background:url(${imageSrc}),transparent;flex-shrink:0;border-radius:100%;width:34px;height:34px;vertical-align: middle;background-size: 100%;margin: 4px 11px 0px 0px;"
      ></div>
      <div class="item-text">
        <div class="item-label">${content}</div>
        <div
          class="item-description"
          style="font-size:11px;margin-top:2px;color:#fffffff0;line-height:1em;"
        >
          ${description}
        </div>
      </div>`;
}

export function rowSlot(vido, props) {
  const { html, onChange, update, api } = vido;

  let img = "";
  onChange((newProps) => {
    props = newProps;
    if (!props || !props.row) return;
    img = props.row.img;
    update();
  });

  return (content) => {
    if (!props || !props.column) return content;
    return api.sourceID(props.column.id) === "label"
      ? html`<div class="row-content-wrapper" style="display:flex">
          <div class="row-content" style="flex-grow:1;">${content}</div>
          <div
            class="row-image"
            style="background:url(${img}),transparent;border-radius:100%;width:34px;height:34px;vertical-align: middle;background-size: 100%;margin: auto 10px;flex-shrink:0;"
          ></div>
        </div>`
      : content;
  };
}

function onItemClick(ev) {
  const itemElement = ev.target.closest(".gstc__chart-timeline-items-row-item");
  const itemId = itemElement.dataset.gstcid;
  const item = gstc.api.getItem(itemId);
  console.log("Item click from template", item);
}

/**
 * @type {import('gantt-schedule-timeline-calendar/dist/gstc').Template}
 */
export function chartTimelineItemsRowItemTemplate({
  className,
  labelClassName,
  styleMap,
  cache,
  shouldDetach,
  cutterLeft,
  cutterRight,
  getContent,
  actions,
  slots,
  html,
  props,
}) {
  const detach = shouldDetach || !props || !props.item;
  return cache(
    detach
      ? null
      : slots.html(
          "outer",
          html`
            <div
              class=${className}
              data-gstcid=${props.item.id}
              data-actions=${actions()}
              style=${styleMap.directive()}
              @click=${onItemClick}
            >
              ${slots.html(
                "inner",
                html`
                  ${cutterLeft()}
                  <div class=${labelClassName}>
                    ${slots.html("content", getContent())}
                  </div>
                  ${cutterRight()}
                `
              )}
            </div>
          `
        )
  );
}

export function onCellCreateVacation({ time, row, vido, content }) {
  if (!row.vacations) return content;
  let isVacation = false;
  for (const vacation of row.vacations) {
    if (time.leftGlobal >= vacation.from && time.rightGlobal <= vacation.to) {
      isVacation = true;
      break;
    }
  }
  if (isVacation) {
    return vido.html`<div title="🏖️ VACATION" style="height:100%;width:100%;background:#A0A0A010;"></div>${content}`;
  }
  return content;
}

export function myVacationRowSlot(vido, props) {
  const { onChange, html, update, api, state } = vido;

  let vacationContent = [];
  onChange((changedProps) => {
    props = changedProps;
    if (!props || !props.row || !props.row.vacations) {
      vacationContent = [];
      return update();
    }
    const configTime = state.get("config.chart.time");
    vacationContent = [];
    for (const vacation of props.row.vacations) {
      if (
        vacation.to < configTime.leftGlobal ||
        vacation.from > configTime.rightGlobal
      )
        continue; // birthday date is out of the current view
      const leftPx = api.time.getViewOffsetPxFromDates(
        api.time.date(vacation.from)
      );
      const rightPx = api.time.getViewOffsetPxFromDates(
        api.time.date(vacation.to)
      );
      const widthPx = rightPx - leftPx - 1;
      if (widthPx < 0) continue;
      let textAlign = "left";
      if (widthPx <= 100) textAlign = "center";
      vacationContent.push(
        html`<div
          style="position:absolute;left:${leftPx}px;width:${widthPx}px;height:14px;white-space: nowrap;text-overflow:ellipsis;overflow:hidden;font-size:11px;background:#A0A0A0;color:white;text-align:${textAlign};"
        >
          Vacation
        </div>`
      );
    }
    update();
  });

  return (content) => html`${vacationContent}${content}`;
}

export function onCellCreateBirthday({ time, row, vido, content }) {
  if (!row.birthday) return content;
  let isBirthday = false;
  for (const birthday of row.birthday) {
    if (time.leftGlobal >= birthday.from && time.rightGlobal <= birthday.to) {
      isBirthday = true;
      break;
    }
  }
  if (isBirthday) {
    return vido.html`<div title="🎁 BIRTHDAY" style="height:100%;width:100%;font-size:18px;background:#F9B32F10;"></div>${content}`;
  }
  return content;
}

export function myBirthdayRowSlot(vido, props) {
  const { onChange, html, update, api, state } = vido;

  let birthdayContent = [];
  onChange((changedProps) => {
    props = changedProps;
    if (!props || !props.row || !props.row.birthday) {
      birthdayContent = [];
      return update();
    }
    const configTime = state.get("config.chart.time");
    birthdayContent = [];
    for (const birthday of props.row.birthday) {
      if (
        birthday.to < configTime.leftGlobal ||
        birthday.from > configTime.rightGlobal
      )
        continue; // birthday date is out of the current view
      const leftPx = api.time.getViewOffsetPxFromDates(
        api.time.date(birthday.from)
      );
      const rightPx = api.time.getViewOffsetPxFromDates(
        api.time.date(birthday.to)
      );
      const widthPx = rightPx - leftPx - 1;
      if (widthPx < 0) continue;
      let textAlign = "left";
      if (widthPx <= 100) textAlign = "center";
      birthdayContent.push(
        html`<div
          style="position:absolute;left:${leftPx}px;width:${widthPx}px;height:14px;white-space: nowrap;text-overflow:ellipsis;overflow:hidden;font-size:11px;background:#F9B32F;color:white;text-align:${textAlign};"
        >
          🎁 Birthday
        </div>`
      );
    }
    update();
  });

  return (content) => html`${birthdayContent}${content}`;
}

export function onLevelDates({ dates, format, time }) {
  if (time.period !== "day") return dates;
  if (format.period !== time.period) return dates;
  if (!hideWeekends) return dates;
  return dates.filter(
    (date) => date.leftGlobalDate.day() !== 0 && date.leftGlobalDate.day() !== 6
  );
}
