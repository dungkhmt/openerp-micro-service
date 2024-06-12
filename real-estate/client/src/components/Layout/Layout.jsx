import React, { useContext, useEffect } from "react";
import Header from "../Header/Header";
import Footer from "../Footer/Footer";
import { Outlet, useNavigate } from "react-router-dom";
import { AppShell, Burger, UnstyledButton } from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { useSelector } from "react-redux";

const Layout = () => {
  return (
    <AppShell header={{ height: { base: 60, md: 70, lg: 80 } }}>
      <AppShell.Header
        style={{
          zIndex: "1001",
          background: "var(--black)",
        }}
      >
        <Header />
      </AppShell.Header>
      <AppShell.Main>
        <Outlet />
      </AppShell.Main>
    </AppShell>
  );
};

export default Layout;
