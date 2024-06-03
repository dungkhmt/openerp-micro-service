import React, {useState} from "react";
import "./Header.css";
import {BiMenuAltRight} from "react-icons/bi";
import {getMenuStyles} from "../../utils/common";
import useHeaderColor from "../../hooks/useHeaderColor";
import OutsideClickHandler from "react-outside-click-handler";
import {Link, NavLink, useNavigate} from "react-router-dom";
import {useDispatch, useSelector} from 'react-redux'
import {logout_success} from "../../store/auth";
import ProfileMenu from "../ProfileMenu/ProfileMenu";
import {Button, Menu} from '@mantine/core'
// import useAuthCheck from "../../hooks/useAuthCheck.jsx";

const Header = () => {
    const navigate = useNavigate();
    const [menuOpened, setMenuOpened] = useState(false);
    const headerColor = useHeaderColor();
    const [modalOpened, setModalOpened] = useState(false);

    const token = useSelector((state) => state.auth.token);
    const isLoggedIn = useSelector((state) => state.auth.isLoggedIn);
    const current_account = useSelector((state) => state.account.currentData)
    const dispatch = useDispatch()

    const logout = () => {
        dispatch(logout_success());
    }
    // const { validateLogin } = useAuthCheck();


    // const handleAddPropertyClick = () => {
    //   if (validateLogin()) {
    //     setModalOpened(true);
    //   }
    // };
    return (
        <section className="h-wrapper" style={{background: headerColor}}>
            <div className="flexCenter innerWidth paddings h-container">
                {/* logo */}
                <Link to="/">
                    <img src="./logo192.png" alt="logo" width={100}/>
                </Link>

                {/* menu */}
                <OutsideClickHandler
                    onOutsideClick={() => {
                        setMenuOpened(false);
                    }}
                >
                    <div
                        // ref={menuRef}
                        className="flexCenter h-menu"
                        style={getMenuStyles(menuOpened)}
                    >
                        <Menu>
                            <Menu.Target>
                                <button>Danh Sách Tin</button>
                            </Menu.Target>
                            <Menu.Dropdown>
                                <Menu.Item onClick={()=> navigate("/buy/properties", {replace: true})}>
                                    Tin Mua
                                </Menu.Item>
                                <Menu.Item onClick={()=> navigate("/sell/properties", {replace: true})}>
                                    Tin Bán
                                </Menu.Item>
                            </Menu.Dropdown>
                        </Menu>
                        <NavLink to="/report">
                            Phân tích thị trường
                        </NavLink>

                        {/* add property */}

                        {isLoggedIn && (
                            <Menu>
                                <Menu.Target>
                                    <button>Đăng Tin</button>
                                </Menu.Target>
                                <Menu.Dropdown>
                                    <Menu.Item onClick={()=> navigate("/add-post-buy", {replace: true})}>
                                        Tin Mua
                                    </Menu.Item>
                                    <Menu.Item onClick={()=> navigate("/add-post-sell", {replace: true})}>
                                        Tin Bán
                                    </Menu.Item>
                                </Menu.Dropdown>
                            </Menu>
                        )}

                        {/*<AddPropertyModal opened={modalOpened} setOpened={setModalOpened} />*/}
                        {/* login button */}
                        {!isLoggedIn ? (
                            <Button>
                                <NavLink to={"/login"}>Đăng Nhập</NavLink>
                            </Button>
                        ) : (
                            <div>
                                <ProfileMenu user={current_account} logout={logout}/>
                            </div>
                        )}
                    </div>
                </OutsideClickHandler>

                {/* for medium and small screens */}
                <div
                    className="menu-icon"
                    onClick={() => setMenuOpened((prev) => !prev)}
                >
                    <BiMenuAltRight size={30}/>
                </div>
            </div>
        </section>
    );
};

export default Header;
