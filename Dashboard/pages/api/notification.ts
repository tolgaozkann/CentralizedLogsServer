import { NextApiRequest, NextApiResponse } from 'next';
import {
  NotificationServiceClient,
  ListNotificationsRequest,
  SetNotificationRequest,
  UpdateNotificationRequest,
  RemoveNotificationRequest,
} from '../../src/compiled/notifications';
import { ChannelCredentials } from '@grpc/grpc-js';

const client = new NotificationServiceClient('127.0.0.1:8888', ChannelCredentials.createInsecure());

export default async (req: NextApiRequest, res: NextApiResponse) => {
  switch (req.method) {
    case 'GET':
      const listRequest = ListNotificationsRequest.create();
      client.listNotifications(listRequest, (error, response) => {
        if (error) {
          res.status(500).json({ error: 'Error fetching notifications' });
        } else {
          res.status(200).json(response);
        }
      });
      break;

    case 'POST':
      const { username, email, attentionLevels } = req.body;
      const setRequest = SetNotificationRequest.create({ username, email, attentionLevels });
      client.setNotification(setRequest, (error, response) => {
        if (error) {
          res.status(500).json({ error: 'Error adding notification' });
        } else {
          res.status(200).json(response);
        }
      });
      break;

    case 'PUT':
      const { userId, attentionLevels: updatedAttentionLevels } = req.body;
      const updateRequest = UpdateNotificationRequest.create({ userId, attentionLevels: updatedAttentionLevels });
      client.updateNotification(updateRequest, (error, response) => {
        if (error) {
          res.status(500).json({ error: 'Error updating notification' });
        } else {
          res.status(200).json(response);
        }
      });
      break;

    case 'DELETE':
      const { id } = req.body;
      const removeRequest = RemoveNotificationRequest.create({ userId: id });
      client.removeNotification(removeRequest, (error, response) => {
        if (error) {
          res.status(500).json({ error: 'Error deleting notification' });
        } else {
          res.status(200).json(response);
        }
      });
      break;

    default:
      res.status(405).json({ error: 'Method not allowed' });
      break;
  }
};
