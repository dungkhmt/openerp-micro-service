import AutocompleteCell from "./AutoCompleteCell";
import { useState } from "react";
import ClassroomAutoCompleteCell from "./ClassRoomAutoCompleteCell";

export const useGeneralTableColumns = (
  setClasses,
  setLoading,
  semester,
  saveRequests,
  setSaveRequests
) => {
  const [classRooms, setClassRooms] = useState([]);

  return [
    {
      headerName: "Mã lớp",
      field: "classCode",
      editable: true,
      width: 100,
    },
    {
      headerName: "Lớp học",
      field: "studyClass",
      editable: true,
      width: 150,
    },
    // {
    //   headerName: "Nhóm",
    //   field: "groupName",
    //   editable: true,
    //   width: 120,
    // },
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      editable: true,
      width: 120,
    },
    // {
    //   headerName: "Mã học phần",
    //   field: "moduleCode",
    //   editable: true,
    //   width: 100,
    // },
    // {
    //   headerName: "Tên học phần",
    //   field: "moduleName",
    //   editable: true,
    //   width: 100,
    // },
    // {
    //   headerName: "SL thực",
    //   field: "quantity",
    //   editable: true,
    //   width: 100,
    // },
    {
      headerName: "SL MAX",
      field: "quantityMax",
      editable: true,
      width: 100,
    },
    {
      headerName: "Loại lớp",
      field: "classType",
      editable: true,
      width: 100,
    },
    {
      headerName: "Thời lượng",
      field: "mass",
      editable: true,
      width: 100,
    },
    {
      headerName: "Trạng thái",
      field: "state",
      editable: true,
      width: 100,
    },
    {
      headerName: "Kíp",
      field: "crew",
      editable: true,
      width: 100,
    },
    // {
    //   headerName: "Đợt",
    //   field: "openBatch",
    //   editable: true,
    //   width: 100,
    // },
    {
      headerName: "Khóa",
      field: "course",
      editable: true,
      width: 100,
    },
    {
      headerName: "Tiết BĐ",
      field: "startTime",
      width: 80,
      editable: true,
      renderCell: (params) => {
        return (
          <AutocompleteCell
            row={params.row}
            saveRequests={saveRequests}
            setSaveRequests={setSaveRequests}
            roomReservationId={params.row.roomReservationId}
            semester={semester}
            setLoading={setLoading}
            setClasses={setClasses}
            options={Array.from({ length: 6 }).map((_, index) => ({
              label: (index + 1)?.toString(),
            }))}
            {...params}
          />
        );
      },
    },
    {
      headerName: "Tiết KT",
      field: "endTime",
      width: 80,
      editable: true,
      renderCell: (params) => (
        <AutocompleteCell
          row={params.row}
          saveRequests={saveRequests}
          setSaveRequests={setSaveRequests}
          roomReservationId={params.row.roomReservationId}
          semester={semester}
          setLoading={setLoading}
          setClasses={setClasses}
          options={Array.from({ length: 6 }).map((_, index) => ({
            label: (index + 1)?.toString(),
          }))}
          {...params}
        />
      ),
    },
    {
      headerName: "Ngày",
      field: "weekday",
      width: 80,
      renderCell: (params) => (
        <AutocompleteCell
          row={params.row}
          saveRequests={saveRequests}
          setSaveRequests={setSaveRequests}
          roomReservationId={params.row.roomReservationId}
          semester={semester}
          setLoading={setLoading}
          setClasses={setClasses}
          options={Array.from({ length: 7 }).map((_, index) => ({
            label: index + 2 === 8 ? "Chủ nhật" : (index + 2).toString(),
          }))}
          {...params}
        />
      ),
    },
    {
      headerName: "Phòng học",
      field: "room",
      width: 120,
      renderCell: (params) => {
        return (
          <ClassroomAutoCompleteCell
            saveRequests={saveRequests}
            setSaveRequests={setSaveRequests}
            groupName={params.row?.groupName}
            maxQuantity={params.row?.quantityMax}
            roomReservationId={params.row.roomReservationId}
            semester={semester}
            setLoading={setLoading}
            setClasses={setClasses}
            options={classRooms?.map((classRoom, index) => ({
              label: classRoom?.classroom,
            }))}
            {...params}
          />
        );
      },
    },
  ];
};
