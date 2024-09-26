import { Navigate, useParams } from "react-router-dom";
import PropTypes from "prop-types";
import { useMemo } from "react";

const Redirect = ({ to }) => {
  const params = useParams();

  const newTo = useMemo(() => {
    let newTo = to;
    Object.keys(params).forEach((key) => {
      newTo = newTo.replace(`:${key}`, params[key]);
    });
    return newTo;
  }, [params, to]);

  return <Navigate to={newTo} replace />;
};

Redirect.propTypes = {
  to: PropTypes.string.isRequired,
};

export default Redirect;
