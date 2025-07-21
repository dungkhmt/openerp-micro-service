import React, { useContext, useState } from "react";
import "./Header.css";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { logout_success } from "../../store/auth";
import ProfileMenu from "../ProfileMenu/ProfileMenu";
import {
  Box,
  Burger,
  Button,
  Center,
  Collapse,
  Drawer,
  Menu,
  rem,
  UnstyledButton,
  Group,
  Divider,
  Text,
  Accordion,
} from "@mantine/core";
import { WebSocketContext } from "../../context/WebSocketContext";
import { IconChevronDown } from "@tabler/icons-react";
import AccountRequest from "../../services/AccountRequest";
import { AccountContext } from "../../context/AccountContext";
import { ConversationContext } from "../../context/ConversationContext";

const Header = () => {
  const navigate = useNavigate();
  const { disconnect } = useContext(WebSocketContext);
  const { setAccount } = useContext(AccountContext);
  const { setConversations } = useContext(ConversationContext);

  const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
  const current_account = useSelector((state) => state.account.currentData);
  const dispatch = useDispatch();
  const [openDrawer, setOpenDrawer] = useState(false);

  const [collapseList, setCollapseList] = useState(false);
  const [collapsePost, setCollapsePost] = useState(false);

  const logout = async () => {
    const accountRequest = new AccountRequest();
    dispatch(logout_success());
    localStorage.clear();
    sessionStorage.clear();
    disconnect();
    setAccount({});
    setConversations([]);
    await accountRequest.logout();
    navigate("/", { replace: true });
  };

  return (
    <section className="h-wrapper">
      <div className="flexCenter flexColEnd innerWidth paddings h-container">
        <Burger
          color={"white"}
          opened={openDrawer}
          onClick={() => setOpenDrawer(true)}
          hiddenFrom="sm"
          size="sm"
        />

        <Box className="flexCenter h-menu" visibleFrom="sm">
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
        </Box>
      </div>

      <Drawer opened={false} onClose={() => setOpenDrawer(false)}>
        <Accordion variant="separated">
          <Accordion.Item value="another-account">
            <Accordion.Control>Bài viết</Accordion.Control>
            <Accordion.Panel>
              <Button>
                <NavLink to={"/login"}>Đăng Nhập</NavLink>
              </Button>
              <Button>
                <NavLink to={"/login"}>Đăng Nhập</NavLink>
              </Button>
            </Accordion.Panel>
          </Accordion.Item>
        </Accordion>
      </Drawer>
    </section>
  );
};

export default Header;
