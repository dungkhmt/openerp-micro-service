import { toast } from "react-toastify";
import { request } from "api";
import { Add, Remove } from "@mui/icons-material";
import { Button } from "@mui/material";

export const useGeneralTableColumns = (setClasses) => {
  const handleRemoveTimeSlot = (generalClassId, roomReservationId) => {
    request(
      "delete",
      `/general-classes/${generalClassId}/room-reservations/${roomReservationId}`,
      (res) => {
        toast.success("Xóa ca thành công!");
        setClasses((prevClasses) => {
          return prevClasses.filter(
            (prevClass) => prevClass.roomReservationId !== roomReservationId
          );
        });
      },
      (err) => {
        if (err.response.status === 410) {
          toast.error(err.response.data);
        } else {
          toast.error("Xóa ca thất bại!");
        }
      }
    );
  };

  const handleAddTimeSlot = (generalClassId, roomReservationId) => {
    console.log(generalClassId);
    request(
      "post",
      `/general-classes/${generalClassId}/room-reservations/`,
      (res) => {
        console.log(res.data);
        const newClasses = res.data.timeSlots.map((timeSlot, index) => {
          const newClass = {
            ...res.data,
            ...timeSlot,
            id: `${res.data.id}-${index + 1}`,
            roomReservationId: timeSlot?.id,
          };
          delete newClass.timeSlots;
          console.log(newClass.roomReservationId);
          return newClass;
        });

        setClasses((prevClasses) => {
          return [
            ...prevClasses.filter(
              (prevClass) => prevClass.id.split("-")[0] !== generalClassId
            ),
            ...newClasses,
          ];
        });
      },
      (err) => {
        if (err.response.status === 410) {
          toast.error(err.response.data);
        } else {
          toast.error("Thêm ca học thất bại!");
        }
      }
    );
  };
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
    // {
    //   headerName: "Nhóm",
    //   field: "groupName",
    //   editable: true,
    //   width: 120,
    // },
    {
      headerName: "Tuần học",
      field: "learningWeeks",
      width: 120,
    },
    {
      headerName: "Mã học phần",
      field: "moduleCode",
      editable: true,
      width: 100,
    },
    {
      headerName: "Tên học phần",
      field: "moduleName",
      editable: true,
      width: 100,
    },
    // {
    //   headerName: "SL thực",
    //   field: "quantity",
    //   editable: true,
    //   width: 100,
    // },
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
    // {
    //   headerName: "Trạng thái",
    //   field: "state",
    //   width: 100,
    // },
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
    // {
    //   headerName: "Mã lớp tạm thời",
    //   field: "id",
    //   width: 100,
    // },
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
    // {
    //   headerName: "Tiết BĐ",
    //   field: "startTime",
    //   width: 80,
    //   renderCell: (params) => {
    //     return (
    //       <AutocompleteCell
    //         row={params.row}
    //         saveRequests={saveRequests}
    //         setSaveRequests={setSaveRequests}
    //         roomReservationId={params.row.roomReservationId}
    //         semester={semester}
    //         setLoading={setLoading}
    //         setClasses={setClasses}
    //         options={Array.from({ length: 6 }).map((_, index) => ({
    //           label: (index + 1)?.toString(),
    //         }))}
    //         {...params}
    //       />
    //     );
    //   },
    // },
    // {
    //   headerName: "Tiết KT",
    //   field: "endTime",
    //   width: 80,
    //   renderCell: (params) => (
    //     <AutocompleteCell
    //       row={params.row}
    //       saveRequests={saveRequests}
    //       setSaveRequests={setSaveRequests}
    //       roomReservationId={params.row.roomReservationId}
    //       semester={semester}
    //       setLoading={setLoading}
    //       setClasses={setClasses}
    //       options={Array.from({ length: 6 }).map((_, index) => ({
    //         label: (index + 1)?.toString(),
    //       }))}
    //       {...params}
    //     />
    //   ),
    // },
    // {
    //   headerName: "Ngày",
    //   field: "weekday",
    //   width: 80,
    //   renderCell: (params) => (
    //     <AutocompleteCell
    //       row={params.row}
    //       saveRequests={saveRequests}
    //       setSaveRequests={setSaveRequests}
    //       roomReservationId={params.row.roomReservationId}
    //       semester={semester}
    //       setLoading={setLoading}
    //       setClasses={setClasses}
    //       options={Array.from({ length: 7 }).map((_, index) => ({
    //         label: index + 2 === 8 ? "Chủ nhật" : (index + 2).toString(),
    //       }))}
    //       {...params}
    //     />
    //   ),
    // },
    // {
    //   headerName: "Phòng học",
    //   field: "room",
    //   width: 120,
    //   renderCell: (params) => {
    //     return (
    //       <ClassroomAutoCompleteCell
    //         saveRequests={saveRequests}
    //         setSaveRequests={setSaveRequests}
    //         groupName={params.row?.groupName}
    //         maxQuantity={params.row?.quantityMax}
    //         roomReservationId={params.row.roomReservationId}
    //         semester={semester}
    //         setLoading={setLoading}
    //         setClasses={setClasses}
    //         options={classRooms?.map((classRoom, index) => ({
    //           label: classRoom?.classroom,
    //         }))}
    //         {...params}
    //       />
    //     );
    //   },
    // },
    {
      headerName: "Thêm",
      field: "addTimeSlotButton",
      width: 120,
      renderCell: (params) => {
        return (
          <Button
            onClick={(e) =>
              handleAddTimeSlot(
                params.row.id.split("-")[0],
                params.row.roomReservationId
              )
            }
          >
            <Add />
          </Button>
        );
      },
    },
    {
      headerName: "Xóa",
      field: "deleteTimeSlotButton",
      width: 120,
      renderCell: (params) => {
        return (
          <Button
            onClick={(e) =>
              handleRemoveTimeSlot(
                params.row.id.split("-")[0],
                params.row.roomReservationId
              )
            }
          >
            <Remove />
          </Button>
        );
      },
    },
  ];
};
