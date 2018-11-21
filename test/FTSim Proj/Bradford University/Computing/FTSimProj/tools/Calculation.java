package southampton.ecs.desktopcloudsim.tools;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;

public class Calculation {

	public static double calculateAverageTime(List<Cloudlet> cloudletList)
	{
		double totalExecutionTime = 0;
		Cloudlet cloudlet;
		int SuccessCloudletNumber = 0;
		double ret = 0;

		for(int index=0; index<cloudletList.size(); index++ )
		{
			cloudlet = cloudletList.get(index);
			//System.out.println("cloudlet.getStatus(): " + cloudlet.getStatus());
			if (cloudlet.getStatus() == Cloudlet.SUCCESS)
			{
				totalExecutionTime = totalExecutionTime + cloudlet.getActualCPUTime();
				SuccessCloudletNumber++;
			}
		}
		if ( SuccessCloudletNumber > 0)
			ret = round(totalExecutionTime/SuccessCloudletNumber);
		return ret;
	}
	
	public static double calculateAverageWaitingTime(List<Cloudlet> cloudletList)
	{
		double totalWaitingTime = 0;
		int SuccessCloudletNumber = 0;
		double ret = 0;

		for(Cloudlet cloudlet: cloudletList)
		{
			if (cloudlet.getStatus() == Cloudlet.SUCCESS)
			{
				totalWaitingTime += cloudlet.getWaitingTime();
				SuccessCloudletNumber++;
			}
		}
		if ( SuccessCloudletNumber > 0)
			ret = round(totalWaitingTime/SuccessCloudletNumber);
		return ret;
	}
	
	public static double calculateMicroAverageTime(List<Cloudlet> cloudletList)
	{
		List<Cloudlet> migratedCloudletList = new ArrayList<Cloudlet> ();
		
		for(Vm vm: MonitorUnit.getVmMigratedlist())
		{
			for(Cloudlet cloudlet: cloudletList)
			{
				if (cloudlet.getVmId() == vm.getId())
					migratedCloudletList.add(cloudlet);
			}			
		}
		return calculateAverageTime(migratedCloudletList);
	}

	public static double round(double number)
	{
		return (double) (Math.round(number * 100))/100;
	}
	
	public static int calculateCloudletSuccessCounter(List<Cloudlet> cloudletList)
	{
		int SuccessCloudletCounter = 0;

		for(Cloudlet cloudlet: cloudletList)
		{
			if (cloudlet.getStatus() == Cloudlet.SUCCESS)
			{
				SuccessCloudletCounter++;
			}
		}
		return SuccessCloudletCounter;
	}
	
	public static int calculateCloudletFailedCounter(List<Cloudlet> cloudletList) 
	{
		int failedCounter = 0;
		int cloudletsNumber = cloudletList.size();
		Cloudlet cloudlet;
		
		for (int i = 0; i < cloudletsNumber; i++) {
			cloudlet = cloudletList.get(i);

			if (cloudlet.getCloudletStatus() != Cloudlet.SUCCESS)
			{
				failedCounter++;
				
			}
		}

		return failedCounter;
	}
	
	public static double getCloudExecutionTime(List<Cloudlet> cloudletList)
	{
		int cloudletsNumber = cloudletList.size();
		Cloudlet cloudlet;

		
		for (int i = 0; i < cloudletsNumber; i++) {
			cloudlet = cloudletList.get(i);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS)
				return cloudlet.getActualCPUTime();
		}

		return -1;
	}

	public static double timeDifference(double avgMicroTime, double cloudletExecutionTime)
	{
		return round((avgMicroTime - cloudletExecutionTime)/cloudletExecutionTime) * 100;
	}
	
	public static double calculateAverageUtilisation(List <Host> hostlist)
	{
		double utililsation = 0;
		
		for(Host host: hostlist)
		{
			utililsation = utililsation + host.getUtilisationPercentage();
		}
		utililsation = utililsation / hostlist.size();
		
		return round(utililsation);
	}
	/**
	 * calculates the total Kilowatt-hour 
	 * @param hostlist
	 * @return
	 */
	public static double calculatePowerConsumption(List <Host> hostlist)
	{
		double powerConumption = 0;
		
		for(Host host: hostlist)
		{
			powerConumption = powerConumption + host.getEnergyConsumption();
		}
		
		return round(powerConumption/1000);
	}
	
	public static double calculateAverageAvailability(List <Host> hostlist)
	{
		double availability =0;
		for(Host host: hostlist)
		{
			availability = availability + host.getAvailabilityPercentage();
		}
		availability = availability / hostlist.size();
		
		return round(availability);
	}
	
	public static int calculateNumberOfIdleMachines(List<Host> tmpHostList)
	{
		int idleMachineCounter = 0;
		
		for(Host host: tmpHostList)
		{
			if(host.getUtilisationPercentage() == 0)
				idleMachineCounter++;
		}
		
		return idleMachineCounter;
	}

	public static double calculateFailureRate(int numberOfFailedMachines, int numberOfmachines)
	{
		return round(numberOfFailedMachines/numberOfmachines) * 100;
	}
	
	public static double cloudletFailureRatio(int cloudletFailedCounter, int cloudletsNumber)
	{
		return round((double) 100 * cloudletFailedCounter/cloudletsNumber);
	}
	
	public static double cloudletSucessRatio(int cloudletSucceedCounter, int cloudletsNumber)
	{
		return round((double) cloudletSucceedCounter/cloudletsNumber);
	}
	
}
