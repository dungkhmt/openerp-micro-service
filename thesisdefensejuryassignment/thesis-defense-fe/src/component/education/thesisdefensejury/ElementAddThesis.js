import React from "react";
import { StandardTable } from "erp-hust/lib/StandardTable";
import KeywordChip from "component/common/KeywordChip";
import { Checkbox } from "@material-ui/core/";
import { useAssignThesis } from "action";
export default function ElementAddThesis({ availableThesisList }) {
  const assignedThesis = useAssignThesis((state) => state.assignedThesis);
  const handleSelectThesis = useAssignThesis(
    (state) => state.handleSelectThesis
  );
  const columns = [
    {
      title: "",
      render: (rowData) => (
        <Checkbox
          checked={
            assignedThesis?.find((item) => item === rowData?.id) !== undefined
          }
          onChange={(e) => handleSelectThesis(rowData)}
          color="default"
        />
      ),
    },
    { title: "Tên đồ án", field: "thesisName" },
    {
      title: "Sinh viên",
      field: "studentName",
    },
    {
      title: "Giáo viên",
      field: "supervisor",
      render: (rowData) => rowData?.supervisor?.teacherName,
    },
    {
      title: "Keyword",
      field: "academicKeywordList",
      render: (rowData) =>
        rowData?.academicKeywordList?.map(({ keyword, description }) => (
          <KeywordChip key={keyword} keyword={description} />
        )),
    },
  ];
  return (
    <StandardTable
      title={"Danh sách đồ án"}
      data={availableThesisList}
      columns={columns}
      options={{
        selection: false,
        pageSize: 5,
        search: true,
        sorting: true,
      }}
    />
  );
}
