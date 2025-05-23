// src/features/rosterConfiguration/ConstraintsManager.jsx
import React from 'react';
import {
  Box, Typography, Paper, FormControlLabel,
  Grid, Switch, Tooltip
} from '@mui/material';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import BlockIcon from '@mui/icons-material/Block';
import TextField from '@mui/material/TextField'; // Thêm nếu TextField chưa được import ở đây

export default function ConstraintsManager({ constraints, setConstraints }) {
  const toggleConstraint = (key) => setConstraints(prev => ({ ...prev, [key]: { ...prev[key], enabled: !prev[key].enabled } }));
  const handleConstraintValueChange = (key, paramName, value, type) => {
    let pValue = type === 'number' ? (value === '' ? '' : Number(value)) : value;
    setConstraints(prev => ({ ...prev, [key]: { ...prev[key], params: { ...prev[key].params, [paramName]: { ...prev[key].params[paramName], value: pValue } } } }));
  };
  return (
    <Paper elevation={0} variant="outlined" sx={{ p: { xs: 1.5, sm: 2 }, mb: 2.5, borderColor: 'rgba(0,0,0,0.08)' }}>
      <Typography variant="h6" gutterBottom sx={{ color: 'primary.dark', fontSize: '1.05rem', display: 'flex', alignItems: 'center', mb: 1.5 }}>
        <BlockIcon sx={{ mr: 1, color: 'primary.main', fontSize: '1.3rem' }} /> Các Ràng Buộc Công Việc
      </Typography>
      <Box sx={{ maxHeight: '220px', overflowY: 'auto', pr: 0.5 }}>
        {Object.entries(constraints).map(([key, constraint]) => (
          <Paper key={key} variant="outlined" sx={{ p: 1.5, mb: 1.5, borderColor: 'rgba(0,0,0,0.1)' }}>
            <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <FormControlLabel control={<Switch checked={constraint.enabled} onChange={() => toggleConstraint(key)} color="secondary" />} label={<Typography sx={{fontSize: '0.9rem'}} fontWeight="500" color={constraint.enabled ? "text.primary" : "text.disabled"}>{constraint.description}</Typography>} />
              {constraint.tooltip && <Tooltip title={constraint.tooltip}><InfoOutlinedIcon color="action" sx={{ ml: 1, cursor: 'help', fontSize: '1rem' }} /></Tooltip>}
            </Box>
            {constraint.enabled && constraint.params && (
              <Box sx={{ pl: { xs: 0, sm: 1 }, pt: 1, mt: 1, borderTop: '1px solid', borderColor: 'divider' }}>
                <Grid container spacing={1.5}>
                  {Object.entries(constraint.params).map(([paramName, paramDetails]) => (
                    <Grid item xs={12} sm={paramDetails.fullWidth ? 12 : 6} key={paramName}>
                      <TextField fullWidth type={paramDetails.type === 'number' ? 'number' : 'text'} label={paramDetails.label} value={paramDetails.value}
                                 inputProps={{ min: paramDetails.type === 'number' ? (paramDetails.min === undefined ? 0 : paramDetails.min) : undefined, step: paramDetails.step || 1 }}
                                 onChange={(e) => handleConstraintValueChange(key, paramName, e.target.value, paramDetails.type)}
                      />
                    </Grid>
                  ))}
                </Grid>
              </Box>
            )}
          </Paper>
        ))}
      </Box>
    </Paper>
  );
}