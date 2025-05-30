import DashboardRoundedIcon from "@mui/icons-material/DashboardRounded";
import { LocalShipping } from "@mui/icons-material";
import HistoryIcon from '@mui/icons-material/History';
import { ShoppingBag } from "@mui/icons-material";
import { ReceiptLong } from "@mui/icons-material";
import MonetizationOnIcon from '@mui/icons-material/MonetizationOn';
import { buildMapPathMenu } from "../utils/MenuUtils";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import StackedLineChartIcon from '@mui/icons-material/StackedLineChart';
import FactCheckIcon from '@mui/icons-material/FactCheck';
import PieChartIcon from '@mui/icons-material/PieChart';
import Inventory2Icon from '@mui/icons-material/Inventory2';
import HandshakeIcon from '@mui/icons-material/Handshake';
import WarehouseIcon from '@mui/icons-material/Warehouse';
import WidgetsIcon from '@mui/icons-material/Widgets';
import GroupsIcon from '@mui/icons-material/Groups';
import StraightenIcon from '@mui/icons-material/Straighten';
import CommuteIcon from '@mui/icons-material/Commute';

import { dashboard } from "./menuconfig/dashboard";
import { directorRevenue } from "./menuconfig/director";
import { directorCategory } from "./menuconfig/director";
import { warehousemanagerWarehouse } from "./menuconfig/warehousemanager";
import { warehousemanagerProduct } from "./menuconfig/warehousemanager";
import { warehousemanagerProcessReceipt } from "./menuconfig/warehousemanager";
import { warehousemanagerOrder } from "./menuconfig/warehousemanager";
import { warehousestaffTask } from "./menuconfig/warehousestaff";
import { purchasestaffProduct } from "./menuconfig/purchasestaff";
import { purchasestaffReceipt } from "./menuconfig/purchasestaff";
import { purchasemanagerSuppliers } from "./menuconfig/purchasemanager";
import { purchasemanagerProcessReceipt } from "./menuconfig/purchasemanager";
import { salemanagerPriceConfig } from "./menuconfig/salemanager";
import { salemanagerOrders } from "./menuconfig/salemanager";
import { deliverymanagerDeliveryPerson } from "./menuconfig/deliverymanager";
import { deliverymanagerShipments } from "./menuconfig/deliverymanager";
import { deliverymanagerDeliveryTrips } from "./menuconfig/deliverymanager";
import { deliverymanagerDistances } from "./menuconfig/deliverymanager";
import { deliveryperson } from "./menuconfig/deliveryperson";
import { customerProducts } from "./menuconfig/customer";
import { customerCart } from "./menuconfig/customer";
import { customerHistory } from "./menuconfig/customer";

export const MENUS = [];

MENUS.push(dashboard);

MENUS.push(directorRevenue);
MENUS.push(directorCategory);

MENUS.push(warehousemanagerWarehouse);
MENUS.push(warehousemanagerProduct);
MENUS.push(warehousemanagerProcessReceipt);
MENUS.push(warehousemanagerOrder);

MENUS.push(warehousestaffTask);

MENUS.push(purchasestaffProduct);
MENUS.push(purchasestaffReceipt);

MENUS.push(purchasemanagerSuppliers);
MENUS.push(purchasemanagerProcessReceipt);

MENUS.push(salemanagerPriceConfig);
MENUS.push(salemanagerOrders);

MENUS.push(deliverymanagerDeliveryPerson);
MENUS.push(deliverymanagerShipments);
MENUS.push(deliverymanagerDeliveryTrips);
MENUS.push(deliverymanagerDistances);

MENUS.push(deliveryperson);

MENUS.push(customerProducts);
MENUS.push(customerCart);
MENUS.push(customerHistory);


export const menuIconMap = new Map();

menuIconMap.set("DashboardRoundedIcon",<DashboardRoundedIcon />);
menuIconMap.set("ProductIcon",<WidgetsIcon/>);
menuIconMap.set("WarehouseIcon",<WarehouseIcon />);
menuIconMap.set("RevenueIcon",<StackedLineChartIcon/>);
menuIconMap.set("CategoryIcon",<PieChartIcon/>);
menuIconMap.set("ProductInventoryIcon",<Inventory2Icon/>);
menuIconMap.set("ReceiptIcon",<ReceiptLong />);
menuIconMap.set("SupplierIcon",<HandshakeIcon />);
menuIconMap.set("MoneyIcon",<MonetizationOnIcon/>);
menuIconMap.set("SaleOrderIcon",<FactCheckIcon/>);
menuIconMap.set("StaffIcon",<GroupsIcon />);
menuIconMap.set("ShipmentIcon",<CommuteIcon/>);
menuIconMap.set("DeliveryTripIcon",<LocalShipping />);
menuIconMap.set("DistanceIcon",<StraightenIcon />);
menuIconMap.set("CustomerProductIcon",<ShoppingBag/>);
menuIconMap.set("CartIcon",<ShoppingCartIcon/>);
menuIconMap.set("HistoryIcon",<HistoryIcon />);

export const mapPathMenu = buildMapPathMenu(MENUS);
