import {Box, Card, CardContent, CardHeader, Chip, Grid, IconButton, List, TextField, Typography,} from "@mui/material";
import {DatePicker, LocalizationProvider} from "@mui/x-date-pickers";
import {AdapterDateFns} from "@mui/x-date-pickers/AdapterDateFns";
import {endOfWeek, format, isMonday, startOfWeek} from "date-fns"; // Import thêm isMonday
import {useEffect, useState} from "react";
import {request} from "@/api";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";
import DeleteConfirmationModal from "./modals/DeleteConfirmationModal";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

const AbsenceHistoryPage = () => {
  const [selectedDate, setSelectedDate] = useState(startOfWeek(new Date(), { weekStartsOn: 1 }));
  const [absenceList, setAbsenceList] = useState([]);
  const [deleteId, setDeleteId] = useState(null);
  const navigate = useNavigate();

  const fetchAbsences = () => {
    const start = startOfWeek(selectedDate, { weekStartsOn: 1 });
    const end = endOfWeek(selectedDate, { weekStartsOn: 1 });

    request(
      "get",
      `/absences/me?startDate=${format(start, "yyyy-MM-dd")}&endDate=${format(end, "yyyy-MM-dd")}`,
      (res) => setAbsenceList(res.data?.data || [])
    );
  };

  useEffect(() => {
    fetchAbsences();
  }, [selectedDate]);

  const handleDelete = () => {
    if (!deleteId) return;
    request(
      "delete",
      `/absences/${deleteId}`,
      () => {
        toast.success("Xoá thành công");
        fetchAbsences();
        setDeleteId(null);
      },
      { onError: (err) => {
          var data = err.response.data.data;
          toast.error("Error when delete: " + data);
          toast.error("Xoá không thành công");
          setDeleteId(null);
        }}
    );
  };

  return (
    <Box sx={{ display: "flex", flexDirection: "column", height: "100vh", overflowY: "auto" }}>
      <Typography variant="h5" mb={3} fontWeight={600} sx={{ textAlign: "left", p: 3 }}>
        Nghỉ phép cá nhân
      </Typography>

      <Box sx={{ p: 3, flexGrow: 1, overflowY: "auto" }}>
        <Box sx={{ p: 3, maxWidth: 900, mx: "auto", marginBottom: "30px" }}>
          <Grid container spacing={2} mb={3}>
            <Grid item xs={12} sm={6}>
              <LocalizationProvider dateAdapter={AdapterDateFns}>
                <DatePicker
                  label="Chọn ngày trong tuần"
                  value={selectedDate}
                  onChange={(date) => date && setSelectedDate(date)}
                  shouldDisableDate={(date) => !isMonday(date)}
                  renderInput={(params) => <TextField {...params} fullWidth variant="standard" />}
                />
              </LocalizationProvider>
            </Grid>
          </Grid>

          <List
            sx={{
              borderRadius: 2,
              overflowY: "auto",
            }}
          >
            {absenceList.length === 0 && (
              <Typography color="text.secondary" p={2}>
                Không có dữ liệu
              </Typography>
            )}
            {absenceList.map((item) => (
              <Card
                key={item.id}
                sx={{
                  mb: 3,
                  borderRadius: 2,
                  boxShadow: 0,
                  border: item.status === "INACTIVE" ? "2px solid rgba(244, 67, 54, 0.5)" : "none",
                  "&:hover": {
                    boxShadow: 3, // Show shadow on hover
                  },
                }}
              >
                <CardHeader
                  title={`📅 ${item.date}`}
                  subheader={
                    <Typography variant="body2" fontWeight={600} color="text.primary">
                      🕒 {item.start_time?.slice(0, 5)} - {item.end_time?.slice(0, 5)}
                    </Typography>
                  }
                  action={
                    item.status === "INACTIVE" ? (
                      <Box>
                        <Typography variant="body2" color="error" fontWeight="bold">
                          Đã huỷ
                        </Typography>
                      </Box>
                    ) : (
                      <Box>
                        <IconButton edge="end" color="primary" onClick={() => navigate(`/hr/absence/${item.id}`)}>
                          <EditIcon />
                        </IconButton>
                        <IconButton edge="end" color="error" onClick={() => setDeleteId(item.id)}>
                          <DeleteIcon />
                        </IconButton>
                      </Box>
                    )
                  }
                />
                <CardContent>
                  <Grid container spacing={2}>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="body2" color="text.secondary">
                        Lý do
                      </Typography>
                      <Typography variant="body2" color="text.primary">
                        {item.reason}
                      </Typography>
                    </Grid>
                    <Grid item xs={12} sm={6}>
                      <Typography variant="body2" color="text.secondary">
                        Loại nghỉ
                      </Typography>
                      <Chip
                        label={item.type === "PAID_LEAVE" ? "Có lương" : "Không lương"}
                        color={item.type === "PAID_LEAVE" ? "success" : "warning"}
                        size="small"
                      />
                    </Grid>
                  </Grid>
                </CardContent>
              </Card>
            ))}
          </List>

          <DeleteConfirmationModal
            open={!!deleteId}
            onClose={() => setDeleteId(null)}
            onSubmit={handleDelete}
            title="Xác nhận huỷ thông báo nghỉ phép?"
            info="Bạn có chắc chắn muốn huỷ thông báo nghỉ phép này không?"
          />
        </Box>
      </Box>
    </Box>
  );
};

export default AbsenceHistoryPage;
