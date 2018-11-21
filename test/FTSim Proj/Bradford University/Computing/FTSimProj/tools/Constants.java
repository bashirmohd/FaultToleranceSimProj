package southampton.ecs.desktopcloudsim.tools;

public class Constants {
	public final static boolean ENABLE_OUTPUT = true;
	public final static boolean OUTPUT_CSV    = false;

	//the cost of chickpointnig 
	public static double chickpointingCost = .7;
	
	
	public static boolean printingFlag = false;
	public static String outputFileName = "test";
	
	
	public final static int vmGreedy = 1;
	public final static int vmRoundRobin = 2;
	public final static int vmFCFS = 3;
	public final static int vmRandom = 4;
	public final static int vmReplication = 5;
	public final static int vmPwrDC = 6;

	public static String getVmAllocationString(int vmAllocationFlag)
	{
		String vmAllocationString;
		switch (vmAllocationFlag)
		{
		case Constants.vmGreedy:
			vmAllocationString = "greedy";
			break;
		case Constants.vmRoundRobin:
			vmAllocationString = "roundRobin";
			break;
		case Constants.vmFCFS:
			vmAllocationString = "FCFS";
			break;
		case Constants.vmRandom:
			vmAllocationString = "random"; 
			break;
		case Constants.vmReplication:
			vmAllocationString = "replication"; 
			break;
		case Constants.vmPwrDC:
			vmAllocationString = "PwrDC"; 
			break;						
		default:
			vmAllocationString = "unknown"; 
				break;
		}
		return vmAllocationString;
	}
}
