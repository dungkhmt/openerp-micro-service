const prefix = "/asset-management";

export const AssetManagementAsset = {
  id: "ASSET_MANAGEMENT_MENU",
  icon: "LibraryBookIcon",
  text: "Quản lý tài sản",
  child: [
    {
      id: "ASSET_MANAGEMENT_MENU.MY_ASSET",
      path: `${prefix}/my-asset`,
      isPublic: true,
      text: "Tài sản của tôi",
      child: [],
    },
		{
			id: "ASSET_MANAGEMENT_MENU.MY_REQUEST",
			path: `${prefix}/my-request`,
			isPublic: true,
			text: "Đề xuất của tôi",
			child: [],
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.LOCATION",
      path: `${prefix}/locations`,
      isPublic: true,
      text: "Quản lý địa điểm",
      child: [],
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.VENDORS",
      path: `${prefix}/vendors`,
      isPublic: true,
      text: "Quản lý nhà cung cấp",
      child: [],
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.ASSET_TYPES",
      path: `${prefix}/types`,
      isPublic: true,
      text: "Quản lý loại tài sản",
      child: [],
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.ASSETS",
			path: `${prefix}/assets`,
			isPublic: true,
			text: "Quản lý tài sản",
			child: [],
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.REQUESTS",
			path: `${prefix}/requests`,
			isPublic: true,
			text: "Quản lý đề xuất",
			child: []
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.REPORT_ASSET",
			path: `${prefix}/reports/asset`,
			isPublic: true,
			text: "Thống kê tài sản",
			child: [],
		}, 
		{
			id: "ASSET_MANAGEMENT_MENU.REPORT_REQUEST",
			path: `${prefix}/reports/request`,
			isPublic: true,
			text: "Thống kê đề xuất",
			child: [],
		}
  ],
};
