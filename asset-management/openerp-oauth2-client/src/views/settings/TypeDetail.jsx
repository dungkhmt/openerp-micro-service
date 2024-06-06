import { Avatar, Button, Card, CardContent, CardHeader, Grid, Typography } from "@mui/material";
import { makeStyles } from "@mui/styles";
import { request } from "api";
import { useEffect } from "react";
import { useState } from "react";
import { BiDetail } from "react-icons/bi";
import { useParams, useHistory } from "react-router-dom";

const useStyles = makeStyles((theme) => ({
  card: {
    marginTop: theme.spacing(2),
  },
  grid: {
    paddingLeft: 56,
  },
  divider: {
    width: "91.67%",
    marginTop: 16,
    marginBottom: 16,
  },
  rootDivider: {
    backgroundColor: "black",
  },
  tabs: {
    backgroundColor: theme.palette.background.paper,
  },
}));

const TypeDetail = () => {
  const classes = useStyles();
  const params = useParams();
  const history = useHistory();

  const [typeDetail, setTypeDetail] = useState({});
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [openDelete, setOpenDelete] = useState(false);

  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: 600,
    bgcolor: "background.paper",
    border: "2px solid #000",
    boxShadow: 24,
    p: 4,
    gap: "30px",
  };

  const getTypeDetail = async () => {
    setLoading(true);
    await request("get", `/asset-type/get/${params.id}`, (res) => {
        setTypeDetail(res.data);
    });
    setLoading(false);
  };

  useEffect(() => {
    getTypeDetail();
  }, []);

  if (loading) {
    return <p>Loading...</p>;
  }

  return (
    <div>
      <Card>
        <CardHeader
          avatar={
            <Avatar style={{ background: "#ff7043" }}>
              <BiDetail size={32} />
            </Avatar>
          }
          title={<Typography variant="h5">Asset Detail</Typography>}
        />
				<div style={{ float: "right", paddingRight: "20px" }}>
					<Button>Edit</Button>
					<Button>Delete</Button>
				</div>
				<CardContent style={{ paddingTop: "20px" }}>
					<Grid container className={classes.grid}>
						<Grid item md={3} sm={3} xs={3} direction={"column"}>
							<Typography>Name</Typography>
							<Typography>Code Prefix</Typography>
							<Typography>Number of Assets</Typography>
							<Typography>Description</Typography>
						</Grid>
						<Grid item md={8} sm={8} xs={8}>
							<Typography>
								<b>:</b> {typeDetail["name"]}
							</Typography>
							<Typography>
								<b>:</b> {typeDetail["code_prefix"]}
							</Typography>
							<Typography>
								<b>:</b> {typeDetail["num_assets"]}
							</Typography>
							<Typography>
								<b>:</b> {typeDetail["description"]}
							</Typography>
						</Grid>
					</Grid>
				</CardContent>
      </Card>
    </div>
  );
};

export default TypeDetail;