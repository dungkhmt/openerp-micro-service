import { Route, Switch, useRouteMatch } from "react-router";
import ProductCategoryScreen from "views/category/product-category/Category";
import ProductUnitScreen from "views/category/product-unit/Unit";
import ContractType from "../views/category/contract-type/ContractType";
import CustomerType from "../views/category/customer-type/CustomerType";
import Customer from "../views/category/customer/Customer";
import CustomerLocation from "../views/category/customer/CustomerMap";
import DistributingChannel from "../views/category/distributing-channel/DistributingChannel";
import Product from "../views/category/product/Product";
export default function CategoryRouter() {
  let { path } = useRouteMatch();
  return (
    <div>
      <Switch>
        <Route component={Product} exact path={`${path}/product`} />
        <Route component={Customer} exact path={`${path}/customer`} />
        <Route
          component={CustomerLocation}
          exact
          path={`${path}/customer/map`}
        />
        <Route
          component={ProductUnitScreen}
          exact
          path={`${path}/unit-product`}
        />
        <Route
          component={ProductCategoryScreen}
          exact
          path={`${path}/category-product`}
        />
        <Route
          component={DistributingChannel}
          exact
          path={`${path}/distributing-channel`}
        />
        <Route component={CustomerType} exact path={`${path}/type-customer`} />
        <Route component={ContractType} exact path={`${path}/type-contract`} />
      </Switch>
    </div>
  );
}
