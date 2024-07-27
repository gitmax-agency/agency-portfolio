export interface RestProps {
  [x: string]: any;
}

export type ITab = {
  title: string;
  path: string;
  active?: boolean;
};

export type IUser = {
  isLoggedIn: boolean;
  email?: string;
  error?: string;
  userId?: number;
  name?: string;
};
