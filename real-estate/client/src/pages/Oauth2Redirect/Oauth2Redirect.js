import {useLocation, useNavigate, useParams} from "react-router-dom";
import {useDispatch} from "react-redux";
import {login_success} from "../../store/auth";
import {useEffect} from "react";

const Oauth2Redirect = () => {
    const location = useLocation()
    const queryParameters = new URLSearchParams(location.search);
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const token = queryParameters.get("token");
    console.log("token: " + token);

    if (token !== null && token!== "") {
        dispatch(login_success(token))
    }
    useEffect(() => {
        navigate("/", { redirect: true})
    }, []);
}

export default Oauth2Redirect;