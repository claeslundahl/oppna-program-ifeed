package se.vgregion.ifeed.scheduler;

import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;

public class iFeedScheduledJob implements MessageListener {

	@Override
	public void receive(Message message) {
		System.out.println("Scheduled job triggered");
	}
}
