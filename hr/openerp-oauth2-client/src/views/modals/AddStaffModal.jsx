import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  CircularProgress,
  Snackbar,
  Alert,
} from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { request } from "../../api";

const AddStaffModal = ({ open, onClose, onSubmit }) => {
  const [formValues, setFormValues] = useState({
    fullname: "",
    email: "",
    department_code: null,
    job_position_code: null,
  });

  const [departments, setDepartments] = useState([]);
  const [jobPositions, setJobPositions] = useState([]);

  const [loadingDepartments, setLoadingDepartments] = useState(false);
  const [loadingJobPositions, setLoadingJobPositions] = useState(false);

  const [searchDepartment, setSearchDepartment] = useState("");
  const [searchJob, setSearchJob] = useState("");

  const [loading, setLoading] = useState(false); // Submit button loading state
  const [error, setError] = useState(null); // Error state for Snackbar

  // Fetch departments based on search term
  const fetchDepartments = async (searchValue) => {
    setLoadingDepartments(true);
    try {
      const payload = {
        department_code: null,
        department_name: searchValue || null,
        status: "ACTIVE",
        pageable_request: null,
      };

      request(
        "post",
        "/department/get-department",
        (res) => {
          setDepartments(res.data.data || []);
        },
        { onError: (err) => console.error("Error fetching departments:", err) },
        payload
      );
    } catch (error) {
      console.error("Error fetching departments:", error);
    } finally {
      setLoadingDepartments(false);
    }
  };

  // Fetch job positions based on search term
  const fetchJobPositions = async (searchValue) => {
    setLoadingJobPositions(true);
    try {
      const payload = {
        code: null,
        name: searchValue || null,
        status: "ACTIVE",
        pageable_request: null,
      };

      request(
        "post",
        "/job/get-job-position",
        (res) => {
          setJobPositions(res.data.data || []);
        },
        { onError: (err) => console.error("Error fetching job positions:", err) },
        payload
      );
    } catch (error) {
      console.error("Error fetching job positions:", error);
    } finally {
      setLoadingJobPositions(false);
    }
  };

  useEffect(() => {
    if (open) {
      fetchDepartments("");
      fetchJobPositions("");
    }
  }, [open]);

  const handleSubmit = async () => {
    setLoading(true);
    const payload = {
      fullname: formValues.fullname,
      email: formValues.email,
      department_code: formValues.department_code,
      job_position_code: formValues.job_position_code,
    };

    try {
      request(
        "post",
        "/staff/add-staff",
        () => {
          onSubmit();
          onClose();
          setFormValues({
            fullname: "",
            email: "",
            department_code: null,
            job_position_code: null,
          });
        },
        {
          onError: (err) => {
            if (err.response && err.response.data) {
              const { meta, data } = err.response.data;

              if (meta && meta.code) {
                console.warn("Validation error:", meta.message, data);
                setError({
                  title: meta.message || "Validation Error",
                  info: data || "Invalid input provided.",
                });
              } else {
                console.warn("API Error:", meta?.message || "Error occurred", data);
                setError({
                  title: meta?.message || "Error",
                  info: data || "An unexpected error occurred.",
                });
              }
            } else {
              console.error("Unexpected error response:", err);
              setError({
                title: "Error",
                info: "Something went wrong. Please try again later.",
              });
            }
          },
          500: (err) => {
            console.error("Server error:", err);
            setError({
              title: "Server Error",
              info: "The server is currently unavailable.",
            });
          },
        },
        payload
      );
    } catch (error) {
      console.error("API request failed:", error);
      setError({
        title: "Error",
        info: `Something went wrong. ${error.message || "Please try again."}`,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
        <DialogTitle>Add Employee</DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Full Name"
            name="fullname"
            value={formValues.fullname}
            onChange={(e) =>
              setFormValues((prev) => ({ ...prev, fullname: e.target.value }))
            }
            margin="normal"
          />
          <TextField
            fullWidth
            label="Email"
            name="email"
            value={formValues.email}
            onChange={(e) =>
              setFormValues((prev) => ({ ...prev, email: e.target.value }))
            }
            margin="normal"
          />
          <Autocomplete
            fullWidth
            options={departments}
            getOptionLabel={(option) => option.department_name || ""}
            isOptionEqualToValue={(option, value) =>
              option.department_code === value.department_code
            }
            inputValue={searchDepartment}
            onInputChange={(e, value) => {
              setSearchDepartment(value);
              fetchDepartments(value);
              if (formValues.department_code) {
                setFormValues((prev) => ({ ...prev, department_code: null }));
              }
            }}
            onChange={(e, value) =>
              setFormValues((prev) => ({
                ...prev,
                department_code: value?.department_code || null,
              }))
            }
            value={
              departments.find(
                (dept) => dept.department_code === formValues.department_code
              ) || null
            }
            loading={loadingDepartments}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Select Department"
                margin="normal"
                InputProps={{
                  ...params.InputProps,
                  endAdornment: (
                    <>
                      {loadingDepartments ? <CircularProgress size={20} /> : null}
                      {params.InputProps.endAdornment}
                    </>
                  ),
                }}
              />
            )}
          />
          <Autocomplete
            fullWidth
            options={jobPositions}
            getOptionLabel={(option) => option.name || ""}
            isOptionEqualToValue={(option, value) => option.code === value.code}
            inputValue={searchJob}
            onInputChange={(e, value) => {
              setSearchJob(value);
              fetchJobPositions(value);
              if (formValues.job_position_code) {
                setFormValues((prev) => ({ ...prev, job_position_code: null }));
              }
            }}
            onChange={(e, value) =>
              setFormValues((prev) => ({
                ...prev,
                job_position_code: value?.code || null,
              }))
            }
            value={
              jobPositions.find(
                (job) => job.code === formValues.job_position_code
              ) || null
            }
            loading={loadingJobPositions}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Select Job Position"
                margin="normal"
                InputProps={{
                  ...params.InputProps,
                  endAdornment: (
                    <>
                      {loadingJobPositions ? (
                        <CircularProgress size={20} />
                      ) : null}
                      {params.InputProps.endAdornment}
                    </>
                  ),
                }}
              />
            )}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={onClose} color="secondary" disabled={loading}>
            Cancel
          </Button>
          <Button onClick={handleSubmit} color="primary" variant="contained" disabled={loading}>
            {loading ? "Submitting..." : "Submit"}
          </Button>
        </DialogActions>
      </Dialog>

      <Snackbar
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
        anchorOrigin={{ vertical: "top", horizontal: "right" }}
      >
        <Alert onClose={() => setError(null)} severity="error" variant="filled">
          <strong>{error?.title || "Error"}</strong>
          <br />
          {typeof error?.info === "string"
            ? error?.info
            : JSON.stringify(error?.info, null, 2)} 
        </Alert>
      </Snackbar>

    </>
  );
};

export default AddStaffModal;
