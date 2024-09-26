import * as React from "react";
import { PieChart } from "@mui/x-charts/PieChart";
import { useState } from "react";
import { request } from "api";
import { useEffect } from "react";

export default function AssetTypePie() {
  const [log, setLog] = useState([]);
	const [types, setTypes] = useState([]);

	const getAllTypes = async() => {
		await request("get", "/asset-type/get-all", (res) => {
			setTypes(res.data);
		});
	};

	const buildData = () => {
		setLog([]);
		const tempData = [];
		for(let i = 0; i < types.length; i++){
			const type = types[i];
			tempData.push({id: i, value: type.num_assets, label: type.name});
		}
		setLog(tempData);
	};

  useEffect(() => {
		getAllTypes();
  }, []);

  useEffect(() => {
		buildData();
  }, [types]);

  return (
    <div>
      <PieChart
        series={[
          {
            data: log,
            highlightScope: { faded: "global", highlighted: "item" },
            faded: { innerRadius: 25, additionalRadius: -30, color: "gray" },
          },
        ]}
        height={350}
      />
      <h3 style={{ textAlign: "center" }}>Asset Types Reports</h3>
    </div>
  );
}
