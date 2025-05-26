// src/features/rosterConfiguration/TemplateListDisplay.jsx
import React, {useState} from 'react';
import {Box, Button, Divider, IconButton, Menu, MenuItem, Paper, Typography} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import PlaylistPlayIcon from '@mui/icons-material/PlaylistPlay';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import SingleTemplateDetails from './SingleTemplateDetails'; // Import component con

export default function TemplateListDisplay({ templates, onEdit, onDelete, onOpenApplyModal, isSubmittingRoster }) {  const [anchorEl, setAnchorEl] = useState(null);
  const [currentTemplateId, setCurrentTemplateId] = useState(null);

  const handleMenuOpen = (event, templateId) => { setAnchorEl(event.currentTarget); setCurrentTemplateId(templateId); };
  const handleMenuClose = () => { setAnchorEl(null); setCurrentTemplateId(null); };
  const handleEdit = () => { const templateToEdit = templates.find(t => t.id === currentTemplateId); if (templateToEdit) onEdit(templateToEdit); handleMenuClose(); };
  const handleDelete = () => { if (currentTemplateId) onDelete(currentTemplateId); handleMenuClose(); };

  if (!templates || templates.length === 0) {
    return (
      <Paper elevation={0} sx={{ p: 3, textAlign: 'center', mt: 0, backgroundColor: 'grey.50', border: '1px dashed', borderColor: 'grey.300' }}>
        <Typography variant="h6" color="text.secondary">Chưa có bộ cấu hình nào.</Typography>
        <Typography color="text.secondary">Nhấn "Tạo Mới" ở trên để bắt đầu.</Typography>
      </Paper>
    );
  }

  return (
    <Box sx={{ mt: 0 }}>
      {templates.map(template => (
        <Paper key={template.id} elevation={3} sx={{ p: 2.5, mb: 2.5, transition: 'box-shadow .3s', '&:hover': {boxShadow: 6} }}>
          <Box sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1.5}}>
            <Typography variant="h5" component="div" color="primary.dark" sx={{fontWeight: 700}}>
              {template.templateName}
            </Typography>
            <Box>
              <Button
                variant="contained"
                color="secondary"
                size="medium"
                startIcon={<PlaylistPlayIcon />}
                onClick={() => onOpenApplyModal(template)}
                sx={{mr:1}}
                disabled={isSubmittingRoster}
              >
                Áp dụng
              </Button>
              <IconButton size="medium" onClick={(e) => handleMenuOpen(e, template.id)}><MoreVertIcon /></IconButton>
            </Box>
          </Box>
          <Divider sx={{my:1.5}}/>
          <SingleTemplateDetails template={template} />
        </Paper>
      ))}
      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleMenuClose} transformOrigin={{horizontal: 'right', vertical: 'top'}} anchorOrigin={{horizontal: 'right', vertical: 'bottom'}}>
        <MenuItem onClick={handleEdit}><EditIcon fontSize="small" sx={{mr:1}}/> Sửa</MenuItem>
        <MenuItem onClick={handleDelete} sx={{color: 'error.main'}}><DeleteIcon fontSize="small" sx={{mr:1}}/> Xóa</MenuItem>
      </Menu>
    </Box>
  );
}