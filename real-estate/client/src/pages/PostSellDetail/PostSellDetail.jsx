import {useEffect, useState} from "react";
import PostRequest from "../../services/PostSellRequest";
import {useLocation, useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import InfoPostSell from "../../components/InfoPostSell/InfoPostSell";
import AuthRequest from "../../services/AuthRequest";
import DashboardRequest from "../../services/DashboardRequest";
import "./PostSellDetail.css"
import Dashboard from "../../components/Dashboard/Dashboard";
import ContactBox from "../../components/ContactBox/ContactBox";
import {transferTimeToDate} from "../../utils/common";
import PostSellRequest from "../../services/PostSellRequest";
import {useSelector} from "react-redux";

const PostSellDetail = () => {
    const current_accountId = useSelector(status => status.account.currentData.accountId);
    console.log(current_accountId)
    const authRequest = new AuthRequest();
    const dashboardRequest = new DashboardRequest();
    const navigate = useNavigate();
    const {pathname} = useLocation();
    const postId = pathname.split("/").slice(-1)[0];

    const [post, setPost] = useState({});
    const [author, setAuthor] = useState({});
    const [dashboard, setDashboard] = useState([]);

    const exposeHistory = (data) => {
        const duration = 604800000;
        const now = Date.now();
        let result = [];
        if (data.length > 0) {
            let firstTime = data[0]?.startTime;
            while (firstTime <= now - duration) {
                const item = data.find(item => item.startTime === firstTime);
                if (item === undefined) {
                    result = [...result, {
                        date: transferTimeToDate(firstTime),
                        highest: null,
                        lowest: null,
                        medium: null,
                    }];
                } else {
                    result = [...result, {
                        date: transferTimeToDate(firstTime),
                        highest: item.highestPricePerM2 / 1000000,
                        lowest: item.lowestPricePerM2 / 1000000,
                        medium: item.mediumPricePerM2 / 1000000,
                    }];
                }
                firstTime += duration;
            }
        }
        // console.log(result);
        return result;
    }

    useEffect(() => {
        const postRequest = new PostSellRequest();
        postRequest.getPostSellById(postId)
            .then(response => {
                const status = response.code;
                if (status === 200) {
                    setPost(response.data)
                    const data = response.data;
                    console.log(data)
                    authRequest.get_info_account_by({
                        accountId: data.authorId,
                    })
                        .then(response => {
                            const status = response.code;
                            if (status === 200) {
                                setAuthor(response.data);
                            }
                        })

                    const now = Date.now();
                    const fromTime = now - now % 604800000 - 604800000 * 10;
                    dashboardRequest.get_dashboard({
                        fromTime: fromTime,
                        districtId: data.districtId,
                        typeProperty: data.typeProperty,
                    }).then(response => {
                        const status = response.code;
                        if (status === 200) {
                            setDashboard(response.data);
                        }
                    })
                    // console.log("o post",response.data);
                } else {
                    toast.error(response.data.message)
                    navigate("/", {replace: true});
                }
            })
            .then()
    }, []);

    if (Object.keys(post).length > 0 && Object.keys(author).length > 0 ) {
        return (
            <div className="postSellDetail-container">
                <div className="information-post-sell">
                    <InfoPostSell propertyDetails={post}/>
                    <Dashboard historyPrice={exposeHistory(dashboard)} currentPrice={post.pricePerM2}/>
                </div>
                <div className="information-author-post">
                    <ContactBox account={author} isOwner={author.accountId == current_accountId}/>
                </div>
            </div>
        )
    } else {
        return (
            <div>
                Không tìm thấy dữ liệu
            </div>
        )
    }
}

export default PostSellDetail;