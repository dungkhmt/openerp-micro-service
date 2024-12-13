import AutocompleteCell from "./AutoCompleteCell";
import ClassroomAutoCompleteCell from "./ClassRoomAutoCompleteCell";

export const useFirstYearTableColumns = (classes, onSaveTimeSlot, semester) => {
  return [
    {
      headerName: "Mã lớp",
      field: "classCode",
      width: 100,
    },
    {
      headerName: "Lớp học",
      field: "studyClass",
      width: 150,
    },
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      width: 120,
    },
    {
      headerName: "SL MAX",
      field: "quantityMax",
      width: 100,
    },
    {
      headerName: "Loại lớp",
      field: "classType",
      width: 100,
    },
    {
      headerName: "Thời lượng",
      field: "mass",
      width: 100,
    },
    {
      headerName: "Trạng thái",
      field: "state",
      width: 100,
    },
    {
      headerName: "Kíp",
      field: "crew",
      width: 100,
    },
    {
      headerName: "Mã lớp cha",
      field: "parentClassId",
      width: 100,
    },
    {
      headerName: "Mã lớp tạm thời",
      field: "id",
      width: 100,
    },
    {
      headerName: "Mã lớp tham chiếu",
      field: "refClassId",
      width: 100,
    },
    {
      headerName: "Khóa",
      field: "course",
      width: 100,
    },
    {
      headerName: "Tiết BĐ",
      field: "startTime",
      width: 80,
      renderCell: (params) => {
        return (
          <AutocompleteCell
            row={params.row}
            onSaveTimeSlot={onSaveTimeSlot}
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
      renderCell: (params) => (
        <AutocompleteCell
          row={params.row}
          onSaveTimeSlot={onSaveTimeSlot}
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
          onSaveTimeSlot={onSaveTimeSlot}
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
            onSaveTimeSlot={onSaveTimeSlot}
            groupName={params.row?.groupName}
            maxQuantity={params.row?.quantityMax}
            {...params}
          />
        );
      },
    },
  ];
};
