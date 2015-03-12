package org.starfishrespect.myconsumption.server.repositories;

import org.starfishrespect.myconsumption.server.entities.SensorDataset;
import org.starfishrespect.myconsumption.server.exception.DaoException;

import java.util.Date;
import java.util.List;

/**
 * Created by thibaud on 12.03.15.
 */
public interface ValuesRepositoryCustom {

    public void setSensor(String sensor);

    public void init() throws DaoException;

    public List<SensorDataset> getSensor(Date startTime, Date endTime) throws DaoException;
}
