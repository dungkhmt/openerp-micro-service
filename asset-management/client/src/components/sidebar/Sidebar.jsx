import React from 'react';
import "./sidebar.scss";
import { Link } from 'react-router-dom';

const Sidebar = () => {
    const menu = [
        {
            id: 1,
            title: "main",
            listItems: [
                {
                    id: 1,
                    title: "Home",
                    url: "/",
                    icon: "home.svg",
                },
                {
                    id: 2,
                    title: "My assets",
                    url: "/assets",
                    icon: "user.svg",
                },
                {
                    id: 3,
                    title: "Settings",
                    url: "/settings",
                    icon: "user.svg",
                },
            ],
        },
        {
            id: 2,
            title: "users",
            listItems: [
                {
                    id: 1,
                    title: "Users",
                    url: "/users",
                    icon: "user.svg",
                },
            ],
        },
        {
            id: 3,
            title: "asset management",
            listItems: [
                {
                    id: 1,
                    title: "Assets",
                    url: "/assets",
                    icon: "element.svg",
                },
                {
                    id: 2,
                    title: "Accessories",
                    url: "/accessories",
                    icon: "note.svg",
                },
                {
                    id: 3,
                    title: "Licenses",
                    url: "/licenses",
                    icon: "form.svg",
                },
            ],
        },
        {
            id: 4,
            title: "asset operations",
            listItems: [
                {
                    id: 1,
                    title: "Requests",
                    url: "/requests",
                    icon: "setting.svg",
                },
                {
                    id: 2,
                    title: "Audits",
                    url: "/audits",
                    icon: "backup.svg",
                },
                {
                    id: 2,
                    title: "Depreciation",
                    url: "/depreciation",
                    icon: "backup.svg",
                },
            ],
        },
        {
            id: 5,
            title: "logs and reports",
            listItems: [
                {
                    id: 1,
                    title: "Dashboards",
                    url: "/dashboards",
                    icon: "chart.svg",
                },
            ],
        },
    ];
    return (
        <div className="menu">
            {menu.map((item) => (
                <div className="item" key={item.id}>
                    <span className="title">{item.title}</span>
                    {item.listItems.map((listItem) => (
                        <Link to='/' className="listItem" key={listItem.id}>
                            <img src={listItem.icon} alt=''/>
                            <span className="listItemTitle">{listItem.title}</span>
                        </Link>
                    ))}
                </div>  
            ))}       
        </div>
    );
};

export default Sidebar;