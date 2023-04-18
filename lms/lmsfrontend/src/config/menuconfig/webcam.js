export const webcam = {
  id: "MENU_VIDEO_WEBCAM_CAPTURE",
  path: "",
  isPublic: false,
  icon: "BlurOnIcon",
  text: "Webcam",
  child: [
    {
      id: "MENU_VIDEO_WEBCAM_CAPTURE_EXECUTE",
      path: "/webcam/recorder",
      isPublic: false,
      icon: "StarBorder",
      text: "Ghi hình",
      child: [],
    },
    {
      id: "MENU_VIDEO_WEBCAM_CAPTURE_LIST",
      path: "/webcam/list",
      isPublic: false,
      icon: "StarBorder",
      text: "Danh sách đã ghi",
      child: [],
    },
  ],
};
