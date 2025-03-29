import PropTypes from "prop-types";
import { useEffect, useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Typography,
  Divider,
  Grid,
} from "@mui/material";
import { DateTimePicker } from "@mui/x-date-pickers";
import dayjs from "dayjs";
import { useDebounce } from "../../hooks/useDebounce";

const CreateSessionsDialog = ({ open, onClose, onCreate }) => {
  const [startTime, setStartTime] = useState(null);
  const [endTime, setEndTime] = useState(null);
  const [sessionLength, setSessionLength] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [warningMessage, setWarningMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [sessions, setSessions] = useState([]);

  // Debounce the input values
  const debouncedStartTime = useDebounce(startTime, 1000);
  const debouncedEndTime = useDebounce(endTime, 1000);
  const debouncedSessionLength = useDebounce(sessionLength, 1000);

  useEffect(() => {
    if (!open) {
      resetFields();
    }
  }, [open]);

  const resetFields = () => {
    setStartTime(null);
    setEndTime(null);
    setSessionLength("");
    setErrorMessage("");
    setWarningMessage("");
    setSessions([]);
    setLoading(false);
  };

  const calculateSessions = () => {
    const MAX_SESSIONS = 50; // Cap the maximum number of sessions

    if (!debouncedStartTime || !debouncedEndTime) {
      setSessions([]);
      setErrorMessage("");
      setWarningMessage("");
      return;
    }

    const start = dayjs(debouncedStartTime);
    const end = dayjs(debouncedEndTime);

    if (end.isBefore(start)) {
      setErrorMessage("Thời gian kết thúc phải lớn hơn thời gian bắt đầu!");
      setSessions([]);
      setWarningMessage("");
      return;
    }

    if (start.isBefore(dayjs())) {
      setErrorMessage("Thời gian bắt đầu phải lớn hơn thời gian hiện tại!");
      setSessions([]);
      setWarningMessage("");
      return;
    }

    setErrorMessage("");
    setWarningMessage("");

    const totalTimeMs = end.diff(start, "minute");
    const sessionLen = debouncedSessionLength
      ? parseInt(debouncedSessionLength)
      : 0;

    const newSessions = [];

    // Edge Case 1: No session length, or session length is invalid (0 or negative)
    if (!sessionLen || sessionLen <= 0) {
      newSessions.push({
        start: start.toDate(),
        end: end.toDate(),
      });
      setSessions(newSessions);
      return;
    }

    // Edge Case 2: Total time is less than the session length
    if (totalTimeMs < sessionLen) {
      newSessions.push({
        start: start.toDate(),
        end: end.toDate(),
      });
      setSessions(newSessions);
      return;
    }

    // Calculate potential number of sessions
    const potentialSessionCount = Math.floor(totalTimeMs / sessionLen);

    // Edge Case 3: Too many sessions due to small session length
    if (potentialSessionCount > MAX_SESSIONS) {
      setWarningMessage(
        `Số lượng phiên họp (${potentialSessionCount}) vượt quá giới hạn (${MAX_SESSIONS}). Chỉ tạo ${MAX_SESSIONS} phiên đầu tiên.`
      );
    }

    const sessionCount = Math.min(
      MAX_SESSIONS,
      Math.max(1, potentialSessionCount)
    );

    let currentStart = start;
    for (let i = 0; i < sessionCount; i++) {
      const currentEnd = currentStart.add(sessionLen, "minute");
      if (currentEnd.isAfter(end)) {
        newSessions.push({
          start: currentStart.toDate(),
          end: end.toDate(),
        });
        break;
      }

      newSessions.push({
        start: currentStart.toDate(),
        end: currentEnd.toDate(),
      });
      currentStart = currentEnd;
    }

    setSessions(newSessions);
  };

  // Trigger calculation when debounced values change
  useEffect(() => {
    calculateSessions();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [debouncedStartTime, debouncedEndTime, debouncedSessionLength]);

  const handleSave = () => {
    if (!startTime || !endTime) {
      setErrorMessage("Hãy chọn thời gian bắt đầu và kết thúc!");
      return;
    }

    if (dayjs(startTime).isBefore(dayjs())) {
      setErrorMessage("Thời gian bắt đầu phải lớn hơn thời gian hiện tại!");
      return;
    }

    if (dayjs(endTime).isBefore(dayjs(startTime))) {
      setErrorMessage("Thời gian kết thúc phải lớn hơn thời gian bắt đầu!");
      return;
    }

    setLoading(true);
    onCreate({
      startTime,
      endTime,
      sessionLength:
        sessionLength || dayjs(endTime).diff(dayjs(startTime), "minute"),
      sessions,
    });
  };

  const formatTime = (date) => {
    if (!date) return "--:--";
    return dayjs(date).format("HH:mm A");
  };

  return (
    <>
      {/* Hidden anchor for popper positioning */}
      <Box
        id="datetimepicker"
        style={{ position: "fixed", top: 0, right: 0, width: 0, height: 0 }}
      />

      <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
        <DialogTitle>Tạo Phiên Họp</DialogTitle>
        <DialogContent>
          <Box sx={{ mt: 2, display: "flex", flexDirection: "column", gap: 2 }}>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <DateTimePicker
                  disablePast
                  label="Thời Gian Bắt Đầu"
                  format="HH:mm dd/MM/yyyy"
                  value={startTime}
                  onChange={(value) => {
                    setStartTime(value);
                    setErrorMessage("");
                    if (value && !endTime) {
                      setEndTime(dayjs(value).add(1, "hour").toDate());
                    }
                  }}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                    },
                    popper: {
                      anchorEl: document.getElementById("datetimepicker"),
                    },
                  }}
                />
              </Grid>
              <Grid item xs={6}>
                <DateTimePicker
                  disablePast
                  label="Thời Gian Kết Thúc"
                  format="HH:mm dd/MM/yyyy"
                  value={endTime}
                  onChange={(value) => {
                    setEndTime(value);
                    setErrorMessage("");
                  }}
                  slotProps={{
                    textField: {
                      fullWidth: true,
                    },
                    popper: {
                      anchorEl: document.getElementById("datetimepicker"),
                    },
                  }}
                />
              </Grid>
            </Grid>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  label="Thời Gian Phiên (phút)"
                  type="number"
                  value={sessionLength}
                  onChange={(e) => {
                    setSessionLength(e.target.value);
                  }}
                  fullWidth
                  size="medium"
                  helperText="Tùy chọn: Nếu không nhập, sẽ tạo một phiên duy nhất."
                  sx={{
                    width: "100%",
                    maxWidth: "none",
                  }}
                />
              </Grid>
            </Grid>

            {sessions.length > 0 && (
              <>
                <Divider sx={{ my: 2 }} />
                <Typography variant="subtitle1">Tóm Tắt Phiên Họp</Typography>
                {sessions.length === 1 &&
                (!sessionLength || parseInt(sessionLength) <= 0) ? (
                  <Typography color="textSecondary">
                    Một phiên họp sẽ được tạo từ {formatTime(startTime)} đến{" "}
                    {formatTime(endTime)}.
                  </Typography>
                ) : (
                  sessions.map((session, index) => (
                    <Typography key={index}>
                      Phiên {index + 1}: {formatTime(session.start)} -{" "}
                      {formatTime(session.end)}
                    </Typography>
                  ))
                )}
              </>
            )}

            {warningMessage && (
              <Typography color="warning.main" sx={{ mt: 1 }}>
                {warningMessage}
              </Typography>
            )}
            {errorMessage && (
              <Typography color="error" sx={{ mt: 1 }}>
                {errorMessage}
              </Typography>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={resetFields}>Đặt Lại</Button>
          <Button onClick={onClose}>Hủy</Button>
          <Button onClick={handleSave} variant="contained" disabled={loading}>
            {loading ? "Đang Tạo..." : "Tạo Phiên Họp"}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

CreateSessionsDialog.propTypes = {
  open: PropTypes.bool.isRequired,
  onClose: PropTypes.func.isRequired,
  onCreate: PropTypes.func.isRequired,
};

export default CreateSessionsDialog;
