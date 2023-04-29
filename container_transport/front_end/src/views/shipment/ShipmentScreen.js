import { Box } from "@mui/material";
import { MapContainer, Marker, Popup, TileLayer } from "react-leaflet";

const ShipmentScreen = () => {
    return (
        <div >
            <MapContainer center={[51.505, -0.09]} zoom={13} scrollWheelZoom={false} style={{ height: "90vh"}}>
                <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                <Marker position={[51.505, -0.09]}>
                    <Popup>
                        A pretty CSS3 popup. <br /> Easily customizable.
                    </Popup>
                </Marker>
            </MapContainer>
        </div>

    )
}
export default ShipmentScreen;