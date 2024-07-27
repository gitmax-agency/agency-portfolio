import { useSelector } from "react-redux";
import { IUser } from "./@types/types";
import "./App.scss";
import LoginPage from "./pages/LoginPage";
import MainPage from "./pages/MainPage";
import { RootState } from "./store/configureStore";

const App = () => {
  const user: IUser = useSelector((state: RootState) => state.user);
  return (
    <div className="App">{user.isLoggedIn ? <MainPage /> : <LoginPage />}</div>
  );
};

export default App;
