package localsearch.domainspecific.vehiclerouting.apps.truckcontainer;

import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ContainerTruckMoocInput;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportContainerRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ExportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportContainerTruckMoocRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportEmptyRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.ImportLadenRequests;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.Truck;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.WarehouseContainerTransportRequest;
import localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model.WarehouseTransportRequest;
import localsearch.domainspecific.vehiclerouting.vrp.utils.*;

public class InputAnalyzer {
	public static final int OFFSET_SECOND = 259200;
	public String analyze(ContainerTruckMoocInput input){
		
		return "OK";
	}
	public void standardize( ContainerTruckMoocInput input){
		// set startWorkingTime of trucks if NULL
		int minDateTime = Integer.MAX_VALUE;
		if(input.getExRequests() != null){
			for(int i = 0; i < input.getExRequests().length; i++){
				ExportContainerTruckMoocRequest R = input.getExRequests()[i];
				if(R.getContainerRequest() != null){
					for(int j = 0; j< R.getContainerRequest().length; j++){
						ExportContainerRequest r = R.getContainerRequest()[j];
						if(r.getEarlyDateTimePickupAtDepot() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimePickupAtDepot());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getEarlyDateTimeUnloadAtPort() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeUnloadAtPort());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimeLoadAtWarehouse() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeLoadAtWarehouse());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimePickupAtDepot() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimePickupAtDepot());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimeUnloadAtPort() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeUnloadAtPort());
							if(dt < minDateTime) minDateTime = dt;
						}
					}
				}
			}
		}
		
		if(input.getImRequests() != null){
			for(int i = 0; i < input.getImRequests().length; i++){
				ImportContainerTruckMoocRequest R = input.getImRequests()[i];
				if(R.getContainerRequest() != null){
					for(int j = 0; j < R.getContainerRequest().length; j++){
						ImportContainerRequest r = R.getContainerRequest()[j];
						if(r.getEarlyDateTimeDeliveryAtDepot() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeDeliveryAtDepot());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getEarlyDateTimePickupAtPort() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimePickupAtPort());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimeDeliveryAtDepot() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeDeliveryAtDepot());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimePickupAtPort() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimePickupAtPort());
							if(dt < minDateTime) minDateTime = dt;
						}
					}
				}
			}
		}
		
		if(input.getImEmptyRequests() != null){
			for(int i = 0; i < input.getImEmptyRequests().length; i++){
				ImportEmptyRequests r = input.getImEmptyRequests()[i];
				if(r.getEarlyDateTimeAttachAtWarehouse() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeAttachAtWarehouse());
					if(dt < minDateTime) minDateTime = dt;
				}

				if(r.getLateDateTimeReturnEmptyAtDepot() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeReturnEmptyAtDepot());
					if(dt < minDateTime) minDateTime = dt;
				}
			}
		}
		if(input.getImLadenRequests() != null){
			for(int i= 0; i < input.getImLadenRequests().length; i++){
				ImportLadenRequests r = input.getImLadenRequests()[i];
//				if(r.getRequestDate() != null){
//					int dt = (int)DateTimeUtils.dateTime2Int(r.getRequestDate());
//					if(dt < minDateTime) minDateTime = dt;
//				}
				if(r.getLateDateTimePickupAtPort() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimePickupAtPort());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getLateDateTimeUnloadAtWarehouse() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeUnloadAtWarehouse());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getEarlyDateTimePickupAtPort() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimePickupAtPort());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getEarlyDateTimeUnloadAtWarehouse() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeUnloadAtWarehouse());
					if(dt < minDateTime) minDateTime = dt;
				}
			}
		}
		
		if(input.getExEmptyRequests() != null){
			for(int i = 0; i < input.getExEmptyRequests().length; i++){
				ExportEmptyRequests r = input.getExEmptyRequests()[i];
//				if(r.getRequestDate() != null){
//					int dt = (int)DateTimeUtils.dateTime2Int(r.getRequestDate());
//					if(dt < minDateTime) minDateTime = dt;
//				}
				if(r.getEarlyDateTimeLoadAtWarehouse() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeLoadAtWarehouse());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getEarlyDateTimePickupAtDepot() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimePickupAtDepot());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getLateDateTimeLoadAtWarehouse() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeLoadAtWarehouse());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getLateDateTimePickupAtDepot() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimePickupAtDepot());
					if(dt < minDateTime) minDateTime = dt;
				}
			}
		}
		if(input.getExLadenRequests() != null){
			for(int i = 0; i < input.getExLadenRequests().length; i++){
				ExportLadenRequests r = input.getExLadenRequests()[i];
//				if(r.getRequestDate() != null){
//					int dt = (int)DateTimeUtils.dateTime2Int(r.getRequestDate());
//					if(dt < minDateTime) minDateTime = dt;
//				}
//				if(r.getEarlyDateTimeUnloadAtPort() != null){
//					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeUnloadAtPort());
//					if(dt < minDateTime) minDateTime = dt;
//				}
				if(r.getEarlyDateTimeAttachAtWarehouse() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeAttachAtWarehouse());
					if(dt < minDateTime) minDateTime = dt;
				}
				if(r.getLateDateTimeUnloadAtPort() != null){
					int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeUnloadAtPort());
					if(dt < minDateTime) minDateTime = dt;
				}
			}
		}
		
		if(input.getWarehouseRequests() != null){
			for(int i = 0; i < input.getWarehouseRequests().length; i++){
				WarehouseTransportRequest R = input.getWarehouseRequests()[i];
				if(R.getWarehouseContainerTransportRequests() != null){
					for(int j = 0; j< R.getWarehouseContainerTransportRequests().length; j++){
						WarehouseContainerTransportRequest r = R.getWarehouseContainerTransportRequests()[j];
						if(r.getEarlyDateTimeLoad() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeLoad());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getEarlyDateTimeUnload() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getEarlyDateTimeUnload());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimeLoad() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeLoad());
							if(dt < minDateTime) minDateTime = dt;
						}
						if(r.getLateDateTimeUnload() != null){
							int dt = (int)DateTimeUtils.dateTime2Int(r.getLateDateTimeUnload());
							if(dt < minDateTime) minDateTime = dt;
						}
					}
				}
			}
		}
		
		if(input.getTrucks() != null){
			for(int i = 0; i < input.getTrucks().length; i++){
				Truck truck = input.getTrucks()[i];
				if(truck.getStartWorkingTime() == null){
					truck.setStartWorkingTime(DateTimeUtils.unixTimeStamp2DateTime(minDateTime - OFFSET_SECOND));
				}
			}
		}
	}
	
}
