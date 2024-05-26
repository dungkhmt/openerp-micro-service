import "./ManagerPost.css"
import React, {useEffect, useState} from "react";
import PostRequest from "../../services/PostRequest";
import {Link} from "react-router-dom"
import CardSell from "../../components/CardSell/CardSell";
import {ActionIcon, Button, Menu, Modal, rem} from "@mantine/core";
import {IoIosMore} from "react-icons/io";
import {MdOutlineDone} from "react-icons/md";
import {FaTrash} from "react-icons/fa";
import {IoHammerOutline} from "react-icons/io5";
import {HiOutlineLockOpen} from "react-icons/hi2";
import {toast, ToastContainer} from "react-toastify";
import AddInfoPostSell from "../../components/AddInfoPostSell/AddInfoPostSell";
import InfoPostSell from "../../components/InfoPostSell/InfoPostSell";

const ManagerPost = () => {
    const postRequest = new PostRequest();
    const [listPostSell, setListPostSell] = useState([]);
    const [showPost, setShowPost] = useState(false);
    const [postSelect, setPostSelect] = useState({});

    const handleFix = () => {
        setShowPost(false);
    }
    const handleChangeStatus = (status, postSellId) => {
        postRequest.updateStatus({
            status: status,
            postSellId: postSellId,
        }).then(response => {
            if (response.code === 200) {
                toast.success(response.data);
            } else {
                toast.error(response.data.message);
            }
        })
    }

    const handleUpdatePost = () => {
        postRequest.updatePost(postSelect)
            .then(response => {
                const statusCode = response.code;
                if (statusCode === 200) {
                    toast.success(response.data)
                    setPostSelect({})
                    console.log("quay ve")
                    // return response.data;
                } else {
                    toast.error(response.data.message)
                }
            })
        ;
    }

    const refreshPosts = () => {
        postRequest.getPostSellOfMe()
            .then(response => {
                if (response.code === 200) {
                    setListPostSell(response.data);
                }
            });
    };

    useEffect(() => {
        Object.keys(postSelect).length === 0 && refreshPosts();
    }, []);
    if (listPostSell.length === 0) {
        return (
            <div>
                <h2>
                    Bạn chưa có bài viết nào
                </h2>
                <Link to="/add-post-sell">
                    Thêm bài viết mới
                </Link>
            </div>
        )
    } else {
        return (
            <div className="list-post-sell-container">
                {listPostSell.map(item => (
                    <div className="manager-post-sell-container">
                        <CardSell key={item.postSellId} item={item}/>
                        <div className="feature-more">
                            <Menu shadow="sm">
                                <Menu.Target>
                                    <ActionIcon variant="subtle" color="gray">
                                        <IoIosMore style={{margin: "0 auto"}} className="icon"/>
                                    </ActionIcon>
                                </Menu.Target>

                                <Menu.Dropdown>
                                    {item.postStatus === "OPENING" ? (
                                        <Menu.Item
                                            onClick={() => handleChangeStatus('DONE', item.postSellId)}
                                            leftSection={<MdOutlineDone style={{width: rem(14), height: rem(14)}}/>}
                                        >
                                            Đã bán
                                        </Menu.Item>
                                    ) : (
                                        <Menu.Item
                                            onClick={() => handleChangeStatus("OPENING", item.postSellId)}
                                            leftSection={<HiOutlineLockOpen style={{width: rem(14), height: rem(14)}}/>}
                                        >
                                            Mở bán
                                        </Menu.Item>
                                    )}
                                    <Menu.Item
                                        onClick={() => handleChangeStatus("CLOSED", item.postSellId)}
                                        leftSection={<FaTrash style={{width: rem(14), height: rem(14)}}/>}
                                    >
                                        Xóa bài viết
                                    </Menu.Item>
                                    <Menu.Item
                                        onClick={
                                            () => setPostSelect(item)
                                        }
                                        leftSection={<IoHammerOutline style={{width: rem(14), height: rem(14)}}/>}
                                        color="red"
                                    >
                                        Sửa
                                    </Menu.Item>
                                </Menu.Dropdown>
                            </Menu>
                        </div>

                    </div>
                ))}
                <Modal
                    opened={Object.keys(postSelect).length > 0}
                    onClose={
                        () => setPostSelect({})
                    }
                    size={"90%"} title="Thay đổi thông tin"
                >
                    {(showPost && Object.keys(postSelect).length > 0) &&
                        (
                            <div>
                                <InfoPostSell propertyDetails={postSelect}/>
                                <div>
                                    <Button style={{
                                        margin: "0 15px"
                                    }} onClick={handleFix}>
                                        Chỉnh sửa
                                    </Button>
                                    <Button onClick={handleUpdatePost}>
                                        Cập nhập
                                    </Button>
                                </div>
                            </div>

                        )
                    }

                    {(!showPost && Object.keys(postSelect).length > 0) &&
                        (
                            <AddInfoPostSell propertyDetails={postSelect}
                                             setPropertyDetails={setPostSelect}
                                             setShowPost={setShowPost}/>

                        )
                    }
                </Modal>
                <ToastContainer
                    position="top-left"
                    autoClose={3000}
                    hideProgressBar={false}
                    newestOnTop={false}
                    closeOnClick
                    rtl={false}
                    pauseOnFocusLoss
                    draggable
                    pauseOnHover
                />


            </div>
        )
    }
}

export default ManagerPost;