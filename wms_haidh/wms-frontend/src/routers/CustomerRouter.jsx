// import ProductCustomerDetailView from "../screens/ecommerce/productCustomerDetailView";
// import { Route, Switch, useRouteMatch } from "react-router";
// import ProductGeneralView from "../screens/ecommerce/productGeneralView";
// import CartDetail from "../screens/ecommerce/cartDetail";
// import OrderHistory from "../screens/ecommerce/orderHistory";

// export default function CustomerRouter () {
//   let { path } = useRouteMatch();
//   return (
//     <div>
//       <Switch>
//         <Route
//           component={ProductGeneralView}
//           exact
//           path={`${path}/products`}
//         ></Route>
//         <Route
//           component={ProductCustomerDetailView}
//           exact
//           path={`${path}/products/:id`}
//         ></Route>
//         <Route
//           component={CartDetail}
//           exact
//           path={`${path}/cart`}
//         ></Route>
//         <Route
//           component={OrderHistory}
//           exact
//           path={`${path}/order_history`}
//         ></Route>
//       </Switch>
//     </div>
//   );
// }
const CustomerRouter = () => {
    return <div>Customer Router</div>;
  };
  
  export default CustomerRouter;