import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  IconButton,
  Snackbar,
  Alert,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { request } from "@/api";

const AddJobPositionModal = ({ open, onClose, onSubmit, initialValues }) => {
  const [formValues, setFormValues] = useState({
    name: "",
    description: "",
  });
  const [error, setError] = useState(null); // Error state for Snackbar
  const [loading, setLoading] = useState(false); // Loading state for submit button

  // Populate form values if editing
  useEffect(() => {
    if (initialValues) {
      setFormValues({
        name: initialValues.name || "",
        description: initialValues.description || "",
      });
    } else {
      setFormValues({ name: "", description: "" });
    }
  }, [initialValues]);

  // Handle form field changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
  };

  // Handle form submission
  const handleSubmit = async () => {
    setLoading(true);

    const payload = {
      name: formValues.name,
      description: formValues.description,
    };

    // If editing, include additional properties
    if (initialValues) {
      payload.code = initialValues.code;
      payload.status = "ACTIVE"; // Example: keeping status ACTIVE during edit
    }

    try {
      const endpoint = initialValues
        ? "/job/update-job-position" // Edit job position
        : "/job/create-job-position"; // Add job position

      request(
        "post",
        endpoint,
        (response) => {
          onSubmit(); // Refresh parent data
          onClose(); // Close the modal
          setFormValues({ name: "", description: "" }); // Reset form
        },
        {
          onError: (err) => {
            if (err.response && err.response.data) {
              const { meta, data } = err.response.data;

              if (meta && meta.code) {
                // Handle validation or other API errors
                console.error("Validation error:", meta.message, data);
                setError({
                  title: meta.message || "Validation Error",
                  info: data || "Invalid input provided.",
                });
              } else {
                console.error("API Error:", meta?.message || "Error occurred", data);
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
      <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" className="custom-modal">
        <DialogTitle>
          {initialValues ? "Edit Job Position" : "Add Job Position"}
          <IconButton
            aria-label="close"
            onClick={onClose}
            style={{ position: "absolute", right: 8, top: 8 }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <TextField
            fullWidth
            label="Name"
            name="name"
            value={formValues.name}
            onChange={handleChange}
            margin="normal"
          />
          <TextField
            fullWidth
            label="Description"
            name="description"
            value={formValues.description}
            onChange={handleChange}
            margin="normal"
            multiline
            rows={3}
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
          <strong>{error?.title}</strong>
          <br />
          {error?.info}
        </Alert>
      </Snackbar>
    </>
  );
};

export default AddJobPositionModal;
