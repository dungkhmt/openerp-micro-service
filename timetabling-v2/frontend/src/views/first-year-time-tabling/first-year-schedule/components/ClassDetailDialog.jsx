import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from "@mui/material";


const ClassDetailDialog = ({ open, setOpen, row }) => {
  return (
    <Dialog
      maxWidth="lg"
      open={open}
      onClose={() => {
        setOpen(false);
      }}
    >
      <DialogTitle>Lớp: {row?.classCode}</DialogTitle>
      <DialogContent>
        <div className="flex flex-row gap-4">
          <div className="flex flex-col">
            <p className="font-semibold text-xl">Thông tin chi tiết: </p>
            <div>Mã học phần: {row?.moduleCode}</div>
            <div>Tên học phần: {row?.moduleName}</div>
            <div>Nhóm: {row?.groupName}</div>
            <div>Loại lớp: {row?.classType}</div>
            <div>
              Số lượng: {row?.quantity}/{row?.quantityMax}
            </div>
            <div>Học kỳ: {row?.semester}</div>
            <div>Thời lượng: {row?.mass}</div>
            <div>Trạng thái: {row?.state}</div>
          </div>
          <div className="flex flex-col">
            <p className="font-semibold text-xl">Thời gian: </p>
            <div>Kíp: {row?.crew}</div>
            <div>Kỳ: {row?.openBatch}</div>
            <div>Khóa: {row?.course}</div>
            <div>Tuần học: {row?.learningWeeks}</div>
            <div className="">Ngày thứ: {row?.weekday}</div>
            <div>
              Thời gian:{" "}
              {row?.startTime || row?.endTime
                ? `Tiết ${row?.startTime} - Tiết ${row?.endTime}`
                : `không có`}
            </div>
            <div>Phòng: {row?.room}</div>
          </div>
        </div>
      </DialogContent>
      <DialogActions>
        <Button disabled={true} onClick={() => {}}>
          Lưu
        </Button>
        <Button
          onClick={() => {
            setOpen(false);
          }}
        >
          Hủy
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default ClassDetailDialog;
