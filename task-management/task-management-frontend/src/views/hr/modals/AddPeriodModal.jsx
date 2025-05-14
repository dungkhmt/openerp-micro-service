import React, { useState, useEffect } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  CircularProgress,
  Button,
  TextField,
  Typography,
  IconButton,
  Snackbar,
  Alert,
  Box,
  Grid,
} from "@mui/material";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import CloseIcon from "@mui/icons-material/Close";
import Autocomplete from "@mui/material/Autocomplete";
import { request } from "@/api";

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
      if (initialValues?.id) {
        fetchPeriodDetail(initialValues.id);
      } else {
        setFormValues({
          name: "",
          description: "",
          checkpoint_date: "",
          configures: [],
        });
      }
      fetchConfigures();
    }
  }, [open, initialValues]);

  const fetchPeriodDetail = async (id) => {
    try {
      request(
        "get",
        `/checkpoints/periods/${id}`,
        (res) => {
          const { data } = res.data;
      
          if (!data) {
            console.error("No data received from API");
            setFormValues({
              name: "",
              description: "",
              checkpoint_date: "",
              configures: [],
            });
            return;
          }
          setFormValues({
            name: data.name || "",
            description: data.description || "",
            checkpoint_date: data.checkpoint_date || "",
            configures: (data.configures || []).map((item) => ({
              configure_id: item.configure?.code || "",
              coefficient: item.coefficient || "",
              description: item.configure?.description || "",
            })),
          });
        },
        {
          onError: (err) =>
            console.error("Error fetching period details:", err),
        }
      );
      
    } catch (error) {
      console.error("Error fetching period details:", error);
    }
  };

  const fetchConfigures = async () => {
    setLoadingConfigures(true);
    try {
      const payload = {
        code: null,
        name: null,
        status: "ACTIVE",
        page: 0,
        pageSize: 100,
      };

      request(
        "post",
        "/checkpoint/",
        (res) => {
          setAvailableConfigures(res.data.data || []);
        },
        {
          onError: (err) =>
            console.error("Error fetching available configures:", err),
        },
        null,
        {params: payload}
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
      const endpoint = initialValues?.id
        ? `/checkpoints/periods/${initialValues.id}`
        : "/checkpoints/periods";
      const methodURL = initialValues
        ? `put`
        : "post";
      await request(
        methodURL,
        endpoint,
        () => {
          onSubmit(); // Callback to refresh parent data
          onClose();  // Close the modal
        },
        {
          onError: (err) =>
            setError(err.response?.data?.meta?.message || "Error occurred"),
        },
        payload
      );
    } catch (error) {
      console.error("Error saving period:", error);
      setError("Failed to save the period.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="md">
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
          InputProps={{ style: { fontSize: "1.2rem" } }}
        />
        <TextField
          fullWidth
          label="Description"
          value={formValues.description}
          onChange={(e) =>
            setFormValues((prev) => ({ ...prev, description: e.target.value }))
          }
          margin="normal"
          InputProps={{ style: { fontSize: "1.2rem" } }}
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
          InputProps={{ style: { fontSize: "1.2rem" } }}
        />
        <Box mt={4}>
          <Typography variant="h6" style={{ marginBottom: "16px" }}>
            Configures
          </Typography>

          <Box>
            {formValues.configures.map((config, index) => (
              <Grid
                container
                alignItems="center"
                spacing={2}
                key={index}
                style={{ marginBottom: "16px" }}
              >
                <Grid item xs={4}>
                  <Autocomplete
                    options={availableConfigures}
                    getOptionLabel={(option) => option.name || ""}
                    value={
                      availableConfigures.find(
                        (c) => c.code === config.configure_id
                      ) || null
                    }
                    onChange={(e, value) =>
                      handleConfigureChange(index, value)
                    }
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
                  />
                </Grid>
                <Grid item xs={3}>
                  <TextField
                    label="Coefficient"
                    type="number"
                    size="medium"
                    value={config.coefficient}
                    onChange={(e) =>
                      handleCoefficientChange(index, e.target.value)
                    }
                  />
                </Grid>
                <Grid item xs={4}>
                  <Typography
                    style={{
                      fontStyle: "italic",
                      color: "#555",
                      overflow: "auto",
                      textOverflow: "ellipsis",
                      whiteSpace: "normal",
                      maxHeight: "8rem",
                      lineHeight: "1.8rem",
                      padding: "1rem",
                      border: "1px solid #ccc",
                      borderRadius: "6px",
                      backgroundColor: "#fefefe",
                      boxShadow: "0 1px 4px rgba(0, 0, 0, 0.1)",
                    }}
                  >
                    {config.description || "No description"}
                  </Typography>
                </Grid>
                <Grid item xs={1}>
                  <IconButton
                    color="error"
                    onClick={() => handleRemoveConfigure(index)}
                  >
                    <CloseIcon />
                  </IconButton>
                </Grid>
              </Grid>
            ))}
          </Box>
          <Button
            onClick={handleAddConfigure}
            startIcon={<AddCircleOutlineIcon />}
            color="primary"
          >
            Add Configure
          </Button>
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
