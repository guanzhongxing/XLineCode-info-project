package com.vertonur.util;

import java.util.List;
import java.util.Set;

import com.vertonur.pojo.Info;
import com.vertonur.pojo.UserReadInfo;

public class ServiceUtil {

	public static void markInfoStatus(List<Info> infos,
			Set<UserReadInfo> readInfos, int hotInfoDef) {
		for (Info info : infos) {
			if (info.getStatistician().getCommentNum() >= hotInfoDef)
				info.setHot(true);
			for (UserReadInfo readInfo : readInfos)
				if (info.getId() == readInfo.getReadInfo().getId()) {
					info.setNewToUser(false);
					info.setReadToUser(true);
				}
		}
	}
}
