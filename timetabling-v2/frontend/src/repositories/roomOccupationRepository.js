import { request } from "api";

export const roomOccupationRepository = {
  getRoomOccupations: (semester, weekIndex) => {
    return request(
      "get", 
      `/room-occupation/?semester=${semester}&weekIndex=${weekIndex}`
    );
  },

  exportToExcel: (semester, week) => {
    return request(
      "post",
      `room-occupation/export?semester=${semester}&week=${week}`,
      null,
      null,
      { responseType: "arraybuffer" }
    );
  }
};
