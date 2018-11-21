package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * The power model of a Supermicro 6025B-TR+ (Intel Xeon processor 5160)
 * http://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071129-00016.html
 */
public class SuperMicro extends PowerModelSpecPower {

//191
	private final double[] power = {0, 202, 213, 223, 234, 245, 255, 265, 275, 284, 291};


	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}
