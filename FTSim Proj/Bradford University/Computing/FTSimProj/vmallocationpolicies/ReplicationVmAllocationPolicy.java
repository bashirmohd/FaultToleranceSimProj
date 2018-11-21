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

public class ReplicationVmAllocationPolicy extends VmAllocationPolicy {

	/** The vm table. */
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
	public ReplicationVmAllocationPolicy(List<? extends Host> list) {
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
		
		double tmpUti, utilisation = -1; 	


		for (int i=0; i < getHostList().size(); i++)
		{			
			Host host = getHostList().get(i);
						
			if (host.equals(vm.getHost()) || !isHostSuitableToAllocate(host, vm))
			{				
				continue;
			}
			
			tmpUti = host.getMaxUtilisationInCore(vm);
			
			if (tmpUti > utilisation)
			{
				utilisation = tmpUti;
				hostIndex =i;
			}
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
	
	public boolean allocateHostForVm(Vm vm) {
		int requiredPes = vm.getNumberOfPes();
		boolean result = false;

		if (!getVmTable().containsKey(vm.getUid()))
		{ 		
			int hostIdx = getHostIndexForAllocation(vm);		


			Host host = new Host();
			if (hostIdx >= 0)
			{
				host = getHostList().get(hostIdx);
				result = host.vmCreate(vm);
			}
			
			if (result) { 
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
				
		if ( idx == -1)		
		{
			return;
		}
		
		try
		{
			int pes = -1;
			if (getUsedPes().containsKey(vm.getUid()))
				pes = getUsedPes().remove(vm.getUid());	
			
			getVmTable().remove(vm.getUid());
			
			getHostList().get(idx).vmDestroy(vm);	
			
			if(pes != -1)
				getFreePes().set(idx, getFreePes().get(idx) + pes);
			
			Log.printLine(CloudSim.clock() + ": " + ": vm# " + vm.getId() + " is  destroyed");
			
			
		}catch (NullPointerException e)
		{
			String msg = "ERROR:- [ReplicationVmAllocationPolicy.deallocateHostForVm]: vm# " + vm.getId() + " **--** host id: " + getHostList().get(idx).getId() + " returns NullPointer Exception"; 
			Log.printLine(msg);
			System.out.println(msg);
			
			if (getUsedPes() == null)
			{
				System.out.println("getUsedPes() == null\n exiting the simulation");
				System.exit(0);
			}
			
			getUsedPes().remove(vm.getUid());
			Log.printLine("getUsedPes().remove(vm.getUid());");
			
			Log.printLine("host id: " + getHostList().get(getHostIndexOfVm(vm.getId())).getId() + " if it is not -1 then that means it is not completely removed");
			
			System.out.println("exiting");
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
		}
		
		else if (host.vmCreate(vm)) { // if vm has been succesfully created in the host
			getVmTable().put(vm.getUid(), host);

			int requiredPes = vm.getNumberOfPes();

			getUsedPes().put(vm.getUid(), requiredPes);
			getFreePes().set(idx, getFreePes().get(idx) - requiredPes);

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

		List<? extends Vm> alloVmList = host.getVmList();
		List<? extends Vm> migVmList = host.getVmsMigratingIn();
		
		int resRam = 0;
		double resMips = 0;
		
		for (int i=0; i < alloVmList.size(); i++)
		{
			resRam = resRam + alloVmList.get(i).getRam();
			resMips = resMips + alloVmList.get(i).getMips();
		}

		for (int i=0; i < migVmList.size(); i++)
		{
			resRam = resRam + migVmList.get(i).getRam();
			resMips = resMips + migVmList.get(i).getMips();
		}	

		if ((host.getRam() - resRam < vm.getRam())
			|| (host.getTotalMips() - resMips < vm.getMips()))
			result = false;
		
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
				if(host.getId() == getHost(vmId, userId).getId())	
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
		if (hostIndex >= 0)
		{
			Host host = getHostList().get(hostIndex);
			
			Vm vmcpyVM = new Vm(-1 * vm.getId(), vm.getUserId(), vm.getMips(), vm.getNumberOfPes(), vm.getRam() , vm.getBw(), vm.getSize(), vm.getVmm(), vm.getCloudletScheduler());
			
			if (host.vmCreate(vmcpyVM)) { 
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
		}
		return ret;		
	}

	public boolean usingReplication(Vm vm)
	{
		boolean ret = false;		
		return true;
	}
	
	public int getHostIndexForMigration(Vm vm)
	{		
		int hostIndex = -1;
		int vmHostIdx;
		
		double tmpUti, utilisation = -1; 	
		
		if (vm.getHost() == null)		
		{
			vmHostIdx = -1;
		}
		else 
			vmHostIdx = vm.getHost().getId();

		for (int i=0; i < getHostList().size(); i++)
		{			
			Host host = getHostList().get(i);
						
			if (host.equals(vm.getHost()) || !isHostSuitableToAllocate(host, vm) || host.getUtilisationPercentage() == 0)
			{				
				continue;
			}
			
			tmpUti = host.getMaxUtilisationInCore(vm);
					
			
			if (tmpUti > utilisation)
			{
				utilisation = tmpUti;
				hostIndex =i;
			}
			
			else if (tmpUti == utilisation && (host.getMaxAvailableMips() > getHostList().get(hostIndex).getAvailableMips() || host.getUtilisationPercentage() > getHostList().get(hostIndex).getUtilisationPercentage()))
			{
				utilisation = tmpUti;
				hostIndex =i;
			}
		}
		return hostIndex;	
	}
}