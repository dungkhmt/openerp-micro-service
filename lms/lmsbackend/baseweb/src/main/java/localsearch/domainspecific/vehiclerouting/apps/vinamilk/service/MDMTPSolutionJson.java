package localsearch.domainspecific.vehiclerouting.apps.vinamilk.service;

import localsearch.domainspecific.vehiclerouting.apps.vinamilk.model.VehicleRoute;

public class MDMTPSolutionJson {
	private VehicleRoute[] vehicleRoutes;
	private StatisticInformation statisticInformation;
	
	public MDMTPSolutionJson(VehicleRoute[] vehicleRoutes,
			StatisticInformation statisticInformation) {
		super();
		this.vehicleRoutes = vehicleRoutes;
		this.statisticInformation = statisticInformation;
	}

	public VehicleRoute[] getVehicleRoutes() {
		return vehicleRoutes;
	}

	public void setVehicleRoutes(VehicleRoute[] vehicleRoutes) {
		this.vehicleRoutes = vehicleRoutes;
	}

	public StatisticInformation getStatisticInformation() {
		return statisticInformation;
	}

	public void setStatisticInformation(StatisticInformation statisticInformation) {
		this.statisticInformation = statisticInformation;
	}
}
