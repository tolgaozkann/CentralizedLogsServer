import React, { useMemo, useState, useEffect, useCallback } from 'react';
import {
  MaterialReactTable,
  useMaterialReactTable,
  MRT_Cell,
  MRT_ColumnDef,
} from 'material-react-table';
import Checkbox from '@mui/material/Checkbox';
import FormControlLabel from '@mui/material/FormControlLabel';
import FormGroup from '@mui/material/FormGroup';
import { Box, Button, IconButton, TextField, Toolbar, Typography, Modal } from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import CloseIcon from '@mui/icons-material/Close';
import { User } from '@/compiled/notifications';

const logTypes = ['fatal', 'info', 'warning', 'debug'];

const UserManagement: React.FC = () => {
  const [rowData, setRowData] = useState<User[]>([]);
  const [open, setOpen] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editingRowIndex, setEditingRowIndex] = useState<number | null>(null);
  const [newRow, setNewRow] = useState<User>({
    id: 0,
    username: '',
    email: '',
    attentionLevels: [],
  });

  const fetchData = useCallback(async () => {
    try {
      const response = await fetch('/api/notification');
      const data = await response.json();
      setRowData(data.users);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleDelete = async (id: number) => {
    try {
      const response = await fetch('/api/notification', {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ id }),
      });
      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }
      fetchData();
    } catch (error) {
      console.error('Error deleting user:', error);
    }
  };

  const handleAddRow = () => {
    setNewRow({
      id: 0,
      username: '',
      email: '',
      attentionLevels: [],
    });
    setIsEditMode(false);
    setOpen(true);
  };

  const handleEditRow = (rowIndex: number) => {
    setNewRow(rowData[rowIndex]);
    setIsEditMode(true);
    setEditingRowIndex(rowIndex);
    setOpen(true);
  };

  const handleSaveNewRow = async () => {
    const url = '/api/notification';
    const method = isEditMode ? 'PUT' : 'POST';

    const payload = isEditMode
      ? {
          userId: newRow.id,
          attentionLevels: newRow.attentionLevels,
        }
      : {
          username: newRow.username,
          email: newRow.email,
          attentionLevels: newRow.attentionLevels,
        };

    // Ensure no invalid numbers in attentionLevels
    payload.attentionLevels = payload.attentionLevels.filter(level => Number.isInteger(level));

    try {
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });
      if (!response.ok) {
        throw new Error(`Error: ${response.statusText}`);
      }
      fetchData();
      setOpen(false);
    } catch (error) {
      console.error('Error saving user:', error);
    }
  };

  const handleClose = () => {
    setOpen(false);
  };

  const columns = useMemo<MRT_ColumnDef<User>[]>(
    () => [
      {
        accessorKey: 'id',
        header: 'ID',
        size: 70,
      },
      {
        accessorKey: 'username',
        header: 'Username',
        size: 130,
      },
      {
        accessorKey: 'email',
        header: 'Email',
        size: 200,
      },
      {
        accessorKey: 'attentionLevels',
        header: 'Attention Levels',
        size: 300,
        Cell: ({ cell }) => (
          <div>
            {(cell.getValue() as number[]).map((index) => logTypes[index]).join(', ')}
          </div>
        ),
      },
      {
        accessorKey: 'actions',
        header: 'Actions',
        size: 150,
        Cell: ({ cell }) => (
          <Box sx={{ display: 'flex', gap: 1 }}>
            <IconButton onClick={() => handleEditRow(cell.row.index)}>
              <EditIcon />
            </IconButton>
            <IconButton onClick={() => handleDelete(cell.row.original.id)}>
              <DeleteIcon />
            </IconButton>
          </Box>
        ),
      },
    ],
    [rowData]
  );

  const table = useMaterialReactTable({
    columns,
    data: rowData,
    enableFullScreenToggle: false,
  });

  return (
    <Box sx={{ width: '100%', marginTop: 2 }}>
      <Toolbar
        sx={{
          pl: { sm: 2 },
          pr: { xs: 1, sm: 1 },
          display: 'flex',
          justifyContent: 'space-between',
        }}
      >
        <Typography
          sx={{ flex: '1 1 100%' }}
          variant="h4"
          id="tableTitle"
          component="div"
        >
          User Management
        </Typography>
      </Toolbar>
      <MaterialReactTable table={table} />
      <Box sx={{ display: 'flex', justifyContent: 'flex-end', marginTop: 2, gap: 2 }}>
        <Button variant="contained" color="success" onClick={handleAddRow}>
          Add
        </Button>
      </Box>
      <Modal
        open={open}
        onClose={handleClose}
        aria-labelledby="modal-title"
        aria-describedby="modal-description"
      >
        <Box sx={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: 400,
          bgcolor: 'background.paper',
          boxShadow: 24,
          p: 4,
          display: 'flex',
          flexDirection: 'column',
          gap: 2,
        }}>
          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography id="modal-title" variant="h6" component="h2">
              {isEditMode ? 'Edit Row' : 'Add New User'}
            </Typography>
            <IconButton onClick={handleClose}>
              <CloseIcon />
            </IconButton>
          </Box>
          <TextField
            label="Username"
            value={newRow.username}
            onChange={(event) => setNewRow({ ...newRow, username: event.target.value })}
            fullWidth
            variant="outlined"
            margin="normal"
          />
          <TextField
            label="Email"
            value={newRow.email}
            onChange={(event) => setNewRow({ ...newRow, email: event.target.value })}
            fullWidth
            variant="outlined"
            margin="normal"
          />
          <FormGroup row>
            {logTypes.map((logType, index) => (
              <FormControlLabel
                key={logType}
                control={
                  <Checkbox
                    checked={newRow.attentionLevels.includes(index)}
                    onChange={(event) => {
                      const updatedAttentionLevels = [...newRow.attentionLevels];
                      if (event.target.checked) {
                        if (!updatedAttentionLevels.includes(index)) {
                          updatedAttentionLevels.push(index);
                        }
                      } else {
                        const indexPos = updatedAttentionLevels.indexOf(index);
                        if (indexPos !== -1) {
                          updatedAttentionLevels.splice(indexPos, 1);
                        }
                      }
                      setNewRow({
                        ...newRow,
                        attentionLevels: updatedAttentionLevels,
                      });
                    }}
                  />
                }
                label={logType}
              />
            ))}
          </FormGroup>
          <Box sx={{ display: 'flex', justifyContent: 'flex-end', gap: 2 }}>
            <Button variant="contained" onClick={handleSaveNewRow} color="success">Save</Button>
            <Button variant="contained" onClick={handleClose} color="inherit">Cancel</Button>
          </Box>
        </Box>
      </Modal>
    </Box>
  );
};

export default UserManagement;
