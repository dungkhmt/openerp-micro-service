import './App.scss';
import {
    createBrowserRouter,
    RouterProvider,
    Outlet
} from "react-router-dom";
import Login from './pages/login/Login';
import SignUp from './pages/signup/Signup';
import Navbar from './components/navbar/Navbar';
import Sidebar from './components/sidebar/Sidebar';
import Home from './pages/home/Home';
import Users from './pages/users/Users';
import Assets from './pages/assets/Assets';

function App() {
    const Layout = () => {
        return (
            <div className="main">
                <Navbar/>
                <div className="container">
                    <div className="menuContainer">
                        <Sidebar/>
                    </div>
                    <div className="contentContainer">
                        <Outlet/>
                    </div>
                </div>
            </div>
        );

    };

    const router = createBrowserRouter([
        {
            path: "/",
            element: <Layout />,
            children: [
                {
                    path: "/",
                    element: <Home />,
                },
                {
                    path: "/users",
                    element: <Users />,
                },
                {
                    path: "/assets",
                    element: <Assets />,
                },
            ],
        },
        {
            path: "/login",
            element: <Login />,
        },
        {
            path: "/signup",
            element: <SignUp />,
        },
    ]);

    return <RouterProvider router={router} />
};

export default App;
