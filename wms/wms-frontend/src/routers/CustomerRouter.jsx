import ProductCustomerDetailView from "screens/ecommerce/productCustomerDetailView";
import { Route, Switch, useRouteMatch } from "react-router";
import ProductGeneralView from "screens/ecommerce/productGeneralView";
import CartDetail from "screens/ecommerce/cartDetail";

export default function CustomerRouter () {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route
          component={ProductGeneralView}
          exact
          path={`${path}/products`}
        ></Route>
        <Route
          component={ProductCustomerDetailView}
          exact
          path={`${path}/products/:id`}
        ></Route>
        <Route
          component={CartDetail}
          exact
          path={`${path}/cart`}
        ></Route>
      </Switch>
    </div>
  );
}