package com.vertonur.dao.api;

import java.util.List;

import com.vertonur.pojo.Moderator;

public interface ModeratorDAO extends GenericDAO<Moderator, Integer> {
	Integer saveModerator(Moderator moderator);

	void updateModerator(Moderator moderator);

	Moderator getModeratorById(int moderatorId);

	Moderator getModeratorByName(String name);

	/**
	 * Get moderators with ascesding order of digesting num
	 * 
	 * @return
	 */
	List<Moderator> getModerators();

	/**
	 * Get moderators with ascending order of digesting num,with digesting num
	 * that less than the specified digest num
	 * 
	 * @param digestNum
	 * @return
	 */
	List<Moderator> getAvailableModerators(int digestNum);

	long getModeratorNum();
}
