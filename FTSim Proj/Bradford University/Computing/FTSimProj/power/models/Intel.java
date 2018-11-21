package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModelSpecPower;

/**
 * The power model of an Intel Corp. Intel Platform SE7520AF2 Server Board (3.6 GHz/1M L2 Intel Xeon processor)
 * http://www.spec.org/power_ssj2008/results/res2007q4/power_ssj2008-20071129-00015.html
 */
public class Intel extends PowerModelSpecPower {

//159
	private final double[] power = {0, 170, 181, 194, 207, 225, 241, 288, 263, 308, 336};


	@Override
	protected double getPowerData(int index) {
		return power[index];
	}

}
