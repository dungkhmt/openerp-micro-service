import './App.css';
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import Layout from "./components/Layout/Layout";
import React, {useEffect} from "react";
import AddPostSell from "./pages/AddPostSell/AddPostSell";
import AddPostBuy from "./pages/AddPostBuy/AddPostBuy";
import PostSellDetail from "./pages/PostSellDetail/PostSellDetail";
import ListPageSell from "./pages/ListPageSell/ListPageSell";
import Oauth2Redirect from "./pages/Oauth2Redirect/Oauth2Redirect";
import Login from "./pages/Login/Login";
import {useDispatch, useSelector} from "react-redux";
import AuthRequest from "./services/AuthRequest";
import {set_current_account} from "./store/account";
import Profile from "./pages/Profile/Profile";
import ManagerPost from "./pages/ManagerPost/ManagerPost";

function App() {
    const isLoggedIn = useSelector(state => state.auth.isLoggedIn)
    const authRequest = new AuthRequest();
    const dispatch = useDispatch();
    useEffect(() => {
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
                        <Route path={"/add-post-sell"} element={<AddPostSell/>}/>
                        <Route path={"/add-post-buy"} element={<AddPostBuy/>}/>
                        <Route path={"/manager-post"} element={<ManagerPost/>}/>
                        <Route path="/sell/properties">
                            <Route index element={<ListPageSell/>}/>
                            <Route path=":propertyId" element={<PostSellDetail/>}/>
                        </Route>
                        <Route path={"/oauth/redirect"} element={<Oauth2Redirect/>}/>
                        <Route path={"/profile"} element={<Profile/>}/>
                    </Route>
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
