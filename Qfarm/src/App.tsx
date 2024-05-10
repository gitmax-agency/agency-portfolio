import React from 'react';
import MainPage from "./pages/MainPage";
import './styles/main.scss'
import {Route, Routes} from "react-router-dom";
import MintPage from "./pages/MintPage";

function App() {
  return (
    <div className="App">
        <Routes>
            <Route path={"/"} element={<MainPage/>}/>
            <Route path={"/mint"} element={<MintPage/>}/>
        </Routes>
    </div>
  );
}

export default App;
