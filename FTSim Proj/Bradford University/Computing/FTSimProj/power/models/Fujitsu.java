package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * The power model of a Fujitsu Siemens PRIMERGY TX150 S5 (Intel Xeon 3070)
 * http://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071128-00005.html
 */
public class Fujitsu extends PowerModelSpecPower {

//90.8
	private final double[] power = {0, 94.2, 97.6, 102, 106, 111, 116, 120, 124, 129, 131};


	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}
