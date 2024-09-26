import React from "react";
import { Sidebar } from "./sidebar.styles";
import { Avatar, Tooltip } from "@nextui-org/react";
import { CompaniesDropdown } from "./companies-dropdown";
import { HomeIcon } from "../icons/sidebar/home-icon";
import { PaymentsIcon } from "../icons/sidebar/payments-icon";
import { BalanceIcon } from "../icons/sidebar/balance-icon";
import { AccountsIcon } from "../icons/sidebar/accounts-icon";
import { CustomersIcon } from "../icons/sidebar/customers-icon";
import { ProductsIcon } from "../icons/sidebar/products-icon";
import { ReportsIcon } from "../icons/sidebar/reports-icon";
import { DevIcon } from "../icons/sidebar/dev-icon";
import { ViewIcon } from "../icons/sidebar/view-icon";
import { SettingsIcon } from "../icons/sidebar/settings-icon";
import { CollapseItems } from "./collapse-items";
import { SidebarItem } from "./sidebar-item";
import { SidebarMenu } from "./sidebar-menu";
import { FilterIcon } from "../icons/sidebar/filter-icon";
import { useSidebarContext } from "../layout/layout-context";
import { ChangeLogIcon } from "../icons/sidebar/changelog-icon";
import { usePathname } from "next/navigation";
import { SupportIcon } from "../icons/navbar/support-icon";
import { ManageIcon } from "../icons/sidebar/manage-icon";

export const SidebarWrapper = () => {
  const pathname = usePathname();
  const { collapsed, setCollapsed } = useSidebarContext();

  return (
    <aside className="h-screen z-[202] sticky top-0">
      {collapsed ? (
        <div className={Sidebar.Overlay()} onClick={setCollapsed} />
      ) : null}
      <div
        className={Sidebar({
          collapsed: collapsed,
        })}
      >
        <div className={Sidebar.Header()}>
          <CompaniesDropdown name="Admin" />
        </div>
        <div className="flex flex-col justify-between h-full">
          <div className={Sidebar.Body()}>
            <SidebarItem
              title="Trang chủ"
              icon={<HomeIcon />}
              isActive={pathname === "/admin"}
              href="/admin"
            />
            <SidebarMenu title="Main Menu">
              <SidebarItem
                isActive={pathname === "/admin/monitoring"}
                title="Theo dõi & giám sát"
                icon={<ManageIcon />}
                href="/admin/monitoring"
              />
              <SidebarItem
                isActive={pathname === "/admin/manipulating"}
                title="Điều phối"
                icon={<ProductsIcon />}
                href="/admin/manipulating"
              />
              <SidebarItem
                isActive={pathname === "/admin/vehicles"}
                title="Lực lượng xe"
                icon={<AccountsIcon />}
                href="/admin/vehicles"
              />
              <SidebarItem
                isActive={pathname === "/admin/employee"}
                title="Đội ngũ"
                icon={<AccountsIcon />}
                href="/admin/employee"
              />
              <SidebarItem
                isActive={pathname === "/admin/parents-students"}
                title="Phụ huynh & học sinh"
                icon={<CustomersIcon />}
                href="/admin/parents-students"
              />
              <SidebarItem
                isActive={pathname === "/admin/register"}
                title="Đăng ký dịch vụ"
                icon={<ReportsIcon />}
                href="/admin/register"
              />
              <SidebarItem
                isActive={pathname === "/admin/history-ride"}
                title="Lịch sử đưa đón"
                icon={<ChangeLogIcon />}
                href="/admin/history-ride"
              />
              <SidebarItem
                isActive={pathname === "/admin/help-center"}
                title="Trung tâm trợ giúp"
                icon={<SupportIcon />}
                href="/admin/help-center"
              />
              <SidebarItem
                isActive={pathname === "/admin/reports"}
                title="Báo cáo"
                icon={<ReportsIcon />}
                href="/admin/reports"
              />
            </SidebarMenu>

            <SidebarMenu title="General">
              <SidebarItem
                isActive={pathname === "/admin/settings"}
                title="Settings"
                icon={<SettingsIcon />}
              />
            </SidebarMenu>
          </div>
          <div className={Sidebar.Footer()}>
            <Tooltip content={"Settings"} color="primary">
              <div className="max-w-fit">
                <SettingsIcon />
              </div>
            </Tooltip>
            <Tooltip content={"Adjustments"} color="primary">
              <div className="max-w-fit">
                <FilterIcon />
              </div>
            </Tooltip>
            <Tooltip content={"Profile"} color="primary">
              <Avatar
                src="https://i.pravatar.cc/150?u=a042581f4e29026704d"
                size="sm"
              />
            </Tooltip>
          </div>
        </div>
      </div>
    </aside>
  );
};
