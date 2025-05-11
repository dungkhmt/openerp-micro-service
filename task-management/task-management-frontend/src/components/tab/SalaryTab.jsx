import React, { useState, useEffect } from "react";
import {
  TextField,
  Button,
  CircularProgress,
  Snackbar,
  Alert,
  Divider,
} from "@mui/material";
import { request } from "@/api";

const SalaryTab = ({ userLoginId }) => {
  const [salaryData, setSalaryData] = useState({
    salary_type: "MONTHLY",
    salary: "",
  });
  const [loading, setLoading] = useState(false); // Loading state for fetching data
  const [saving, setSaving] = useState(false); // Loading state for saving data
  const [notification, setNotification] = useState(null); // Success or error notification

  useEffect(() => {
    if (userLoginId) {
      fetchSalary();
    }
  }, [userLoginId]);

  const showNotification = (type, message) => {
    setNotification({ type, message });
  };

  const fetchSalary = async () => {
    setLoading(true);
    const payload = { user_login_id: userLoginId };

    try {
      request(
        "post",
        "/salaries",
        (res) => {
          if (res.data?.data) {
            setSalaryData({
              salary_type: res.data.data.salary_type || "MONTHLY",
              salary: res.data.data.salary || "",
            });
          }
        },
        {
          onError: (err) => {
            console.error("Error fetching salary:", err);
            showNotification("error", "Failed to fetch salary data.");
          },
        },
        payload
      );
    } catch (err) {
      console.error("Failed to fetch salary data:", err);
      showNotification("error", "An unexpected error occurred while fetching salary.");
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    const payload = {
      salary_type: salaryData.salary_type,
      salary: salaryData.salary,
    };

    try {
      request(
        "put",
        `/salaries/${userLoginId}`,
        () => {
          fetchSalary(); 
          showNotification("success", "Salary saved successfully.");
        },
        {
          onError: (err) => {
            if (err.response && err.response.data) {
              const { meta } = err.response.data;

              if (meta?.code) {
                console.error("Validation error:", meta.message);
                showNotification("error", meta.message || "Validation error occurred.");
              } else {
                console.error("API Error:", meta?.message || "Error occurred");
                showNotification("error", "An error occurred while saving salary data.");
              }
            } else {
              console.error("Unexpected error response:", err);
              showNotification("error", "Unexpected error occurred while saving salary.");
            }
          },
        },
        payload
      );
    } catch (err) {
      console.error("Error while saving salary:", err);
      showNotification("error", "An unexpected error occurred while saving salary.");
    } finally {
      setSaving(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setSalaryData((prev) => ({ ...prev, [name]: value }));
  };

  if (loading) return <CircularProgress />;

  return (
    <div style={{ padding: "20px", border: "1px solid #ddd", borderRadius: "8px" }}>
      <h3 style={{ marginBottom: "15px" }}>Basic Salary Information</h3>
      <Divider style={{ marginBottom: "15px" }} />
      <div style={{ marginBottom: "20px" }}>
        <TextField
          fullWidth
          select
          SelectProps={{
            native: true,
          }}
          label="Salary Basis"
          name="salary_type"
          value={salaryData.salary_type}
          onChange={handleChange}
          variant="outlined"
          margin="normal"
        >
          <option value="MONTHLY">Monthly</option>
          <option value="WEEKLY">Weekly</option>
          <option value="HOURLY">Hourly</option>
        </TextField>
        <TextField
          fullWidth
          type="number"
          label="Salary Amount (₫)"
          name="salary"
          value={salaryData.salary}
          onChange={handleChange}
          variant="outlined"
          margin="normal"
        />
      </div>
      <Button
        variant="contained"
        color="primary"
        onClick={handleSave}
        disabled={saving}
      >
        {saving ? "Saving..." : "Save"}
      </Button>

      {/* Notification Snackbar */}
      <Snackbar
        open={!!notification}
        autoHideDuration={6000}
        onClose={() => setNotification(null)}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert
          severity={notification?.type || "error"}
          onClose={() => setNotification(null)}
          variant="filled"
        >
          {notification?.message}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default SalaryTab;
