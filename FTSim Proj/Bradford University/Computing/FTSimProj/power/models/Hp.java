package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * The power model of an HP Proliant DL160 G5 (3.0 GHz, Intel Xeon processor E5450)
 * http://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071205-00023.html
 */
public class Hp extends PowerModelSpecPower {

//160
	private final double[] power = {0, 174, 189, 203, 215, 227, 238, 247, 256, 264, 269};


	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}
