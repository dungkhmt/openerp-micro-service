import axios from "axios";
const fetchRoute = async (coordinates, setRoute, setDistance, setLoadingMap) => {
  setLoadingMap(true);

  try {
    const response = await axios.post("http://localhost:8082/api/routes/fetch", { coordinates });

    const data = response.data;
    const routeCoordinates = data.features[0].geometry.coordinates.map(([lng, lat]) => ({
      lat,
      lng
    }));

    const distance = data.features[0].properties.summary.distance;

    setRoute(routeCoordinates);
    setDistance(distance);
  } catch (error) {
    console.error("Error fetching route:", error);
  } finally {
    setLoadingMap(false);
  }
};
export default fetchRoute;