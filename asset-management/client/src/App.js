import './App.css';
import {
    BrowserRouter as Router,
    Routes,
    Route
} from "react-router-dom";
import Login from './pages/login/Login';
import SignUp from './pages/signup/Signup';

function App() {
    return (
        <Router>
            <Routes>
                <Route path='/' element={<Login/>}/>
                <Route path='/signup' element={<SignUp/>}/>
            </Routes>
        </Router>
    );
};

export default App;
