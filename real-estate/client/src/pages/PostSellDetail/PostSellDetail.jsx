import {useEffect, useState} from "react";
import PostRequest from "../../services/PostRequest";
import {useLocation, useNavigate} from "react-router-dom";
import {toast} from "react-toastify";
import InfoPostSell from "../../components/PostBuyDetail/InfoPostSell";
import AuthRequest from "../../services/AuthRequest";
import DashboardRequest from "../../services/DashboardRequest";
import "./PostSellDetail.css"
import Dashboard from "../../components/Dashboard/Dashboard";
import ContactBox from "../../components/ContactBox/ContactBox";
import {transferTimeToDate} from "../../utils/common";

const PostSellDetail = () => {
    const authRequest = new AuthRequest();
    const dashboardRequest = new DashboardRequest();
    const navigate = useNavigate();
    const {pathname} = useLocation();
    const id = pathname.split("/").slice(-1)[0];

    const [post, setPost] = useState(null);
    const [author, setAuthor] = useState(null);
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

    useEffect(() => {
        if (post !== null) {
            authRequest.get_info_account_by({
                accountId: post.authorId,
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
                districtId: post.districtId,
                typeProperty: post.typeProperty,
            }).then(response => {
                const status = response.code;
                if (status === 200) {
                    setDashboard(response.data);
                }
            })
        }
    }, [post]);

    if (post !== null) {
        return (
            <div className="postSellDetail-container">
                <div className="information-post-sell">
                    <InfoPostSell propertyDetails={post}/>
                    <Dashboard historyPrice={exposeHistory(dashboard)} currentPrice={post.pricePerM2}/>
                </div>
                <div className="information-author-post">
                    <ContactBox account={author}/>
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