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
  CircularProgress,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { request } from "@/api";

const AddCheckpointConfigureModal = ({ open, onClose, onSubmit, initialValues }) => {
  const [formValues, setFormValues] = useState({
    name: "",
    description: "",
  });
  const [error, setError] = useState(null); 
  const [loading, setLoading] = useState(false); 

 
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
  };


  const handleSubmit = async () => {
    setLoading(true);

    const payload = {
      name: formValues.name,
      description: formValues.description,
    };

    try {
      const endpoint = initialValues
        ? `/checkpoints/configures/${initialValues.code}`
        : "/checkpoints/configures";
      const methodURL = initialValues
        ? "put"
        : "post";

      request(
        methodURL,
        endpoint,
        (response) => {
          onSubmit(); 
          onClose(); 
          setFormValues({ name: "", description: "" }); 
        },
        {
          onError: (err) => {
            if (err.response && err.response.data) {
              const { meta, data } = err.response.data;

              if (meta && meta.code) {
             
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
          {initialValues ? "Edit Configure" : "Add Configure"}
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
            {loading ? <CircularProgress size={24} /> : "Submit"}
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

export default AddCheckpointConfigureModal;
