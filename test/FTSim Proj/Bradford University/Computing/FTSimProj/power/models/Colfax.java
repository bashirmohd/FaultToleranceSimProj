package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * The power model of a Colfax International CX2266-N2
 * http://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071129-00018.html
 */
public class Colfax extends PowerModelSpecPower {

//164
	private final double[] power = {0, 204, 225, 234, 242, 248, 254, 260, 276, 272, 276};


	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}
