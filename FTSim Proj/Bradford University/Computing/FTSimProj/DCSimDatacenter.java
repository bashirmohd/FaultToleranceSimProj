package southampton.ecs.desktopcloudsim;
import java.util.List;

import org.cloudbus.cloudsim.*;

public class DCSimDatacenter extends Datacenter{

	public DCSimDatacenter(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList,
			double schedulingInterval) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);	
		
	}
}
