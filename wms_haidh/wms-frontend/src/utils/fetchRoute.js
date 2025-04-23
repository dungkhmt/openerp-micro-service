import axios from "axios";
const fetchRoute = async (coordinates, setRoute, setDistance, setLoadingMap) => {
  setLoadingMap(true);

  try {
    const response = await axios.post("http://localhost:8082/api/routes/fetch", { coordinates });

    const data = response.data;

    setRoute(data.path);
    setDistance(data.distance);
  } catch (error) {
    console.error("Error fetching route:", error);
  } finally {
    setLoadingMap(false);
  }
};
export default fetchRoute;