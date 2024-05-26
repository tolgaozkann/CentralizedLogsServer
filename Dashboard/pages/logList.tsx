import React from 'react';
import { GetServerSideProps } from 'next';
import { MaterialReactTable, MRT_Cell } from 'material-react-table';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import AppBar from '@mui/material/AppBar';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Button from '@mui/material/Button';
import Container from '@mui/material/Container';
import Box from '@mui/material/Box';
import Head from 'next/head';

interface Log {
  id: string;
  message: string;
  application: string;
  level: number;  // level should be a number
  date: string;
}

interface LogListProps {
  logs: Log[];
}

const logLevels = ['DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL'];

const LogList: React.FC<LogListProps> = ({ logs }) => {
  const columns = [
    { accessorKey: 'id', header: 'Id', size: 100 },
    { accessorKey: 'message', header: 'Message', size: 150 },
    { accessorKey: 'application', header: 'Application', size: 200 },
    {
      accessorKey: 'level',
      header: 'Level',
      size: 150,
      Cell: ({ cell }: { cell: MRT_Cell<Log> }) => logLevels[cell.getValue<number>()]
    },
    { accessorKey: 'date', header: 'Date', size: 150 },
  ];

  const darkTheme = createTheme({
    palette: {
      mode: 'dark',
    },
  });

  return (
    <>
      <Head>
        <title>Log Monitor - Logalyses</title>
      </Head>
      <ThemeProvider theme={darkTheme}>
        <CssBaseline />
        <Container style={{ height: 'calc(100vh - 64px)', overflow: 'auto', maxWidth: '100%' }}>
          <Box mt={2} height="calc(100vh - 128px)" width="100%">
            <MaterialReactTable
              columns={columns}
              data={logs}
              enableFullScreenToggle={false}
              muiTableContainerProps={{ style: { height: '100%', width: '100%' } }}
            />
          </Box>
        </Container>
        <AppBar position="static" style={{ top: 'auto', bottom: 0 }}>
          <Toolbar>
            <Typography variant="h6" sx={{ flexGrow: 1 }}>
              Logalyses Log Management System
            </Typography>
            <Button color="inherit" href="/userManagement">User Management</Button>
            <Button color="inherit" href="/logList">Log Monitor</Button>
          </Toolbar>
        </AppBar>
      </ThemeProvider>
    </>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const { req } = context;
  const protocol = req.headers['x-forwarded-proto'] || 'http';
  const host = req.headers['host'];
  const apiUrl = `${protocol}://${host}/api/logs`;

  const res = await fetch(apiUrl);
  const data = await res.json();

  return {
    props: {
      logs: data.logs,
    },
  };
};

export default LogList;
