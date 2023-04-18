package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class TruckMoocContainerOutputJson {
	private TruckRoute[] truckRoutes;
	private ExportEmptyRequests[] unscheduledExEmptyRequests;
	private ExportLadenRequests[] unscheduledExLadenRequests;
	private ImportEmptyRequests[] unscheduledImEmptyRequests;
	private ImportLadenRequests[] unscheduledImLadenRequests;
	private StatisticInformation statisticInformation;
	
	public TruckMoocContainerOutputJson(TruckRoute[] truckRoutes,
			ExportEmptyRequests[] unscheduledExEmptyRequests,
			ExportLadenRequests[] unscheduledExLadenRequests,
			ImportEmptyRequests[] unscheduledImEmptyRequests,
			ImportLadenRequests[] unscheduledImLadenRequests,
			StatisticInformation statisticInformation) {
		super();
		this.truckRoutes = truckRoutes;
		this.unscheduledExEmptyRequests = unscheduledExEmptyRequests;
		this.unscheduledExLadenRequests = unscheduledExLadenRequests;
		this.unscheduledImEmptyRequests = unscheduledImEmptyRequests;
		this.unscheduledImLadenRequests = unscheduledImLadenRequests;
		this.statisticInformation = statisticInformation;
	}

	public TruckRoute[] getTruckRoutes() {
		return truckRoutes;
	}

	public void setTruckRoutes(TruckRoute[] truckRoutes) {
		this.truckRoutes = truckRoutes;
	}

	public ExportEmptyRequests[] getUnscheduledExEmptyRequests() {
		return unscheduledExEmptyRequests;
	}

	public void setUnscheduledExEmptyRequests(
			ExportEmptyRequests[] unscheduledExEmptyRequests) {
		this.unscheduledExEmptyRequests = unscheduledExEmptyRequests;
	}

	public ExportLadenRequests[] getUnscheduledExLadenRequests() {
		return unscheduledExLadenRequests;
	}

	public void setUnscheduledExLadenRequests(
			ExportLadenRequests[] unscheduledExLadenRequests) {
		this.unscheduledExLadenRequests = unscheduledExLadenRequests;
	}

	public ImportEmptyRequests[] getUnscheduledImEmptyRequests() {
		return unscheduledImEmptyRequests;
	}

	public void setUnscheduledImEmptyRequests(
			ImportEmptyRequests[] unscheduledImEmptyRequests) {
		this.unscheduledImEmptyRequests = unscheduledImEmptyRequests;
	}

	public ImportLadenRequests[] getUnscheduledImLadenRequests() {
		return unscheduledImLadenRequests;
	}

	public void setUnscheduledImLadenRequests(
			ImportLadenRequests[] unscheduledImLadenRequests) {
		this.unscheduledImLadenRequests = unscheduledImLadenRequests;
	}

	public StatisticInformation getStatisticInformation() {
		return statisticInformation;
	}

	public void setStatisticInformation(StatisticInformation statisticInformation) {
		this.statisticInformation = statisticInformation;
	}
	
	
}
