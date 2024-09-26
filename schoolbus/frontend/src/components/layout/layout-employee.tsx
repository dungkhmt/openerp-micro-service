import React from "react";
import { useLockedBody } from "../hooks/useBodyLock";
import { SidebarContext } from "./layout-context";
import { EmployeeSidebarWrapper } from "../sidebar/employee-sidebar";
import { EmployeeNavbarWrapper } from "../navbar/employee-navbar";

interface Props {
    children: React.ReactNode;
}

export const LayoutEmployee = ({ children }: Props) => {
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
                <EmployeeSidebarWrapper />
                <EmployeeNavbarWrapper>{children}</EmployeeNavbarWrapper>
            </section>
        </SidebarContext.Provider>
    );
};
