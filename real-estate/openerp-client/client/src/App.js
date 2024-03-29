import './App.css';
import {path} from "./utils/constants";
import {Routes, Route, BrowserRouter} from 'react-router-dom'
import Layout from "./components/Layout/Layout";
import React from "react";
import Home from "./pages/Home";

function App() {
    return (
        <div>
            <BrowserRouter>
                <Routes>
                    <Route element={<Layout/>}>
                        <Route path={"/*"} element={<Home/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
