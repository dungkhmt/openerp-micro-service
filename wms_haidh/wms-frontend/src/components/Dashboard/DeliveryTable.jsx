const DeliveryTable = () => {
  const deliveries = [
    {
      number: "N#122412-B3",
      status: { text: "Completed", color: "text-green-500", bgColor: "bg-emerald-50" },
      weight: "40 kg",
      date: "19 Jul 2023",
      time: "10:30 PM",
    },
    {
      number: "N#122412-B3",
      status: { text: "In progress", color: "text-amber-500", bgColor: "bg-yellow-50" },
      weight: "40 kg",
      date: "19 Jul 2023",
      time: "10:30 PM",
    },
  ];

  return (
    <div className="w-full mt-9">
      <table className="w-full border-collapse text-base font-normal">
        {/* Tiêu đề cột: chữ trắng, không có nền, căn trái */}
        <thead>
          <tr className="text-left text-white">
            <th className="px-4 py-2 font-normal">Delivery Number</th>
            <th className="px-4 py-2 font-normal">Status</th>
            <th className="px-4 py-2 font-normal">Delivery Weight</th>
            <th className="px-4 py-2 font-normal">Date</th>
            <th className="px-4 py-2 font-normal">Time</th>
          </tr>
        </thead>
        {/* Dữ liệu đơn giao hàng: chữ trắng, không nền, căn trái */}
        <tbody>
          {deliveries.map((delivery, index) => (
            <tr key={index} className="text-left text-white">
              <td className="px-4 py-2">{delivery.number}</td>
              <td className="px-4 py-2">
                <span className={`px-3 py-1 rounded-2xl ${delivery.status.bgColor} ${delivery.status.color}`}>
                  {delivery.status.text}
                </span>
              </td>
              <td className="px-4 py-2">{delivery.weight}</td>
              <td className="px-4 py-2">{delivery.date}</td>
              <td className="px-4 py-2">{delivery.time}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DeliveryTable;
