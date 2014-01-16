/**
 * 
 */
package com.vertonur.dao.api;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.vertonur.pojo.Info;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;

/**
 * @author Vertonur
 * 
 */
public interface InfoDAO {

	List<Info> getCategoryAnnouncements(int categoryId);

	List<Info> getDeptAnnouncements(int departmentId);

	List<Info> getSystemAnnouncements();

	List<Info> getInfosByUser(User user, int start, int offset);

	List<Info> getInfosByTitle(String title);

	List<Info> getInfosByCategory(int categoryId, int start, int offset);

	List<Info> getNewInfosByCategory(int categoryId, int inHours,
			int startPoint, int infoEntries);

	List<Info> getRecentInfosByCategory(int categoryId, Date specifiedDate,
			int recentInfosEntries);

	List<Info> getHottestInfosByCategory(int categoryId,
			int hottestInfosDef, int hottestInfosEntries);

	/**
	 * Get the info with the specified id and the value of deprecated and
	 * pending of state of info is false
	 * 
	 * @param id
	 * @return
	 */
	Info getInfoById(int id);

	/**
	 * Get the pending info with the specified id
	 * 
	 * @param id
	 * @return
	 */
	Info getPendingInfoById(int id);

	/**
	 * Get the latest info from the specified category,with the updated time
	 * less than the passed in date and the date parameter will not be used as a
	 * condition if it's null
	 */
	public Info getLatestInfo(int categoryId, Date date);

	/**
	 * Get the first info from the system,with the created time greater than the
	 * passed in date and the date parameter will not be used as a condition if
	 * it's null
	 */
	public Info getFirstInfo(Date date);

	long getAllInfoNum();

	long getInfoNumByCategory(int categoryId);

	long getInfoNumInCategories();

	long getInfoNumByCreator(User creator);

	Integer saveInfo(Info info);

	void updateInfo(Info info);

	void deleteInfo(Info info);

	void saveUserReadInfo(UserReadInfo userReadInfo);

	public UserReadInfo getReadInfoByUserAndInfo(User reader, Info readInfo);

	public Set<UserReadInfo> getSpecifiedUserReadInfos(User reader);

	public Set<UserReadInfo> getReadInfosByUserAndInfos(User reader,
			List<Info> infos);

	public Info getFirstInfo();
}
