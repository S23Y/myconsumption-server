package org.starfishrespect.myconsumption.server.business;


import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * entry point for the data retrieve service
 */
public class SRMyConsumption {

    private static AbstractApplicationContext ctx;

    public static void main(String args[]) {
        ctx = new GenericXmlApplicationContext("org/starfishrespect/myconsumption/server/business/myconsumption-business.xml");
        ctx.registerShutdownHook();
        SRMyConsumption srMyConsumption = new SRMyConsumption();
    }

    private SRMyConsumption() {
        RetrieveScheduler scheduler = (RetrieveScheduler) ctx.getBean("retrieveScheduler");
        scheduler.schedule();
    }


}
