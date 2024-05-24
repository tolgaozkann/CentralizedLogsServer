import React from 'react';
import { GetServerSideProps } from 'next';
import { MaterialReactTable } from 'material-react-table';

interface Log {
  id: string;
  message: string;
  application: string;
  level: string;
  date: string;
}

interface LogListProps {
  logs: Log[];
}

const LogList: React.FC<LogListProps> = ({ logs }) => {
  const columns = [
    { accessorKey: 'id', header: 'Id', size: 100 },
    { accessorKey: 'message', header: 'Message', size: 150 },
    { accessorKey: 'application', header: 'Application', size: 200 },
    { accessorKey: 'level', header: 'Level', size: 150 },
    { accessorKey: 'date', header: 'Date', size: 150 },
  ];

  return (
    <MaterialReactTable
      columns={columns}
      data={logs}
      enableFullScreenToggle={false}
      initialState={{ isFullScreen: true }}
    />
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
