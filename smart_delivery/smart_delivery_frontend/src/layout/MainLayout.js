import React, { useState } from 'react';
import { CarOutlined, EnvironmentOutlined, LineChartOutlined, NodeIndexOutlined } from '@ant-design/icons';
import { Layout, Menu } from 'antd';
import { useNavigate } from 'react-router-dom';

const { Header, Content, Footer } = Layout;

const MainLayout = () => {
    const navigate = useNavigate();
    const [collapsed, setCollapsed] = useState(false);

    const handleMenuClick = (key) => {
        switch (key) {
            case 'delivery-tracking':
                navigate('/delivery/tracking');
                break;
            case 'delivery-analytics':
                navigate('/delivery/analytics');
                break;
            case 'route-optimization':
                navigate('/delivery/route');
                break;
            default:
                break;
        }
    };

    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Header style={{ padding: 0 }}>
                {/* Header content */}
            </Header>
            <Layout>
                <Layout.Sider
                    collapsible
                    collapsed={collapsed}
                    onCollapse={(value) => setCollapsed(value)}
                >
                    <Menu
                        theme="dark"
                        defaultSelectedKeys={['1']}
                        mode="inline"
                        onClick={handleMenuClick}
                    >
                        <Menu.Item key="delivery" icon={<CarOutlined />}>
                            Delivery Management
                            <Menu.SubMenu key="delivery" title="Delivery Management">
                                <Menu.Item key="delivery-tracking">Order Tracking</Menu.Item>
                                <Menu.Item key="delivery-analytics">Analytics Dashboard</Menu.Item>
                                <Menu.Item key="route-optimization">Route Optimization</Menu.Item>
                            </Menu.SubMenu>
                        </Menu.Item>
                    </Menu>
                </Layout.Sider>
                <Layout.Content style={{ margin: '0 16px' }}>
                    <Content style={{ padding: 24, minHeight: 360 }}>
                        {/* Content of the main layout */}
                    </Content>
                </Layout.Content>
            </Layout>
            <Footer style={{ textAlign: 'center' }}>
                {/* Footer content */}
            </Footer>
        </Layout>
    );
};

export default MainLayout; 