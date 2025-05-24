import React, {useEffect, useState} from "react";
import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Divider,
  Grid,
  TextField,
  Typography,
} from "@mui/material";
import {request} from "@/api";
import toast from "react-hot-toast";

const MAX_POINT = 10; // Maximum point per configure

const GradeModal = ({ open, onClose, staff, period }) => {
  const [configures, setConfigures] = useState([]);
  const [loading, setLoading] = useState(false);
  const [periodDetails, setPeriodDetails] = useState({});
  const [totalPoint, setTotalPoint] = useState(null);
  const [success, setSuccess] = useState(false); // State for success Snackbar

  const fetchPeriodDetails = async () => {
    setLoading(true);
    try {
      const periodResponse = await request("get", `/checkpoints/periods/${period.id}`);
      const checkpointResponse = await request("get", `/checkpoints/${period.id}/${staff.user_login_id}`);

      const periodConfigs = periodResponse.data.data.configures || [];
      const checkpointConfigs = checkpointResponse.data.data.configure_points || [];

      const configs = periodConfigs.map((config) => ({
        configure_id: config.configure.code,
        name: config.configure.name,
        description: config.configure.description,
        coefficient: config.coefficient,
        point: checkpointConfigs.find((c) => c.configure_id === config.configure.code)?.point || "",
      }));

      const calculatedTotalPoint = checkpointResponse.data.data.total_point || null;

      setPeriodDetails(periodResponse.data.data);
      setConfigures(configs);
      setTotalPoint(calculatedTotalPoint);
    } catch (error) {
      console.error("Error fetching period details:", error);
    } finally {
      setLoading(false);
    }
  };

  const calculateTotalPoints = () => {
    const totalPoints = configures.reduce((sum, config) => {
      const point = parseFloat(config.point || 0);
      const coefficient = parseFloat(config.coefficient || 0);
      return sum + point * coefficient;
    }, 0);

    const totalCoefficient = configures.reduce((sum, config) => {
      return sum + parseFloat(config.coefficient || 0);
    }, 0);

    const result = totalCoefficient > 0 ? (totalPoints / totalCoefficient).toFixed(2) : "N/A";
    setTotalPoint(result);
  };

  const handleSave = async () => {
    setLoading(true);
    try {
      const payload = {
        period_id: period.id,
        user_id: staff.user_login_id,
        checkpoint_configures: configures.map((config) => ({
          configure_id: config.configure_id,
          point: parseFloat(config.point),
        })),
      };
      await request("post", "/checkpoints",
        null,
        null,
        payload);
      setSuccess(true);
      toast.success("Chấm điểm thành công")
      onClose(); 
    } catch (error) {
      toast.error("Chấm điểm thất bại");
    } finally {
      setLoading(false);
    }
  };

  const handlePointChange = (index, value) => {
    const updatedConfigs = [...configures];
    const point = Math.min(Math.max(0, value), MAX_POINT); // Ensure the point is between 0 and MAX_POINT
    updatedConfigs[index].point = point;
    setConfigures(updatedConfigs);
  };

  useEffect(() => {
    if (open && staff && period) {
      fetchPeriodDetails();
    }
  }, [open, staff, period]);

  useEffect(() => {
    calculateTotalPoints();
  }, [configures]);

  return (
    <>
      <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
        <DialogTitle sx={{ fontSize: "1.8rem", fontWeight: "bold" }}>{periodDetails.name}</DialogTitle>
        <DialogContent>
          {loading ? (
            <CircularProgress />
          ) : (
            <>
              {/* Period Description */}
              <Box mb={2}>
                <Typography variant="body1" color="textSecondary" sx={{ whiteSpace: "pre-wrap" }}>
                  {periodDetails.description}
                </Typography>
              </Box>

              <Divider />

              {/* Grader Information */}
              <Box mt={2} mb={2}>
                <Typography variant="subtitle1" fontWeight="bold">
                  Grading for: {staff?.fullname}
                </Typography>
                <Typography variant="body2" color="textSecondary">
                  Email: {staff?.email}
                </Typography>
              </Box>

              <Divider />

              {/* Configure Points */}
              <Box mt={2}>
                {configures.map((config, index) => (
                  <Box key={config.configure_id} mb={3} p={2} border="1px solid #ddd" borderRadius="8px">
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={4}>
                        <Typography variant="body1" fontWeight="bold">
                          {config.name}
                        </Typography>
                        <Typography variant="body2" color="textSecondary" sx={{ whiteSpace: "pre-wrap" }}>
                          {config.description}
                        </Typography>
                        <Typography variant="body2" color="textSecondary">
                          Coefficient: {config.coefficient}
                        </Typography>
                      </Grid>
                      <Grid item xs={12} sm={8}>
                        <TextField
                          fullWidth
                          type="number"
                          label={`Point (0-${MAX_POINT})`}
                          value={config.point}
                          onChange={(e) => handlePointChange(index, e.target.value)}
                        />
                      </Grid>
                    </Grid>
                  </Box>
                ))}
              </Box>

              <Divider />

              {/* Total Points */}
              <Box mt={2}>
                <Typography variant="h6" align="right" fontWeight="bold">
                  Total Points: {totalPoint !== null ? totalPoint : "N/A"}
                </Typography>
              </Box>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="secondary">
            Cancel
          </Button>
          <Button onClick={handleSave} color="primary" variant="contained" disabled={loading}>
            {loading ? "Saving..." : "Save"}
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default GradeModal;
