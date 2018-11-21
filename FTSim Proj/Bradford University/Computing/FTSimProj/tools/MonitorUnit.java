package southampton.ecs.desktopcloudsim.tools;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Cloudlet;


public class MonitorUnit {
	private static double simulationTime;
	private static double cloudletAvgExecTime;
	private static double cloudletMicroAvgExecTime;
	
	private static double availability;
	private static double utilisation;
	private static double powerConsumption;

	private static ArrayList<Cloudlet> finishedCloudletList;
	private static ArrayList<Vm> vmMigratedlist;
	private static ArrayList<Vm> vmDestroyedlist;
	
	private static int machineFailureCounter;
	private static int idleMachineNumber;


	public static void initialize()
	{		
		simulationTime = 0;
		cloudletAvgExecTime = 0;
		cloudletMicroAvgExecTime = 0;
		
		availability = 0;
		utilisation = 0;
		powerConsumption = 0;
		
		finishedCloudletList = new ArrayList<Cloudlet>();
		vmMigratedlist = new ArrayList<Vm>();
		vmDestroyedlist = new ArrayList<Vm>();
		
		machineFailureCounter = 0;
		idleMachineNumber = 0;
	}
	
	public static void setSimulationTime(double time)
	{
		simulationTime = time;
	}
	
	public static double getSimulationTime()
	{
		return simulationTime;
	}
	
	public static void setCloudletAvgExecTime(double time)
	{
		cloudletAvgExecTime = time;
	}
	
	public static double getCloudletAvgExecTime()
	{
		return cloudletAvgExecTime;
	}
	
	public static void setCloudletMicroAvgExecTime(double time)
	{
		cloudletMicroAvgExecTime = time;
	}
	
	public static double getCloudletMicroAvgExecTime()
	{
		return cloudletMicroAvgExecTime;
	}
	
	public static void setFinishedCloudletList(ArrayList<Cloudlet> cloudletList)
	{
		finishedCloudletList = cloudletList;
	}
	
	public static void addFinishedCloudlet(Cloudlet cloudlet)
	{
		finishedCloudletList.add(cloudlet);
	}
	
	public static ArrayList<Cloudlet> getFinishedCloudletList()
	{
		return finishedCloudletList;
	}
	
	public static void setAavailability(double aval)
	{
		availability = aval;
	}
	
	public static double getAvailability()
	{
		return availability;
	}
	
	public static void setUtilisation(double util)
	{
		utilisation = util;
	}
	
	public static double getUtilisation()
	{
		return utilisation;
	}
	
	public static void setPowerConsumption(double power)
	{
		powerConsumption = power;
	}
	
	public static double getPowerConsumption()
	{
		return powerConsumption;
	}
	
	public static void addMigratedVm(Vm vm)
	{
		vmMigratedlist.add(vm);
	}
	
	public static ArrayList<Vm> getVmMigratedlist()
	{
		return vmMigratedlist;
	}
	
	public static int getNumberOfMigratedVm()
	{
		return vmMigratedlist.size();
	}

	public static void addDestroyedVm(Vm vm)
	{
		vmDestroyedlist.add(vm);
	}
	
	public static void addMachineFailure()
	{
		machineFailureCounter++;
	}
	
	public static void removeMachineFailure()
	{
		machineFailureCounter--;
	}
	
	public static int getMachineFailure()
	{
		return machineFailureCounter;
	}
	
	public static void setIdleMachineNumber(int numebrOfMachines)
	{
		idleMachineNumber = numebrOfMachines;
	}
	
}
