import * as React from "react";
import Icon from "./Icon";
import Avatar from "./Avatar";
import UserGreeting from "./UserGreeting";
import DeliveryTable from "./DeliveryTable";
import MetricCard from "./MetricCard";
import WarehouseWorkload from "./WarehouseWorkload";
import ProgressIndicators from "./ProgressIndicators";

function Dashboard() {
  return (
    <div className="flex overflow-hidden flex-col justify-center px-2 py-3.5 bg-white">
      <div className="flex overflow-hidden flex-wrap px-5 pt-5 pb-14 bg-gray-700 rounded-3xl border-solid border-[18px] border-slate-500 border-opacity-70 max-md:max-w-full">
        {/* <div className="flex flex-col justify-between items-center px-5 py-14 my-auto bg-slate-600 max-md:hidden">
          <div className="flex flex-col justify-center items-center w-[53px]">
            <img
              loading="lazy"
              src="https://cdn.builder.io/api/v1/image/assets/TEMP/e6e6196b528a1b08fb71a783e7317917721b4c103f1727d7e8598a87d2fd0011?placeholderIfAbsent=true&apiKey=f54479d50a174cf2a101c0f7e478a896"
              alt=""
              className="object-contain w-full aspect-square rounded-[62px]"
            />
            <div className="flex flex-col items-center mt-16 max-md:mt-10">
              <div className="flex relative gap-2.5 items-start p-2.5">
                <div className="flex z-0 shrink-0 h-[26px] w-[26px]" />
                <div className="flex absolute top-2/4 z-0 shrink-0 w-1 h-11 bg-sky-400 rounded -translate-y-2/4 right-[-22px] translate-x-[0%]" />
              </div>
              <Icon />
              <Icon />
              <Icon />
              <Icon />
              <Icon />
            </div>
          </div>
          <div className="flex flex-col justify-center items-center mt-96 max-md:mt-10">
            <Icon />
            <Icon />
          </div>
        </div> */}
        <div className="flex flex-col grow shrink-0 basis-0 w-fit max-md:max-w-full">
          {/* <div className="flex flex-wrap gap-10 justify-between items-center px-11 py-5 w-full bg-slate-600 max-md:px-5 max-md:max-w-full">
            <UserGreeting name="Ducky" date="26 / 12 / 2023" />
            <div className="flex gap-10 justify-center items-center self-stretch my-auto min-w-[240px]">
              <div className="flex gap-2.5 justify-center items-center self-stretch my-auto">
                <Icon />
                <Icon />
                <Icon />
              </div>
              <Avatar
                src="https://cdn.builder.io/api/v1/image/assets/TEMP/5840cb08879dd396552816bbb088d4636c079c8eab9903a713a2d1d698083764?placeholderIfAbsent=true&apiKey=f54479d50a174cf2a101c0f7e478a896"
                alt="User Avatar"
                name="Ducky Lee"
                id="Admin #1234"
              />
            </div>
          </div> */}
          <div className="flex flex-wrap gap-7 mx-9 mt-9 min-h-[202px] max-md:mr-2.5">
            <MetricCard
              bgColor="slate-600"
              icon={{ bgColor: "bg-sky-400" }}
              title="Current balance"
              value="$999,999"
              description="Today"
              change={{
                value: "+$123,123",
                percentage: "+10.05%",
                colorCode: "green-500",
              }}
            />
            <MetricCard
              bgColor="slate-600"
              icon={{ bgColor: "bg-green-500" }}
              title="Income"
              value="$223,324"
              description="Today"
              change={{
                value: "+$142,245",
                percentage: "+23.23%",
                colorCode: "green-500",
              }}
            />
            <MetricCard
              bgColor="slate-600"
              icon={{ bgColor: "bg-rose-500" }}
              title="Expence"
              value="$123,434"
              description="Today"
              change={{
                value: "-$123,123",
                percentage: "-10.05%",
                colorCode: "rose-500",
              }}
            />
            <MetricCard
              bgColor="slate-600"
              icon={{ bgColor: "bg-amber-400" }}
              title="Nearest delivery"
              value="26.12.23"
              description="Responsible for delivery"
              change={{ value: "", percentage: "", colorCode: "" }}
              imgSrc="https://cdn.builder.io/api/v1/image/assets/TEMP/2d3ba0626918cb257b7dd5a1f024d87077480c741acc9391e5d83696b610759e?placeholderIfAbsent=true&apiKey=f54479d50a174cf2a101c0f7e478a896"
              alt="Delivery Responsible"
            />
          </div>
          <div className="mx-9 mt-7 max-md:mr-2.5 max-md:max-w-full">
            <div className="flex gap-5 max-md:flex-col items-stretch">
              {/* Cột 1 */}
              <div className="flex flex-col w-[46%] max-md:w-full min-h-full">
                <WarehouseWorkload
                  title="Warehouse workload"
                  imgSrc="https://cdn.builder.io/api/v1/image/assets/TEMP/53a420c37f11f9850dd4726217bf76d29e65e267ed3ec07bd3638feca9da4d3c?placeholderIfAbsent=true&apiKey=f54479d50a174cf2a101c0f7e478a896"
                  alt="Warehouse workload"
                  iconClasses={[{}, {}]}
                  className="h-full"
                />
              </div>
              {/* Cột 2 */}
              <div className="flex flex-col ml-5 w-[54%] max-md:w-full min-h-full">
                <WarehouseWorkload
                  title="Warehouse workload"
                  imgSrc="https://cdn.builder.io/api/v1/image/assets/TEMP/53aeaedc43b0403012c74eba0cf82a96fbebf376e491899b515a1a0225ef4801?placeholderIfAbsent=true&apiKey=f54479d50a174cf2a101c0f7e478a896"
                  alt="Warehouse workload"
                  iconClasses={[{}, {}]}
                  hasAnnotation={true}
                  className="h-full"
                />
              </div>
            </div>
          </div>

          <div className="flex overflow-hidden flex-col justify-center px-9 py-7 mx-9 mt-9 rounded-2xl bg-slate-600 max-md:px-5 max-md:mr-2.5 max-md:max-w-full">
            <div className="flex flex-wrap gap-10 justify-between items-end w-full max-md:max-w-full">
              <div className="text-2xl font-semibold text-white">
                Accepted deliveries
              </div>
              <div className="flex gap-3.5 items-start">
                <Icon />
                <Icon />
              </div>
            </div>
            <DeliveryTable />

          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
