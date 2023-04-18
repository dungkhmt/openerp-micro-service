package localsearch.domainspecific.vehiclerouting.apps.truckcontainer.model;

public class MoocGroup {
	private int id;
	private String code;
	private MoocPacking[] packing;
	
	public MoocGroup(int id, String code, MoocPacking[] packing){
		this.id = id;
		this.code = code;
		this.packing = packing;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public void setCode(String code){
		this.code = code;
	}
	
	public MoocPacking[] getPacking(){
		return this.packing;
	}
	
	public void setPacking(MoocPacking[] packing){
		this.packing = packing;
	}

	public MoocGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
