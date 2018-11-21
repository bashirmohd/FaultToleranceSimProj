package southampton.ecs.examples;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.models.PowerModelSpecPowerHpProLiantMl110G3PentiumD930;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import southampton.ecs.desktopcloudsim.vmallocationpolicies.FCFSVmAllocationPolicy;
import southampton.ecs.desktopcloudsim.vmallocationpolicies.GreedyVmAllocationPolicy;
import southampton.ecs.desktopcloudsim.vmallocationpolicies.RandomVmAllocationPolicy;
import southampton.ecs.desktopcloudsim.vmallocationpolicies.ReplicationVmAllocationPolicy;
import southampton.ecs.desktopcloudsim.vmallocationpolicies.RoundRobinVmAllocationPolicy;

import southampton.ecs.desktopcloudsim.tools.Calculation;
import southampton.ecs.desktopcloudsim.tools.Constants;


public class runDesktopCloudSim {
	private static List<Cloudlet> cloudletList;
	private static List<Vm> vmlist;
	private static int vmAllocationFlag;

	public static void main(String[] args) 
	{
		Log.printLine("Starting CloudSimExample3...");
		
		runVMmechanim(Constants.vmReplication);
		runVMmechanim(Constants.vmRoundRobin);

	}
	private static void runVMmechanim(int vmMechanism)
	{
		vmAllocationFlag = vmMechanism;
		System.out.println("running " + Constants.getVmAllocationString(vmAllocationFlag));
		run(Constants.getVmAllocationString(vmAllocationFlag));
	}
	private static void run(String outputFileName)
	{
		Constants.outputFileName = outputFileName; //without extension
		try {
			int num_user = 1;   
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = true;  

			CloudSim.init(num_user, calendar, trace_flag);
			
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
					
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			cloudletList = creatCloudLetList(brokerId);
			vmlist = createVmList(brokerId);
			
			broker.submitVmList(vmlist);
			broker.submitCloudletList(cloudletList);

			CloudSim.startSimulation();
			
			
			List<Cloudlet> cloudletSubmittedList = broker.getCloudletSubmittedList();


			CloudSim.stopSimulation();

			Log.printLine("printing cloudletSubmittedList:");
        	printCloudletList(cloudletSubmittedList);


			Log.printLine("average execution time for cloudletSubmittedList = " + Calculation.round(Calculation.calculateAverageTime(cloudletSubmittedList))
					+ ", cloudlet number = " + cloudletSubmittedList.size() + "\t number of failed cloudlet : " + Calculation.calculateCloudletFailedCounter(cloudletSubmittedList));			
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	private static List<Vm> createVmList(int brokerId)
	{
		List<Vm> vmTmpList = new ArrayList<Vm>();

		int totalVmNumber = 3;
		int vmid = 1;
		int mips = 1000;
		long size = 2 * 1000; //image size (MB)
		int ram = 1 * 512; //vm memory (MB)
		long bw = 300;
		int pesNumber = 1; //number of cpus
		String vmm = "Xen"; //VMM name

		for (; vmid <= totalVmNumber; vmid ++)
		{
			Vm vm = new Vm(vmid, brokerId, mips, pesNumber, 
					ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
			vmTmpList.add(vm);
		}

		
		return vmTmpList;
	}
	/**
	 * Creates the cloudlet list planet lab.
	 * 
	 * @param brokerId the broker id
	 * @param inputFolderName the input folder name
	 * @return the list
	 * @throws FileNotFoundException the file not found exception
	 */
	
	private static List<Cloudlet> creatCloudLetList(int brokerId)
	{
		List<Cloudlet> cloudletTmpList = new ArrayList<Cloudlet>();
	
		long length = 10 * 1000;
		int pesNumber = 1; //number of cpus
		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModel = new UtilizationModelFull();
		int id = 1;

		for(int eventCounter = 1; eventCounter <= 10; eventCounter++, id++)
		{
		
			Cloudlet cloudlet = new Cloudlet(id, length, pesNumber,
					fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
			
			cloudlet.setUserId(brokerId);
			cloudlet.setExecStartTime(10);
			
			cloudletTmpList.add(cloudlet);
		}

		return cloudletTmpList;
	}
	
	private static List<Host> createHostList()
	{

		//Host details
		int hostTotalNumber = 2;
		int mips = 4000;
		int hostId = 0;
		int ram = 36 * 512;; //host memory (MB)
		long storage = 60 * 1000; //host storage
		int bw = 20 * 300;
		List<Host> hostList = new ArrayList<Host>();
		
		for (; hostId < hostTotalNumber; hostId++)
		{
			List<Pe> peList = new ArrayList<Pe>();

			peList.add(new Pe(0, new PeProvisionerSimple(mips)));
			
			hostList.add(
	    			new Host(
	    				hostId,
	    				new RamProvisionerSimple(ram),
	    				new BwProvisionerSimple(bw),
	    				storage,
	    				peList,
	    				new VmSchedulerTimeShared(peList)
	    				, new PowerModelSpecPowerHpProLiantMl110G3PentiumD930()
	    			)
	    		); 
		}

		return hostList;
	}

	private static void issueFailures(Datacenter dc, int hostId, int failureTime)
	{
		dc.issueFailure(hostId, failureTime);
	}
	
	private static void issueFailures(Datacenter dc, int hostId, int failureTime, int aliveTime)
	{
		dc.issueFailure(hostId, failureTime, aliveTime);
	}
	/**
	 * create a list of hosts, can issue failure here
	 * @param name
	 * @return
	 */

	private static Datacenter createDatacenter(String name)
	{

		String arch = "x86";     
		String os = "Linux";         
		String vmm = "Xen";
		double time_zone = 10.0;        
		double cost = 3.0;              
		double costPerMem = 0.05;		
		double costPerStorage = 0.001;	
		double costPerBw = 0.0;			
		LinkedList<Storage> storageList = new LinkedList<Storage>();	

		List<Host> hostList = createHostList();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		Datacenter datacenter = null;
		try {
			switch (vmAllocationFlag)
			{
			case Constants.vmGreedy:
				datacenter = new Datacenter(name, characteristics, new GreedyVmAllocationPolicy(hostList), storageList, 0);
				break;
			case Constants.vmRoundRobin:
				datacenter = new Datacenter(name, characteristics, new RoundRobinVmAllocationPolicy(hostList), storageList, 0);
				break;
			case Constants.vmFCFS:
				datacenter = new Datacenter(name, characteristics, new FCFSVmAllocationPolicy(hostList), storageList, 0);
				break;
			case Constants.vmRandom:
				datacenter = new Datacenter(name, characteristics, new RandomVmAllocationPolicy(hostList), storageList, 0);
				break;
			case Constants.vmReplication:
				datacenter = new Datacenter(name, characteristics, new ReplicationVmAllocationPolicy(hostList), storageList, 0);
				break;
			default:
				System.out.print("ERROR: [runExperiment.createDatacenter] wrong vmAllocationFlag number ... \t System aborted");
				System.exit(0);
					break;
			}
			
			issueFailures(datacenter, 1, 5);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			Log.print(Cloudlet.getStatusString(cloudlet.getCloudletStatus()));

			/*
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS)			
				Log.print("SUCCESS");
			else if (cloudlet.getCloudletStatus() == Cloudlet.CANCELED || cloudlet.getCloudletStatus() == Cloudlet.FAILED)
				Log.print("FAILED");
			else if (cloudlet.getCloudletStatus() == Cloudlet.QUEUED)
				Log.print("QUEUED");
			else
				Log.print(cloudlet.getCloudletStatus());/**/


			Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			
		}

	}
		
	private static void printCloudletListForSpeceificVm(List<Cloudlet> list, int vmId) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			
			if (cloudlet.getVmId() != vmId && cloudlet.getVmId() != vmId * -1)
				continue;
			
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);
			Log.print(Cloudlet.getStatusString(cloudlet.getCloudletStatus()));

			/*
			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS)			
				Log.print("SUCCESS");
			else if (cloudlet.getCloudletStatus() == Cloudlet.CANCELED || cloudlet.getCloudletStatus() == Cloudlet.FAILED)
				Log.print("FAILED");
			else if (cloudlet.getCloudletStatus() == Cloudlet.QUEUED)
				Log.print("QUEUED");
			else
				Log.print(cloudlet.getCloudletStatus());/**/


			Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			
		}

	}
}
