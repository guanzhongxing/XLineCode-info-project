package com.vertonur.dms;

import com.vertonur.dao.api.SystemStatisticianDAO;
import com.vertonur.pojo.statistician.SystemStatistician;

public class SystemService extends GenericService {

	private SystemStatisticianDAO statisticianDAO;

	protected SystemService() {
		statisticianDAO = manager.getSystemStatisticianDAO();
	}

	public SystemStatistician getSystemStatistician() {
		return statisticianDAO.getSystemStatistician();
	}

	public void saveSystemStatistician(SystemStatistician statistician) {
		statisticianDAO.saveSystemStatistician(statistician);
	}

	public void updateSystemStatistician(SystemStatistician statistician) {
		statisticianDAO.updateSystemStatistician(statistician);
	}
}
