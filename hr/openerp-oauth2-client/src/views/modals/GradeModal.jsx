import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  CircularProgress,
  TextField,
  Grid,
} from "@mui/material";
import { request } from "../api";

const GradeModal = ({ open, onClose, staff, period }) => {
  const [configures, setConfigures] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchPeriodDetails = async () => {
    setLoading(true);
    try {
      const periodResponse = await request("post", "/checkpoint/get-period-detail", null, null, {
        id: period.id,
      });
      const checkpointResponse = await request("post", "/checkpoint/get-checkpoint", null, null, {
        period_id: period.id,
        user_id: staff.user_login_id,
      });

      const periodConfigs = periodResponse.data.data.configures || [];
      const checkpointConfigs = checkpointResponse.data.data.configure_points || [];

      const configs = periodConfigs.map((config) => ({
        configure_id: config.configure.code,
        name: config.configure.name,
        coefficient: config.coefficient,
        point: checkpointConfigs.find((c) => c.configure_id === config.configure.code)?.point || "",
      }));

      setConfigures(configs);
    } catch (error) {
      console.error("Error fetching period details:", error);
    } finally {
      setLoading(false);
    }
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
      await request("post", "/checkpoint/checkpoint-staff", null, null, payload);
      alert("Grades saved successfully!");
      onClose();
    } catch (error) {
      console.error("Error saving grades:", error);
      alert("Error saving grades.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (open && staff && period) {
      fetchPeriodDetails();
    }
  }, [open, staff, period]);

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Grade: {staff?.fullname}</DialogTitle>
      <DialogContent>
        {loading ? (
          <CircularProgress />
        ) : (
          configures.map((config, index) => (
            <Grid container spacing={2} key={config.configure_id} alignItems="center" style={{ marginBottom: "16px" }}>
              <Grid item xs={6}>
                <strong>{config.name}</strong> (Coefficient: {config.coefficient})
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="Point"
                  value={config.point}
                  onChange={(e) => {
                    const updatedConfigs = [...configures];
                    updatedConfigs[index].point = e.target.value;
                    setConfigures(updatedConfigs);
                  }}
                />
              </Grid>
            </Grid>
          ))
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
  );
};

export default GradeModal;
