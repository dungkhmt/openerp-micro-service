// src/features/rosterConfiguration/api.js
export const mockApiRequest = (path, params) => {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      // console.log(`Mock API Call: GET ${path} with params`, params);
      if (path === "/departments") {
        resolve({
          data: {
            data: [
              { department_code: "D001", department_name: "Phòng Kỹ thuật" }, { department_code: "D002", department_name: "Phòng Nhân sự" },
              { department_code: "D003", department_name: "Phòng Kinh doanh" },{ department_code: "D004", department_name: "Phòng Marketing" },
              { department_code: "D005", department_name: "Ban Giám đốc" }, { department_code: "D006", department_name: "Kế toán" },
              { department_code: "D007", department_name: "Kho vận" }, { department_code: "D008", department_name: "Sản xuất" },
            ], meta: { page_info: { total_page: 1, page: 0 } }
          }
        });
      } else if (path === "/jobs") {
        resolve({
          data: {
            data: [
              { code: "J001", name: "Lập trình viên Backend" }, { code: "J002", name: "Chuyên viên Nhân sự" },
              { code: "J003", name: "Nhân viên Kinh doanh" }, { code: "J004", name: "Digital Marketer" },
              { code: "J005", name: "Giám đốc Điều hành (CEO)" }, { code: "J006", name: "Lập trình viên Frontend" },
              { code: "J007", name: "Kế toán trưởng" }, { code: "J008", name: "Nhân viên kho" }, { code: "J009", name: "Công nhân sản xuất" },
            ], meta: { page_info: { total_page: 1, page: 0 } }
          }
        });
      } else { reject(new Error("Unknown API path")); }
    }, 300);
  });
};