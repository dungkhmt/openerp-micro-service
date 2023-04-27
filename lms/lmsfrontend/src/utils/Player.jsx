import ReactPlayer from "react-player";
import { config } from "../config/config";

function Player({ id }) {
  return (
    <ReactPlayer
      controls
      url={`${config.url.API_URL}/videos/videos/${id}`}
      width="100%"
      height="100%"
      config={{
        file: {
          attributes: {
            controlsList: "nodownload",
          },
        },
      }}
    />
  );
}

export default Player;
