/**
 * 
 */
package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.ModerationLog;
import com.vertonur.pojo.ModerationLog.ModerationStatus;

/**
 * @author Vertonur
 * 
 */
public interface ModerationLogDAO extends GenericDAO<ModerationLog, Integer> {

	ModerationLog getLog(int logId);

	int saveLog(ModerationLog log);

	void updateLog(ModerationLog log);

	/**
	 * Get logs according to parameters passed in,if categoryId is null ,it's
	 * ignored for the query conditions
	 * 
	 * @param categoryId
	 * @param start
	 * @param offset
	 * @param moderatorId
	 * @param statuses
	 * @return
	 */
	List<ModerationLog> getLogs(Integer categoryId, int start, int offset,
			int moderatorId, ModerationStatus... statuses);

	/**
	 * Get num of moderation log with the specified status
	 * 
	 * @param status
	 * @return
	 */
	public long getLogNum(int categoryId, int moderatorId,
			ModerationStatus... statuses);

	/**
	 * Get num of moderation log with the specified status
	 * 
	 * @param status
	 * @return
	 */
	public long getLogNum(int moderatorId, ModerationStatus... statuses);

	/**
	 * Get num of moderation log with the pending status
	 * 
	 * @param status
	 * @return
	 */
	public long getPendingLogNum(int categoryId);

	List<ModerationLog> getPendingLogs(int start, int offset);
}
