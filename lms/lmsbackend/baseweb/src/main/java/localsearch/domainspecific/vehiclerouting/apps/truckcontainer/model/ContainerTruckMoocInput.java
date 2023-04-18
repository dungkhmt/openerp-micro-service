package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class ContainerTruckMoocInput {
	private ExportContainerTruckMoocRequest[] exRequests;
	private ImportContainerTruckMoocRequest[] imRequests;
	private WarehouseTransportRequest[] warehouseRequests;
	
	private ExportEmptyRequests[] exEmptyRequests;
	private ExportLadenRequests[] exLadenRequests;
	private ImportEmptyRequests[] imEmptyRequests;
	private ImportLadenRequests[] imLadenRequests;
	
	
	private ShipCompany[] companies;
	private DepotContainer[] depotContainers;
	private DepotMooc[] depotMoocs;
	private DepotTruck[] depotTrucks;
	private Warehouse[] warehouses;
	private Truck[] trucks;
	private Mooc[] moocs;
	private MoocGroup[] moocGroup;
	private Port[] ports;
	
	private Container[] containers;
	
	DistanceElement[] distance;
	DistanceElement[] travelTime;

	private ConfigParam params;
	

	
	

	public ContainerTruckMoocInput(
			ExportContainerTruckMoocRequest[] exRequests,
			ImportContainerTruckMoocRequest[] imRequests,
			WarehouseTransportRequest[] warehouseRequests,
			ExportEmptyRequests[] exEmptyRequests,
			ExportLadenRequests[] exLadenRequests,
			ImportEmptyRequests[] imEmptyRequests,
			ImportLadenRequests[] imLadenRequests, ShipCompany[] companies,
			DepotContainer[] depotContainers, DepotMooc[] depotMoocs,
			DepotTruck[] depotTrucks, Warehouse[] warehouses, Truck[] trucks,
			Mooc[] moocs, MoocGroup[] moocGroup, Port[] ports, Container[] containers,
			DistanceElement[] distance, DistanceElement[] travelTime,
			ConfigParam params) {
		super();
		this.exRequests = exRequests;
		this.imRequests = imRequests;
		this.warehouseRequests = warehouseRequests;
		this.exEmptyRequests = exEmptyRequests;
		this.exLadenRequests = exLadenRequests;
		this.imEmptyRequests = imEmptyRequests;
		this.imLadenRequests = imLadenRequests;
		this.companies = companies;
		this.depotContainers = depotContainers;
		this.depotMoocs = depotMoocs;
		this.depotTrucks = depotTrucks;
		this.warehouses = warehouses;
		this.trucks = trucks;
		this.moocs = moocs;
		this.moocGroup = moocGroup;
		this.ports = ports;
		this.containers = containers;
		this.distance = distance;
		this.travelTime = travelTime;
		this.params = params;
	}


	public ExportEmptyRequests[] getExEmptyRequests() {
		return exEmptyRequests;
	}


	public void setExEmptyRequests(ExportEmptyRequests[] exEmptyRequests) {
		this.exEmptyRequests = exEmptyRequests;
	}


	public ExportLadenRequests[] getExLadenRequests() {
		return exLadenRequests;
	}


	public void setExLadenRequests(ExportLadenRequests[] exLadenRequests) {
		this.exLadenRequests = exLadenRequests;
	}


	public ImportEmptyRequests[] getImEmptyRequests() {
		return imEmptyRequests;
	}


	public void setImEmptyRequests(ImportEmptyRequests[] imEmptyRequests) {
		this.imEmptyRequests = imEmptyRequests;
	}


	public ImportLadenRequests[] getImLadenRequests() {
		return imLadenRequests;
	}


	public void setImLadenRequests(ImportLadenRequests[] imLadenRequests) {
		this.imLadenRequests = imLadenRequests;
	}

	public Port[] getPorts() {
		return ports;
	}


	public void setPorts(Port[] ports) {
		this.ports = ports;
	}








	public ContainerTruckMoocInput(
			ExportContainerTruckMoocRequest[] exRequests,
			ImportContainerTruckMoocRequest[] imRequests,
			WarehouseTransportRequest[] warehouseRequests,
			ShipCompany[] companies, DepotContainer[] depotContainers,
			DepotMooc[] depotMoocs, DepotTruck[] depotTrucks,
			Warehouse[] warehouses, Truck[] trucks, Mooc[] moocs, MoocGroup[] moocGroup,
			Port[] ports, Container[] containers, DistanceElement[] distance,
			DistanceElement[] travelTime, ConfigParam params) {
		super();
		this.exRequests = exRequests;
		this.imRequests = imRequests;
		this.warehouseRequests = warehouseRequests;
		this.companies = companies;
		this.depotContainers = depotContainers;
		this.depotMoocs = depotMoocs;
		this.depotTrucks = depotTrucks;
		this.warehouses = warehouses;
		this.trucks = trucks;
		this.moocs = moocs;
		this.moocGroup = moocGroup;
		this.ports = ports;
		this.containers = containers;
		this.distance = distance;
		this.travelTime = travelTime;
		this.params = params;
	}


	public Warehouse[] getWarehouses() {
		return warehouses;
	}


	public void setWarehouses(Warehouse[] warehouses) {
		this.warehouses = warehouses;
	}


	public ExportContainerTruckMoocRequest[] getExRequests() {
		return exRequests;
	}


	public void setExRequests(ExportContainerTruckMoocRequest[] exRequests) {
		this.exRequests = exRequests;
	}


	public ImportContainerTruckMoocRequest[] getImRequests() {
		return imRequests;
	}


	public void setImRequests(ImportContainerTruckMoocRequest[] imRequests) {
		this.imRequests = imRequests;
	}


	public WarehouseTransportRequest[] getWarehouseRequests() {
		return warehouseRequests;
	}


	public void setWarehouseRequests(WarehouseTransportRequest[] warehouseRequests) {
		this.warehouseRequests = warehouseRequests;
	}


	public ShipCompany[] getCompanies() {
		return companies;
	}


	public void setCompanies(ShipCompany[] companies) {
		this.companies = companies;
	}


	public DepotContainer[] getDepotContainers() {
		return depotContainers;
	}


	public void setDepotContainers(DepotContainer[] depotContainers) {
		this.depotContainers = depotContainers;
	}


	public DepotMooc[] getDepotMoocs() {
		return depotMoocs;
	}


	public void setDepotMoocs(DepotMooc[] depotMoocs) {
		this.depotMoocs = depotMoocs;
	}


	public DepotTruck[] getDepotTrucks() {
		return depotTrucks;
	}


	public void setDepotTrucks(DepotTruck[] depotTrucks) {
		this.depotTrucks = depotTrucks;
	}


	public Truck[] getTrucks() {
		return trucks;
	}


	public void setTrucks(Truck[] trucks) {
		this.trucks = trucks;
	}


	public Mooc[] getMoocs() {
		return moocs;
	}


	public void setMoocs(Mooc[] moocs) {
		this.moocs = moocs;
	}

	public MoocGroup[] getMoocGroup(){
		return this.moocGroup;
	}
	
	public void setMoocGroup(MoocGroup[] moocGroup){
		this.moocGroup = moocGroup;
	}

	public Container[] getContainers() {
		return containers;
	}


	public void setContainers(Container[] containers) {
		this.containers = containers;
	}


	public DistanceElement[] getDistance() {
		return distance;
	}


	public void setDistance(DistanceElement[] distance) {
		this.distance = distance;
	}


	public DistanceElement[] getTravelTime() {
		return travelTime;
	}


	public void setTravelTime(DistanceElement[] travelTime) {
		this.travelTime = travelTime;
	}


	public ConfigParam getParams() {
		return params;
	}


	public void setParams(ConfigParam params) {
		this.params = params;
	}


	public ContainerTruckMoocInput() {
		super();
		// TODO Auto-generated constructor stub
	}



//	public static void main(String[] args){
//		String[] locationCodes = {"0001","0002","0003","0004","0005","0006","0007","0008","0009","0010","0011","0012","0013"};
//		double[] lat = {21.01,21.02,21.03,21.04,21.05,21.06,21.07,21.08,21.09,21.10,21.11,21.12,21.13};
//		double[] lng = {105.01,105.02,105.03,105.04,105.05,105.06,105.07,105.08,105.09,105.10,105.11,105,12,105.13};
//		String[] depotContainerCode = {"DepotContainer001","DepotContainer002","DepotContainer003"};
//		String[] depotTruckCode = {"DepotTruck001","DepotTruck002"};
//		String[] depotMoocCode = {"DepotMooc001","DepotMooc002"};
//		String[] warehouseCode = {"Warehouse001","Warehouse002","Warehouse003","Warehouse004"};
//		String[] portCode = {"Port001","Port002"};
//		
//		ShipCompany[] companies = new ShipCompany[2];
//		companies[0] = new ShipCompany("Com01",depotContainerCode);
//		companies[1] = new ShipCompany("Com02",depotContainerCode);
//		
//		DepotContainer[] depotContainers = new DepotContainer[3];
//		depotContainers[0] = new DepotContainer("DepotContainer001","0010",1800,1800);
//		depotContainers[1] = new DepotContainer("DepotContainer002","0009",1800,1800);
//		depotContainers[2] = new DepotContainer("DepotContainer003","0011",1800,1800);
//		
//		DepotTruck[] depotTrucks = new DepotTruck[2];
//		depotTrucks[0] = new DepotTruck("DepotTruck001","0005");
//		depotTrucks[1] = new DepotTruck("DepotTruck002","0006");
//		
//		DepotMooc[] depotMoocs = new DepotMooc[2];
//		depotMoocs[0] = new DepotMooc("DepotMooc001","0008",900,900);
//		depotMoocs[1] = new DepotMooc("DepotMooc002","0007",900,900);
//		
//		Warehouse[] warehouses = new Warehouse[warehouseCode.length];
//		warehouses[0] = new Warehouse("Warehouse001","0001");
//		warehouses[1] = new Warehouse("Warehouse002","0002");
//		warehouses[2] = new Warehouse("Warehouse003","0003");
//		warehouses[3] = new Warehouse("Warehouse004","0004");
//		
//		Truck[] trucks = new Truck[6];
//		String[] returnDepotTruckCodes = {"DepotTruck001","DepotTruck002"};
//		trucks[0] = new Truck("Truck0001", 39, "DepotTruck001","2018-07-17 06:00:00","2018-07-17 21:00:00",returnDepotTruckCodes);
//		trucks[1] = new Truck("Truck0002", 35, "DepotTruck001","2018-07-17 06:00:00","2018-07-17 21:00:00",returnDepotTruckCodes);
//		trucks[2] = new Truck("Truck0003", 30, "DepotTruck001","2018-07-17 06:00:00","2018-07-17 21:00:00",returnDepotTruckCodes);
//		trucks[3] = new Truck("Truck0004", 32, "DepotTruck002","2018-07-17 06:00:00","2018-07-17 21:00:00",returnDepotTruckCodes);
//		trucks[4] = new Truck("Truck0005", 31, "DepotTruck002","2018-07-17 06:00:00","2018-07-17 21:00:00",returnDepotTruckCodes);
//		trucks[5] = new Truck("Truck0006", 36, "DepotTruck002","2018-07-17 06:00:00","2018-07-17 21:00:00",returnDepotTruckCodes);
//		
//		Mooc[] moocs = new Mooc[6];
//		String[] returnDepotMoocCodes = {"DepotMooc001","DepotMooc002"};
//		moocs[0] = new Mooc("Mooc0001","20",20,"DepotMooc001",returnDepotMoocCodes);
//		moocs[1] = new Mooc("Mooc0002","40",40,"DepotMooc001",returnDepotMoocCodes);
//		moocs[2] = new Mooc("Mooc0003","45",45,"DepotMooc001",returnDepotMoocCodes);
//		moocs[3] = new Mooc("Mooc0004","20",20,"DepotMooc002",returnDepotMoocCodes);
//		moocs[4] = new Mooc("Mooc0005","40",40,"DepotMooc002",returnDepotMoocCodes);
//		moocs[5] = new Mooc("Mooc0006","45",45,"DepotMooc002",returnDepotMoocCodes);
//		
//		MoocGroup[] moocGroup = new MoocGroup[1];
//		MoocPacking[] packing = new MoocPacking[2];
//		packing[0] = new MoocPacking("20DC", 2);
//		packing[1] = new MoocPacking("40DC", 1);
//		moocGroup[0] = new MoocGroup("Mooc xuong 40", packing);
//		
//		Container[] containers = new Container[6];
//		String[] returnDepotCodes = {"DepotContainer001","DepotContainer002","DepotContainer003"};
//		containers[0] = new Container("Container001",20,"20","DepotContainer001",returnDepotCodes);
//		containers[1] = new Container("Container002",40,"40","DepotContainer001",returnDepotCodes);
//		containers[2] = new Container("Container003",20,"20","DepotContainer002",returnDepotCodes);
//		containers[3] = new Container("Container004",40,"40","DepotContainer002",returnDepotCodes);
//		containers[4] = new Container("Container005",20,"20","DepotContainer003",returnDepotCodes);
//		containers[5] = new Container("Container006",45,"45","DepotContainer003",returnDepotCodes);
//		
//		Port[] ports = new Port[2];
//		ports[0] = new Port("Port001","0012");
//		ports[1] = new Port("Port002","0013");
//		
//		ExportContainerRequest[] exContReq = new ExportContainerRequest[3];
//		PickupWarehouseInfo[] pwi = new PickupWarehouseInfo[1];
//		pwi[0] = new PickupWarehouseInfo("Warehouse004",
//				"2018-07-17 08:00:00",
//				"2018-07-20 14:00:00",
//				36000,
//				600,
//				"2018-07-17 08:00:00",
//				"2018-07-20 18:00:00",
//				600
//				);
//		exContReq[0] = new ExportContainerRequest(
//				"ORD0001",
//				"Com01",
//				"DepotContainer001",
//				"20", 20,
//				"2018-07-17 08:00:00",
//				"2018-07-20 12:00:00",
//				pwi,
//				"Port001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 20:00:00",
//				36000
//				);
//		pwi = new PickupWarehouseInfo[1];
//		pwi[0] = new PickupWarehouseInfo("Warehouse001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 14:00:00",
//				36000,
//				600,
//				"2018-07-17 08:00:00",
//				"2018-07-20 18:00:00",
//				600
//				);
//		exContReq[1] = new ExportContainerRequest(
//				"ORD0002",
//				"Com01",
//				"DepotContainer001",
//				"20", 20,
//				"2018-07-17 08:00:00",
//				"2018-07-20 12:00:00",
//				pwi,
//				"Port002",
//				"2018-07-17 08:00:00",
//				"2018-07-20 20:00:00",
//				36000
//				);
//		pwi = new PickupWarehouseInfo[1];
//		pwi[0] = new PickupWarehouseInfo("Warehouse002",
//				"2018-07-17 08:00:00",
//				"2018-07-20 14:00:00",
//				36000,
//				600,
//				"2018-07-17 08:00:00",
//				"2018-07-20 18:00:00",
//				600
//				);
//		exContReq[2] = new ExportContainerRequest(
//				"ORD0003",
//				"Com01",
//				"DepotContainer001",
//				"40", 40,
//				"2018-07-17 08:00:00",
//				"2018-07-20 12:00:00",
//				pwi,
//				"Port001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 20:00:00",
//				36000
//				);
//		
//		ImportContainerRequest[] imContReq = new ImportContainerRequest[3];
//		String[] depotContainer0 = {"DepotContainer001"};
//		
//		DeliveryWarehouseInfo[] dwi = new DeliveryWarehouseInfo[1];
//		dwi[0] = new DeliveryWarehouseInfo(
//				"Warehouse004",
//				"2018-07-17 08:00:00",
//				"2018-07-20 14:00:00",
//				36000,
//				600,
//				"2018-07-17 08:00:00",
//				"2018-07-20 18:00:00",
//				600);
//		imContReq[0] = new ImportContainerRequest(
//				"ORD0004",
//				"Com01",
//				depotContainer0,
//				"20", "",20,
//				"Port001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 12:00:00",
//				36000,
//				dwi,
//				"2018-07-17 08:00:00",
//				"2018-07-20 22:00:00"
//				);
//		
//		dwi = new DeliveryWarehouseInfo[1];
//		dwi[0] = new DeliveryWarehouseInfo(
//				"Warehouse002",
//				"2018-07-17 08:00:00",
//				"2018-07-20 14:00:00",
//				36000,
//				600,
//				"2018-07-17 08:00:00",
//				"2018-07-20 18:00:00",
//				600);
//		String[] depotContainer1 = {"DepotContainer001"};
//		imContReq[1] = new ImportContainerRequest(
//				"ORD0005",
//				"Com01",
//				depotContainer1,
//				"20", "", 20,
//				"Port001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 12:00:00",
//				36000,
//				dwi,
//				"2018-07-17 08:00:00",
//				"2018-07-20 20:00:00"
//				);
//		
//		dwi = new DeliveryWarehouseInfo[1];
//		dwi[0] = new DeliveryWarehouseInfo(
//				"Warehouse001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 14:00:00",
//				36000,
//				600,
//				"2018-07-17 08:00:00",
//				"2018-07-20 18:00:00",
//				600);
//		String[] depotContainer2 = {"DepotContainer001"};
//		imContReq[2] = new ImportContainerRequest(
//				"ORD0006",
//				"Com01",
//				depotContainer2,
//				"45", "",45,
//				"Port001",
//				"2018-07-17 08:00:00",
//				"2018-07-20 12:00:00",
//				36000,
//				dwi,
//				"2018-07-17 08:00:00",
//				"2018-07-20 22:00:00"
//				);
//
//		WarehouseContainerTransportRequest[] wReq = new WarehouseContainerTransportRequest[2];
//		String[] returnDepotContainerCodes = {"DepotContainer001","DepotContainer002"};
//		wReq[0] = new WarehouseContainerTransportRequest(
//				"ORD0007", 
//				"20", 
//				20, 
//				"Com01",
//				"Warehouse002", 
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				18000, 
//				600,
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				600,
//				"Warehouse001",
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				18000,
//				600,
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				600,
//				returnDepotContainerCodes,
//				"123"
//				);
//		
//		wReq[1] = new WarehouseContainerTransportRequest(
//				"ORD0008", 
//				"20", 
//				20, 
//				"Com01",
//				"Warehouse003", 
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				18000, 
//				600,
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				600,
//				"Warehouse002",
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				18000,
//				600,
//				"2018-07-17 08:00:00", 
//				"2018-07-20 20:00:00", 
//				600,
//				returnDepotContainerCodes,
//				"123"
//				);
//		
//		EmptyContainerFromDepotRequest[] emptyContainerFromDepotRequests = new EmptyContainerFromDepotRequest[1];
//		String[] returnDepotContainerCodesE = {"DepotContainer001","DepotContainer002"};
//		emptyContainerFromDepotRequests[0] = new EmptyContainerFromDepotRequest(
//				"ECFDR001", 
//				"Com01", 
//				returnDepotContainerCodesE, 
//				"40", 
//				"0004", 
//				"2018-07-17 08:00:00", 
//				"2018-07-17 22:00:00", 
//				600);
//		
//		EmptyContainerToDepotRequest[] emptyContainerToDepotRequests = new EmptyContainerToDepotRequest[1];
//		emptyContainerToDepotRequests[0] = new EmptyContainerToDepotRequest(
//				"ECTDR0001", 
//				"", 
//				"Container006", 
//				"0003", 
//				returnDepotContainerCodesE, 
//				"2018-07-17 08:00:00", 
//				"2018-07-17 20:00:00", 
//				600);
//		
//		TransportContainerRequest[] transportContainerRequests = new TransportContainerRequest[1];
//		TransportContainerLocationInfo[] tcli = new TransportContainerLocationInfo[2];
//		tcli[0] = new TransportContainerLocationInfo("0002", 
//				"2018-07-17 08:00:00", 
//				"2018-07-17 20:00:00", 
//				600);
//		tcli[1] = new TransportContainerLocationInfo("0004", 
//				"2018-07-17 08:00:00", 
//				"2018-07-17 20:00:00", 
//				600);
//		transportContainerRequests[0] = new TransportContainerRequest(
//				"TCR0001", 
//				"Container005", 
//				"20",
//				1000000, 
//				600,
//				tcli,
//				600
//				);
//		
//		
//		ExportContainerTruckMoocRequest[] exReq = new ExportContainerTruckMoocRequest[1];
//		exReq[0] = new ExportContainerTruckMoocRequest("OR0001","OR0001",exContReq);
//		
//		ImportContainerTruckMoocRequest[] imReq = new ImportContainerTruckMoocRequest[1];
//		imReq[0] = new ImportContainerTruckMoocRequest("OR0002","OR0002",imContReq);
//		
//		WarehouseTransportRequest[] wtr = new WarehouseTransportRequest[1];
//		wtr[0] = new WarehouseTransportRequest("OR0003", "OR0003", wReq);
//		
//		int n = locationCodes.length;
//		DistanceElement[] dis = new DistanceElement[n*n];
//		DistanceElement[] t = new DistanceElement[n*n];
//		
//		GoogleMapsQuery G = new GoogleMapsQuery();
//		int idx = -1;
//		for(int i = 0; i < n; i++){
//			for(int j = 0; j < n; j++){
//				String code1 = locationCodes[i];
//				String code2 = locationCodes[j];
//				double d = G.computeDistanceHaversine(lat[i], lng[i], lat[j], lng[j]);
//				idx++;
//				dis[idx] = new DistanceElement(code1, code2, d);
//				t[idx] = new DistanceElement(code1, code2, d*1000.0/(40*1000/3600));// meter/second
//			}
//		}
//		
//		ConfigParam params = new ConfigParam(600, 600);
//		ContainerTruckMoocInput input = new ContainerTruckMoocInput(
//				exReq, 
//				imReq, 
//				wtr, 
//				emptyContainerFromDepotRequests, 
//				emptyContainerToDepotRequests,
//				transportContainerRequests, 
//				companies,
//				depotContainers, 
//				depotMoocs, 
//				depotTrucks, 
//				warehouses, 
//				trucks, 
//				moocs, 
//				moocGroup,
//				ports, 
//				containers, 
//				dis, 
//				t,
//				params);
//		
//		
//		
//		
//		Gson gson = new Gson();
//		String json = gson.toJson(input);
//		
//		try{
//			PrintWriter out = new PrintWriter("C:/DungPQ/daily-opt/smart-log/container.json");
//			out.print(json);
//			out.close();
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
}
