import './App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Layout from "./components/Layout/Layout";
import React from "react";
import Home from "./pages/Home";
import PostSell from "./pages/PostSell/PostSell";
import PostBuy from "./pages/PostBuy/PostBuy";
import PostSellDetail from "./pages/PostSellDetail/PostSellDetail";
import ListPageSell from "./pages/ListPageSell/ListPageSell";

function App() {
    return (
        <div>
            <BrowserRouter>
                <Routes>
                    <Route element={<Layout/>}>
                        <Route path={"/*"} element={<Home/>}/>
                        <Route path={"/postSell"} element={<PostSell/>}/>
                        <Route path={"/postBuy"} element={<PostBuy/>}/>
                        <Route path="/sell/properties">
                            <Route index element={<ListPageSell/>}/>
                            <Route path=":propertyId" element={<PostSellDetail/>}/>
                        </Route>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
