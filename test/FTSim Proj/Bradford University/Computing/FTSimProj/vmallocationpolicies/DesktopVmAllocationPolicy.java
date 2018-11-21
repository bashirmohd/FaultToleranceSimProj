package southampton.ecs.desktopcloudsim.vmallocationpolicies;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicy;

public abstract class DesktopVmAllocationPolicy extends VmAllocationPolicy{
	
	public DesktopVmAllocationPolicy(List<? extends Host> list) {
		super(list);
		
	}

	public int getHostIndexForReplication(Vm vm)
	{
		return -1;
	}
}
