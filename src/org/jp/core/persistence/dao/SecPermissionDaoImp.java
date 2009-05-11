package org.jp.core.persistence.dao;

import java.util.List;

import org.jp.core.persistence.dao.imp.ISecPermissionDao;
import org.jp.core.persistence.pojo.SecPermission;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * encuestame:  system online surveys
 * Copyright (C) 2009  encuestame Development Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 3 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Id: SecPermissionDaoImp.java Date: 11/05/2009 11:27:49
 * @author juanpicado
 * package: org.jp.core.persistence.dao
 * @version 1.0
 */
public class SecPermissionDaoImp extends HibernateDaoSupport implements ISecPermissionDao {

	public void delete(Object obj) {
		// TODO Auto-generated method stub

	}

	public Object find(Class clazz, Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer lastRow(Class clase, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveOrUpdate(Object permisos) {
		getHibernateTemplate().save((SecPermission)permisos);
	}

}
