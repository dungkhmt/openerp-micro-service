import { request } from "../api";
const fetchRoute = async (coordinates, setRoute, setDistance, setLoadingMap) => {
  setLoadingMap(true);
  const payload = { coordinates };
  request("post", "/routes/fetch", (res) => {
    if (res.status === 200) {
      setRoute(res.data.path);
      setDistance(res.data.distance);
    }
  }, {
    onError: (e) => {
      alert("Error occurred while loading route !");
    }
  }, payload).then(() => setLoadingMap(false));
  ;
};
export default fetchRoute;