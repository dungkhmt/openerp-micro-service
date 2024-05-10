import './App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Layout from "./components/Layout/Layout";
import React, {useEffect} from "react";
import PostSell from "./pages/PostSell/PostSell";
import PostBuy from "./pages/PostBuy/PostBuy";
import PostSellDetail from "./pages/PostSellDetail/PostSellDetail";
import ListPageSell from "./pages/ListPageSell/ListPageSell";
import Oauth2Redirect from "./pages/Oauth2Redirect/Oauth2Redirect";
import Login from "./pages/Login/Login";
import {useDispatch, useSelector} from "react-redux";
import AuthRequest from "./services/AuthRequest";
import account, {set_current_account} from "./store/account";

function App() {
    const isLoggedIn = useSelector(state => state.auth.isLoggedIn)
    const authRequest = new AuthRequest();
    const dispatch = useDispatch();
    useEffect(() => {
        console.log("gia tri", isLoggedIn)
        if (isLoggedIn) {
            authRequest.get_current_account()
                .then((response) => {
                    const status = response.code;
                    if (status === 200) {
                        dispatch(set_current_account(response.data));
                    }
                })
        }
    }, [isLoggedIn]);
    return (
        <div>
            <BrowserRouter>
                <Routes>
                    <Route path={"/login"} element={<Login/>}/>

                    <Route element={<Layout/>}>
                        <Route path={"/*"} element={<ListPageSell/>}/>
                        <Route path={"/postSell"} element={<PostSell/>}/>
                        <Route path={"/postBuy"} element={<PostBuy/>}/>
                        <Route path="/sell/properties">
                            <Route index element={<ListPageSell/>}/>
                            <Route path=":propertyId" element={<PostSellDetail/>}/>
                        </Route>
                        <Route path={"/oauth/redirect"} element={<Oauth2Redirect/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
