import Header from "../Header/Header";

const Layout = () => {
    return (
        <>
            <div style={{ background: "var(--black)", overflow: "hidden" }}>
                <Header />
                {/*<Outlet />*/}
            </div>
            {/*<Footer />*/}
        </>
    )
}

export default Layout;