package com.vertonur.dao.api;

import com.vertonur.pojo.statistician.SystemStatistician;

public interface SystemStatisticianDAO extends
		GenericDAO<SystemStatistician, Integer> {

	SystemStatistician getSystemStatistician();

	void updateSystemStatistician(SystemStatistician statistician);

	void saveSystemStatistician(SystemStatistician statistician);
}
