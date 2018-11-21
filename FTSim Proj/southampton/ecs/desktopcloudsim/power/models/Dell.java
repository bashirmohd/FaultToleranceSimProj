package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * The power model of a Dell PowerEdge 2950 III (Intel Xeon E5440)
 * http://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071128-00013.html
 */
public class Dell extends PowerModelSpecPower {

//157
	private final double[] power = {0, 173, 189, 204, 217, 230, 243, 253, 262, 270, 276};


	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}
