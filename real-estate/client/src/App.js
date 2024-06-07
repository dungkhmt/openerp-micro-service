import "./App.css";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout/Layout";
import React, { useContext, useEffect } from "react";
import AddPostSell from "./pages/AddPostSell/AddPostSell";
import AddPostBuy from "./pages/AddPostBuy/AddPostBuy";
import PostSellDetail from "./pages/PostSellDetail/PostSellDetail";
import ListPageSell from "./pages/ListPageSell/ListPageSell";
import Oauth2Redirect from "./pages/Oauth2Redirect/Oauth2Redirect";
import Login from "./pages/Login/Login";
import { useDispatch, useSelector } from "react-redux";
import AccountRequest from "./services/AccountRequest";
import { set_current_account } from "./store/account";
import Profile from "./pages/Profile/Profile";
import ManagerPost from "./pages/ManagerPost/ManagerPost";
import PageBuy from "./pages/PageBuy/PageBuy";
import Report from "./pages/Report/Report";
import { AccountContext } from "./context/AccountContext";
import Chat from "./pages/Chat/Chat";

function App() {
  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
  const accountRequest = new AccountRequest();
  const dispatch = useDispatch();
  const { setAccount } = useContext(AccountContext);
  useEffect(() => {
    if (isLoggedIn) {
      accountRequest.get_current_account().then((response) => {
        const status = response.code;
        if (status === 200) {
          setAccount(response.data);
          dispatch(set_current_account(response.data));
        }
      });
    }
  }, [isLoggedIn]);
  return (
    <div>
      <BrowserRouter>
        <Routes>
          <Route path={"/login"} element={<Login />} />

          <Route element={<Layout />}>
            <Route path={"/*"} element={<ListPageSell />} />
            <Route path={"/add-post-sell"} element={<AddPostSell />} />
            <Route path={"/add-post-buy"} element={<AddPostBuy />} />
            <Route
              path={"/manager-post/:accountId"}
              element={<ManagerPost />}
            />
            <Route path="/sell/properties">
              <Route index element={<ListPageSell />} />
              <Route path=":propertyId" element={<PostSellDetail />} />
            </Route>
            <Route path={"/report"} element={<Report />} />
            <Route path={"/buy/properties"} element={<PageBuy />} />
            <Route path={"/oauth/redirect"} element={<Oauth2Redirect />} />
            <Route path={"/profile"} element={<Profile />} />
            <Route path={"/chat"} element={<Chat />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </div>
  );
}

export default App;
