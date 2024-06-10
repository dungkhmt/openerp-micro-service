import React, { useContext, useState } from "react";
import "./Header.css";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { logout_success } from "../../store/auth";
import ProfileMenu from "../ProfileMenu/ProfileMenu";
import { Box, Button, Center, Menu, rem } from "@mantine/core";
import { WebSocketContext } from "../../context/WebSocketContext";
import { IconChevronDown } from "@tabler/icons-react";

const Header = () => {
  const navigate = useNavigate();
  const { disconnect } = useContext(WebSocketContext);

  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
  const current_account = useSelector((state) => state.account.currentData);
  const dispatch = useDispatch();

  const logout = () => {
    dispatch(logout_success());
    localStorage.clear();
    sessionStorage.clear();
    navigate("/", { replace: true });
    disconnect();
  };

  return (
    <section className="h-wrapper">
      <div className="flexCenter flexColEnd innerWidth paddings h-container">
        <div className="flexCenter h-menu">
          <Menu zIndex={1002}>
            <Menu.Target>
              <Center>
                <Box>Bài đăng</Box>
                <IconChevronDown
                  style={{ width: rem(16), height: rem(16) }}
                  color={"rgba(255, 255, 255, 0.78"}
                />
              </Center>
            </Menu.Target>
            <Menu.Dropdown>
              <Menu.Item
                onClick={() => navigate("/buy/properties", { replace: true })}
              >
                Tin Mua
              </Menu.Item>
              <Menu.Item
                onClick={() => navigate("/sell/properties", { replace: true })}
              >
                Tin Bán
              </Menu.Item>
            </Menu.Dropdown>
          </Menu>
          <NavLink to="/report">Phân tích thị trường</NavLink>

          {/* add property */}

          {isLoggedIn && (
            <Menu zIndex={1002}>
              <Menu.Target>
                <Center>
                  <Box>Đăng tin</Box>
                  <IconChevronDown
                    style={{ width: rem(16), height: rem(16) }}
                    color={"rgba(255, 255, 255, 0.78"}
                  />
                </Center>
              </Menu.Target>
              <Menu.Dropdown>
                <Menu.Item
                  onClick={() => navigate("/add-post-buy", { replace: true })}
                >
                  Tin Mua
                </Menu.Item>
                <Menu.Item
                  onClick={() => navigate("/add-post-sell", { replace: true })}
                >
                  Tin Bán
                </Menu.Item>
              </Menu.Dropdown>
            </Menu>
          )}

          {!isLoggedIn ? (
            <Button>
              <NavLink to={"/login"}>Đăng Nhập</NavLink>
            </Button>
          ) : (
            <div>
              <ProfileMenu user={current_account} logout={logout} />
            </div>
          )}
        </div>
      </div>
    </section>
  );
};

export default Header;
