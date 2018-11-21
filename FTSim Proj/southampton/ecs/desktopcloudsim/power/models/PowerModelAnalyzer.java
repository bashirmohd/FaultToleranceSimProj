package southampton.ecs.desktopcloudsim.power.models;

import org.cloudbus.cloudsim.power.models.PowerModel;

public class PowerModelAnalyzer {
	public final static int Colfax = 0;
	public final static int Dell = 1;
	public final static int Fujitsu = 2;
	public final static int Hp = 3;
	public final static int Intel = 4;
	public final static int SuperMicro = 5;
	
	
	public static PowerModel getPowerModel(int machineType)
	{
		PowerModel powerModel = null;
		
		switch (machineType)
		{
			case Colfax: 
				powerModel = new Colfax();
			break;
			case Dell:
				powerModel = new Dell();
			break;
			case Fujitsu:
				powerModel = new Fujitsu();
			break;
			case Hp:
				powerModel = new Hp();
			break;
			case Intel:
				powerModel = new Intel();
			break;
			case SuperMicro:
				powerModel = new SuperMicro();
			break;
		}
		
		return powerModel;
	}
}
