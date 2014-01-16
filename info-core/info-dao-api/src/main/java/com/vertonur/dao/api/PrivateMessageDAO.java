package com.vertonur.dao.api;

import java.util.List;
import java.util.Set;

import com.vertonur.pojo.PrivateMessage;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadPrivateMessage;

public interface PrivateMessageDAO extends GenericDAO<PrivateMessage, Integer> {
	Integer savePrivateMsg(PrivateMessage pm);

	PrivateMessage getPrivateMessageById(int pmId);

	List<PrivateMessage> getPrivateMsgsByAuthor(User author, int start,
			int offset);

	List<PrivateMessage> getPrivateMsgsByReceiver(User receiver, int start,
			int offset);

	List<PrivateMessage> getNewPrivateMsgsByReceiver(User receiver, int days,
			int entries);

	long getPrivateMsgNumByReceiver(User receiver);

	long getPrivateMsgNumByAuthor(User author);

	void saveUserReadPriateMsg(UserReadPrivateMessage userReadPm);

	Set<UserReadPrivateMessage> getReadPrivateMsgsByReceiver(User receiver);

	UserReadPrivateMessage getReadPrivateMsgByUserAndPrivateMsg(User user,
			PrivateMessage pm);
}
