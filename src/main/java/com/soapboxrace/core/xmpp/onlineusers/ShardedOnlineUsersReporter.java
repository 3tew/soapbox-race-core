/*
 * This file is part of the Soapbox Race World core source code.
 * If you use any of this code for third-party purposes, please provide attribution.
 * Copyright (c) 2020.
 */

package com.soapboxrace.core.xmpp.onlineusers;

import com.soapboxrace.core.bo.ParameterBO;
import com.soapboxrace.core.dao.OnlineUsersDAO;
import com.soapboxrace.core.jpa.OnlineUsersEntity;
import com.soapboxrace.core.xmpp.OnlineUsersReporter;
import com.soapboxrace.core.xmpp.OpenFireRestApiCli;

import java.util.Date;

public class ShardedOnlineUsersReporter implements OnlineUsersReporter {
    private static final ParameterBO PARAMETER_BO = new ParameterBO();
    private OpenFireRestApiCli openFireRestApiCli = new OpenFireRestApiCli();

    @Override
    public void insertNumberOfUsesOnlineNow(OnlineUsersDAO onlineUsersDAO) {
        if (PARAMETER_BO.isShardingMaster()) {
            Long timeLong = new Date().getTime() / 1000L;
            OnlineUsersEntity onlineUsersEntity = new OnlineUsersEntity();
            onlineUsersEntity.setNumberOfUsers(openFireRestApiCli.getTotalOnlineUsers());
            onlineUsersEntity.setTimeRecord(timeLong.intValue());
            onlineUsersDAO.insert(onlineUsersEntity);
        }
    }

    @Override
    public int getNumberOfUsersOnlineNow(OnlineUsersDAO onlineUsersDAO) {
        Date lastMinutes = getLastMinutes(1);
        OnlineUsersEntity onlineUsersEntity = onlineUsersDAO.findByTime(lastMinutes);
        return onlineUsersEntity != null ? onlineUsersEntity.getNumberOfUsers() : 0;
    }

    private Date getLastMinutes(int minutes) {
        long time = new Date().getTime();
        time = time - (minutes * 90000);

        return new Date(time);
    }
}
