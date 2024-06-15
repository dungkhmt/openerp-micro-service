import React, { createContext, useEffect, useState } from "react";
import AccountRequest from "../services/AccountRequest";

const AccountContext = createContext(undefined);

const AccountContextProvider = ({ children }) => {
  const [account, setAccount] = useState({});
  // const getAccountData = () => {
  //   const accountRequest = new AccountRequest();
  //   accountRequest.get_current_account().then((response) => {
  //     if (response.code === 200) {
  //       setAccount(response.data);
  //     } else {
  //       if (
  //         window.location.pathname !== "/login" &&
  //         window.location.pathname !== "/" &&
  //         window.location.pathname !== "/sell/properties"
  //       ) {
  //         window.location.pathname = "/";
  //       }
  //     }
  //   });
  // };
  //
  // useEffect(() => {
  //   localStorage.getItem("token") && getAccountData();
  // }, []);
  return (
    <AccountContext.Provider value={{ account, setAccount }}>
      {children}
    </AccountContext.Provider>
  );
};

export { AccountContext, AccountContextProvider };
