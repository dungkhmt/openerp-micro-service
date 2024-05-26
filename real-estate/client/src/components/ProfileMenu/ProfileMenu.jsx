import React from 'react'
import {Avatar, Menu} from '@mantine/core'
import {useNavigate} from 'react-router-dom'

const ProfileMenu = ({user, logout}) => {
    const navigate = useNavigate()
    return (
        <Menu>
            <Menu.Target>
                <Avatar
                    src={user.avatar}
                    alt='user image' radius={"xl"}/>
            </Menu.Target>
            <Menu.Dropdown>
                <Menu.Item onClick={() => navigate("/profile", {replace: true})}>
                    Thông tin cá nhân
                </Menu.Item>

                <Menu.Item onClick={() => navigate("/manager-post", {replace: true})}>
                    Quản lý bài viết
                </Menu.Item>

                <Menu.Item onClick={() => {
                    localStorage.clear();
                    logout()
                }}>
                    Logout
                </Menu.Item>
            </Menu.Dropdown>
        </Menu>
    )
}

export default ProfileMenu