import React from "react";
import { useLockedBody } from "../hooks/useBodyLock";
import { SidebarContext } from "./layout-context";
import { ClientSidebarWrapper } from "../sidebar/client-sidebar";
import { ClientNavbarWrapper } from "../navbar/client-navbar";

interface Props {
    children: React.ReactNode;
}

export const LayoutClient = ({ children }: Props) => {
    const [sidebarOpen, setSidebarOpen] = React.useState(false);
    const [_, setLocked] = useLockedBody(false);
    const handleToggleSidebar = () => {
        setSidebarOpen(!sidebarOpen);
        setLocked(!sidebarOpen);
    };

    return (
        <SidebarContext.Provider
            value={{
                collapsed: sidebarOpen,
                setCollapsed: handleToggleSidebar,
            }}
        >
            <section className="flex">
                <ClientSidebarWrapper />
                <ClientNavbarWrapper>{children}</ClientNavbarWrapper>
            </section>
        </SidebarContext.Provider>
    );
};
