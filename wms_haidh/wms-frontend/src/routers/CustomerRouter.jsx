import { Routes, Route } from "react-router-dom";
import Home from "../views/customer/Home";
import Cart from "../views/customer/cart";
import Checkout from "../views/customer/Checkout";
import NewAddress from "../views/customer/NewAddress";
import ProductDetail from "../views/customer/ProductDetail";
import OrderHistory from "../views/customer/OrderHistory";
import OrderDetail from "../views/customer/OrderDetail";
const CustomerRouter = () => {
  return (
    <Routes>
      <Route path="products" element={<Home />} />
      <Route path="products/:id" element={<ProductDetail />} />
      <Route path="cart" element={<Cart />} />
      <Route path="cart/checkout" element={<Checkout />} />
      <Route path="cart/checkout/new-address" element={<NewAddress />} />
      <Route path="order-history" element={<OrderHistory/>} />
      <Route path="order-history/:orderId" element={<OrderDetail />} />
    </Routes>
  );
};

export default CustomerRouter;