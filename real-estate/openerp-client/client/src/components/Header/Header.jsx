import React, { useState } from "react";
import "./Header.css";
import { BiMenuAltRight } from "react-icons/bi";
import { getMenuStyles } from "../../utils/common";
// import useHeaderColor from "../../hooks/useHeaderColor";
import OutsideClickHandler from "react-outside-click-handler";
import { Link, NavLink } from "react-router-dom";
import { useAuth0 } from "@auth0/auth0-react";
import ProfileMenu from "../ProfileMenu/ProfileMenu";
// import useAuthCheck from "../../hooks/useAuthCheck.jsx";
import AddProperty from "../AddProperty/AddProperty";

const logo = "assets/logo.png";
const Header = () => {
    const [menuOpened, setMenuOpened] = useState(false);
    // const headerColor = useHeaderColor();
    const [modalOpened, setModalOpened] = useState(false);
    const { loginWithRedirect, isAuthenticated, user, logout } = useAuth0();
    // const { validateLogin } = useAuthCheck();


    const handleAddPropertyClick = () => {
        if ( true) {
            console.log('Add property')
            setModalOpened(true);
        }
    };
    return (
        <section className="h-wrapper" style={{ background: "red" }}>
            <div className="flexCenter innerWidth paddings h-container">
                {/* logo */}
                <Link to="/">
                    <img src="./logo.png" alt="logo" style={{width: "100px"}} />
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
                        <NavLink to="/properties">Properties</NavLink>

                        <a href="mailto:zainkeepscode@gmail.com">Contact</a>

                        {/* add property */}
                        <div onClick={handleAddPropertyClick} style={{background: "blue",height: "100%", alignContent: "center"}}>Add Property</div>
                        <AddProperty opened={modalOpened} setOpened={setModalOpened} />
                        {/* login button */}
                        {!isAuthenticated ? (
                            <button className="button" onClick={loginWithRedirect}>
                                Login
                            </button>
                        ) : (
                            <ProfileMenu user={user} logout={logout} />
                        )}
                    </div>
                </OutsideClickHandler>

                {/* for medium and small screens */}
                <div
                    style={{background: "yellow"}}
                    className="menu-icon"
                    onClick={() => setMenuOpened((prev) => !prev)}
                >
                    <BiMenuAltRight size={30} style={{background: "yellow"}}/>
                </div>
            </div>
        </section>
    );
};

export default Header;
