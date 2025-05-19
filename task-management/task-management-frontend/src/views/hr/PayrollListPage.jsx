import React, { useState, useEffect } from 'react';
import { startOfWeek, addDays, format, subDays, parseISO, isSameDay } from 'date-fns';

// MUI Icons - thay thế @mdi/react
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import StarIcon from '@mui/icons-material/Star'; // Hoặc StarBorderIcon nếu muốn viền
import AccessTimeIcon from '@mui/icons-material/AccessTime'; // Tương đương mdiClockOutline
import DownloadIcon from '@mui/icons-material/Download';
import FilterListIcon from '@mui/icons-material/FilterList'; // Tương đương mdiFilterVariant
import AddIcon from '@mui/icons-material/Add'; // Theo yêu cầu cho EmptyShiftSlot
import AddCircleOutlineIcon from '@mui/icons-material/AddCircleOutline'; // Có thể dùng cho EmptyShiftSlot nếu muốn icon tròn
import SettingsIcon from '@mui/icons-material/Settings'; // Tương đương mdiCogOutline
import DeleteIcon from '@mui/icons-material/Delete'; // Icon xóa, sẽ thêm vào ShiftCard

// Dữ liệu mẫu (Mock Data)
const initialShifts = [
  { id: 's1', userId: 'u1', day: '2025-05-19', startTime: '9:00 AM', endTime: '5:15 PM', duration: '8h 15min', details: 'backend + neu', subDetails: 'fixb', color: 'bg-green-100', borderColor: 'border-green-500' },
  { id: 's2', userId: 'u2', day: '2025-05-20', startTime: '9:00 AM', endTime: '5:15 PM', duration: '8h 15min', details: 'frontend + neu', subDetails: 'neu', color: 'bg-red-100', borderColor: 'border-red-500' },
  // Thêm ca làm việc khác nếu cần
];

const initialUsers = [
  { id: 'u1', name: 'CA ĐÃ LÊN LỊCH 1', summary: '8h 15min + $41.25', avatarInitial: '1', color: 'bg-purple-500' },
  { id: 'u2', name: 'CA ĐÃ LÊN LỊCH 2', summary: '8h 15min + $41.25', avatarInitial: '2', color: 'bg-pink-500' },
  { id: 'u3', name: 'CA ĐÃ LÊN LỊCH 3', summary: '0h + $0.00', avatarInitial: '3', color: 'bg-indigo-500' },
  { id: 'uHPT', name: 'Hieu Phan Trung', summary: '0h + $0.00', avatarInitial: 'HP', color: 'bg-teal-500' },
  { id: 't4', name: 'Test 4', summary: '0h + $0.00', avatarInitial: 'T4', color: 'bg-cyan-500' },
  { id: 't5', name: 'Test 5', summary: '0h + $0.00', avatarInitial: 'T5', color: 'bg-sky-500' },
  { id: 't6', name: 'Test 6', summary: '0h + $0.00', avatarInitial: 'T6', color: 'bg-blue-500' },
  { id: 't7', name: 'Test 7', summary: '0h + $0.00', avatarInitial: 'T7', color: 'bg-gray-500' },
];

// Hàm tiện ích lấy chữ cái đầu cho avatar
const getInitials = (name) => {
  if (!name) return '';
  const parts = name.split(' ');
  if (parts.length === 1) return name.substring(0, 2).toUpperCase();
  if (!isNaN(parseInt(parts[parts.length -1 ]))) {
    return parts[0].charAt(0).toUpperCase() + parts[parts.length - 1];
  }
  return parts.map(part => part[0]).join('').toUpperCase().substring(0,2);
};

// Thành phần hiển thị một ca làm việc (Shift Card Component)
function ShiftCard({ shift, onDeleteShift }) { // Thêm prop onDeleteShift
  return (
    <div className={`relative p-2 my-1 rounded-md text-xs shadow-sm ${shift.color} ${shift.borderColor} border-l-4 group`}>
      <div className="font-semibold">{`${shift.startTime} - ${shift.endTime}`} <span className="font-normal text-gray-600">({shift.duration})</span></div>
      <div className="text-gray-700">{shift.details}</div>
      {shift.subDetails && <div className="text-gray-500">{shift.subDetails}</div>}
      <button
        onClick={() => onDeleteShift(shift.id)}
        className="absolute top-1 right-1 p-0.5 bg-red-500 text-white rounded-full opacity-0 group-hover:opacity-100 hover:bg-red-600 transition-opacity"
        title="Xóa ca"
      >
        <DeleteIcon style={{ fontSize: '1rem' }} /> {/* 0.8rem or 1rem for small icon */}
      </button>
    </div>
  );
}

// Thành phần hiển thị ô trống để thêm ca (Empty Shift Slot Component)
function EmptyShiftSlot({ onAdd }) {
  return (
    <div
      className="border-2 border-dashed border-gray-300 rounded-md h-16 m-1 flex items-center justify-center text-gray-400 hover:bg-gray-50 cursor-pointer"
      onClick={onAdd}
      title="Thêm ca làm việc mới"
    >
      <AddIcon style={{ fontSize: '1.5rem' }} /> {/* Sử dụng AddIcon từ MUI */}
    </div>
  );
}

// Thành phần tiêu đề lịch (các ngày trong tuần) (Calendar Header)
function CalendarHeader({ currentDate }) {
  const weekStartsOn = 1; // Thứ Hai
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <div className="grid grid-cols-[180px_repeat(7,1fr)] bg-gray-50 border-b border-gray-300 sticky top-0 z-10">
      <div className="p-3 border-r border-gray-300 flex items-center justify-between">
        <span className="font-semibold text-sm text-gray-700">NHÂN VIÊN</span>
      </div>
      {days.map(day => (
        <div key={day.toISOString()} className="p-3 text-center border-r border-gray-300 last:border-r-0">
          <div className="text-xs text-gray-500">{format(day, 'EEE').toUpperCase()}</div>
          <div className="text-lg font-semibold text-gray-700">{format(day, 'dd')}</div>
        </div>
      ))}
    </div>
  );
}

// Lưới hiển thị ca làm việc chính (Main Shifts Grid)
function ShiftsGrid({ currentDate, shifts, users, onAddShift, onDeleteShift }) { // Thêm onDeleteShift
  const weekStartsOn = 1; // Thứ Hai
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const daysOfWeek = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <div className="divide-y divide-gray-300">
      {users.map(user => (
        <div key={user.id} className="grid grid-cols-[180px_repeat(7,1fr)] min-h-[80px]">
          <div className="p-3 border-r border-gray-300 bg-gray-50 flex flex-col justify-center">
            <div className="flex items-center mb-1">
              <div className={`w-7 h-7 rounded-full ${user.color || 'bg-gray-400'} flex items-center justify-center text-white text-xs font-bold mr-2`}>
                {user.avatarInitial || getInitials(user.name)}
              </div>
              <span className="font-medium text-sm text-gray-800 truncate" title={user.name}>{user.name}</span>
            </div>
            <div className="text-xs text-gray-500">{user.summary}</div>
          </div>
          {daysOfWeek.map(day => {
            const shiftsForUserAndDay = shifts.filter(shift =>
              shift.userId === user.id && isSameDay(parseISO(shift.day), day)
            );
            return (
              <div
                key={day.toISOString()}
                className="p-1 border-r border-gray-300 last:border-r-0 min-h-[70px] bg-white hover:bg-gray-50 transition-colors duration-150"
              >
                {shiftsForUserAndDay.length > 0 ? (
                  shiftsForUserAndDay.map(shift => <ShiftCard key={shift.id} shift={shift} onDeleteShift={onDeleteShift} />)
                ) : (
                  <EmptyShiftSlot onAdd={() => onAddShift(user.id, day)} />
                )}
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );
}

// Banner "Ca có sẵn" ("Available Shifts" Banner)
function AvailableShiftsBanner() {
  return (
    <div className="p-3 bg-green-50 border-b border-green-200 text-green-700 font-medium text-sm flex items-center">
      <StarIcon fontSize="small" className="mr-2 text-yellow-400" /> {/* fill-current không cần thiết với MUI SVG */}
      CA CÓ SẴN
    </div>
  );
}

// Hàng "Doanh số dự kiến" - Hiện tại là tĩnh ("Projected Sales" Row)
function ProjectedSalesRow({ currentDate }) {
  const weekStartsOn = 1; // Thứ Hai
  const startDate = startOfWeek(currentDate, { weekStartsOn });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(startDate, i));

  return (
    <div className="grid grid-cols-[180px_repeat(7,1fr)] bg-gray-100 border-b border-gray-300">
      <div className="p-3 border-r border-gray-300 font-medium text-sm text-gray-600">DOANH SỐ DỰ KIẾN</div>
      {days.map(day => (
        <div key={`sales-${day.toISOString()}`} className="p-3 text-center border-r border-gray-300 last:border-r-0 text-sm text-gray-700">
          $0.00
        </div>
      ))}
    </div>
  );
}

// Thành phần chính của ứng dụng
export default function ShiftScheduler() {
  const [currentDate, setCurrentDate] = useState(new Date('2025-05-19'));
  const [shifts, setShifts] = useState(initialShifts);
  const [users, setUsers] = useState(initialUsers);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newShiftInfo, setNewShiftInfo] = useState({ userId: null, day: null, startTime: '09:00', endTime: '17:00', details: '' });

  const handlePrevWeek = () => setCurrentDate(prev => subDays(prev, 7));
  const handleNextWeek = () => setCurrentDate(prev => addDays(prev, 7));
  const handleToday = () => setCurrentDate(new Date());

  const handleOpenAddShiftModal = (userId, day) => {
    setNewShiftInfo({ userId, day: format(day, 'yyyy-MM-dd'), startTime: '09:00', endTime: '17:00', details: '' });
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleSaveShift = (e) => {
    e.preventDefault();
    const { userId, day, startTime, endTime, details } = newShiftInfo;
    const start = new Date(`${day}T${startTime}`);
    const end = new Date(`${day}T${endTime}`);
    let durationMs = end - start;
    if (durationMs < 0) { durationMs += 24 * 60 * 60 * 1000; }

    const durationHours = Math.floor(durationMs / (1000 * 60 * 60));
    const durationMinutes = Math.floor((durationMs % (1000 * 60 * 60)) / (1000 * 60));

    const newShiftToAdd = {
      id: `s${Date.now()}`, userId, day,
      startTime: format(start, 'p'), endTime: format(end, 'p'),
      duration: `${durationHours}h ${durationMinutes}min`,
      details, color: 'bg-blue-100', borderColor: 'border-blue-500',
    };
    setShifts(prevShifts => [...prevShifts, newShiftToAdd]);
    handleCloseModal();
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewShiftInfo(prev => ({ ...prev, [name]: value }));
  };

  const handleDeleteShift = (shiftIdToDelete) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa ca làm việc này không?")) {
      setShifts(prevShifts => prevShifts.filter(shift => shift.id !== shiftIdToDelete));
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 font-sans">
      <div className="bg-white shadow-md p-3 flex justify-between items-center sticky top-0 z-20">
        <div className="flex items-center space-x-4">
          <h1 className="text-xl font-semibold text-gray-700">Lịch làm việc</h1>
          <button onClick={handlePrevWeek} className="p-1 rounded hover:bg-gray-200 transition-colors">
            <ChevronLeftIcon />
          </button>
          <button onClick={handleToday} className="px-3 py-1.5 text-sm border border-gray-300 rounded hover:bg-gray-50 transition-colors">Hôm nay</button>
          <button onClick={handleNextWeek} className="p-1 rounded hover:bg-gray-200 transition-colors">
            <ChevronRightIcon />
          </button>
          <span className="text-lg font-medium text-gray-600">{format(currentDate, 'MMMM yyyy')}</span>
        </div>
        <div className="flex items-center space-x-3">
          <button className="px-3 py-1.5 text-sm border border-gray-300 rounded hover:bg-gray-50 flex items-center">
            <FilterListIcon fontSize="small" className="mr-1.5"/> Lọc
          </button>
          <button className="px-3 py-1.5 text-sm border border-gray-300 rounded hover:bg-gray-50 flex items-center">
            <DownloadIcon fontSize="small" className="mr-1.5"/> Xuất
          </button>
          <button className="p-1 rounded hover:bg-gray-200 transition-colors">
            <SettingsIcon />
          </button>
        </div>
      </div>

      <div className="container mx-auto p-4 pt-0">
        <AvailableShiftsBanner />
        <ProjectedSalesRow currentDate={currentDate} />
        <div className="shadow-lg border border-gray-300 rounded-b-lg overflow-hidden">
          <CalendarHeader currentDate={currentDate} />
          <div className="overflow-x-auto">
            <div className="min-w-[1000px]">
              <ShiftsGrid
                currentDate={currentDate}
                shifts={shifts}
                users={users}
                onAddShift={handleOpenAddShiftModal}
                onDeleteShift={handleDeleteShift} // Truyền hàm xóa xuống
              />
            </div>
          </div>
        </div>
      </div>

      <div className="fixed bottom-5 right-5 bg-white p-3 rounded-lg shadow-xl border border-gray-200 flex items-center space-x-2 text-sm">
        <AccessTimeIcon fontSize="small" className="text-blue-500" />
        <span>Bật đồng hồ chấm công</span>
        <input type="checkbox" className="form-checkbox h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500" />
      </div>

      {isModalOpen && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-30 p-4">
          <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-md">
            <h2 className="text-xl font-semibold mb-4">Thêm ca làm việc mới</h2>
            <form onSubmit={handleSaveShift}>
              <div className="mb-4">
                <label htmlFor="details" className="block text-sm font-medium text-gray-700 mb-1">Chi tiết</label>
                <input type="text" name="details" id="details" value={newShiftInfo.details} onChange={handleInputChange} className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" required />
              </div>
              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label htmlFor="startTime" className="block text-sm font-medium text-gray-700 mb-1">Giờ bắt đầu</label>
                  <input type="time" name="startTime" id="startTime" value={newShiftInfo.startTime} onChange={handleInputChange} className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" required />
                </div>
                <div>
                  <label htmlFor="endTime" className="block text-sm font-medium text-gray-700 mb-1">Giờ kết thúc</label>
                  <input type="time" name="endTime" id="endTime" value={newShiftInfo.endTime} onChange={handleInputChange} className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500" required />
                </div>
              </div>
              <div className="mb-6">
                <p className="text-sm text-gray-600">Nhân viên: <span className="font-medium">{users.find(u => u.id === newShiftInfo.userId)?.name}</span></p>
                <p className="text-sm text-gray-600">Ngày: <span className="font-medium">{format(parseISO(newShiftInfo.day), 'EEE, dd MMM, yyyy')}</span></p>
              </div>
              <div className="flex justify-end space-x-3">
                <button type="button" onClick={handleCloseModal} className="px-4 py-2 text-sm border border-gray-300 rounded-md hover:bg-gray-100">Hủy</button>
                <button type="submit" className="px-4 py-2 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700">Lưu ca</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
