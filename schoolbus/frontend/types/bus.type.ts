
interface IBusTable {
  bus: IBus;
  driver: IEmployee;
  driverMate: IEmployee;
}

interface IBus {
  id: number;
  numberPlate: string;
  seatNumber: number;
  driverId: number | null;
  driverMateId: number | null;
  status: string;
  createdAt: string;
  updatedAt: string;
}

interface IGetListBusParams {
  numberPlate: string | null;
  seatNumber: number | null;
  statuses: string | null;
  driverName: string | null;
  driverId: number | null;
  driverMateName: string | null;
  driverMateId: number | null;
  page: number | null;
  size: number | null;
  sort: string | null;
}

interface IGetListManipulateBusParams {
  isToSchool: boolean;
  date: string;
  numberPlate: string;
  status: string;
  page: number;
  size: number;
  sort: string;
}

interface IManipulateBus {
  bus: IBus;
  ride: IRide;
  pickupPoints: IPickupPoint[];
}

// employee
interface IEmployeeUpdateBusRequest {
  numberPlate: string;
  status: string;
}

interface IGetBusDetailResponse {
  bus: IBus;
  driver: IEmployee;
  driverMate: IEmployee;
}