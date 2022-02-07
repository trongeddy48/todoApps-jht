import dayjs from 'dayjs';

export interface ITodo {
  id?: number;
  name?: string;
  description?: string | null;
  createdAt?: string | null;
  updatedAt?: string | null;
}

export const defaultValue: Readonly<ITodo> = {};
