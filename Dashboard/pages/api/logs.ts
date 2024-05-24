// pages/api/logs.ts

import { NextApiRequest, NextApiResponse } from 'next';
import { LogsRequest, LogsResponse, LogsServiceClient } from '../../src/compiled/logs';
import { ChannelCredentials } from '@grpc/grpc-js';

const client = new LogsServiceClient('127.0.0.1:8888', ChannelCredentials.createInsecure());

export default async (req: NextApiRequest, res: NextApiResponse) => {
  if (req.method !== 'GET') {
    return res.status(405).json({ error: 'Method not allowed' });
  }

  const request = LogsRequest.create({ page: 1, pageSize: 100, index: "", filters: [] });

  client.list(request, (err: any, response: LogsResponse) => {
    if (err) {
      console.error('Error fetching data:', err);
      res.status(500).json({ error: 'Error fetching logs' });
    } else {
      const logs = response.logs.map((log) => ({
        id: log.id,
        message: log.message,
        application: log.application?.name || '',
        level: log.level,
        date: new Date(log.logTime).toLocaleString(),
      }));
      res.status(200).json({ logs });
    }
  });
};
