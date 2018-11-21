package southampton.ecs.desktopcloudsim.vmallocationpolicies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public class GreedyVmAllocationPolicy extends VmAllocationPolicy {

	/** The vm table. */
	//the string represents the vm.getUid
	private Map<String, Host> vmTable;

	/** The used pes. */
	private Map<String, Integer> usedPes;

	/** The free pes. */
	private List<Integer> freePes;

	/**
	 * Creates the new VmAllocationPolicySimple object.
	 * 
	 * @param list the list
	 * @pre $none
	 * @post $none
	 */
	public GreedyVmAllocationPolicy(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		for (Host host : getHostList()) {
			getFreePes().add(host.getNumberOfPes());

		}

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
	}
	
	/**
	 * chooses host with least utilisation
	 * @param vm
	 * @return
	 */
	public boolean allocateHostForVm_ChoosingLeastUtilisation(Vm vm) {
		int requiredPes = vm.getNumberOfPes();
		boolean result = false;

		if (!getVmTable().containsKey(vm.getUid()))
		{ // if this vm was not created			
			double utilisation = Double.MAX_VALUE;
			double tmpUti;
			int hostIdx = -1;		//chosen host id to accomodate the vm
			
			for (int i=0; i < getHostList().size(); i++)
			{
				Host host = getHostList().get(i);
				tmpUti = host.getUtilisationPercentage();
				if (tmpUti < utilisation && host.isSuitableForVm(vm))
				{
					utilisation = tmpUti;
					hostIdx =i;
				}
			}

			Host host = getHostList().get(hostIdx);
			result = host.vmCreate(vm);

			if (result) { // if vm were succesfully created in the host
				getVmTable().put(vm.getUid(), host);
				getUsedPes().put(vm.getUid(), requiredPes);
				getFreePes().set(hostIdx, getFreePes().get(hostIdx) - requiredPes);
				result = true;
				
			}		 
		}
		return result;
	}
	public void addFeePes(int numberOfPes)
	{
		getFreePes().add(numberOfPes);
	}

	/**
	 * Allocates a host for a given VM.
	 * 
	 * @param vm VM specification
	 * @return $true if the host could be allocated; $false otherwise
	 * @pre $none
	 * @post $none
	 */
	@Override
	//this method is to be called when creating vms only .... 
	public boolean allocateHostForVm(Vm vm) {
		int requiredPes = vm.getNumberOfPes();
		boolean result = false;

		if (!getVmTable().containsKey(vm.getUid()))
		{ // if this vm was not created			
			int hostIdx = getHostIndexForAllocation(vm);		//chosen host id to accomodate the vm


			Host host = new Host();
			if (hostIdx >= 0)
			{
				host = getHostList().get(hostIdx);
				result = host.vmCreate(vm);
			}
			
			if (result) { // if vm were succesfully created in the host
				getVmTable().put(vm.getUid(), host);
				getUsedPes().put(vm.getUid(), requiredPes);
				getFreePes().set(hostIdx, getFreePes().get(hostIdx) - requiredPes);
				result = true;
				
			}
		}

		return result;
	}

	/**
	 * Releases the host used by a VM.
	 * 
	 * @param vm the vm
	 * @pre $none
	 * @post none
	 */
	@Override
	public void deallocateHostForVm(Vm vm) {
		Host host = getVmTable().remove(vm.getUid());
		int idx = getHostList().indexOf(host);
		int pes = getUsedPes().remove(vm.getUid());
		if (host != null) {
			host.vmDestroy(vm);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
		}
	}

	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vm the vm
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(Vm vm) {
		return getVmTable().get(vm.getUid());
	}

	/**
	 * Gets the host that is executing the given VM belonging to the given user.
	 * 
	 * @param vmId the vm id
	 * @param userId the user id
	 * @return the Host with the given vmID and userID; $null if not found
	 * @pre $none
	 * @post $none
	 */
	@Override
	public Host getHost(int vmId, int userId) {
		return getVmTable().get(Vm.getUid(userId, vmId));
	}

	/**
	 * Gets the vm table.
	 * 
	 * @return the vm table
	 */
	public Map<String, Host> getVmTable() {
		return vmTable;
	}

	/**
	 * Sets the vm table.
	 * 
	 * @param vmTable the vm table
	 */
	protected void setVmTable(Map<String, Host> vmTable) {
		this.vmTable = vmTable;
	}

	/**
	 * Gets the used pes.
	 * 
	 * @return the used pes
	 */
	protected Map<String, Integer> getUsedPes() {
		return usedPes;
	}

	/**
	 * Sets the used pes.
	 * 
	 * @param usedPes the used pes
	 */
	protected void setUsedPes(Map<String, Integer> usedPes) {
		this.usedPes = usedPes;
	}

	/**
	 * Gets the free pes.
	 * 
	 * @return the free pes
	 */
	protected List<Integer> getFreePes() {
		return freePes;
	}

	/**
	 * Sets the free pes.
	 * 
	 * @param freePes the new free pes
	 */
	protected void setFreePes(List<Integer> freePes) {
		this.freePes = freePes;
	}

	/*
	 * (non-Javadoc)
	 * @see cloudsim.VmAllocationPolicy#optimizeAllocation(double, cloudsim.VmList, double)
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.cloudbus.cloudsim.VmAllocationPolicy#allocateHostForVm(org.cloudbus.cloudsim.Vm,
	 * org.cloudbus.cloudsim.Host)
	 */
	@Override
	public boolean allocateHostForVm(Vm vm, Host host) {
		boolean ret = false;
		int idx = getHostList().indexOf(host);
		
		if (idx <0)
		{			
			ret = false;
			/*System.out.println("\t\t [vmallocationPolicySimple.allocateHostForVm]: host id = " + host.getId() + " vm id: " + vm.getId() + " therefore, creating another migration order");
			
			int datacenterId = host.getDatacenter().getId();
			CloudSim.send(datacenterId, vm.getUserId(), 0, CloudSimTags.VM_DESTROY, vm);
			Log.printLine("*******vm destruction ******\t\t vm " + vm.getId() + " is set to be destroyed, no availability\t datacentre id: " + datacenterId);
			CloudSim.send(datacenterId, datacenterId, CloudSim.getMinTimeBetweenEvents(), CloudSimTags.VM_DESTROY, vm);	/**/		
		}
		
		else if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			int requiredPes = vm.getNumberOfPes();

			getUsedPes().put(vm.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

			//Log.formatLine("%.2f: VM #" + vm.getId() + " has been migrated to the host #" + host.getId(),CloudSim.clock());
			ret = true;
		}

		return ret;
	}
	
	/**
	 * retuns true if the host is also failing at the same simulation time
	 * not implemented 
	 * @param hostId
	 * @return
	 */
	protected boolean isHostFainling(int hostId)
	{
		boolean ret = false;
		
		//CloudSim.get
		return ret;
	}
	
	/**
	 * checks if the host can accommodate this vm, it takes the future migrated vms in account
	 * @param host
	 * @param vm
	 * @return true if so
	 */
	protected boolean isHostSuitableToAllocate(Host host, Vm vm)
	{
		boolean result = true;
		
		if ((vm.getMips() > host.getMaxAvailableMips()) || !host.isSuitableForVm(vm))
		{
			result = false;
		}
		return result;
	}
	
	/**
	 * returns the index of a host most suitable for allocating a vm for maximising utilisation 
	 * note: it will choose max utilised + best available CPU power
	 * note: it includes vm's mips that is going to migrate to the utilisation power 
	 * @param vm
	 * @return host index
	 */
	public int getHostIndexForAllocation(Vm vm)
	{
		int hostIndex = -1;
		double tmpUti, utilisation = -1; 	

		for (int i=0; i < getHostList().size(); i++)
		{			
			Host host = getHostList().get(i);
			//in case of migration, avoid the same host while in case of allocation: vm.getHost() returns null 
			//if the host can not accommodate the vm INCLUDING future migrations if there are any
			if (host.equals(vm.getHost()) || !isHostSuitableToAllocate(host, vm))
			{				
				continue;
			}
			
			tmpUti = host.getMaxUtilisationInCore(vm);
			//tmpUti = host.getUtilisation();			
			
			if (tmpUti > utilisation)
			{
				utilisation = tmpUti;
				hostIndex =i;
			}
			//chooses the host with better availble CPU power if the utilisation is the same
			else if (tmpUti == utilisation && (host.getMaxAvailableMips() > getHostList().get(hostIndex).getAvailableMips() || host.getUtilisationPercentage() > getHostList().get(hostIndex).getUtilisationPercentage()))
			{
				utilisation = tmpUti;
				hostIndex =i;
			}
		}
		return hostIndex;				
	}
	
	public int getVmForCloudletMigration(int vmId, int userId)
	{
		int vmIndex = -1;
		Random r = new Random();
		Host host =  null;
		
		while(host == null)
		{
			vmIndex = r.nextInt() % getVmTable().size();
			if (vmIndex < 0)
				vmIndex = vmIndex * -1;
			
			host = getHost(vmIndex, userId);
			
			if (getHost(vmId, userId) != null && host != null)
				if(host.getId() == getHost(vmId, userId).getId())	//if chosen a vm that is allocated to the same failing host
					host = null;
		}		

		
		return vmIndex;
	} 
}