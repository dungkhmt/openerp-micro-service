import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  CircularProgress,
  Alert,
} from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import { request } from "@/api";
import toast from "react-hot-toast";

const AddStaffModal = ({ open, onClose, onSubmit, initialValues }) => {
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

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null); 

  useEffect(() => {
    if (open) {
      setFormValues({
        fullname: initialValues?.fullname || "",
        email: initialValues?.email || "",
        department_code: initialValues?.department?.department_code || null,
        job_position_code: initialValues?.job_position?.job_position_code || null,
      });

      fetchDepartments("", initialValues?.department?.department_code);
      fetchJobPositions("", initialValues?.job_position?.job_position_code);
    }
  }, [open, initialValues]);


  const fetchDepartments = async (searchValue, defaultCode = null) => {
    setLoadingDepartments(true);
    try {
      const payload = {
        department_code: null,
        department_name: searchValue || null,
        status: "ACTIVE",
        pageable_request: null,
      };

      request(
          "get",
          "/departments",
          (res) => {
            const data = res.data.data || [];
            setDepartments(data);


            if (defaultCode) {
              const defaultDepartment = data.find(
                  (dept) => dept.department_code === defaultCode
              );
              if (defaultDepartment) {
                setFormValues((prev) => ({
                  ...prev,
                  department_code: defaultDepartment.department_code,
                }));
              }
            }
          },
          { onError: (err) => console.error("Error fetching departments:", err) },
        null,
          payload
      );
    } catch (error) {
      console.error("Error fetching departments:", error);
    } finally {
      setLoadingDepartments(false);
    }
  };


  const fetchJobPositions = async (searchValue, defaultCode = null) => {
    setLoadingJobPositions(true);
    try {
      const payload = {
        code: null,
        name: searchValue || null,
        status: "ACTIVE",
        pageable_request: null,
      };

      request(
          "get",
          "/jobs",
          (res) => {
            const data = res.data.data || [];
            setJobPositions(data);

            if (defaultCode) {
              const defaultJob = data.find((job) => job.code === defaultCode);
              if (defaultJob) {
                setFormValues((prev) => ({
                  ...prev,
                  job_position_code: defaultJob.code,
                }));
              }
            }
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

  const handleSubmit = async () => {
    setLoading(true);

    const payload = {
      fullname: formValues.fullname,
      email: formValues.email,
      department_code:
          formValues.department_code === initialValues?.department?.department_code
              ? null
              : formValues.department_code,
      job_position_code:
          formValues.job_position_code === initialValues?.job_position?.job_position_code
              ? null
              : formValues.job_position_code,
    };

    try {
      const apiEndpoint = initialValues
          ? `/staffs/${initialValues?.staff_code}`
          : "/staffs";
      const methodURL = initialValues
        ? `put`
        : "post";
      await request(
        methodURL,
          apiEndpoint,
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
                  toast.error("Invalid input provided.")
                } else {
                  console.warn("API Error:", meta?.message || "Error occurred", data);
                  toast.error("An unexpected error occurred.");
                }
              } else {
                console.error("Unexpected error response:", err);
                toast.error("Something went wrong. Please try again later.");
              }
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
          <DialogTitle>{initialValues ? "Edit Employee" : "Add Employee"}</DialogTitle>
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
                disabled={!!initialValues}
                InputProps={{
                  style: !!initialValues ? { color: "#8c8c8c", backgroundColor: "#f5f5f5" } : {}, // Add grayed-out style
                }}
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
              {loading ? "Submitting..." : initialValues ? "Save Changes" : "Submit"}
            </Button>
          </DialogActions>
        </Dialog>
      </>
  );
};

export default AddStaffModal;
