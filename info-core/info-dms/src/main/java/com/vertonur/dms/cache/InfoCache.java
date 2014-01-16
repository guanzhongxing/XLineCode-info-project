package com.vertonur.dms.cache;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanUtils;

import com.vertonur.pojo.Info;
import com.vertonur.pojo.User;
import com.vertonur.pojo.UserReadInfo;
import com.vertonur.pojo.config.InfoConfig;

/**
 * Cache that used to store data of infos and is sorted by the nature ordering
 * of Integer automatically
 * 
 * @author 210002000487
 * 
 */
@Deprecated
public class InfoCache {

	private InfoConfig infoConfig;

	private Map<Integer, TreeMap<Integer, Info>> categoryInfos;
	private long infoNum = 0;
	private Map<Integer, Long> categoryInfoNums;

	private int categoryInfoCacheNum;

	public InfoCache(InfoConfig infoConfig) {
		this.infoConfig = infoConfig;
		categoryInfos = new HashMap<Integer, TreeMap<Integer, Info>>();
		setCategoryInfoCacheNum(infoConfig.getCategoryInfoCacheNum());
		categoryInfoNums = new HashMap<Integer, Long>();
	}

	public void updateInfoIfExist(Info info) {
		Map<Integer, Info> infos = categoryInfos
				.get(info.getCategory().getId());
		int id = info.getId();
		if (infos.containsKey(id))
			infos.put(id, info);
	}

	/**
	 * Init an info cache for a new added category
	 * 
	 * @param categoryId
	 */
	public void initCategoryInfoCache(int categoryId) {
		TreeMap<Integer, Info> infos = new TreeMap<Integer, Info>();
		categoryInfos.put(categoryId, infos);
		categoryInfoNums.put(categoryId, new Long(0));
	}

	public void addCategoryInfo(Info info) {
		addOrUpdateCategoryInfo(info, false);
	}

	public void addCategoryInfo(Info info, boolean ignoreOrdering) {
		addOrUpdateCategoryInfo(info, ignoreOrdering);
		increaseInfoNum();
		increaseCategoryInfoNum(info.getCategory().getId());
	}

	/**
	 * Used by the system to init cache or to add a new info or to update an
	 * info. Note that only the latest ones will be added to this cache, others
	 * will simply be ignored
	 * 
	 * @param info
	 * @param ignoreOrdering
	 *            used to turn off the checking that compares the lowest id of
	 *            cached infos with the passed info, which will be useful when
	 *            initializing the cache during system setup.
	 */
	private void addOrUpdateCategoryInfo(Info info, boolean ignoreOrdering) {
		int infoId = info.getId();
		int categoryId = info.getCategory().getId();
		TreeMap<Integer, Info> infos = categoryInfos.get(categoryId);
		if (infos == null) {
			infos = new TreeMap<Integer, Info>();
			categoryInfos.put(categoryId, infos);
		}

		if (infos.isEmpty())
			infos.put(infoId, info);
		else {
			Integer lowestId = infos.firstKey();
			if (infoId >= lowestId || ignoreOrdering) {
				if (!infos.containsKey(infoId)) {
					int size = infos.size();
					if (size >= getCategoryInfoCacheNum()) {
						infos.pollFirstEntry();
					}
				}

				infos.put(infoId, info);
			}
		}
	}

	/**
	 * This method should only be used during cache setup.
	 * 
	 * @param infos
	 */
	public void addCategoryInfos(List<Info> infos) {
		for (Info info : infos)
			addCategoryInfo(info, true);
	}

	public List<Info> getInfosByCategory(User user, int categoryId, int start,
			Set<UserReadInfo> readInfos) throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		List<Info> infoCopyList = new ArrayList<Info>();
		TreeMap<Integer, Info> infos = categoryInfos.get(categoryId);
		if (infos != null) {
			NavigableMap<Integer, Info> descInfos = infos.descendingMap();
			if (descInfos.size() > start) {
				int offset = infoConfig.getInfoPgnOffset();
				List<Info> infoList = new ArrayList<Info>();
				Iterator<Integer> iterator = descInfos.keySet().iterator();
				int counter = 0;
				int marker = 0;
				while (iterator.hasNext() && counter < offset) {
					Integer id = iterator.next();
					if (marker >= start) {
						infoList.add(descInfos.get(id));
						counter++;
					}
					marker++;
				}

				for (Info info : infoList) {
					// Clone infos into another list to avoid reference
					// modification
					Info clonedInfo = (Info) BeanUtils.cloneBean(info);
					infoCopyList.add(clonedInfo);
				}
//				ServiceUtil.markInfoStatus(infoCopyList, readInfos);
			}
		}

		return infoCopyList;
	}

	public Info getInfoById(int categoryId, int infoId) {
		TreeMap<Integer, Info> infos = categoryInfos.get(categoryId);
		if (infos == null) {
			infos = new TreeMap<Integer, Info>();
			categoryInfos.put(categoryId, infos);

			return null;
		}
		return infos.get(infoId);
	}

	public void removeInfo(int categoryId, int infoId) {
		Map<Integer, Info> infos = categoryInfos.get(categoryId);
		if (infos.containsKey(infoId))
			infos.remove(infoId);

		decreaseInfoNum();
		decreaseCategoryInfoNum(categoryId);
	}

	public long getInfoNum() {
		return infoNum;
	}

	public void setInfoNum(long infoNum) {
		this.infoNum = infoNum;
	}

	private void increaseInfoNum() {
		infoNum++;
	}

	private void decreaseInfoNum() {
		infoNum--;
	}

	public long getCategoryInfoNum(int categoryId) {
		return categoryInfoNums.get(categoryId);
	}

	public void setCategoryInfoNum(int categoryId, long categoryInfoNum) {
		categoryInfoNums.put(categoryId, categoryInfoNum);
	}

	private void increaseCategoryInfoNum(int categoryId) {
		Long num = categoryInfoNums.get(categoryId);
		if (num == null)
			num = new Long(0);
		num++;
		categoryInfoNums.put(categoryId, num);
	}

	private void decreaseCategoryInfoNum(int categoryId) {
		long num = categoryInfoNums.get(categoryId);
		num--;
		categoryInfoNums.put(categoryId, num);
	}

	public int getCategoryInfoCacheNum() {
		return categoryInfoCacheNum;
	}

	public void setCategoryInfoCacheNum(int categoryInfoCacheNum) {
		this.categoryInfoCacheNum = categoryInfoCacheNum;
	}

	public void setInfoConfig(InfoConfig infoConfig) {
		this.infoConfig = infoConfig;
	}

	// TODO: add a robot to check periodly to make an info out of date, and mark
	// its status of new to false
}
