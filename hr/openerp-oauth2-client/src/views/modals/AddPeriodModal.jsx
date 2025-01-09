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
  IconButton,
  Typography,
  Box,
} from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import { request } from "../../api";

const AddPeriodModal = ({ open, onClose, onSubmit, initialValues }) => {
  const [formValues, setFormValues] = useState({
    name: "",
    description: "",
    checkpoint_date: "",
    configures: [],
  });

  const [availableConfigures, setAvailableConfigures] = useState([]);
  const [loadingConfigures, setLoadingConfigures] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (open) {
      setFormValues({
        name: initialValues?.name || "",
        description: initialValues?.description || "",
        checkpoint_date: initialValues?.checkpoint_date || "",
        configures: initialValues?.configures || [],
      });
      fetchConfigures();
    }
  }, [open, initialValues]);

  const fetchConfigures = async () => {
    setLoadingConfigures(true);
    try {
      const payload = {
        code: null,
        name: null,
        status: "ACTIVE",
        pageable_request: {
          page: 0,
          page_size: 100,
        },
      };

      request(
        "post",
        "/checkpoint/get-all-configure",
        (res) => {
          setAvailableConfigures(res.data.data || []);
        },
        {
          onError: (err) =>
            console.error("Error fetching available configures:", err),
        },
        payload
      );
    } catch (error) {
      console.error("Error fetching available configures:", error);
    } finally {
      setLoadingConfigures(false);
    }
  };

  const handleAddConfigure = () => {
    setFormValues((prev) => ({
      ...prev,
      configures: [
        ...prev.configures,
        { configure_id: "", coefficient: "", description: "" },
      ],
    }));
  };

  const handleConfigureChange = (index, value) => {
    const selectedConfigure = availableConfigures.find(
      (config) => config.code === value?.code
    );

    setFormValues((prev) => {
      const updatedConfigures = [...prev.configures];
      updatedConfigures[index] = {
        configure_id: value?.code || "",
        coefficient: updatedConfigures[index].coefficient || "",
        description: selectedConfigure?.description || "",
      };
      return { ...prev, configures: updatedConfigures };
    });
  };

  const handleCoefficientChange = (index, value) => {
    setFormValues((prev) => {
      const updatedConfigures = [...prev.configures];
      updatedConfigures[index].coefficient = value;
      return { ...prev, configures: updatedConfigures };
    });
  };

  const handleRemoveConfigure = (index) => {
    setFormValues((prev) => {
      const updatedConfigures = prev.configures.filter((_, i) => i !== index);
      return { ...prev, configures: updatedConfigures };
    });
  };

  const handleSubmit = async () => {
    setLoading(true);

    const payload = {
      name: formValues.name,
      description: formValues.description,
      checkpoint_date: formValues.checkpoint_date,
      status: "ACTIVE",
      configures: formValues.configures.map((config) => ({
        configure_id: config.configure_id,
        coefficient: parseFloat(config.coefficient),
      })),
    };

    try {
      await request(
        "post",
        "/checkpoint/create-period",
        () => {
          onSubmit();
          onClose();
        },
        {
          onError: (err) =>
            setError(err.response?.data?.meta?.message || "Error occurred"),
        },
        payload
      );
    } catch (error) {
      console.error("Error creating period:", error);
      setError("Failed to save the period.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>{initialValues ? "Edit Period" : "Add Period"}</DialogTitle>
      <DialogContent>
        <TextField
          fullWidth
          label="Name"
          value={formValues.name}
          onChange={(e) =>
            setFormValues((prev) => ({ ...prev, name: e.target.value }))
          }
          margin="normal"
        />
        <TextField
          fullWidth
          label="Description"
          value={formValues.description}
          onChange={(e) =>
            setFormValues((prev) => ({ ...prev, description: e.target.value }))
          }
          margin="normal"
        />
        <TextField
          fullWidth
          label="Checkpoint Date"
          type="date"
          value={formValues.checkpoint_date}
          onChange={(e) =>
            setFormValues((prev) => ({
              ...prev,
              checkpoint_date: e.target.value,
            }))
          }
          margin="normal"
          InputLabelProps={{
            shrink: true,
          }}
        />
        <Box mt={2}>
          <Typography variant="h6">Configures</Typography>
          {formValues.configures.map((config, index) => (
            <Box
              key={index}
              display="flex"
              alignItems="center"
              justifyContent="space-between"
              mt={2}
              mb={1}
            >
              <Autocomplete
                options={availableConfigures}
                getOptionLabel={(option) => option.name || ""}
                value={
                  availableConfigures.find(
                    (c) => c.code === config.configure_id
                  ) || null
                }
                onChange={(e, value) => handleConfigureChange(index, value)}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label="Configure"
                    size="medium"
                    InputProps={{
                      ...params.InputProps,
                      endAdornment: (
                        <>
                          {loadingConfigures ? (
                            <CircularProgress size={20} />
                          ) : null}
                          {params.InputProps.endAdornment}
                        </>
                      ),
                    }}
                  />
                )}
                style={{ flex: 2, marginRight: "10px" }}
              />
              <TextField
                label="Coefficient"
                type="number"
                size="medium"
                value={config.coefficient}
                onChange={(e) =>
                  handleCoefficientChange(index, e.target.value)
                }
                style={{ width: "120px", marginRight: "10px" }}
              />
              <Typography
                style={{
                  flex: 3,
                  fontStyle: "italic",
                  color: "#555",
                  marginRight: "10px",
                }}
              >
                {config.description || "No description"}
              </Typography>
              <IconButton
                color="secondary"
                onClick={() => handleRemoveConfigure(index)}
              >
                <DeleteOutlineIcon />
              </IconButton>
            </Box>
          ))}
          <IconButton
            color="primary"
            onClick={handleAddConfigure}
            style={{ marginTop: "10px" }}
          >
            <AddCircleOutlineIcon fontSize="large" />
          </IconButton>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} color="secondary" disabled={loading}>
          Cancel
        </Button>
        <Button
          onClick={handleSubmit}
          color="primary"
          variant="contained"
          disabled={loading}
        >
          {loading ? "Submitting..." : "Submit"}
        </Button>
      </DialogActions>
      {error && (
        <Snackbar
          open={!!error}
          autoHideDuration={6000}
          onClose={() => setError(null)}
        >
          <Alert severity="error">{error}</Alert>
        </Snackbar>
      )}
    </Dialog>
  );
};

export default AddPeriodModal;
