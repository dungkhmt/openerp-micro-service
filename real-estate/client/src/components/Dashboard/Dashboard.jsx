import "./Dashboard.css";
import { LineChart } from '@mantine/charts';
import '@mantine/charts/styles.css';
import {transferPrice} from "../../utils/common";

const Dashboard = ({historyPrice, currentPrice}) => {
    console.log(historyPrice)

    return (
        <div className="dashboard-container">
            <h2
                style={{
                    margin: "20px 0"
                }}
            >
                Giá tài sản đang là {transferPrice(currentPrice)}/m²
            </h2>
            <LineChart
                h={350}
                data={historyPrice}
                dataKey="date"
                series={[
                    { name: 'highest', label : 'Cao nhất', color: 'indigo.6' },
                    { name: 'lowest', label : 'Thấp nhất', color: 'blue.6' },
                    { name: 'medium', label : 'Trung bình', color: 'teal.6' },
                ]}
                curveType="linear"
                connectNulls
                tickLine="xy"
                gridAxis="xy"
                xAxisLabel="Ngày"
                yAxisLabel="triệu/m²"
            />
        </div>
    )
}

export default Dashboard;