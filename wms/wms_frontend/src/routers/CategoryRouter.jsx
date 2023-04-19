import { Route, Switch, useRouteMatch } from "react-router";
import ProductCategoryScreen from "views/category/product-category/Category";
import ProtectedScreen from "../views/ProtectedScreen";

export default function CategoryRouter() {
  let { path } = useRouteMatch();
  console.log("Path: ", path);
  return (
    <div>
      <Switch>
        <Route
          component={ProtectedScreen}
          exact
          path={`${path}/unit-product`}
        />
        <Route
          component={ProductCategoryScreen}
          exact
          path={`${path}/category-product`}
        />
      </Switch>
    </div>
  );
}
