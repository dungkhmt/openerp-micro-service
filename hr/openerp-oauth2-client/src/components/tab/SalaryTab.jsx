import React, { useState, useEffect } from "react";
import {
  TextField,
  Button,
  CircularProgress,
  Snackbar,
  Alert,
  Divider,
} from "@mui/material";
import { request } from "../../api";

const SalaryTab = ({ userLoginId }) => {
  const [salaryData, setSalaryData] = useState({
    salary_type: "MONTHLY",
    salary: "",
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null); // Error state for Snackbar
  const [saving, setSaving] = useState(false); // Loading state for saving

  useEffect(() => {
    if (userLoginId) {
      fetchSalary();
    }
  }, [userLoginId]);

  const fetchSalary = async () => {
    setLoading(true);
    const payload = { user_login_id: userLoginId };

    try {
      request(
        "post",
        "/salary/get-salary",
        (res) => {
          if (res.data?.data) {
            setSalaryData({
              salary_type: res.data.data.salary_type || "MONTHLY",
              salary: res.data.data.salary || "",
            });
          }
        },
        {
          onError: (err) => console.error("Error fetching salary:", err),
        },
        payload
      );
    } catch (err) {
      console.error("Failed to fetch salary data:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    const payload = {
      user_login_id: userLoginId,
      salary_type: salaryData.salary_type,
      salary: salaryData.salary,
    };

    try {
      request(
        "post",
        "/salary/update-salary",
        () => {
          setError(null);
          fetchSalary(); // Refresh salary after update
        },
        {
          onError: (err) => {
            console.error("Error updating salary:", err);
            setError("Failed to save salary data. Please try again.");
          },
        },
        payload
      );
    } catch (err) {
      console.error("Error while saving salary:", err);
      setError("An unexpected error occurred.");
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

      {/* Error Snackbar */}
      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
      >
        <Alert severity="error" onClose={() => setError(null)} variant="filled">
          {error}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default SalaryTab;
