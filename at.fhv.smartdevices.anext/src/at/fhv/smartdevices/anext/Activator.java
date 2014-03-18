package at.fhv.smartdevices.anext;

import org.osgi.framework.BundleContext;

import at.anext.os.firmware.devsupport.AbstractActivator;
import at.fhv.smartdevices.anext.controller.ANextMainExecutor;
import at.fhv.smartdevices.anext.controller.ANextSmartController;

public class Activator extends AbstractActivator {

	private static Activator plugin;
	private ANextSmartController smartController;
	private ANextMainExecutor mainExecutor;

	@Override
	protected void registerContent() {
//		Locale loc = Locale.getDefault();
//
//		NodeDesc nd = getNodeDesc(SmartDeviceConstants.NODE_IDENTIFIER);
//		if (nd != null) {
//			URL image = nd.getImagePath(loc,
//					SmartDeviceConstants.NODE_IDENTIFIER);
//			System.out.println(image);
//		}

	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;
		
		smartController = new ANextSmartController();
		mainExecutor = new ANextMainExecutor(smartController);
		mainExecutor.startExecution();

	}

	@Override
	public void stop(BundleContext context) throws Exception {
		mainExecutor.stopExecution();
		
		plugin = null;
		smartController = null;

		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
}
