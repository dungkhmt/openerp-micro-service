import {useEffect, useState} from "react";
import PostRequest from "../../services/PostRequest";
import {useLocation, useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import InfoPostSell from "../../components/PostBuyDetail/InfoPostSell";

const PostSellDetail = () => {
    const navigate = useNavigate();
    const { pathname } = useLocation();
    const id = pathname.split("/").slice(-1)[0];

    const [post, setPost] = useState();

    useEffect( () => {
        const postRequest = new PostRequest();
        postRequest.getPostSellById(id)
            .then(response => {
                const status = response.code;
                if (status === 200) {
                    setPost(response.data)
                    // console.log("o post",response.data);
                } else {
                    toast.error(response.data.message)
                    navigate("/", {replace: true});
                }
            })
            .then()
    }, []);
    return (
        <div>
            <InfoPostSell propertyDetails={post} />
        </div>
    )
}

export default PostSellDetail;