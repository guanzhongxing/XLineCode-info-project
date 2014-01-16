package com.vertonur.dao.api;

import java.io.Serializable;

import com.vertonur.pojo.ExtendedBean;

/**
 * Marker interface
 * 
 * @author Vertonur
 * 
 * @param <T>
 * @param <ID>
 */
public interface ExtendedBeanDAO<T extends ExtendedBean, ID extends Serializable>
		extends GenericDAO<T, ID> {
}
