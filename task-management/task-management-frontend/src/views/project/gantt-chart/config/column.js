import GSTC from "gantt-schedule-timeline-calendar/dist/gstc.wasm.esm.min.js";
import { GSTCID } from "./lib";

export const columns = {
  data: {
    [GSTCID("id")]: {
      id: GSTCID("id"),
      data: ({ row }) => GSTC.api.sourceID(row.id),
      width: 50,
      sortable: ({ row }) => Number(GSTC.api.sourceID(row.id)),
      header: {
        content: "ID",
      },
    },
    [GSTCID("label")]: {
      id: GSTCID("label"),
      data: "label",
      sortable: "label",
      expander: true,
      isHTML: false,
      width: 260,
      header: {
        content: "Tên",
      },
    },
    [GSTCID("progress")]: {
      id: GSTCID("progress"),
      data({ row, vido }) {
        return vido.html`<div style="text-align:center">${row.progress}</div>`;
      },
      width: 80,
      sortable: "progress",
      header: {
        content: "Progress",
      },
    },
  },
};
