/**
 *  Copyright (C) 2010 Cloud.com, Inc.  All rights reserved.
 * 
 * This software is licensed under the GNU General Public License v3 or later.
 * 
 * It is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.cloud.upgrade.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.cloud.upgrade.dao.VersionVO.Step;
import com.cloud.utils.component.ComponentLocator;
import com.cloud.utils.db.DbTestUtils;
import com.cloud.utils.db.Transaction;
import com.cloud.utils.exception.CloudRuntimeException;

public class InstanceGroup217To223UpgradeTest extends TestCase {
    private static final Logger s_logger = Logger.getLogger(InstanceGroup217To223UpgradeTest.class);

    @Override
    @Before
    public void setUp() throws Exception {
        VersionVO version = new VersionVO("2.1.7");
        version.setStep(Step.Cleanup);
        DbTestUtils.executeScript("VersionDaoImplTest/clean-db.sql", false, true);
    }
    
    @Override
    @After
    public void tearDown() throws Exception {
    }
    
    public void test217to22Upgrade() {
        s_logger.debug("Finding sample data from 2.1.7");
        DbTestUtils.executeScript("VersionDaoImplTest/2.1.7/2.1.7_sample_instanceGroups.sql", false, true);
        
        Connection conn = Transaction.getStandaloneConnection();
        PreparedStatement pstmt;
        
        VersionDaoImpl dao = ComponentLocator.inject(VersionDaoImpl.class);
        DatabaseUpgradeChecker checker = ComponentLocator.inject(DatabaseUpgradeChecker.class);
        
        String version = dao.getCurrentVersion();
        
        if (!version.equals("2.1.7")) {
            s_logger.error("Version returned is not 2.1.7 but " + version);
        } else {
            s_logger.debug("Basic zone test version is " + version);
        }
        
        checker.upgrade("2.1.7", "2.2.3");
        
        conn = Transaction.getStandaloneConnection();
        try {
            
            s_logger.debug("Starting tesing upgrade from 2.1.7 to 2.2.3 for Instance groups...");
            
            //Version check
            pstmt = conn.prepareStatement("SELECT version FROM version");
            ResultSet rs = pstmt.executeQuery();
            
            if (!rs.next()) {
                s_logger.error("ERROR: No version selected");
            } else if (!rs.getString(1).equals("2.2.3")) {
                s_logger.error("ERROR: VERSION stored is not 2.2.3: " + rs.getString(1));
            }
            rs.close();
            pstmt.close();
            
            //Check that correct number of instance groups were created
            Long groupNumberVmInstance = 0L;
            pstmt = conn.prepareStatement("SELECT DISTINCT v.group, v.account_id from vm_instance v where v.group is not null");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                groupNumberVmInstance++;
            }

            rs.close();
            pstmt.close();
            
            Long groupNumber = 0L;
            pstmt = conn.prepareStatement("SELECT COUNT(*) FROM instance_group");
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                groupNumber = rs.getLong(1);
            }

            rs.close();
            pstmt.close();
            
            if (groupNumber != groupNumberVmInstance) {
                s_logger.error("ERROR: instance groups were updated incorrectly. Have " + groupNumberVmInstance + " groups in vm_instance table, and " + groupNumber + " where created in instance_group table. Stopping the test");
                System.exit(2);
            }
            
            
            //For each instance group from vm_instance table check that 1) entry was created in the instance_group table 2) vm to group map exists in instance_group_vm_map table
            //Check 1)
            pstmt = conn.prepareStatement("SELECT DISTINCT v.group, v.account_id from vm_instance v where v.group is not null");
            rs = pstmt.executeQuery();
            ArrayList<Object[]> groups = new ArrayList<Object[]>();
            while (rs.next()) {
                Object[] group = new Object[10];
                group[0] = rs.getString(1); // group name
                group[1] = rs.getLong(2);  // accountId
                groups.add(group);
            }
            rs.close();
            pstmt.close();
            
            for (Object[] group : groups) {
                String groupName = (String)group[0];
                Long accountId = (Long)group[1];
                if (!checkInstanceGroup(conn, groupName, accountId)) {
                    s_logger.error("ERROR: Unable to find group with name " + groupName + " for account id=" + accountId + ", stopping the test");
                    System.exit(2);
                }
            } 
            
            rs.close();
            pstmt.close();
            
            //Check 2)
            pstmt = conn.prepareStatement("SELECT g.id, v.id from vm_instance v, instance_group g where g.name=v.group and g.account_id=v.account_id and v.group is not null");
            rs = pstmt.executeQuery();
            ArrayList<Object[]> groupVmMaps = new ArrayList<Object[]>();
            while (rs.next()) {
                Object[] groupMaps = new Object[10];
                groupMaps[0] = rs.getLong(1); // vmId
                groupMaps[1] = rs.getLong(2);  // groupId
                groupVmMaps.add(groupMaps);
            }
            rs.close();
            pstmt.close();
            
            for (Object[] groupMap : groupVmMaps) {
                Long groupId = (Long)groupMap[0];
                Long instanceId = (Long)groupMap[1];
                if (!checkInstanceGroupVmMap(conn, groupId, instanceId)) {
                    s_logger.error("ERROR: unable to find instanceGroupVMMap for vm id=" + instanceId + " and group id=" + groupId + ", stopping the test");
                    System.exit(2);
                }
            }  
            
            rs.close();
            pstmt.close();
            
            s_logger.debug("Instance group upgrade test is passed");
            
        } catch (SQLException e) {
            throw new CloudRuntimeException("Problem testing instance group update", e);
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
    
    protected boolean checkInstanceGroup(Connection conn, String groupName, long accountId) throws SQLException{
        
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM  instance_group WHERE name = ? and account_id = ?");
        pstmt.setString(1, groupName);
        pstmt.setLong(2, accountId);
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            return false;
        } else {
            return true;
        }
    }
    
    protected boolean checkInstanceGroupVmMap(Connection conn, long groupId, long vmId) throws SQLException{
        
        PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM  instance_group_vm_map WHERE group_id = ? and instance_id = ?");
        pstmt.setLong(1, groupId);
        pstmt.setLong(2, vmId);
        ResultSet rs = pstmt.executeQuery();
        
        if (!rs.next()) {
            return false;
        } else {
            return true;
        }
    }
    
}

