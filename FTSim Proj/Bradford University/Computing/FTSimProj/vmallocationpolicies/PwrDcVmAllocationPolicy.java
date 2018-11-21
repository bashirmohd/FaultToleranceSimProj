package southampton.ecs.desktopcloudsim.vmallocationpolicies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

import southampton.ecs.desktopcloudsim.tools.Calculation;

public class PwrDcVmAllocationPolicy extends VmAllocationPolicy {

	/** The vm table. */
	//the string represents the vm.getUid
	private Map<String, Host> vmTable;

	private Map<String, Host> vmReplicaTable;
	private Map<Integer, Vm> vmMapTable;	//primary id to the replica
	
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
	public PwrDcVmAllocationPolicy(List<? extends Host> list) {
		super(list);

		setFreePes(new ArrayList<Integer>());
		for (Host host : getHostList()) {
			getFreePes().add(host.getNumberOfPes());

		}

		setVmTable(new HashMap<String, Host>());
		setUsedPes(new HashMap<String, Integer>());
	}
	
	private int getHostindex_MaximumUtilisation(Vm vm) 
	{
		int hostIndex = -1;
		int vmHostIdx;
		
		double tmpUti, utilisation = -1; 	
		
		if (vm.getHost() == null)		//this happens when a vm restart event is called
		{
			vmHostIdx = -1;
			//Log.printLine(CloudSim.clock() + " [getHostindex_MaximumUtilisation] : vm.getHost() == null for the vm# " + vm.getId() + ", however, getHostIndexOfVm(vm.getId() = " + getHostIndexOfVm(vm.getId()));
			//System.exit(0);
		}
		else 
			vmHostIdx = vm.getHost().getId();

		for (int i=0; i < getHostList().size(); i++)
		{			
			Host host = getHostList().get(i);
						
			if (host.equals(vm.getHost()) || !isHostSuitableToAllocate(host, vm))
			{				
				continue;
			}
			/*
			//host.getId() == vmHostIdx
			if (host.equals(vm.getHost()))
			{	
				//if(host.getId() != vmHostIdx)	Log.printLine(" host id " + host.getId() + " is equal to " + vm.getHost().getId());
				continue;
			}
			if (!host.isSuitableForVm(vm))
				continue;
			/*
			if (!isHostSuitableToAllocate(host, vm))
			{				
				continue;
			}/**/
			
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
	

	/**
	 * chooses host with least utilisation
	 * @param vm
	 * @return
	 */
	private int getHostindex_MinimumUtilisation(Vm vm) 
	{
			double tmpUti, utilisation = Double.MAX_VALUE;
			int hostIdx = -1;		//chosen host id to accomodate the vm
			
			for (int i=0; i < getHostList().size(); i++)
			{			
				Host host = getHostList().get(i);
				if (host.equals(vm.getHost()) || !isHostSuitableToAllocate(host, vm))
				{				
					continue;
				}
				
				tmpUti = host.getMinUtilisationInCore(vm);

				if (tmpUti < utilisation)
				{
					utilisation = tmpUti;
					hostIdx =i;
				}
				//chooses the host with better availble CPU power if the utilisation is the same
				else if (tmpUti == utilisation && host.getMaxAvailableMips() > getHostList().get(hostIdx).getAvailableMips())
				{
					utilisation = tmpUti;
					hostIdx =i;
				}
			}
			return hostIdx;	
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
		int idx = getHostIndexOfVm(vm.getId());
				
		if ( idx == -1)				// no host is allocated to the given vm
		{
			return;
		}

		try
		{
			//Log.printLine("[ReplicationVmAllocationPolicy.deallocateHostForVm]: vm# " + vm.getId() + " is allocated to (idx) " + idx);
			
			int pes = getUsedPes().remove(vm.getUid());
			getVmTable().remove(vm.getUid());			//remove the given vm from the table ... if it is there
			
			getHostList().get(idx).vmDestroy(vm);
			getFreePes().set(idx, getFreePes().get(idx) + pes);
			
			Log.printLine(CloudSim.clock() + ": " + ": vm# " + vm.getId() + " is  destroyed");
			
			/*
			 * just for checking if the vm is actually removed ... 
			 */
			if (getHostIndexOfVm(vm.getId()) != -1)
			{
				System.out.println(CloudSim.clock() + "[ReplicationVmAllocationPolicy.deallocateHostForVm]: vm#  " + vm.getId() + " could not be destroy .... ");
				//System.exit(0);
			}
			
		}catch (NullPointerException e)
		{
			String msg = "ERROR:- [ReplicationVmAllocationPolicy.deallocateHostForVm]: vm# " + vm.getId() + " **--** host id: " + getHostList().get(idx).getId() + " returns NullPointer Exception"; 
			Log.printLine(msg);
			System.out.println(msg);
			System.out.println("vm.getUid() " + vm.getUid());
			

			int x = getUsedPes().remove(vm.getUid());
			System.out.println("getUsedPes().remove(vm.getUid()) is " + getUsedPes().remove(vm.getUid()));

			System.out.println("vm.getUid() " + vm.getUid());
			
			System.exit(0);
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
		
		if ((vm.getMips() > host.getMaxAvailableMips()) || !host.isSuitableForVm(vm) || vm.getMips() > host.getAvailableMIPS())
		{
			result = false;
		}

		if (host.getRamProvisioner().getAvailableRam() < vm.getRam()) {
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
		return getHostindex_MinimumUtilisation(vm);				
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
	
	private int getHostIndexForReplicationVm(Vm vm)
	{
		return getHostindex_MaximumUtilisation(vm);
	}
	
	/**
	 * returns ture if there is a copy of the given vm 
	 * @param vm
	 * @return
	 */
	private boolean checkVmReplication(Vm vm)
	{
		if (getHostIndexOfVm(-1 * vm.getId()) == -1)
			return false;
		else
			return true;
	}
	private int getHostIndexOfVm(int vmId)
	{
		int idx=0;
		for(Host host: getHostList())
		{
			for (Vm tmpVm: host.getVmList())
			{
				if (vmId == tmpVm.getId() )
					return idx;
			}
			idx ++;
		}		
		return -1;
	}
	
	public boolean replicate(Vm vm)
	{
		boolean ret = false;
		int hostIndex = getHostIndexForReplicationVm(vm);
		//add line to double check if there is already another copy o the VM ...
		if (hostIndex >= 0)
		{
			Host host = getHostList().get(hostIndex);
			
			//replica vm id = -1 * id of primary vm
			Vm vmcpyVM = new Vm(-1 * vm.getId(), vm.getUserId(), vm.getMips(), vm.getNumberOfPes(), vm.getRam() , vm.getBw(), vm.getSize(), vm.getVmm(), vm.getCloudletScheduler());
			
			if (host.vmCreate(vmcpyVM)) { // if vm has been succesfully created in the host
				//getVmTable().put(vm.getUid(), host);
	
				int requiredPes = vmcpyVM.getNumberOfPes();
				getUsedPes().put(vmcpyVM.getUid(), requiredPes);
				getFreePes().set(hostIndex, getFreePes().get(hostIndex) - requiredPes);
	
				Log.printLine(CloudSim.clock() + "[replicat] vm# " + vmcpyVM.getId() + " been created to host id " + host.getId());
				ret = true;
			}
		}/**/
		else
		{
			String msg = Calculation.round(CloudSim.clock()/3600) + " : [replicate] no host is available to replicate vm# = " + vm.getId() + " due to the high failure rate ";
			Log.printLine(msg);
			//System.out.println(msg);			
		}
		return ret;		
	}
	
	private void printComputingCabailitiyes(Vm vm)
	{
		double utilisation;
		System.out.println("\n:printing vm details vm id " + vm.getId() +" vm.getMips() = " + vm.getMips() + " vm.getCurrentRequestedTotalMips() " + vm.getCurrentRequestedTotalMips() 
				+ " vm.getRam()" + vm.getRam() + " vm.getCurrentRequestedRam() = " + vm.getCurrentRequestedRam());
		//System.exit(0);
		
		System.out.println("(printing hosts getHostList().size() : " + getHostList().size());
		
		int hostCounter =0;
		for(Host host: getHostList())
		{
			utilisation = host.getUtilisationPercentage();
			if (host.isFailed())
			{
				hostCounter++;
				continue;
			}
			if(host.getVmScheduler().getPeCapacity()>= vm.getMips() && host.getVmScheduler().getAvailableMips()>= vm.getMips() 
					&& (host.getRamProvisioner().getRam() - host.getRamProvisioner().getUsedRam()) >= vm.getRam())
				System.out.println("host id " + host.getId() + " suitable ram & mips");
			
			if (host.isSuitableForVm(vm) && !host.isFailed())
				System.out.println("host id " + host.getId());
		}
		System.out.println("hostCounter " + hostCounter);
	}
	
	public boolean usingReplication(Vm vm)
	{
		boolean ret = false;		
		/*
		for(Host host: getHostList())
		{
			for (Vm tmpVm: host.getVmList())
			{
				if (-1 * vm.getId() == tmpVm.getId() ) //found a replica
					return true;
			}
		}/**/
		
		return true;
	}
}