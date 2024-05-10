import React from "react";
import ClassOpenPlanTable from "./components/ClassOpenPlanTable";
import { Button } from "@mui/material";
import GeneralSemesterAutoComplete from "../common-components/GeneralSemesterAutoComplete";

const GeneralPlanClassOpenScreen = () => {
  return (
    <div>
      <div className="flex flex-row justify-between mb-4">
        <GeneralSemesterAutoComplete />
        <div className="flex flex-row justify-end gap-2">
          <Button variant="contained">Thêm lớp</Button>
          <Button variant="contained">Lưu thay đổi</Button>
          <Button variant="contained">Nhập Excel</Button>
          <Button variant="contained">Xóa kế hoạch mở lớp</Button>
        </div>
      </div>
      <ClassOpenPlanTable />
    </div>
  );
};

export default GeneralPlanClassOpenScreen;
