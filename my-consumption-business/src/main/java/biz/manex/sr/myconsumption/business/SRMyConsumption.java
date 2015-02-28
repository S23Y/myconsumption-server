package biz.manex.sr.myconsumption.business;


import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * entry point for the data retrieve service
 */
public class SRMyConsumption {

    private static AbstractApplicationContext ctx;

    public static void main(String args[]) {
        ctx = new GenericXmlApplicationContext("biz/manex/sr/myconsumption/business/myconsumption-business.xml");
        ctx.registerShutdownHook();
        SRMyConsumption srMyConsumption = new SRMyConsumption();
    }

    private SRMyConsumption() {
        RetrieveScheduler scheduler = (RetrieveScheduler) ctx.getBean("retrieveScheduler");
        scheduler.schedule();
    }


}
